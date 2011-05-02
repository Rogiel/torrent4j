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
import java.util.Arrays;

import net.torrent.protocol.peerwire.codec.PeerWireReadableMessage;
import net.torrent.protocol.peerwire.codec.PeerWireWritableMessage;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * <pre>
 * AllowedFast: [len=0x0004+pieces(4 bytes each)][id=0x11][piece indexes(int)]*
 * </pre>
 * 
 * With the BitTorrent protocol specified in BEP 0003 [2], new peers take
 * several minutes to ramp up before they can effectively engage in BitTorrent's
 * tit-for-tat. The reason is simple: starting peers have few pieces to trade.
 * <p>
 * Allowed Fast is an advisory message which means
 * "if you ask for this piece, I'll give it to you even if you're choked."
 * Allowed Fast thus shortens the awkward stage during which the peer obtains
 * occasional optimistic unchokes but cannot sufficiently reciprocate to remain
 * unchoked.
 * <p>
 * The pieces that can be downloaded when choked constitute a peer's allowed
 * fast set. The set is generated using a canonical algorithm that produces
 * piece indices unique to the message receiver so that if two peers offer k
 * pieces fast it will be the same k, and if one offers k+1 it will be the same
 * k plus one more. k should be small enough to avoid abuse, but large enough to
 * ramp up tit-for-tat. We currently set k to 10, but peers are free to change
 * this number, e.g., to suit load.
 * <p>
 * The message sender MAY list pieces that the message sender does not have. The
 * receiver MUST NOT interpret an Allowed Fast message as meaning that the
 * message sender has the piece. This allows peers to generate and communicate
 * allowed fast sets at the beginning of a connection. However, a peer MAY send
 * Allowed Fast messages at any time.
 * <p>
 * A peer SHOULD send Allowed Fast messages to any starting peer unless the
 * local peer lacks sufficient resources. A peer MAY reject requests for already
 * Allowed Fast pieces if the local peer lacks sufficient resources, if the
 * requested piece has already been sent to the requesting peer, or if the
 * requesting peer is not a starting peer. Our current implementation rejects
 * requests for Allowed Fast messages whenever the requesting peer has more than
 * * k * pieces.
 * <p>
 * When the fast extension is disabled, if a peer recieves an Allowed Fast
 * message then the peer MUST close the connection.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source http://www.bittorrent.org/beps/bep_0006.html
 */
public class AllowedFastMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	public static final byte MESSAGE_ID = 0x11;

	/**
	 * The pieces indexes
	 */
	private int[] pieces;

	public AllowedFastMessage(int... pieces) {
		this.pieces = pieces;
	}

	public AllowedFastMessage() {
	}

	@Override
	public void read(ChannelBuffer buffer) throws IOException {
		this.pieces = new int[buffer.readableBytes() / 4];
		for (int i = 0; i < pieces.length; i++) {
			pieces[i] = buffer.readInt();
		}
	}

	@Override
	public void write(ChannelBuffer buffer) throws IOException {
		buffer.writeByte(MESSAGE_ID);
		for (final int piece : pieces) {
			buffer.writeInt(piece);
		}
	}

	@Override
	public int length() {
		return 1 + pieces.length * 4;
	}

	public int[] getPieces() {
		return pieces;
	}

	public void setPieces(int[] pieces) {
		this.pieces = pieces;
	}

	@Override
	public String toString() {
		return "AllowedFastMessage [pieces="
				+ (pieces != null ? Arrays.toString(pieces) : null) + "]";
	}
}
