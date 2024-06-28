package ru.leymooo.botfilter.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import java.util.BitSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.LongArrayTag;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, of =
    {
    "x", "z"
    })
public class EmptyChunkPacket extends DefinedPacket
{

    int x;
    int z;

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int version)
    {
        buf.writeInt( this.x );
        buf.writeInt( this.z );


        if ( version < ProtocolConstants.MINECRAFT_1_17 )
        {
            buf.writeBoolean( true );
        }

        if ( version >= ProtocolConstants.MINECRAFT_1_16 && version < ProtocolConstants.MINECRAFT_1_16_2 )
        {
            buf.writeBoolean( true );
        }

        //BitMasks
        if ( version < ProtocolConstants.MINECRAFT_1_17 )
        {
            if ( version == ProtocolConstants.MINECRAFT_1_8 )
            {
                buf.writeShort( 1 );
            } else
            {
                writeVarInt( 0, buf );
            }
        } else if ( version < ProtocolConstants.MINECRAFT_1_18 )
        {
            BitSet bitSet = new BitSet();
            for ( int i = 0; i < 16; i++ )
            {
                bitSet.set( i, false );
            }
            long[] mask = bitSet.toLongArray();
            DefinedPacket.writeVarInt( mask.length, buf );
            for ( long l : mask )
            {
                buf.writeLong( l );
            }
        }
        if ( version >= ProtocolConstants.MINECRAFT_1_14 )
        {
            this.write1_14Heightmaps( buf, version );
            if ( version >= ProtocolConstants.MINECRAFT_1_15 && version < ProtocolConstants.MINECRAFT_1_18 )
            {
                if ( version >= ProtocolConstants.MINECRAFT_1_16_2 )
                {
                    writeVarInt( 1024, buf );
                    for ( int i = 0; i < 1024; i++ )
                    {
                        writeVarInt( 1, buf );
                    }
                } else
                {
                    for ( int i = 0; i < 1024; i++ )
                    {
                        buf.writeInt( 0 );
                    }
                }
            }
        }
        if ( version < ProtocolConstants.MINECRAFT_1_13 )
        {
            writeArray( new byte[256], buf ); //1.8 - 1.12.2
        } else if ( version < ProtocolConstants.MINECRAFT_1_15 )
        {
            writeArray( new byte[1024], buf ); //1.13 - 1.14.4
        } else if ( version < ProtocolConstants.MINECRAFT_1_18 )
        {
            writeVarInt( 0, buf ); //1.15 - 1.17.1
        } else
        {
            byte[] sectionData = new byte[] {0, 0, 0, 0, 0, 0, 1, 0};
            writeVarInt( sectionData.length * 16, buf );
            for ( int i = 0; i < 16; i++ )
            {
                buf.writeBytes( sectionData );
            }
        }
        if ( version >= ProtocolConstants.MINECRAFT_1_9_4 )
        {
            DefinedPacket.writeVarInt( 0, buf );
        }

        if ( version >= ProtocolConstants.MINECRAFT_1_18 ) //light data
        {
            byte[] lightData = new byte[] {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, -1, -1, 0, 0};
            buf.ensureWritable( lightData.length );
            if ( version >= ProtocolConstants.MINECRAFT_1_20 )
            {
                buf.writeBytes( lightData, 1, lightData.length - 1 );
            } else
            {
                buf.writeBytes( lightData );
            }

        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
    }

    private void write1_14Heightmaps(ByteBuf buf, int version)
    {
        long[] longArrayTag = new long[version < ProtocolConstants.MINECRAFT_1_18 ? 36 : 37];
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.add( "MOTION_BLOCKING", new LongArrayTag( longArrayTag ) );
        Tag write = version >= ProtocolConstants.MINECRAFT_1_20_2 ? compoundTag : new NamedTag( "", compoundTag );
        writeTag( write, buf, version );

    }
}
