package ru.leymooo.botfilter.packets;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UpdateTags extends DefinedPacket
{
    private Map<String, List<Entry>> entries;


    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {

        writeVarInt( entries.size(), buf );
        entries.forEach( (id, tags) -> {
            writeString( id, buf );
            writeVarInt( tags.size(), buf );
            for (Entry tag : tags) {
                writeString( tag.name, buf );
                writeVarInt( tag.ids.size(), buf );
                for ( int i : tag.ids )
                {
                    writeVarInt( i, buf );
                }
            }
        });
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
    }


    @AllArgsConstructor
    public static class Entry
    {
        private String name;
        private List<Integer> ids;
    }

}
