package com.torrent4j.strategy.standard;

import java.util.List;

import com.torrent4j.model.TorrentPiece;
import com.torrent4j.model.peer.TorrentPeer;

/**
 * Randomly selects an piece from the available peer pieces
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class RandomPieceSelector implements PieceSelector {
	@Override
	public TorrentPiece selectPiece(TorrentPeer peer) {
		final List<TorrentPiece> pieces = peer.getPieces().getMissingPieces(
				peer.getTorrent().getCompletedPieces());
		if (pieces.isEmpty())
			return null;
		return pieces.get((int) (Math.random() * pieces.size()));
	}
}
