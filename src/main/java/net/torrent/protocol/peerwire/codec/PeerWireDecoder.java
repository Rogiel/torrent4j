/*
 * Copyright 2011 Rogiel Josias Sulzbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.torrent.protocol.peerwire.codec;

import net.torrent.protocol.peerwire.message.BitfieldMessage;
import net.torrent.protocol.peerwire.message.CancelMessage;
import net.torrent.protocol.peerwire.message.ChokeMessage;
import net.torrent.protocol.peerwire.message.HandshakeMessage;
import net.torrent.protocol.peerwire.message.HaveMessage;
import net.torrent.protocol.peerwire.message.InterestedMessage;
import net.torrent.protocol.peerwire.message.KeepAliveMessage;
import net.torrent.protocol.peerwire.message.NotInterestedMessage;
import net.torrent.protocol.peerwire.message.PieceMessage;
import net.torrent.protocol.peerwire.message.PortMessage;
import net.torrent.protocol.peerwire.message.RequestMessage;
import net.torrent.protocol.peerwire.message.UnchokeMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * BitTorrent has two types of message headers:
 * <p>
 * <h1>Handshake message</h1> Handshake messages are composed of 1 byte, which
 * indicates the size of protocol string ("BitTorrent protocol").
 * <p>
 * <h1>Messages</h1> All other messages are 4-byte integer containing the
 * message length, followed by 1 byte opcode.
 * <p>
 * <p>
 * Instances of this class keep channel state content and must not be shared nor
 * cached.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PeerWireDecoder extends FrameDecoder {
	private boolean handshaked = false;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		buffer.markReaderIndex();

		if (!handshaked) {
			if (buffer.readableBytes() <= 47) // at least 47 bytes
				return null;

			final int pstrlen = buffer.readByte();
			if (buffer.readableBytes() < pstrlen + 47) {
				buffer.resetReaderIndex();
				return null;
			}
			buffer.readerIndex(buffer.readerIndex() - 1);

			final HandshakeMessage message = new HandshakeMessage();
			message.read(buffer);
			handshaked = true;

			return message;
		} else {
			if (buffer.readableBytes() <= 4) {
				buffer.resetReaderIndex();
				return null;
			}

			int len = buffer.readInt();
			if (len == 0) {
				return new KeepAliveMessage();
			} else if (buffer.readableBytes() < len) {
				buffer.resetReaderIndex();
				return null;
			}

			final byte id = buffer.readByte();
			final PeerWireReadableMessage message = getMessage(id);
			if (message == null)
				// force connection to be closed
				throw new CorruptedFrameException("unknown message " + id);
			message.read(buffer);
			return message;
		}
	}

	/**
	 * Return the message represented by <tt>id</tt>. Will return null if
	 * message id is unknown.
	 * 
	 * @param id
	 *            the id of the message
	 * @return the message
	 */
	private PeerWireReadableMessage getMessage(byte id) {
		PeerWireReadableMessage message = null;
		switch (id) {
		case BitfieldMessage.MESSAGE_ID:
			message = new BitfieldMessage();
			break;
		case CancelMessage.MESSAGE_ID:
			message = new CancelMessage();
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
		case PieceMessage.MESSAGE_ID:
			message = new PieceMessage();
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
		}
		return message;
	}
}
