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

import java.util.concurrent.atomic.AtomicInteger;

import net.torrent.torrent.TorrentPiece;
import net.torrent.torrent.context.TorrentPeer;
import net.torrent.torrent.context.TorrentSwarm;
import net.torrent.util.SwarmCallback;

/**
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PieceRarenessScoreAlgorithm implements PieceScoreAlgorithm {
	@Override
	public int calculate(TorrentSwarm swarm, final TorrentPiece piece) {
		final AtomicInteger score = new AtomicInteger();
		swarm.execute(new SwarmCallback() {
			@Override
			public void callback(TorrentPeer peer) {
				int peerScore = (int) (Math.random() * 10); // bit of randomness
				if (!peer.isAccessible()) {
					peerScore += 100;
				}
				if (!peer.getBitfield().hasPiece(piece))
					peerScore += 1000;
				score.addAndGet(peerScore);
			}
		});
		return score.get();
	}
}
