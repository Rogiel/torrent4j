package com.torrent4j.strategy.standard;

import com.torrent4j.model.TorrentPiece;
import com.torrent4j.model.peer.TorrentPeer;

/**
 * Selects an suitable piece for download
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface PieceSelector {
	/**
	 * Applies an algorithm to determine the most suitable piece for download
	 * from the peer. If no pieces are available or none are worth to download
	 * from this peer, <code>null</code> should be returned.
	 * <p>
	 * Also note that further interest decisions can be performed on the
	 * {@link StandardTorrentDownloadStrategy#blockReceived(com.torrent4j.model.Torrent, com.torrent4j.model.TorrentPieceBlock, TorrentPeer)}
	 * that can set to no interest even if this algorithm has interest into
	 * downloading an piece.
	 * 
	 * @param peer
	 *            the peer to download the piece from
	 * @return the piece to start downloading, if any
	 */
	TorrentPiece selectPiece(TorrentPeer peer);
}
