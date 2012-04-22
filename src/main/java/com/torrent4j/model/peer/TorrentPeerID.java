package com.torrent4j.model.peer;

import com.torrent4j.model.Torrent;

/**
 * This ID represents an PeerID on the BitTorrent network
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class TorrentPeerID {
	/**
	 * The torrent peer
	 */
	private final TorrentPeer peer;
	/**
	 * The underlying peer ID
	 */
	private final String peerID;

	/**
	 * Creates a new instance
	 * 
	 * @param peer
	 *            the peer
	 * @param peerID
	 *            the peer ID
	 */
	public TorrentPeerID(TorrentPeer peer, String peerID) {
		this.peer = peer;
		this.peerID = peerID;
	}

	/**
	 * @return the peerID
	 */
	public String getPeerID() {
		return peerID;
	}

	/**
	 * Tries to detect the peer's client. If unknown,
	 * {@link TorrentPeerClient#UNKNOWN} is returned.
	 * 
	 * @return the peer torrent client
	 */
	public TorrentPeerClient getClient() {
		for (TorrentPeerClient client : TorrentPeerClient.values()) {
			if (client == TorrentPeerClient.UNKNOWN)
				continue;
			if (peerID.startsWith(client.prefix))
				return client;
		}
		return TorrentPeerClient.UNKNOWN;
	}

	/**
	 * @return <code>true</code> if the client version is known
	 */
	public boolean isClientVersionKnown() {
		return getClient().versioned;
	}

	/**
	 * @return the client version as by the PeerID
	 */
	public String getClientVersion() {
		if (isClientVersionKnown()) {
			return this.peerID.substring(3, 7);
		} else {
			return null;
		}
	}

	/**
	 * @return the peer
	 */
	public TorrentPeer getPeer() {
		return peer;
	}

	/**
	 * @return the torrent
	 */
	public Torrent getTorrent() {
		return peer.getTorrent();
	}
}
