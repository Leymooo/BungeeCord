package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import lombok.Setter;
import ru.leymooo.botfilter.utils.FastCorruptedFrameException;

public class Varint21FrameDecoder extends ByteToMessageDecoder
{
    @Setter
    private boolean fromBackend;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        // If we decode an invalid packet and an exception is thrown (thus triggering a close of the connection),
        // the Netty ByteToMessageDecoder will continue to frame more packets and potentially call fireChannelRead()
        // on them, likely with more invalid packets. Therefore, check if the connection is no longer active and if so
        // sliently discard the packet.
        if ( !ctx.channel().isActive() )
        {
            in.skipBytes( in.readableBytes() );
            return;
        }

        int origReaderIndex = in.readerIndex();

        int i = 3;
        while ( i-- > 0 )
        {
            if ( !in.isReadable() )
            {
                in.readerIndex( origReaderIndex );
                return;
            }

            byte read = in.readByte();
            if ( read >= 0 )
            {
                // Make sure reader index of length buffer is returned to the beginning
                in.readerIndex( origReaderIndex );
                int packetLength = DefinedPacket.readVarInt( in );

                if ( packetLength <= 0 && !fromBackend ) // BotFilter dont throw exception for empty packets from backend
                {
                    super.setSingleDecode( true );  // BotFilter
                    throw new FastCorruptedFrameException( "Empty Packet!" );
                }
                if ( in.readableBytes() < packetLength )
                {
                    in.readerIndex( origReaderIndex );
                    return;
                }
                out.add( in.readRetainedSlice( packetLength ) );
                return;
            }
        }

        super.setSingleDecode( true ); // BotFilter
        throw new FastCorruptedFrameException( "length wider than 21-bit" ); // BotFilter
    }

    //BotFilter end
}
