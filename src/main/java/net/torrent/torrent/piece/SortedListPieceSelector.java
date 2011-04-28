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
package net.torrent.torrent.piece;

import java.util.List;

import net.torrent.protocol.peerwire.manager.TorrentManager;
import net.torrent.torrent.TorrentPiece;
import net.torrent.torrent.context.TorrentPeer;

/**
 * An sorted {@link PieceSelector} select pieces sequentially (whenever
 * possible) in a pre-sorted list. The list is sorted on construction by the
 * {@link SortedListPieceSelector#sort(List) sort} method.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public abstract class SortedListPieceSelector implements PieceSelector {
	/**
	 * The torrent manager
	 */
	private final TorrentManager manager;
	/**
	 * The sorted list of pieces
	 */
	private final List<TorrentPiece> pieces;

	/**
	 * Creates a new instance
	 * 
	 * @param manager
	 *            the torrent manager
	 * @param pieces
	 *            the <b>unsorted</b> list of pieces
	 */
	protected SortedListPieceSelector(TorrentManager manager,
			List<TorrentPiece> pieces) {
		this.manager = manager;
		this.pieces = pieces;
		this.sort(this.pieces);
	}

	@Override
	public synchronized TorrentPiece select(TorrentPeer peer) {
		for (int index = 0; index < pieces.size(); index++) {
			final TorrentPiece piece = pieces.get(index);
			if (manager.getContext().getBitfield().hasPiece(piece))
				continue;
			if (!peer.getBitfield().hasPiece(piece))
				continue;
			if (manager.getDownloadManager().isDownloading(piece))
				continue;
			return piece;
		}
		return null;
	}

	/**
	 * Sorts the set using an implementation specific algorithm.
	 * 
	 * @param pieces
	 *            the unsorted pieces list that will be sorted.
	 */
	protected abstract void sort(List<TorrentPiece> pieces);
}
