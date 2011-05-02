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
import net.torrent.protocol.peerwire.message.header.PeerWireFastPeersMessageHeaderManager;
import net.torrent.protocol.peerwire.message.header.PeerWireMessageHeaderManager;
import net.torrent.protocol.peerwire.message.header.PeerWireSpecificationMessageHeaderManager;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

/**
 * This decoder decodes a {@link ChannelBuffer} into an
 * {@link PeerWireReadableMessage}.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @see PeerWireMessageDecoder#state
 */
public class PeerWireMessageDecoder extends OneToOneDecoder {
	/**
	 * This connection state. This need to be shared with other encoders,
	 * decoders or handlers. But more importantly <b>NEVER</b> share the same
	 * instance across more than one {@link Channel}.
	 */
	private final PeerWireState state;

	/**
	 * The is an list of header managers that will create message instances for
	 * each message id passed as argument.
	 * <p>
	 * {@link PeerWireSpecificationMessageHeaderManager} and
	 * {@link PeerWireFastPeersMessageHeaderManager} are already in the list.
	 */
	private PeerWireMessageHeaderManager[] headerManager = new PeerWireMessageHeaderManager[] {
			PeerWireSpecificationMessageHeaderManager.SHARED_INSTANCE,
			PeerWireFastPeersMessageHeaderManager.SHARED_INSTANCE };

	/**
	 * Creates a new instance of this decoder
	 * 
	 * @param state
	 *            the connection state
	 */
	public PeerWireMessageDecoder(PeerWireState state) {
		this.state = state;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (!(msg instanceof ChannelBuffer))
			return msg;
		final ChannelBuffer buffer = (ChannelBuffer) msg;
		if (buffer.readableBytes() == 0)
			return new KeepAliveMessage();

		final PeerWireReadableMessage message;
		if (state.hasPeerHandshaked()) {
			buffer.markReaderIndex();

			final byte id = buffer.readByte();
			message = getMessage(id);
		} else {
			message = new HandshakeMessage();
		}

		if (message == null) { // unknown message
			buffer.resetReaderIndex();
			return msg;
		}
		message.read(buffer);
		return message;
	}

	/**
	 * Adds a new message header manager to this decoder
	 * 
	 * @param newHeaderManager
	 *            the new header manager
	 */
	public void addMessageHeader(PeerWireMessageHeaderManager newHeaderManager) {
		headerManager = Arrays
				.copyOf(headerManager, (headerManager.length + 1));
		headerManager[(headerManager.length - 1)] = newHeaderManager;
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
		for (final PeerWireMessageHeaderManager handler : headerManager) {
			if (handler == null)
				continue;
			message = handler.getMessage(id);
			if (message != null)
				break;
		}
		return message;
	}
}
