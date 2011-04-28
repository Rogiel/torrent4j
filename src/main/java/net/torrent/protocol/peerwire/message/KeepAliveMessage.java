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
package net.torrent.protocol.peerwire.message;

import java.io.IOException;

import net.torrent.protocol.peerwire.codec.PeerWireReadableMessage;
import net.torrent.protocol.peerwire.codec.PeerWireWritableMessage;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * <pre>
 * keep-alive: [len=0000]
 * </pre>
 * 
 * The keep-alive message is a message with zero bytes, specified with the
 * length prefix set to zero. There is no message ID and no payload. Peers may
 * close a connection if they receive no messages (keep-alive or any other
 * message) for a certain period of time, so a keep-alive message must be sent
 * to maintain the connection alive if no command have been sent for a given
 * amount of time. This amount of time is generally two minutes.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source BitTorrent documentation
 */
public class KeepAliveMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	@Override
	public void read(ChannelBuffer buffer) throws IOException {
	}

	@Override
	public void write(ChannelBuffer buffer) throws IOException {
	}

	@Override
	public String toString() {
		return "KeepAliveMessage []";
	}
}
