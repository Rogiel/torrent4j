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

import java.util.Arrays;

import net.torrent.protocol.peerwire.PeerWireState;
import net.torrent.protocol.peerwire.message.HandshakeMessage;
import net.torrent.protocol.peerwire.message.KeepAliveMessage;
import net.torrent.protocol.peerwire.message.header.PeerWireMessageHeaderManager;
import net.torrent.protocol.peerwire.message.header.PeerWireSpecificationMessageHeaderManager;

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
 * @see PeerWireOldDecoder#state
 */
@Deprecated
public class PeerWireOldDecoder extends FrameDecoder {
	/**
	 * This connection state. This need to be shared with other encoders or
	 * decoders. But more importantly <b>NEVER</b> share the same instance
	 * across more than one {@link Channel}.
	 */
	private final PeerWireState state;

	/**
	 * The is an list of handlers that will create message instances for each
	 * message id passed as argument.
	 */
	private PeerWireMessageHeaderManager[] handlers = new PeerWireMessageHeaderManager[] { PeerWireSpecificationMessageHeaderManager.SHARED_INSTANCE };

	/**
	 * Creates a new instance of this decoder
	 * 
	 * @param state
	 *            the connection state
	 */
	public PeerWireOldDecoder(final PeerWireState state) {
		this.state = state;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		buffer.markReaderIndex();

		if (!state.hasHandshaked()) {
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
			state.setHandshaked(true);

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
	 * Adds a new message handler to this decoder
	 * 
	 * @param handler
	 *            the handler
	 */
	public void addMessageHandler(PeerWireMessageHeaderManager handler) {
		Arrays.copyOf(this.handlers, (this.handlers.length + 1));
		this.handlers[(this.handlers.length - 1)] = handler;
	}

	/**
	 * Return the message represented by <tt>id</tt>.
	 * <p>
	 * Iterate over all <tt>handlers</tt> and try to locate the message. Will
	 * return null if message id is unknown.
	 * 
	 * @param id
	 *            the id of the message
	 * @return the message
	 */
	private PeerWireReadableMessage getMessage(byte id) {
		PeerWireReadableMessage message = null;
		for (final PeerWireMessageHeaderManager handler : handlers) {
			if (handler == null)
				continue;
			message = handler.getMessage(id);
			if (message != null)
				break;
		}
		return message;
	}
}
