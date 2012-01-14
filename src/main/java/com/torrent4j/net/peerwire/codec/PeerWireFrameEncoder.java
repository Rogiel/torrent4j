package com.torrent4j.net.peerwire.codec;

import io.netty.buffer.ChannelBuffer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.oneone.OneToOneEncoder;

public class PeerWireFrameEncoder extends OneToOneEncoder {
	private boolean handshaked = false;

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (!(msg instanceof ChannelBuffer) || !handshaked)
			return msg;
		final ChannelBuffer buffer = (ChannelBuffer) msg;

		buffer.readerIndex(0);
		if (handshaked) {
			final int len = buffer.readableBytes() - 4;
			buffer.setInt(0, len);
		}

		return buffer;
	}

	public void setHandshaked(boolean handshaked) {
		this.handshaked = handshaked;
	}
}
