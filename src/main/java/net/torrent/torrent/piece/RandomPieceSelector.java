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

import net.torrent.protocol.peerwire.manager.TorrentManager;
import net.torrent.torrent.TorrentPiece;

/**
 * Selects randomly an piece. Once this instance is created it takes all pieces
 * and put them into an list, then the list is sorted by an random algorithm.
 * Once the next piece is called the pieces are returned in-list order.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class RandomPieceSelector extends SortedListPieceSelector {
	/**
	 * Creates a new instance
	 * 
	 * @param manager
	 *            the torrent manager
	 */
	public RandomPieceSelector(TorrentManager manager) {
		super(manager, Arrays.asList(manager.getTorrent().getPieces()));
	}

	@Override
	protected void sort(List<TorrentPiece> pieces) {
		Collections.shuffle(pieces);
	}
}
