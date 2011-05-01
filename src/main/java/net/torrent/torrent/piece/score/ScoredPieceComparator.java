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
package net.torrent.torrent.piece.score;

import java.util.Comparator;

import net.torrent.torrent.TorrentPiece;
import net.torrent.torrent.context.TorrentSwarm;

/**
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class ScoredPieceComparator implements Comparator<TorrentPiece> {
	private final PieceScoreAlgorithm score;
	private final TorrentSwarm swarm;

	public ScoredPieceComparator(TorrentSwarm swarm, PieceScoreAlgorithm score) {
		this.swarm = swarm;
		this.score = score;
	}

	@Override
	public int compare(TorrentPiece piece1, TorrentPiece piece2) {
		return score.calculate(swarm, piece1) - score.calculate(swarm, piece2);
	}
}
