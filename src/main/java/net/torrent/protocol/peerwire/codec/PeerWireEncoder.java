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

import net.torrent.protocol.peerwire.message.HandshakeMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Messages are encoded in {@link PeerWireWritableMessage#write(ChannelBuffer)}
 * method. Message length is measured automatically by the encoder.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PeerWireEncoder extends OneToOneEncoder {
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		if (!(msg instanceof PeerWireWritableMessage))
			return msg;

		if (msg instanceof HandshakeMessage) {
			final HandshakeMessage message = (HandshakeMessage) msg;
			message.write(buffer);
		} else {
			final PeerWireWritableMessage message = (PeerWireWritableMessage) msg;
			buffer.writeInt(0); // allocate 4 bytes for header
			message.write(buffer);
			int len = buffer.readableBytes();
			buffer.setInt(0, len - 4);
		}
		return buffer;
	}
}
