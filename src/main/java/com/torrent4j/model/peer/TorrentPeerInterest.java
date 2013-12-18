package com.torrent4j.model.peer;

/**
 * Determines whether there are interest on each other pieces
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public enum TorrentPeerInterest {
	/**
	 * Determines that the peer is interest in any piece from the other peer.
	 */
	INTERESTED,
	
	/**
	 * Determines that the peer is NOT interest in any piece from the other peer.
	 */
	NOT_INTERESTED;
}