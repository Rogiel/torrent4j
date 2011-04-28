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

/**
 * An {@link TorrentAlgorithm} defines the rules for download, upload and
 * connection management. These algorithms provide limited control, in the
 * boundaries of standard torrent behavior. If you wish more control, at the
 * protocol layer, try implementing a new {@link PeerWireAlgorithmHandler}.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @see TorrentPeerAlgorithm
 * @see TorrentInterestAlgorithm
 * @see TorrentPieceDownloadAlgorithm
 * @see TorrentPieceUploadAlgorithm
 */
public interface TorrentAlgorithm {
	/**
	 * Creates a new instance of {@link TorrentPeerAlgorithm}.
	 * 
	 * @return the new {@link TorrentPeerAlgorithm} instance
	 */
	TorrentPeerAlgorithm getPeerAlgorithm();

	/**
	 * Creates a new instance of {@link TorrentInterestAlgorithm}.
	 * 
	 * @return the new {@link TorrentInterestAlgorithm} instance
	 */
	TorrentInterestAlgorithm getInterestAlgorithm();

	/**
	 * Creates a new instance of {@link TorrentPieceDownloadAlgorithm}.
	 * 
	 * @return the new {@link TorrentPieceDownloadAlgorithm} instance
	 */
	TorrentPieceDownloadAlgorithm getDownloadAlgorithm();

	/**
	 * Creates a new instance of {@link TorrentPieceUploadAlgorithm}.
	 * 
	 * @return the new {@link TorrentPieceUploadAlgorithm} instance
	 */
	TorrentPieceUploadAlgorithm getUploadAlgorithm();
}
