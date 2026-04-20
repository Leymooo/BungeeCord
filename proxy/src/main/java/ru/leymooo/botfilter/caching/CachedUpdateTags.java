package ru.leymooo.botfilter.caching;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.md_5.bungee.protocol.Protocol;
import ru.leymooo.botfilter.packets.UpdateTags;
import ru.leymooo.botfilter.utils.UpdateTagsHelper;

/**
 * @author Leymooo
 */
public class CachedUpdateTags
{

    private ByteBuf buf;

    public CachedUpdateTags(int version)
    {
        create( version );
    }

    private void create(int version)
    {
        UpdateTags updateTags = UpdateTagsHelper.createPacket();
        buf = PacketUtils.createPacket( updateTags, Protocol.BOTFILTER_CONFIGURATION, PacketUtils.getPacketId( updateTags, version, Protocol.BOTFILTER_CONFIGURATION ), version );

    }

    public void write(Channel channel)
    {
        channel.write( buf.retainedDuplicate(), channel.voidPromise() );

    }

    public void release()
    {
        buf.release();
    }
}
