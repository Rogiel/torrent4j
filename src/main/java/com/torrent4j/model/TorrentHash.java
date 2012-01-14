package com.torrent4j.model;

import com.torrent4j.util.Hash;
import com.torrent4j.util.HashType;

public class TorrentHash extends Hash {
	private final Torrent torrent;

	public TorrentHash(Torrent torrent, byte[] hash) {
		super(HashType.SHA1, hash);
		this.torrent = torrent;
	}	

	public Torrent getTorrent() {
		return torrent;
	}

}
