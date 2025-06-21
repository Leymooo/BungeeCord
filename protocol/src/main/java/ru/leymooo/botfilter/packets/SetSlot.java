package ru.leymooo.botfilter.packets;

import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.nbt.NamedTag;
import net.md_5.bungee.nbt.Tag;
import net.md_5.bungee.nbt.type.CompoundTag;
import net.md_5.bungee.nbt.type.IntTag;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SetSlot extends DefinedPacket
{

    private int windowId;
    private int slot;
    private int item;
    private int count;
    private int data;

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int version)
    {
        if ( version >= ProtocolConstants.MINECRAFT_1_21_2 )
        {
            DefinedPacket.writeVarInt( this.windowId, buf );
        } else
        {
            buf.writeByte( this.windowId );
        }
        if ( version >= ProtocolConstants.MINECRAFT_1_17_1 )
        {
            DefinedPacket.writeVarInt( 0, buf );
        }

        buf.writeShort( this.slot );
        boolean isMap = this.item == 358;
        int id = isMap ? getCapthcaId( version ) : this.item;
        boolean present = id > 0;

        if ( version >= ProtocolConstants.MINECRAFT_1_13_2 )
        {
            if ( version < ProtocolConstants.MINECRAFT_1_20_5 )
            {
                buf.writeBoolean( present );
            }

            if ( !present && version >= ProtocolConstants.MINECRAFT_1_20_5 )
            {
                writeVarInt( 0, buf );
                return;
            }
        }
        if ( !present && version < ProtocolConstants.MINECRAFT_1_13_2 )
        {
            buf.writeShort( -1 );
        }

        if ( present )
        {
            if ( version < ProtocolConstants.MINECRAFT_1_13_2 )
            {
                buf.writeShort( id );
            } else
            {
                if ( version >= ProtocolConstants.MINECRAFT_1_20_5 )
                {
                    DefinedPacket.writeVarInt( count, buf );
                    DefinedPacket.writeVarInt( id, buf );
                } else
                {
                    DefinedPacket.writeVarInt( id, buf );
                    buf.writeByte( this.count );
                }
            }
            if ( version < ProtocolConstants.MINECRAFT_1_13 )
            {
                buf.writeShort( this.data );
            }

            if ( version < ProtocolConstants.MINECRAFT_1_17 )
            {
                buf.writeByte( 0 ); //No Nbt
            } else if ( version < ProtocolConstants.MINECRAFT_1_20_5 )
            {
                CompoundTag nbt = new CompoundTag(new LinkedHashMap<>() );
                nbt.put( "map", new IntTag( 0 ) );

                Tag write = version >= ProtocolConstants.MINECRAFT_1_20_2 ? nbt : new NamedTag( "", nbt );

                DefinedPacket.writeTag( write, buf, version );
            } else
            {
                if ( isMap )
                {
                    DefinedPacket.writeVarInt( 1, buf ); //added components
                    DefinedPacket.writeVarInt( 0, buf ); //removed components

                    if ( version < ProtocolConstants.MINECRAFT_1_21_2 )
                    {
                        DefinedPacket.writeVarInt( 26, buf ); //map data component
                    } else if (version < ProtocolConstants.MINECRAFT_1_21_5 )
                    {
                        DefinedPacket.writeVarInt( 36, buf ); //map data component
                    } else {
                        DefinedPacket.writeVarInt( 37, buf ); //map data component
                    }
                    DefinedPacket.writeVarInt( 0, buf ); //component value


                } else
                {
                    DefinedPacket.writeVarInt( 0, buf );
                    DefinedPacket.writeVarInt( 0, buf );
                }
            }

        }

    }

    private int getCapthcaId(int version)
    {
        if ( version <= ProtocolConstants.MINECRAFT_1_12_2 )
        {
            return 358;
        } else if ( version == ProtocolConstants.MINECRAFT_1_13 )
        {
            return 608;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_13_2 )
        {
            return 613;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_15_2 )
        {
            return 671;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_16_4 )
        {
            return 733;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_18_2 )
        {
            return 847;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_19_1 )
        {
            return 886;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_19_3 )
        {
            return 914;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_19_4 )
        {
            return 937;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_20_2 )
        {
            return 941;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_20_3 )
        {
            return 979;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_21_2 )
        {
            return 982;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_21_4 )
        {
            return 1031;
        } else if ( version <= ProtocolConstants.MINECRAFT_1_21_5 )
        {
            return 1042;
        } else
        {
            return 1059;
        }

    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
    }
}
