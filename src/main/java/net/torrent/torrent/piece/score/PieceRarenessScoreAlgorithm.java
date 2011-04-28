/**
 * 
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
