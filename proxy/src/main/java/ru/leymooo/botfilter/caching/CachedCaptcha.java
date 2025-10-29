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
    private static final int PACKETID_1162and1193 = 0x25;
    private static final int PACKETID_1194 = 0x29;
    private static final int PACKETID_1202 = 0x2A;
    private static final int PACKETID_1205_AND1215 = 0x2C;
    private static final int PACKETID_1212 = 0x2D;
    private static final int PACKETID_1219 = 0x31;


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
        ByteBuf byteBuf1162 = PacketUtils.createPacket( map, PACKETID_1162and1193, ProtocolConstants.MINECRAFT_1_16_2 );
        ByteBuf byteBuf117 = PacketUtils.createPacket( map, PACKETID_115and117, ProtocolConstants.MINECRAFT_1_17 );
        ByteBuf byteBuf119 = PacketUtils.createPacket( map, PACKETID_19and119, ProtocolConstants.MINECRAFT_1_19 );
        ByteBuf byteBuf1191 = PacketUtils.createPacket( map, PACKETID_113and114and116_and1191, ProtocolConstants.MINECRAFT_1_19_1 );
        ByteBuf byteBuf1193 = PacketUtils.createPacket( map, PACKETID_1162and1193, ProtocolConstants.MINECRAFT_1_19_3 );
        ByteBuf byteBuf1194 = PacketUtils.createPacket( map, PACKETID_1194, ProtocolConstants.MINECRAFT_1_19_4 );
        ByteBuf byteBuf1202 = PacketUtils.createPacket( map, PACKETID_1202, ProtocolConstants.MINECRAFT_1_20_2 );
        ByteBuf byteBuf1205 = PacketUtils.createPacket( map, PACKETID_1205_AND1215, ProtocolConstants.MINECRAFT_1_20_5 );
        ByteBuf byteBuf1212 = PacketUtils.createPacket( map, PACKETID_1212, ProtocolConstants.MINECRAFT_1_21_2 );
        ByteBuf byteBuf1219 = PacketUtils.createPacket( map, PACKETID_1219, ProtocolConstants.MINECRAFT_1_21_9 );

        captchas[counter.getAndIncrement()] = new CaptchaHolder( answer, byteBuf18, byteBuf19, byteBuf113, byteBuf114And116, byteBuf115, byteBuf1162, byteBuf117, byteBuf119, byteBuf1191, byteBuf1193, byteBuf1194, byteBuf1202, byteBuf1205, byteBuf1212, byteBuf1219 );

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
        //ну и кринж...
        private final ByteBuf buf18, buf19, buf113, buf114And116, buf115, buf1162, buf117, buf119, buf1191, buf1193, buf1194,buf1202,buf1205and1215,buf1212,buf1219;

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
            } else if ( version <= ProtocolConstants.MINECRAFT_1_19_1 )
            {
                channel.write( buf1191.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_19_3 )
            {
                channel.write( buf1193.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_20 )
            {
                channel.write( buf1194.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_20_3 )
            {
                channel.write( buf1202.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_21 )
            {
                channel.write( buf1205and1215.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_21_4 )
            {
                channel.write( buf1212.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_21_7 )
            {
                channel.write( buf1205and1215.retainedDuplicate(), channel.voidPromise() );
            } else if ( version <= ProtocolConstants.MINECRAFT_1_21_9 )
            {
                channel.write( buf1219.retainedDuplicate(), channel.voidPromise() );
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
