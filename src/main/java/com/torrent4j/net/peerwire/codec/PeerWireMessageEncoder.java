package com.torrent4j.net.peerwire.codec;

import io.netty.buffer.ChannelBuffer;
import io.netty.buffer.ChannelBuffers;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.oneone.OneToOneEncoder;

import com.torrent4j.net.peerwire.PeerWireMessage;
import com.torrent4j.net.peerwire.messages.KeepAliveMessage;

public class PeerWireMessageEncoder extends OneToOneEncoder {
	private boolean handshaked = false;

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (!(msg instanceof PeerWireMessage))
			return msg;
		final PeerWireMessage message = (PeerWireMessage) msg;
		final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

		if (handshaked && !(message instanceof KeepAliveMessage))
			buffer.writeInt(0x00);
		message.write(buffer);

		return buffer;
	}

	public void setHandshaked(boolean handshaked) {
		this.handshaked = handshaked;
	}
}
