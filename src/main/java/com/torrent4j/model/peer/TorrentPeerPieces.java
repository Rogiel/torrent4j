package com.torrent4j.model.peer;

import java.util.BitSet;

import com.torrent4j.model.AbstractTorrentPiecesContainer;


public class TorrentPeerPieces extends AbstractTorrentPiecesContainer {
	private final TorrentPeer peer;
	
	public void load(BitSet bitSet) {
		this.bitSet.set(0, this.bitSet.size(), false);
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
			this.bitSet.set(i, true);
		}
	}

	public TorrentPeerPieces(TorrentPeer peer) {
		super(peer.getTorrent());
		this.peer = peer;
	}

	public TorrentPeer getPeer() {
		return peer;
	}
}
