package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.incubator.channel.uring.IOUring;
import io.netty.incubator.channel.uring.IOUringDatagramChannel;
import io.netty.incubator.channel.uring.IOUringEventLoopGroup;
import io.netty.incubator.channel.uring.IOUringServerSocketChannel;
import io.netty.incubator.channel.uring.IOUringSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.internal.PlatformDependent;
import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.ConnectionThrottle;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.KickStringWriter;
import net.md_5.bungee.protocol.LegacyDecoder;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.Varint21FrameDecoder;
import net.md_5.bungee.protocol.Varint21LengthFieldExtraBufPrepender;
import net.md_5.bungee.protocol.Varint21LengthFieldPrepender;

public class PipelineUtils
{

    public static final AttributeKey<ListenerInfo> LISTENER = AttributeKey.valueOf( "ListerInfo" );

    public static final ChannelInitializer<Channel> SERVER_CHILD = new ChannelInitializer<Channel>()
    {
        @Override
        protected void initChannel(Channel ch) throws Exception
        {
            SocketAddress remoteAddress = ( ch.remoteAddress() == null ) ? ch.parent().localAddress() : ch.remoteAddress();

            BungeeCord instance = BungeeCord.getInstance();
            ConnectionThrottle throttle = instance.getConnectionThrottle();
            if ( throttle != null && throttle.throttle( remoteAddress ) )
            {
                ch.close();
                return;
            }

            ListenerInfo listener = ch.attr( LISTENER ).get();

            if ( instance.getPluginManager().callEvent( new ClientConnectEvent( remoteAddress, listener ) ).isCancelled() )
            {
                ch.close();
                return;
            }

            BASE.initChannel( ch );
            ch.pipeline().addBefore( FRAME_DECODER, LEGACY_DECODER, new LegacyDecoder() );
            ch.pipeline().addAfter( FRAME_DECODER, PACKET_DECODER, new MinecraftDecoder( Protocol.HANDSHAKE, true, ProxyServer.getInstance().getProtocolVersion() ) );
            ch.pipeline().addAfter( FRAME_PREPENDER, PACKET_ENCODER, new MinecraftEncoder( Protocol.HANDSHAKE, true, ProxyServer.getInstance().getProtocolVersion() ) );
            ch.pipeline().addBefore( FRAME_PREPENDER, LEGACY_KICKER, legacyKicker );
            ch.pipeline().get( HandlerBoss.class ).setHandler( new InitialHandler( BungeeCord.getInstance(), listener ) );

            if ( listener.isProxyProtocol() )
            {
                ch.pipeline().addFirst( new HAProxyMessageDecoder() );
            }
        }
    };
    public static final Base BASE = new Base( false );
    public static final Base BASE_SERVERSIDE = new Base( true );
    private static final KickStringWriter legacyKicker = new KickStringWriter();
    private static final Varint21LengthFieldExtraBufPrepender serverFramePrepender = new Varint21LengthFieldExtraBufPrepender();
    public static final String TIMEOUT_HANDLER = "timeout";
    public static final String PACKET_DECODER = "packet-decoder";
    public static final String PACKET_ENCODER = "packet-encoder";
    public static final String BOSS_HANDLER = "inbound-boss";
    public static final String ENCRYPT_HANDLER = "encrypt";
    public static final String DECRYPT_HANDLER = "decrypt";
    public static final String FRAME_DECODER = "frame-decoder";
    public static final String FRAME_PREPENDER = "frame-prepender";
    public static final String LEGACY_DECODER = "legacy-decoder";
    public static final String LEGACY_KICKER = "legacy-kick";

    private static boolean epoll;
    private static boolean io_uring;

