package com.torrent4j.net.peerwire.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PeerWireFrameDecoder extends ByteToMessageDecoder {
	private boolean handshaked = false;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,
			List<Object> out) throws Exception {
		if (!handshaked) {
			out.add(buffer);
			return;
		}

		if (buffer.readableBytes() < 4)
			return;

		int pos = buffer.readerIndex();
		final int len = buffer.readInt();
		if (buffer.readableBytes() >= len) {
			try {
				out.add(buffer.slice(buffer.readerIndex(), len));
				return;
			} finally {
				buffer.skipBytes(len);
			}
		} else {
			buffer.readerIndex(pos);
			return;
		}
	}

	public void setHandshaked(boolean handshaked) {
		this.handshaked = handshaked;
	}
}
