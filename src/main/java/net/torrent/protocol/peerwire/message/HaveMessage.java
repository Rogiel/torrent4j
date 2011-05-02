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
 * have: [len=0005][id=4][piece index(int)]
 * </pre>
 * 
 * The have message is fixed length. The payload is the zero-based index of a
 * that has just been successfully downloaded and verified via the hash.<br>
 * <br>
 * <b>Implementer's Note</b>: That is the strict definition, in reality some
 * games may be played. In particular because peers are extremely unlikely to
 * download pieces that they already have, a peer may choose not to advertise
 * having a piece to a peer that already has that piece. At a minimum
 * "HAVE supression" will result in a 50% reduction in the number of HAVE
 * messages, this translates to around a 25-35% reduction in protocol overhead.
 * At the same time, it may be worthwhile to send a HAVE message to a peer that
 * has that piece already since it will be useful in determining which piece is
 * rare. A malicious peer might also choose to advertise having pieces that it
 * knows the peer will never download. Due to this attempting to model peers
 * using this information is a bad idea.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source BitTorrent documentation
 */
public class HaveMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	public static final byte MESSAGE_ID = 0x04;

	/**
	 * The new obtained piece index
	 */
	private int piece;

	public HaveMessage(int piece) {
		this.piece = piece;
	}

	public HaveMessage() {
	}

	@Override
	public void read(ChannelBuffer buffer) throws IOException {
		this.piece = buffer.readInt();
	}

	@Override
	public void write(ChannelBuffer buffer) throws IOException {
		buffer.writeByte(MESSAGE_ID);
		buffer.writeInt(piece);
	}
	
	@Override
	public int length() {
		return 5;
	}

	public int getPiece() {
		return piece;
	}

	public void setPiece(int piece) {
		this.piece = piece;
	}

	@Override
	public String toString() {
		return "HaveMessage [piece=" + piece + "]";
	}
}
