package com.torrent4j.net.peerwire.codec;

import io.netty.buffer.ChannelBuffer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.oneone.OneToOneDecoder;

import com.torrent4j.net.peerwire.PeerWireMessage;
import com.torrent4j.net.peerwire.messages.BitFieldMessage;
import com.torrent4j.net.peerwire.messages.BlockMessage;
import com.torrent4j.net.peerwire.messages.CancelMessage;
import com.torrent4j.net.peerwire.messages.ChokeMessage;
import com.torrent4j.net.peerwire.messages.HandshakeMessage;
import com.torrent4j.net.peerwire.messages.HaveMessage;
import com.torrent4j.net.peerwire.messages.InterestedMessage;
import com.torrent4j.net.peerwire.messages.KeepAliveMessage;
import com.torrent4j.net.peerwire.messages.NotInterestedMessage;
import com.torrent4j.net.peerwire.messages.PortMessage;
import com.torrent4j.net.peerwire.messages.RequestMessage;
import com.torrent4j.net.peerwire.messages.UnchokeMessage;

public class PeerWireMessageDecoder extends OneToOneDecoder {
	private boolean handshaked = false;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (!(msg instanceof ChannelBuffer))
			return msg;
		final ChannelBuffer buffer = (ChannelBuffer) msg;

		if (!handshaked) {
			final HandshakeMessage message = new HandshakeMessage();
			message.read(buffer);

			return message;
		} else {
			if(buffer.readableBytes() == 0)
				return new KeepAliveMessage();
			
			final byte opcode = buffer.readByte();
			final PeerWireMessage message;
			switch (opcode) {
			case CancelMessage.MESSAGE_ID:
				message = new CancelMessage();
				break;
			case BitFieldMessage.MESSAGE_ID:
				message = new BitFieldMessage();
				break;
			case ChokeMessage.MESSAGE_ID:
				message = new ChokeMessage();
				break;
			case HaveMessage.MESSAGE_ID:
				message = new HaveMessage();
				break;
			case InterestedMessage.MESSAGE_ID:
				message = new InterestedMessage();
				break;
			case NotInterestedMessage.MESSAGE_ID:
				message = new NotInterestedMessage();
				break;
			case BlockMessage.MESSAGE_ID:
				message = new BlockMessage();
				break;
			case PortMessage.MESSAGE_ID:
				message = new PortMessage();
				break;
			case RequestMessage.MESSAGE_ID:
				message = new RequestMessage();
				break;
			case UnchokeMessage.MESSAGE_ID:
				message = new UnchokeMessage();
				break;
			default:
				return null;
			}
			message.read(buffer);
			return message;
		}
	}

	public void setHandshaked(boolean handshaked) {
		this.handshaked = handshaked;
	}
}
