package com.torrent4j.strategy;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPeer;
import com.torrent4j.model.TorrentPiece;
import com.torrent4j.model.TorrentPieceBlock;

/**
 * Determines the actions that should be taken regarding downloads
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface TorrentDownloadStrategy {
	/**
	 * Notifies that an block has been received and stored in the storage.
	 * 
	 * @param torrent
	 *            the torrent
	 * @param block
	 *            the block that has been downloaded
	 * @param peer
	 *            the peer who uploaded the block
	 */
	void blockReceived(Torrent torrent, TorrentPieceBlock block,
			TorrentPeer peer);

	/**
	 * Notifies that an piece is complete and its checksum its corrects
	 * 
	 * @param torrent
	 *            the torrent
	 * @param piece
	 *            the completed piece
	 * @param peer
	 *            the peer who uploaded the last block to complete this piece
	 */
	void pieceComplete(Torrent torrent, TorrentPiece piece, TorrentPeer peer);

	/**
	 * Notifies that an piece has completed but its checksum data is incorrect.
	 * 
	 * @param torrent
	 *            the torrent
	 * @param piece
	 *            the piece which is complete but with wrong data
	 * @param peer
	 *            the peer who uploaded the last block of the corrupted piece
	 */
	void pieceChecksumFailed(Torrent torrent, TorrentPiece piece,
			TorrentPeer peer);
}
