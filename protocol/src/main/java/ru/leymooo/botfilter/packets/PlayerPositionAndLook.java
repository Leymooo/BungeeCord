package ru.leymooo.botfilter.packets;

import io.netty.buffer.ByteBuf;
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
@EqualsAndHashCode(callSuper = false, exclude =
    {
    "onGround", "teleportId"
    })
public class PlayerPositionAndLook extends DefinedPacket
{

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private int teleportId;
    private boolean onGround;

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {

        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_21_2 )
        {
            DefinedPacket.writeVarInt( teleportId, buf );
            buf.writeDouble( this.x );
            buf.writeDouble( this.y );
            buf.writeDouble( this.z );
            buf.writeDouble( 0 ); //velocity x
            buf.writeDouble( 0 ); //velocity y
            buf.writeDouble( 0 ); //velocity z
            buf.writeFloat( this.yaw );
            buf.writeFloat( this.pitch );
            buf.writeInt( 0 );//teleport flags
        } else
        {
            buf.writeDouble( this.x );
            buf.writeDouble( this.y );
            buf.writeDouble( this.z );
            buf.writeFloat( this.yaw );
            buf.writeFloat( this.pitch );
            buf.writeByte( 0x00 );
            if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_9 )
            {
                PlayerPositionAndLook.writeVarInt( teleportId, buf );
            }
            if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_17 && protocolVersion <= ProtocolConstants.MINECRAFT_1_19_3 )
            {
                buf.writeBoolean( true ); // Dismount Vehicle
            }
        }


    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        if (protocolVersion < ProtocolConstants.MINECRAFT_1_21_2)
        {
            this.onGround = buf.readBoolean();
        } else {
            short flags = buf.readUnsignedByte();
            this.onGround = (flags & 1) != 0;
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
