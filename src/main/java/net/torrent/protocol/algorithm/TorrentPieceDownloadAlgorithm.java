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
package net.torrent.protocol.algorithm;

import net.torrent.protocol.peerwire.handler.PeerWireAlgorithmHandler;
import net.torrent.torrent.TorrentPart;
import net.torrent.torrent.TorrentPiece;
import net.torrent.torrent.context.TorrentPeer;

/**
 * This algorithm is used to return the {@link TorrentPart part} for download
 * and validates if an certain {@link TorrentPiece piece} is complete. Please
 * note that this algorithm DOES NOT do the checksum in the piece! The checksum
 * is done at the {@link PeerWireAlgorithmHandler handler} level.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public interface TorrentPieceDownloadAlgorithm {
	/**
	 * Return the next part desired for download
	 * 
	 * @param peer
	 *            the peer
	 * @param part
	 *            the part which has completed. Might be null!
	 * @return the new part
	 * @see TorrentPart
	 */
	TorrentPart getNextPart(TorrentPeer peer, TorrentPart part);

	/**
	 * Issued when an suggestion is received. If wished to accept it, return the
	 * first part of it, otherwise return null.
	 * 
	 * @param peer
	 *            the suggesting peer
	 * @param piece
	 *            the suggested piece
	 */
	TorrentPart sugested(TorrentPeer peer, TorrentPiece piece);

	/**
	 * Issued when allowed to fast download an given piece, even while choked.
	 * If multiple pieces are allowed, one call per piece will be done. If
	 * willing to download pieces, return the first part of the piece.
	 * 
	 * @param peer
	 *            the allowing peer
	 * @param piece
	 *            the allowed piece
	 */
	TorrentPart allowedFast(TorrentPeer peer, TorrentPiece piece);

	/**
	 * Issued when the peer has rejected our request.
	 * 
	 * @param peer
	 *            the rejecting peer
	 * @param part
	 *            the rejected part
	 * @see RejectAction
	 */
	RejectAction rejected(TorrentPeer peer, TorrentPart part);

	public enum RejectAction {
		/**
		 * Only disconnects the peer, does not initiate a new connection with
		 * anyone.
		 */
		DISCONNECT,

		/**
		 * Disconnects the current peer and connects a new one
		 */
		CONNECT_NEW_PEER,

		/**
		 * Choke this peer
		 */
		NOT_INTERESTED,

		/**
		 * Request again the same part
		 */
		RETRY,

		/**
		 * Try to download another piece
		 */
		TRY_ANOTHER_PIECE,

		/**
		 * Do nothing, ignore. Might cause peer to become idle.
		 */
		IGNORE;
	}

	/**
	 * Test if an certain piece has all its parts already download. If true, a
	 * checksum will be performed and a message informing we have this piece
	 * will be broadcasted. This call is only valid once the next part has
	 * already been taken.
	 * 
	 * @param peer
	 *            the peer
	 * @param piece
	 *            the piece to test
	 * @return true if complete, false otherwise.
	 */
	boolean isComplete(TorrentPeer peer, TorrentPiece piece);

	/**
	 * Called when an piece is complete but found to be corrupted
	 * 
	 * @param peer
	 *            the peer who send the piece (more precisely the completing
	 *            part)
	 * @param piece
	 *            the piece
	 * @return the action to be performed
	 * @see CorruptedAction
	 */
	CorruptedAction corrupted(TorrentPeer peer, TorrentPiece piece);

	/**
	 * Actions to be taken when a corrupted piece is downloaded.
	 * 
	 * @author Rogiel Josias Sulzbach (<a
	 *         href="http://www.rogiel.com/">http://www.rogiel.com/</a>)
	 */
	public enum CorruptedAction {
		/**
		 * Only disconnects the peer, does not initiate a new connection with
		 * anyone.
		 */
		DISCONNECT,

		/**
		 * Disconnects the current peer and connects a new one
		 */
		CONNECT_NEW_PEER,

		/**
		 * Choke this peer
		 */
		CHOKE,

		/**
		 * Retry to download the piece
		 */
		CONTINUE,

		/**
		 * Do nothing, ignore. Might cause peer to become idle.
		 */
		CANCEL;
	}
}
