package com.torrent4j.strategy;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPeer;
import com.torrent4j.model.TorrentPiece;

/**
 * Determine actions that the client should take regarding peers and their
 * requests.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface TorrentPeerStrategy {
	/**
	 * Performs an action with a peer that has been recently discovered.
	 * Discovery can happen through tracker announcements, DHT, peer exchange or
	 * by manually adding them to the torrent object.
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the discovered peer
	 */
	void peerDiscovered(Torrent torrent, TorrentPeer peer);

	/**
	 * Performs an action once handshaked with a peer.
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the connected peer
	 */
	void peerConnected(Torrent torrent, TorrentPeer peer);

	/**
	 * Notifies that the bitfield for the given peer has been received.
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the peer which sent his bitfield
	 */
	void bitField(Torrent torrent, TorrentPeer peer);

	/**
	 * Notifies that the peer has gotten a new piece
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the peer which downloaded a new piece
	 * @param piece
	 *            the piece which the peer has received
	 */
	void havePiece(Torrent torrent, TorrentPeer peer, TorrentPiece piece);

	/**
	 * Notifies that the peer has unchoked the connection
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the peer
	 */
	void unchoked(Torrent torrent, TorrentPeer peer);

	/**
	 * Notifies that the peer has choked the connection
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the peer
	 */
	void choked(Torrent torrent, TorrentPeer peer);

	/**
	 * Notifies that the peer has interest in any of our pieces. Normally, when
	 * this message is received, the remote peer is asking for an unchoke.
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the peer that is interested in our pieces
	 */
	void interested(Torrent torrent, TorrentPeer peer);

	/**
	 * Notifies that the peer no more has interest in any of our pieces.
	 * Normally, it is safe to choke the peer after this.
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the peer that has no more intested in any of the pieces
	 */
	void notInterested(Torrent torrent, TorrentPeer peer);

	/**
	 * Notifies that the peer has been idle for some time. If required, an
	 * <code>keepalive</code> message should be sent. It is also possible to
	 * close the connection if the peer is no longer interesting.
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the idle peer
	 * @param idleTime
	 *            the peer's idle time in milliseconds
	 */
	void peerIdle(Torrent torrent, TorrentPeer peer, long idleTime);

	/**
	 * Notifies the the peer has been disconnected. Note that this method is
	 * called either if the client closed the connection or if we requested an
	 * disconnection.
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the disconnected peer
	 */
	void peerDisconnected(Torrent torrent, TorrentPeer peer);

	/**
	 * Notifies that the peer has been removed from the swarm list. Although it
	 * is possible to re-add the peer to the list, this is not recommended!
	 * 
	 * @param torrent
	 *            the torrent
	 * @param peer
	 *            the removed peer
	 */
	void peerRemoved(Torrent torrent, TorrentPeer peer);
}
