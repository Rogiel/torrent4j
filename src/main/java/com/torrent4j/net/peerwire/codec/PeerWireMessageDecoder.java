package com.torrent4j.net.peerwire.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

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

public class PeerWireMessageDecoder extends ReplayingDecoder<ByteBuf> {
	private boolean handshaked = false;
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,
			List<Object> out) throws Exception {
			if (!handshaked) {
				final HandshakeMessage message = new HandshakeMessage();
				message.read(buffer);
				
				out.add(message);
				return;
			} else {
				if(buffer.readableBytes() == 0) {
					out.add(new KeepAliveMessage());
					return;
				}
				
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
					return;
				}
				message.read(buffer);
				out.add(message);
			}
	}

	public void setHandshaked(boolean handshaked) {
		this.handshaked = handshaked;
	}
}
