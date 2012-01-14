package com.torrent4j.net.peerwire.codec;

import io.netty.buffer.ChannelBuffer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.frame.FrameDecoder;

public class PeerWireFrameDecoder extends FrameDecoder {
	private boolean handshaked = false;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		if (!handshaked) {
			int pos = buffer.readerIndex();
			buffer.skipBytes(68);
			return buffer.copy(pos, 68);
		}

		if (buffer.readableBytes() < 4)
			return null;

		int pos = buffer.readerIndex();
		final int len = buffer.readInt();
		if (buffer.readableBytes() >= len) {
			try {
				return buffer.slice(buffer.readerIndex(), len);
			} finally {
				buffer.skipBytes(len);
			}
		} else {
			buffer.readerIndex(pos);
			return null;
		}
	}

	public void setHandshaked(boolean handshaked) {
		this.handshaked = handshaked;
	}
}
