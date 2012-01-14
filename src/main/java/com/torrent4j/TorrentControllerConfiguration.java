package com.torrent4j;

import com.torrent4j.util.PeerIDGenerator;

public class TorrentControllerConfiguration {
	private String peerID = PeerIDGenerator.generateRandomPeerID();

	public String getPeerID() {
		return peerID;
	}

	public void setPeerID(String peerID) {
		this.peerID = peerID;
	}
}
