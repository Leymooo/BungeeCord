package ru.leymooo.botfilter.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public final class ChannelShutdownTracker {
	private final Channel ch;
	private boolean shutdown;

	public void shutdown(ChannelHandlerContext ctx) {
		if (this.shutdown) return;
		this.shutdown = true;
		val ch = this.ch;
		ch.pipeline().addFirst(ChannelDiscardHandler.DISCARD_FIRST, ChannelDiscardHandler.INSTANCE)
				.addAfter(ctx.name(), ChannelDiscardHandler.DISCARD, ChannelDiscardHandler.INSTANCE);
	}

	public boolean isShuttedDown() {
		return this.shutdown;
	}

	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	private static class DiscardHandler extends ChannelInboundHandlerAdapter
	{
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (msg instanceof ByteBuf) {
				((ByteBuf) msg).release();
				val ch = ctx.channel();
				if (ch.isActive()) ch.close();
			}
		}
	}
}
