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
package net.torrent.protocol.algorithm.impl;

import net.torrent.protocol.algorithm.TorrentAlgorithm;
import net.torrent.protocol.algorithm.TorrentInterestAlgorithm;
import net.torrent.protocol.algorithm.TorrentPeerAlgorithm;
import net.torrent.protocol.algorithm.TorrentPieceDownloadAlgorithm;
import net.torrent.protocol.algorithm.TorrentPieceUploadAlgorithm;
import net.torrent.protocol.peerwire.manager.TorrentManager;
import net.torrent.torrent.piece.PieceSelector;

/**
 * Standard torrent algorithm
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentTestAlgorithm implements TorrentAlgorithm {
	private final TorrentPeerAlgorithm peerAlgorithm;
	private final TorrentInterestAlgorithm interestAlgorithm;
	private final TorrentPieceDownloadAlgorithm downloadAlgorithm;
	private final TorrentPieceUploadAlgorithm uploadAlgorithm;

	public TorrentTestAlgorithm(final TorrentManager manager,
			final PieceSelector selector) {
		peerAlgorithm = new TorrentTestPeerAlgorithm(manager);
		interestAlgorithm = new TorrentTestInterestAlgorithm(manager, selector);
		downloadAlgorithm = new TorrentTestPieceDownloadAlgorithm(manager,
				selector);
		uploadAlgorithm = new TorrentTestPieceUploadAlgorithm(manager);
	}

	@Override
	public TorrentPeerAlgorithm getPeerAlgorithm() {
		return peerAlgorithm;
	}

	@Override
	public TorrentInterestAlgorithm getInterestAlgorithm() {
		return interestAlgorithm;
	}

	@Override
	public TorrentPieceDownloadAlgorithm getDownloadAlgorithm() {
		return downloadAlgorithm;
	}

	@Override
	public TorrentPieceUploadAlgorithm getUploadAlgorithm() {
		return uploadAlgorithm;
	}
}
