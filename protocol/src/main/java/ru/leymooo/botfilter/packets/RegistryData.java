package ru.leymooo.botfilter.packets;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import ru.leymooo.botfilter.utils.Dimension;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegistryData extends DefinedPacket
{

    private Tag codec;

    private String name;
    private List<Entry> entries;

    public RegistryData(Tag codec)
    {
        this.codec = codec;
    }

    public RegistryData(String name, List<Entry> entries)
    {
        this.name = name;
        this.entries = entries;
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        if (protocolVersion < ProtocolConstants.MINECRAFT_1_20_5)
        {
            writeTag( codec, buf, protocolVersion );
        } else {
            writeString( name, buf );
            writeVarInt( entries.size(), buf );
            for ( Entry entry : entries )
            {
                writeString( entry.id, buf );
                if (entry.tag != null) {
                    buf.writeBoolean( true );
                    writeTag( entry.tag, buf, protocolVersion );
                } else {
                    buf.writeBoolean( false );
                }
            }
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
    }


    @AllArgsConstructor
    public static class Entry{
        private String id;
        private Tag tag;
    }

    public static List<RegistryData> transform(Dimension dimension, int protocolVersion) {
        CompoundTag codec = (CompoundTag) dimension.getFullCodec( protocolVersion );

        if (protocolVersion <= ProtocolConstants.MINECRAFT_1_20_3) {
            return Collections.singletonList( new RegistryData( codec ) );
        }

        List<RegistryData> result = new ArrayList<>();
        for (NamedTag tag : codec) {
            String type = tag.getTag().get( "type" ).stringValue();
            ListTag values = tag.getTag().get( "value" ).asList();
            List<Entry> entries = new ArrayList<>(values.size());
            for (Tag value : values) {
                Tag element = value.get( "element" );
                entries.add( new Entry(value.get( "name" ).stringValue(), element.isError() ? null : element) );
            }
            result.add(new RegistryData(type, entries));
        }
        return result;
    }
}
