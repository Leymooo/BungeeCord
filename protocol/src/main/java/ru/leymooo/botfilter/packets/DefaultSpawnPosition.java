package ru.leymooo.botfilter.packets;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import ru.leymooo.botfilter.utils.Dimension;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DefaultSpawnPosition extends DefinedPacket
{

    private Dimension dimension;
    private int posX;
    private int posY;
    private int posZ;
    private float yaw; //angle
    private float pitch;

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {

        if (protocolVersion >= ProtocolConstants.MINECRAFT_1_21_9)
        {
            DefinedPacket.writeString( dimension.getKey(), buf );
        }

        long location;
        if ( protocolVersion < ProtocolConstants.MINECRAFT_1_14 )
        {
            location = ( ( this.posX & 0x3FFFFFFL ) << 38 ) | ( ( this.posY & 0xFFFL ) << 26 ) | ( this.posZ & 0x3FFFFFFL );
        } else
        {
            location = ( ( this.posX & 0x3FFFFFFL ) << 38 ) | ( ( this.posZ & 0x3FFFFFFL ) << 12 ) | ( this.posY & 0xFFFL );
        }

        buf.writeLong( location );

        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_17 )
        {
            buf.writeFloat( this.yaw );
            if (protocolVersion >= ProtocolConstants.MINECRAFT_1_21_9)
            {
                buf.writeFloat( this.pitch );
            }
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        throw new UnsupportedOperationException();
    }


}
