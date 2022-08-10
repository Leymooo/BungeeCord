package ru.leymooo.botfilter.caching;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.protocol.ProtocolConstants;
import ru.leymooo.botfilter.packets.MapDataPacket;

/**
 * @author Leymooo
 */
public class CachedCaptcha
{

    //уже пора с этим чтото придумать
    //В принципе я вроде чтото придумал для версии под Velocity, но будет ли она?....
    private static final int PACKETID_18 = 0x34;
    private static final int PACKETID_19and119 = 0x24;

    private static final int PACKETID_113and114and116_and1191 = 0x26;
    private static final int PACKETID_115and117 = 0x27;
    private static final int PACKETID_1162 = 0x25;


    private static final Random random = new Random();

    private static final CaptchaHolder[] captchas = new CaptchaHolder[900];
    private static final AtomicInteger counter = new AtomicInteger();

    public static boolean generated = false;

    public void createCaptchaPacket(MapDataPacket map, String answer)
    {

        ByteBuf byteBuf18 = PacketUtils.createPacket( map, PACKETID_18, ProtocolConstants.MINECRAFT_1_8 );
        ByteBuf byteBuf19 = PacketUtils.createPacket( map, PACKETID_19and119, ProtocolConstants.MINECRAFT_1_9 );
        ByteBuf byteBuf113 = PacketUtils.createPacket( map, PACKETID_113and114and116_and1191, ProtocolConstants.MINECRAFT_1_13 );
        ByteBuf byteBuf114And116 = PacketUtils.createPacket( map, PACKETID_113and114and116_and1191, ProtocolConstants.MINECRAFT_1_14 );
        ByteBuf byteBuf115 = PacketUtils.createPacket( map, PACKETID_115and117, ProtocolConstants.MINECRAFT_1_15 );
        ByteBuf byteBuf1162 = PacketUtils.createPacket( map, PACKETID_1162, ProtocolConstants.MINECRAFT_1_16_2 );
        ByteBuf byteBuf117 = PacketUtils.createPacket( map, PACKETID_115and117, ProtocolConstants.MINECRAFT_1_17 );
        ByteBuf byteBuf119 = PacketUtils.createPacket( map, PACKETID_19and119, ProtocolConstants.MINECRAFT_1_19 );
        ByteBuf byteBuf1191 = PacketUtils.createPacket( map, PACKETID_113and114and116_and1191, ProtocolConstants.MINECRAFT_1_19_1 );

        captchas[counter.getAndIncrement()] = new CaptchaHolder( answer, byteBuf18, byteBuf19, byteBuf113, byteBuf114And116, byteBuf115, byteBuf1162, byteBuf117, byteBuf119, byteBuf1191 );

        //TODO: Do something with this shit.
    }

    public CaptchaHolder randomCaptcha()
    {
        return captchas[random.nextInt( captchas.length )];
    }

    @RequiredArgsConstructor
    @Getter
    public static class CaptchaHolder
    {
        private final String answer;
        //now its not funny
        private final ByteBuf buf18, buf19, buf113, buf114And116, buf115, buf1162, buf117, buf119, buf1191;

        public void write(Channel channel, int version, boolean flush)
        {

            if ( version == ProtocolConstants.MINECRAFT_1_8 )
            {
                channel.write( buf18.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_12_2 )
            {
                channel.write( buf19.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_13_2 )
            {
                channel.write( buf113.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_14_4 )
            {
                channel.write( buf114And116.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_15_2 )
            {
                channel.write( buf115.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_16_1 )
            {
                channel.write( buf114And116.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_16_4 )
            {
                channel.write( buf1162.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_18_2 )
            {
                channel.write( buf117.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_19 )
            {
                channel.write( buf119.retainedDuplicate(), channel.voidPromise() );
            } else if ( version == ProtocolConstants.MINECRAFT_1_19_1 )
            {
                channel.write( buf1191.retainedDuplicate(), channel.voidPromise() );
            } else
            {
                throw new IllegalArgumentException( "version not found: " + version );
            }
            if ( flush )
            {
                channel.flush();
            }
        }
    }
}
