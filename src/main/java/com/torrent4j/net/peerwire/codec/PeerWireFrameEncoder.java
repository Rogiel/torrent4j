package com.torrent4j.net.peerwire.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PeerWireFrameEncoder extends MessageToByteEncoder<ByteBuf> {
	private boolean handshaked = false;

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf inputBuffer, ByteBuf outputBuffer)
			throws Exception {
		if (!handshaked) {
			outputBuffer.writeBytes(inputBuffer);
		} else {
			inputBuffer.readerIndex(0);
			outputBuffer.writeInt(0x00);
			outputBuffer.writeBytes(inputBuffer);
			if (handshaked) {
				final int len = inputBuffer.readableBytes();
				outputBuffer.setInt(0, len);
			}
		}
		System.out.println(ByteBufUtil.hexDump(outputBuffer));
	}

	public void setHandshaked(boolean handshaked) {
		this.handshaked = handshaked;
	}
}
