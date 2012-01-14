package com.torrent4j.strategy;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPeer;
import com.torrent4j.model.TorrentPieceBlock;

/**
 * Determines the actions that should be taken regarding uploads
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface TorrentUploadStrategy {
	/**
	 * Notifies that an given peer is requesting an block upload
	 * 
	 * @param torrent
	 *            the torrent
	 * @param block
	 *            the block requested for upload
	 * @param peer
	 *            the peer requesting the block
	 */
	void blockRequested(Torrent torrent, TorrentPieceBlock block,
			TorrentPeer peer);

	/**
	 * Notifies that the peer has cancelled its previous request for the block
	 * 
	 * @param torrent
	 *            the torrent
	 * @param block
	 *            the previous requested block (now cancelled)
	 * @param peer
	 *            the peer canceling the block request
	 */
	void blockRequestCancelled(Torrent torrent, TorrentPieceBlock block,
			TorrentPeer peer);
}
