package ru.leymooo.botfilter.packets;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;


@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JoinGame extends DefinedPacket
{

    private final int entityId;
    private boolean hardcore = false;
    private short gameMode = 0;
    private short previousGameMode = 0;
    private Set<String> worldNames = new HashSet<>( Arrays.asList( "minecraft:overworld" ) );
    //private Tag dimensions;
    //private Object dimension;
    private String worldName = "minecraft:overworld";
    private long seed = 1;
    private short difficulty = 0;
    private short maxPlayers = 1;
    private String levelType = "flat";
    private int viewDistance = 1;
    private boolean reducedDebugInfo = false;
    private boolean normalRespawn = true;
    private boolean debug = false;
    private boolean flat = true;

    private byte[] dimensions116 = new byte[] {
        10, 0, 0, 9, 0, 9, 100, 105, 109, 101, 110, 115, 105, 111, 110, 10, 0, 0, 0, 1, 1, 0, 11, 112, 105, 103, 108, 105, 110, 95, 115, 97, 102, 101, 0, 1, 0, 7, 110, 97, 116, 117, 114, 97, 108, 1, 5, 0, 13, 97, 109, 98, 105, 101, 110, 116, 95, 108, 105, 103, 104, 116, 0, 0, 0, 0, 8, 0, 10, 105, 110, 102, 105, 110, 105, 98, 117, 114, 110, 0, 30, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 105, 110, 102, 105, 110, 105, 98, 117, 114, 110, 95, 111, 118, 101, 114, 119, 111, 114, 108, 100, 1, 0, 20, 114, 101, 115, 112, 97, 119, 110, 95, 97, 110, 99, 104, 111, 114, 95, 119, 111, 114, 107, 115, 0, 1, 0, 12, 104, 97, 115, 95, 115, 107, 121, 108, 105, 103, 104, 116, 1, 1, 0, 9, 98, 101, 100, 95, 119, 111, 114, 107, 115, 1, 4, 0, 10, 102, 105, 120, 101, 100, 95, 116, 105, 109, 101, 0, 0, 0, 0, 0, 0, 39, 16, 8, 0, 4, 110, 97, 109, 101, 0, 19, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 111, 118, 101, 114, 119, 111, 114, 108, 100, 1, 0, 9, 104, 97, 115, 95, 114, 97, 105, 100, 115, 1, 1, 0, 6, 115, 104, 114, 117, 110, 107, 0, 1, 0, 14, 108, 111, 103, 105, 99, 97, 108, 95, 104, 101, 105, 103, 104, 116, 0, 1, 0, 11, 104, 97, 115, 95, 99, 101, 105, 108, 105, 110, 103, 0, 1, 0, 9, 117, 108, 116, 114, 97, 119, 97, 114, 109, 0, 0, 0
    };

    private byte[] dimensions1162 = new byte[]
    {
        10, 0, 0, 10, 0, 24, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 100, 105, 109, 101, 110, 115, 105, 111, 110, 95, 116, 121, 112, 101, 8, 0, 4, 116, 121, 112, 101, 0, 24, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 100, 105, 109, 101, 110, 115, 105, 111, 110, 95, 116, 121, 112, 101, 9, 0, 5, 118, 97, 108, 117, 101, 10, 0, 0, 0, 1, 8, 0, 4, 110, 97, 109, 101, 0, 19, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 111, 118, 101, 114, 119, 111, 114, 108, 100, 3, 0, 2, 105, 100, 0, 0, 0, 0, 10, 0, 7, 101, 108, 101, 109, 101, 110, 116, 1, 0, 11, 112, 105, 103, 108, 105, 110, 95, 115, 97, 102, 101, 0, 1, 0, 7, 110, 97, 116, 117, 114, 97, 108, 1, 5, 0, 13, 97, 109, 98, 105, 101, 110, 116, 95, 108, 105, 103, 104, 116, 0, 0, 0, 0, 8, 0, 10, 105, 110, 102, 105, 110, 105, 98, 117, 114, 110, 0, 30, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 105, 110, 102, 105, 110, 105, 98, 117, 114, 110, 95, 111, 118, 101, 114, 119, 111, 114, 108, 100, 1, 0, 20, 114, 101, 115, 112, 97, 119, 110, 95, 97, 110, 99, 104, 111, 114, 95, 119, 111, 114, 107, 115, 0, 1, 0, 12, 104, 97, 115, 95, 115, 107, 121, 108, 105, 103, 104, 116, 1, 1, 0, 9, 98, 101, 100, 95, 119, 111, 114, 107, 115, 1, 8, 0, 7, 101, 102, 102, 101, 99, 116, 115, 0, 19, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 111, 118, 101, 114, 119, 111, 114, 108, 100, 1, 0, 9, 104, 97, 115, 95, 114, 97, 105, 100, 115, 1, 1, 0, 14, 108, 111, 103, 105, 99, 97, 108, 95, 104, 101, 105, 103, 104, 116, 0, 5, 0, 16, 99, 111, 111, 114, 100, 105, 110, 97, 116, 101, 95, 115, 99, 97, 108, 101, 63, -128, 0, 0, 3, 0, 5, 109, 105, 110, 95, 121, 0, 0, 0, 0, 1, 0, 11, 104, 97, 115, 95, 99, 101, 105, 108, 105, 110, 103, 0, 1, 0, 9, 117, 108, 116, 114, 97, 119, 97, 114, 109, 0, 3, 0, 6, 104, 101, 105, 103, 104, 116, 0, 0, 1, 0, 0, 0, 0, 10, 0, 24, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 119, 111, 114, 108, 100, 103, 101, 110, 47, 98, 105, 111, 109, 101, 8, 0, 4, 116, 121, 112, 101, 0, 24, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 119, 111, 114, 108, 100, 103, 101, 110, 47, 98, 105, 111, 109, 101, 9, 0, 5, 118, 97, 108, 117, 101, 10, 0, 0, 0, 3, 8, 0, 4, 110, 97, 109, 101, 0, 16, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 112, 108, 97, 105, 110, 115, 3, 0, 2, 105, 100, 0, 0, 0, 1, 10, 0, 7, 101, 108, 101, 109, 101, 110, 116, 8, 0, 13, 112, 114, 101, 99, 105, 112, 105, 116, 97, 116, 105, 111, 110, 0, 4, 114, 97, 105, 110, 5, 0, 5, 100, 101, 112, 116, 104, 62, 0, 0, 0, 5, 0, 11, 116, 101, 109, 112, 101, 114, 97, 116, 117, 114, 101, 63, 76, -52, -51, 5, 0, 5, 115, 99, 97, 108, 101, 61, 76, -52, -51, 5, 0, 8, 100, 111, 119, 110, 102, 97, 108, 108, 62, -52, -52, -51, 8, 0, 8, 99, 97, 116, 101, 103, 111, 114, 121, 0, 6, 112, 108, 97, 105, 110, 115, 10, 0, 7, 101, 102, 102, 101, 99, 116, 115, 3, 0, 9, 115, 107, 121, 95, 99, 111, 108, 111, 114, 0, 120, -89, -1, 3, 0, 15, 119, 97, 116, 101, 114, 95, 102, 111, 103, 95, 99, 111, 108, 111, 114, 0, 5, 5, 51, 3, 0, 9, 102, 111, 103, 95, 99, 111, 108, 111, 114, 0, -64, -40, -1, 3, 0, 11, 119, 97, 116, 101, 114, 95, 99, 111, 108, 111, 114, 0, 63, 118, -28, 10, 0, 10, 109, 111, 111, 100, 95, 115, 111, 117, 110, 100, 3, 0, 10, 116, 105, 99, 107, 95, 100, 101, 108, 97, 121, 0, 0, 23, 112, 6, 0, 6, 111, 102, 102, 115, 101, 116, 64, 0, 0, 0, 0, 0, 0, 0, 3, 0, 19, 98, 108, 111, 99, 107, 95, 115, 101, 97, 114, 99, 104, 95, 101, 120, 116, 101, 110, 116, 0, 0, 0, 8, 8, 0, 5, 115, 111, 117, 110, 100, 0, 22, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 97, 109, 98, 105, 101, 110, 116, 46, 99, 97, 118, 101, 0, 0, 0, 0, 8, 0, 4, 110, 97, 109, 101, 0, 15, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 115, 119, 97, 109, 112, 3, 0, 2, 105, 100, 0, 0, 0, 6, 10, 0, 7, 101, 108, 101, 109, 101, 110, 116, 8, 0, 13, 112, 114, 101, 99, 105, 112, 105, 116, 97, 116, 105, 111, 110, 0, 4, 114, 97, 105, 110, 5, 0, 5, 100, 101, 112, 116, 104, -66, 76, -52, -51, 5, 0, 11, 116, 101, 109, 112, 101, 114, 97, 116, 117, 114, 101, 63, 76, -52, -51, 5, 0, 5, 115, 99, 97, 108, 101, 61, -52, -52, -51, 5, 0, 8, 100, 111, 119, 110, 102, 97, 108, 108, 63, 102, 102, 102, 8, 0, 8, 99, 97, 116, 101, 103, 111, 114, 121, 0, 5, 115, 119, 97, 109, 112, 10, 0, 7, 101, 102, 102, 101, 99, 116, 115, 3, 0, 9, 115, 107, 121, 95, 99, 111, 108, 111, 114, 0, 120, -89, -1, 3, 0, 15, 119, 97, 116, 101, 114, 95, 102, 111, 103, 95, 99, 111, 108, 111, 114, 0, 35, 35, 23, 3, 0, 9, 102, 111, 103, 95, 99, 111, 108, 111, 114, 0, -64, -40, -1, 3, 0, 11, 119, 97, 116, 101, 114, 95, 99, 111, 108, 111, 114, 0, 97, 123, 100, 8, 0, 20, 103, 114, 97, 115, 115, 95, 99, 111, 108, 111, 114, 95, 109, 111, 100, 105, 102, 105, 101, 114, 0, 5, 115, 119, 97, 109, 112, 3, 0, 13, 102, 111, 108, 105, 97, 103, 101, 95, 99, 111, 108, 111, 114, 0, 106, 112, 57, 10, 0, 10, 109, 111, 111, 100, 95, 115, 111, 117, 110, 100, 3, 0, 10, 116, 105, 99, 107, 95, 100, 101, 108, 97, 121, 0, 0, 23, 112, 6, 0, 6, 111, 102, 102, 115, 101, 116, 64, 0, 0, 0, 0, 0, 0, 0, 3, 0, 19, 98, 108, 111, 99, 107, 95, 115, 101, 97, 114, 99, 104, 95, 101, 120, 116, 101, 110, 116, 0, 0, 0, 8, 8, 0, 5, 115, 111, 117, 110, 100, 0, 22, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 97, 109, 98, 105, 101, 110, 116, 46, 99, 97, 118, 101, 0, 0, 0, 0, 8, 0, 4, 110, 97, 109, 101, 0, 21, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 115, 119, 97, 109, 112, 95, 104, 105, 108, 108, 115, 3, 0, 2, 105, 100, 0, 0, 0, -122, 10, 0, 7, 101, 108, 101, 109, 101, 110, 116, 8, 0, 13, 112, 114, 101, 99, 105, 112, 105, 116, 97, 116, 105, 111, 110, 0, 4, 114, 97, 105, 110, 5, 0, 5, 100, 101, 112, 116, 104, -67, -52, -52, -51, 5, 0, 11, 116, 101, 109, 112, 101, 114, 97, 116, 117, 114, 101, 63, 76, -52, -51, 5, 0, 5, 115, 99, 97, 108, 101, 62, -103, -103, -102, 5, 0, 8, 100, 111, 119, 110, 102, 97, 108, 108, 63, 102, 102, 102, 8, 0, 8, 99, 97, 116, 101, 103, 111, 114, 121, 0, 5, 115, 119, 97, 109, 112, 10, 0, 7, 101, 102, 102, 101, 99, 116, 115, 3, 0, 9, 115, 107, 121, 95, 99, 111, 108, 111, 114, 0, 120, -89, -1, 3, 0, 15, 119, 97, 116, 101, 114, 95, 102, 111, 103, 95, 99, 111, 108, 111, 114, 0, 35, 35, 23, 3, 0, 9, 102, 111, 103, 95, 99, 111, 108, 111, 114, 0, -64, -40, -1, 3, 0, 11, 119, 97, 116, 101, 114, 95, 99, 111, 108, 111, 114, 0, 97, 123, 100, 8, 0, 20, 103, 114, 97, 115, 115, 95, 99, 111, 108, 111, 114, 95, 109, 111, 100, 105, 102, 105, 101, 114, 0, 5, 115, 119, 97, 109, 112, 3, 0, 13, 102, 111, 108, 105, 97, 103, 101, 95, 99, 111, 108, 111, 114, 0, 106, 112, 57, 10, 0, 10, 109, 111, 111, 100, 95, 115, 111, 117, 110, 100, 3, 0, 10, 116, 105, 99, 107, 95, 100, 101, 108, 97, 121, 0, 0, 23, 112, 6, 0, 6, 111, 102, 102, 115, 101, 116, 64, 0, 0, 0, 0, 0, 0, 0, 3, 0, 19, 98, 108, 111, 99, 107, 95, 115, 101, 97, 114, 99, 104, 95, 101, 120, 116, 101, 110, 116, 0, 0, 0, 8, 8, 0, 5, 115, 111, 117, 110, 100, 0, 22, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 97, 109, 98, 105, 101, 110, 116, 46, 99, 97, 118, 101, 0, 0, 0, 0, 0, 0
    };

    private byte[] dimension = new byte[]
    {
        10, 0, 0, 1, 0, 11, 112, 105, 103, 108, 105, 110, 95, 115, 97, 102, 101, 0, 1, 0, 7, 110, 97, 116, 117, 114, 97, 108, 1, 5, 0, 13, 97, 109, 98, 105, 101, 110, 116, 95, 108, 105, 103, 104, 116, 0, 0, 0, 0, 8, 0, 10, 105, 110, 102, 105, 110, 105, 98, 117, 114, 110, 0, 30, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 105, 110, 102, 105, 110, 105, 98, 117, 114, 110, 95, 111, 118, 101, 114, 119, 111, 114, 108, 100, 1, 0, 20, 114, 101, 115, 112, 97, 119, 110, 95, 97, 110, 99, 104, 111, 114, 95, 119, 111, 114, 107, 115, 0, 1, 0, 12, 104, 97, 115, 95, 115, 107, 121, 108, 105, 103, 104, 116, 1, 1, 0, 9, 98, 101, 100, 95, 119, 111, 114, 107, 115, 1, 8, 0, 7, 101, 102, 102, 101, 99, 116, 115, 0, 19, 109, 105, 110, 101, 99, 114, 97, 102, 116, 58, 111, 118, 101, 114, 119, 111, 114, 108, 100, 1, 0, 9, 104, 97, 115, 95, 114, 97, 105, 100, 115, 1, 1, 0, 14, 108, 111, 103, 105, 99, 97, 108, 95, 104, 101, 105, 103, 104, 116, 0, 5, 0, 16, 99, 111, 111, 114, 100, 105, 110, 97, 116, 101, 95, 115, 99, 97, 108, 101, 63, -128, 0, 0, 3, 0, 5, 109, 105, 110, 95, 121, 0, 0, 0, 0, 1, 0, 11, 104, 97, 115, 95, 99, 101, 105, 108, 105, 110, 103, 0, 1, 0, 9, 117, 108, 116, 114, 97, 119, 97, 114, 109, 0, 3, 0, 6, 104, 101, 105, 103, 104, 116, 0, 0, 1, 0, 0
    };

    public JoinGame()
    {
        entityId = 0;
    }

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {

        buf.writeInt( entityId );
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_16_2 )
        {
            buf.writeBoolean( hardcore );
        }
        buf.writeByte( gameMode );
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_16 )
        {
            buf.writeByte( previousGameMode );

            writeVarInt( worldNames.size(), buf );
            for ( String world : worldNames )
            {
                writeString( world, buf );
            }

            if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_16_2 )
            {
                buf.writeBytes( dimensions1162 );
            } else
            {
                buf.writeBytes( dimensions116 );
            }
        }

        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_16 )
        {
            if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_16_2 )
            {
                buf.writeBytes( dimension );
            } else
            {
                writeString( (String) "minecraft:overworld", buf );
            }
            writeString( worldName, buf );
        } else if ( protocolVersion > ProtocolConstants.MINECRAFT_1_9 )
        {
            buf.writeInt( 0 ); //dim
        } else
        {
            buf.writeByte( 0 ); //dim
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_15 )
        {
            buf.writeLong( seed );
        }
        if ( protocolVersion < ProtocolConstants.MINECRAFT_1_14 )
        {
            buf.writeByte( difficulty );
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_16_2 )
        {
            writeVarInt( maxPlayers, buf );
        } else
        {
            buf.writeByte( maxPlayers );
        }
        if ( protocolVersion < ProtocolConstants.MINECRAFT_1_16 )
        {
            writeString( levelType, buf );
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_14 )
        {
            writeVarInt( viewDistance, buf );
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_18 )
        {
            writeVarInt( viewDistance, buf );
        }
        if ( protocolVersion >= 29 )
        {
            buf.writeBoolean( reducedDebugInfo );
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_15 )
        {
            buf.writeBoolean( normalRespawn );
        }
        if ( protocolVersion >= ProtocolConstants.MINECRAFT_1_16 )
        {
            buf.writeBoolean( debug );
            buf.writeBoolean( flat );
        }

    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        throw new UnsupportedOperationException();
    }
}