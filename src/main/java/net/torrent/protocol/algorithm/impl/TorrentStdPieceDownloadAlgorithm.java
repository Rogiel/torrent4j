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

import java.util.HashSet;
import java.util.Set;

import net.torrent.protocol.algorithm.TorrentPieceDownloadAlgorithm;
import net.torrent.protocol.peerwire.manager.TorrentManager;
import net.torrent.torrent.TorrentPart;
import net.torrent.torrent.TorrentPiece;
import net.torrent.torrent.context.TorrentPeer;
import net.torrent.torrent.piece.PieceSelector;
import net.torrent.torrent.piece.RandomPieceSelector;

/**
 * This standard implementation of {@link TorrentPieceDownloadAlgorithm} chooses
 * a random missing piece and tries to download all the parts from the same
 * peer, following the standard behavior of most of torrent clients.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentStdPieceDownloadAlgorithm implements
		TorrentPieceDownloadAlgorithm {
	/**
	 * The torrent manager
	 */
	private final TorrentManager manager;
	// private final TorrentContext context;
	// private final Torrent torrent;

	/**
	 * This selector is used to find the next piece to be downloaded. Parts are
	 * managed inside this algorithm.
	 */
	private final PieceSelector selector;
	/**
	 * Maps all unchecked completed pieces. The piece is removed from the list
	 * once
	 * {@link TorrentPieceDownloadAlgorithm#isComplete(TorrentPeer, TorrentPiece)}
	 * is called.
	 */
	private Set<TorrentPiece> completedPieces = new HashSet<TorrentPiece>();

	/**
	 * Creates a new instance of this algorithm.
	 * 
	 * @param manager
	 *            the torrent manager instance. With this object is possible to
	 *            retrieve current downloads/uploads and connections.
	 */
	public TorrentStdPieceDownloadAlgorithm(TorrentManager manager) {
		this.manager = manager;
		// this.context = this.manager.getContext();
		// this.torrent = this.manager.getTorrent();
		selector = new RandomPieceSelector(manager);
	}

	@Override
	public TorrentPart getNextPart(TorrentPeer peer, TorrentPart part) {
		if (part != null) {
			if (part.isLast()) {
				completedPieces.add(part.getPiece());
			} else {
				return part.getNextPart();
			}
		}
		TorrentPiece piece = selector.select(peer);
		if (piece == null)
			// no piece, return null. The default handler will check, again, the
			// interest on this peer.
			return null;
		return piece.getFirstPart();
	}

	@Override
	public TorrentPart sugested(TorrentPeer peer, TorrentPiece piece) {
		return piece.getFirstPart();
	}

	@Override
	public TorrentPart allowedFast(TorrentPeer peer, TorrentPiece piece) {
		return piece.getFirstPart();
	}

	@Override
	public boolean isComplete(TorrentPeer peer, TorrentPiece piece) {
		if (manager.getContext().getBitfield().hasPiece(piece))
			return true;
		// minimum overhead possible, will return true if was on list
		return completedPieces.remove(piece);
	}

	@Override
	public CorruptedAction corrupted(TorrentPeer peer, TorrentPiece piece) {
		// TODO ban peer sending many corrupted pieces
		return CorruptedAction.CANCEL;
	}
}
