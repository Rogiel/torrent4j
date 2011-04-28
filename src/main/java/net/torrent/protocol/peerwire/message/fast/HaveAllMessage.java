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
package net.torrent.protocol.peerwire.message.fast;

import java.io.IOException;

import net.torrent.protocol.peerwire.codec.PeerWireReadableMessage;
import net.torrent.protocol.peerwire.codec.PeerWireWritableMessage;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * <pre>
 * HaveAll: [len=0x0001][id=0x0E]
 * </pre>
 * 
 * Have All and Have None specify that the message sender has all or none of the
 * pieces respectively. When present, Have All or Have None replace the Have
 * Bitfield. Exactly one of Have All, Have None, or Have Bitfield MUST appear
 * and only immediately after the handshake. The reason for these messages is to
 * save bandwidth. Also slightly to remove the idiosyncrasy of sending no
 * message when a peer has no pieces.
 * <p>
 * When the fast extension is disabled, if a peer receives Have All or Have None
 * then the peer MUST close the connection.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source http://www.bittorrent.org/beps/bep_0006.html
 */
public class HaveAllMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	public static final byte MESSAGE_ID = 0x0E;

	@Override
	public void read(ChannelBuffer buffer) throws IOException {
	}

	@Override
	public void write(ChannelBuffer buffer) throws IOException {
		buffer.writeByte(MESSAGE_ID);
	}

	@Override
	public String toString() {
		return "HaveAllMessage []";
	}
}
