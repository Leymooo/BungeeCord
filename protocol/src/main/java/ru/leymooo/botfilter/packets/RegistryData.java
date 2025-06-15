package ru.leymooo.botfilter.packets;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.nbt.NamedTag;
import net.md_5.bungee.nbt.Tag;
import net.md_5.bungee.nbt.TypedTag;
import net.md_5.bungee.nbt.type.CompoundTag;
import net.md_5.bungee.nbt.type.ListTag;
import net.md_5.bungee.nbt.type.StringTag;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import ru.leymooo.botfilter.utils.Dimension;

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
        for ( TypedTag tag : codec.getValue().values()) {
            String type = (( StringTag ) get( tag, "type" ) ).getValue();
            ListTag values = get(tag, "value");
            List<Entry> entries = new ArrayList<>(values.size());
            for (Tag value : values.getValue()) {
                Tag element = get( value,"element" );
                entries.add( new Entry(((StringTag)get( value,"name" )).getValue(), element) );
            }
            result.add(new RegistryData(type, entries));
        }
        return result;
    }


    private static <T> T get(Tag tag, String key) {
        CompoundTag compoundTag = (CompoundTag) tag;
        T result = (T) compoundTag.get( key );
        return result;
    }
}
