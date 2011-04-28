/**
 * 
 */
package net.torrent.torrent.piece.score;

import net.torrent.torrent.TorrentPiece;
import net.torrent.torrent.context.TorrentSwarm;

/**
 * Computes the score of an piece inside the swarm.
 * <p>
 * Pieces with higher scores will be more suitable to download than others.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public interface PieceScoreAlgorithm {
	/**
	 * Computes the score of an piece inside an swarm.
	 * 
	 * @return
	 */
	int calculate(TorrentSwarm swarm, TorrentPiece piece);
}
