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
 * SuggestPiece: [len=0x0005][id=0x0D][piece index(int)]
 * </pre>
 * 
 * Suggest Piece is an advisory message meaning
 * "you might like to download this piece." The intended usage is for
 * 'super-seeding' without throughput reduction, to avoid redundant downloads,
 * and so that a seed which is disk I/O bound can upload continguous or
 * identical pieces to avoid excessive disk seeks. In all cases, the seed SHOULD
 * operate to maintain a roughly equal number of copies of each piece in the
 * network. A peer MAY send more than one suggest piece message at any given
 * time. A peer receiving multiple suggest piece messages MAY interpret this as
 * meaning that all of the suggested pieces are equally appropriate.
 * <p>
 * When the fast extension is disabled, if a peer receives a Suggest Piece
 * message, the peer MUST close the connection.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source http://www.bittorrent.org/beps/bep_0006.html
 */
public class SuggestPieceMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	public static final byte MESSAGE_ID = 0x0D;

	/**
	 * The new obtained piece index
	 */
	private int piece;

	public SuggestPieceMessage(int piece) {
		this.piece = piece;
	}

	public SuggestPieceMessage() {
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

	public int getPiece() {
		return piece;
	}

	public void setPiece(int piece) {
		this.piece = piece;
	}

	@Override
	public String toString() {
		return "SuggestPieceMessage [piece=" + piece + "]";
	}
}
