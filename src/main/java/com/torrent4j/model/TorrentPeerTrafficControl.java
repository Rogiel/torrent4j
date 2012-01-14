package com.torrent4j.model;

public class TorrentPeerTrafficControl extends TorrentTrafficControl {
	private final TorrentPeer peer;

	public TorrentPeerTrafficControl(TorrentPeer peer) {
		super(peer.getTorrent());
		this.peer = peer;
	}

	/**
	 * @return the peer
	 */
	public TorrentPeer getPeer() {
		return peer;
	}
}
