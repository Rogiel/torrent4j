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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.torrent.protocol.peerwire.manager.TorrentManager;
import net.torrent.torrent.TorrentPiece;
import net.torrent.torrent.context.TorrentPeer;
import net.torrent.torrent.piece.score.PieceRarenessScoreAlgorithm;
import net.torrent.torrent.piece.score.ScoredPieceComparator;

/**
 * Select pieces in sequential order. Can be used for streaming purposes.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class ScoredPieceSelector extends SortedListPieceSelector {
	/**
	 * Sort again the list each <tt>n</tt> calls to {@link #select(TorrentPeer)}
	 */
	private static final int SORT_INTERVAL = 10;

	/**
	 * Call counter. Used to sort the list.
	 */
	private AtomicInteger calls = new AtomicInteger(0);

	/**
	 * The piece comparator
	 */
	private final ScoredPieceComparator comparator;

	/**
	 * Creates a new instance
	 * 
	 * @param manager
	 *            the torrent manager
	 */
	public ScoredPieceSelector(TorrentManager manager) {
		super(manager, Arrays.asList(manager.getTorrent().getPieces()));
		this.comparator = new ScoredPieceComparator(manager.getContext()
				.getSwarm(), new PieceRarenessScoreAlgorithm());
		// initial sort will be skipped because comparator is null.
		this.sort(pieces);
	}

	@Override
	protected void sort(List<TorrentPiece> pieces) {
		if (comparator != null)
			Collections.sort(pieces, comparator);
	}

	@Override
	public synchronized TorrentPiece select(TorrentPeer peer) {
		if (calls.get() % SORT_INTERVAL == 0)
			this.sort(pieces);
		calls.incrementAndGet();
		return super.select(peer);
	}
}
