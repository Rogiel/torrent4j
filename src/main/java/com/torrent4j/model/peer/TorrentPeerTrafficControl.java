package com.torrent4j.model.peer;

import com.torrent4j.model.TorrentTrafficControl;

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
