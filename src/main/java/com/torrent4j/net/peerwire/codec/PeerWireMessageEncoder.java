package com.torrent4j.net.peerwire.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.torrent4j.net.peerwire.PeerWireMessage;
import com.torrent4j.net.peerwire.messages.KeepAliveMessage;

public class PeerWireMessageEncoder extends MessageToByteEncoder<PeerWireMessage> {
	private boolean handshaked = false;

	@Override
	protected void encode(ChannelHandlerContext ctx, PeerWireMessage message,
			ByteBuf buffer) throws Exception {
		if (handshaked && !(message instanceof KeepAliveMessage))
			buffer.writeInt(0x00);
		message.write(buffer);
	}

	public void setHandshaked(boolean handshaked) {
		this.handshaked = handshaked;
	}
}