    static
    {
        if ( !PlatformDependent.isWindows() )
        {
            // disable by default (experimental)
            if ( Boolean.parseBoolean( System.getProperty( "bungee.io_uring", "false" ) ) )
            {
                ProxyServer.getInstance().getLogger().info( "Not on Windows, attempting to use enhanced IOUringEventLoopGroup" );
                if ( io_uring = IOUring.isAvailable() )
                {
                    ProxyServer.getInstance().getLogger().log( Level.WARNING, "io_uring is enabled and working, utilising it! (experimental feature)" );
                } else
                {
                    ProxyServer.getInstance().getLogger().log( Level.WARNING, "io_uring is not working: {0}", Util.exception( IOUring.unavailabilityCause() ) );
                }
            }

            if ( !io_uring && Boolean.parseBoolean( System.getProperty( "bungee.epoll", "true" ) ) )
            {
                ProxyServer.getInstance().getLogger().info( "Not on Windows, attempting to use enhanced EpollEventLoop" );
                if ( epoll = Epoll.isAvailable() )
                {
                    ProxyServer.getInstance().getLogger().info( "Epoll is working, utilising it!" );
                } else
                {
                    ProxyServer.getInstance().getLogger().log( Level.WARNING, "Epoll is not working, falling back to NIO: {0}", Util.exception( Epoll.unavailabilityCause() ) );
                }
            }
        }
    }

    public static EventLoopGroup newEventLoopGroup(int threads, ThreadFactory factory)
    {
        return io_uring ? new IOUringEventLoopGroup( threads, factory ) : epoll ? new EpollEventLoopGroup( threads, factory ) : new NioEventLoopGroup( threads, factory );
    }

    public static Class<? extends ServerChannel> getServerChannel(SocketAddress address)
    {
        if ( address instanceof DomainSocketAddress )
        {
            Preconditions.checkState( epoll, "Epoll required to have UNIX sockets" );

            return EpollServerDomainSocketChannel.class;
        }

        return io_uring ? IOUringServerSocketChannel.class : epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends Channel> getChannel(SocketAddress address)
    {
        if ( address instanceof DomainSocketAddress )
        {
            Preconditions.checkState( epoll, "Epoll required to have UNIX sockets" );

            return EpollDomainSocketChannel.class;
        }

        return io_uring ? IOUringSocketChannel.class : epoll ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends DatagramChannel> getDatagramChannel()
    {
        return io_uring ? IOUringDatagramChannel.class : epoll ? EpollDatagramChannel.class : NioDatagramChannel.class;
    }

    private static final int LOW_MARK = Integer.getInteger( "net.md_5.bungee.low_mark", 2 << 18 ); // 0.5 mb
    private static final int HIGH_MARK = Integer.getInteger( "net.md_5.bungee.high_mark", 2 << 20 ); // 2 mb
    private static final WriteBufferWaterMark MARK = new WriteBufferWaterMark( LOW_MARK, HIGH_MARK );

    @NoArgsConstructor // for backwards compatibility
    @AllArgsConstructor
    public static final class Base extends ChannelInitializer<Channel>
    {

        private boolean toServer = false;

        @Override
        public void initChannel(Channel ch) throws Exception
        {
            try
            {
                ch.config().setOption( ChannelOption.IP_TOS, 0x18 );
            } catch ( ChannelException ex )
            {
                // IP_TOS is not supported (Windows XP / Windows Server 2003)
            }
            ch.config().setOption( ChannelOption.TCP_NODELAY, true ); //BotFilter //WaterFall backport
            ch.config().setAllocator( PooledByteBufAllocator.DEFAULT );
            ch.config().setWriteBufferWaterMark( MARK );

            ch.pipeline().addLast( FRAME_DECODER, new Varint21FrameDecoder() );
            ch.pipeline().addLast( TIMEOUT_HANDLER, new ReadTimeoutHandler( BungeeCord.getInstance().config.getTimeout(), TimeUnit.MILLISECONDS ) );
            // No encryption bungee -> server, therefore use extra buffer to avoid copying everything for length prepending
            // Not used bungee -> client as header would need to be encrypted separately through expensive JNI call
            ch.pipeline().addLast( FRAME_PREPENDER, ( toServer ) ? serverFramePrepender : new Varint21LengthFieldPrepender() );

            ch.pipeline().addLast( BOSS_HANDLER, new HandlerBoss() );
        }
    }
}
