package ru.leymooo.botfilter.caching;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.protocol.Protocol;
import ru.leymooo.botfilter.packets.RegistryData;
import ru.leymooo.botfilter.utils.Dimension;

/**
 * @author Leymooo
 */
public class CachedRegistryData
{

    private List<ByteBuf> bufs = new ArrayList<>();

    public CachedRegistryData(Dimension dimension, int version)
    {
        create( dimension, version );
    }

    private void create(Dimension dimension, int version)
    {
        List<RegistryData> registryDatas = RegistryData.transform( dimension, version );
        for ( RegistryData registryData : registryDatas )
        {
            bufs.add( PacketUtils.createPacket( registryData, Protocol.BOTFILTER_CONFIGURATION, PacketUtils.getPacketId( registryData, version, Protocol.BOTFILTER_CONFIGURATION ), version ) );
        }
    }

    public void write(Channel channel)
    {
        for ( ByteBuf buf : bufs )
        {
            channel.write( buf.retainedDuplicate(), channel.voidPromise() );
        }
    }

    public void release()
    {
        for ( ByteBuf buf : bufs )
        {
            buf.release();
        }
    }
}
