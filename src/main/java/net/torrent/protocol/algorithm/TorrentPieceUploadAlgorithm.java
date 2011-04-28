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

import net.torrent.torrent.TorrentPart;
import net.torrent.torrent.context.TorrentPeer;

/**
 * Algorithm used for upload management. It verifies if we have interest in
 * uploading an piece and handles cancel requests. TODO how to handle cancels?
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public interface TorrentPieceUploadAlgorithm {
	/**
	 * Called when an peer has requested a piece to be uploaded.
	 * 
	 * @param peer
	 *            the peer requesting this piece
	 * @param part
	 *            the part requested
	 * @return true if allowed to upload, false otherwise.
	 */
	RequestAction request(TorrentPeer peer, TorrentPart part);

	public enum RequestAction {
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
		 * Reject this request (only if supports Fast Extension). If not
		 * supported will fall back to {@link RequestAction#NONE}
		 */
		REJECT,

		/**
		 * Choke this peer
		 */
		CHOKE,

		/**
		 * Upload a new piece (this is only valid if unchoked)
		 */
		UPLOAD,

		/**
		 * Do nothing, ignore.
		 */
		NONE;
	}

	/**
	 * Cancels a part request.
	 * 
	 * @param peer
	 *            the peer
	 * @param part
	 *            the part
	 * @return TODO
	 */
	boolean cancel(TorrentPeer peer, TorrentPart part);
}
