/**
 * 
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
