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

import net.torrent.protocol.peerwire.PeerWireState;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * BitTorrent has two types of message headers:
 * <p>
 * <h1>Handshake message</h1> Handshake messages are composed of 1 byte, which
 * indicates the size of protocol string ("BitTorrent protocol").
 * <p>
 * <h1>Messages</h1> All other messages are 4-byte integer containing the
 * message length, an 1 byte opcode followed by the message content.
 * <p>
 * <p>
 * Instances of this class keep channel state content and must not be shared nor
 * cached.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @see PeerWireFrameDecoder#state
 */
public class PeerWireFrameEncoder extends OneToOneEncoder {
	/**
	 * This connection state. This need to be shared with other encoders,
	 * decoders or handlers. But more importantly <b>NEVER</b> share the same
	 * instance across more than one {@link Channel}.
	 */
	private final PeerWireState state;

	/**
	 * Creates a new instance of this encoder
	 * 
	 * @param state
	 *            the connection state
	 */
	public PeerWireFrameEncoder(PeerWireState state) {
		this.state = state;
	}

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (!(msg instanceof ChannelBuffer))
			return msg;
		if (!state.hasHandshaked())
			// on handshake message there is no visible "frame"
			return msg;
		ChannelBuffer buffer = (ChannelBuffer) msg;
		if (buffer.readableBytes() < 4)
			throw new CorruptedFrameException(
					"A frame must have at least 4 bytes!");
		buffer.setInt(0, buffer.readableBytes() - 4);
		return buffer;
	}
}
