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
package net.torrent.util.comparator;

import java.util.Comparator;

import net.torrent.torrent.TorrentPiece;

/**
 * Compare two pieces indexes.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PieceIndexComparator implements Comparator<TorrentPiece> {
	/**
	 * Shared instance
	 */
	public static final PieceIndexComparator SHARED_INSTANCE = new PieceIndexComparator();

	@Override
	public int compare(TorrentPiece piece1, TorrentPiece piece2) {
		if (piece1 == null)
			return Integer.MAX_VALUE;
		if (piece2 == null)
			return Integer.MAX_VALUE;
		return piece2.getIndex() - piece1.getIndex();
	}
}
