package com.torrent4j.model;

import com.torrent4j.util.Hash;
import com.torrent4j.util.HashType;

public class TorrentFileHash extends Hash {
	private final TorrentFile file;

	public TorrentFileHash(TorrentFile file, byte[] hash) {
		super(HashType.MD5, hash);
		this.file = file;
	}

	public TorrentFile getFile() {
		return file;
	}

	public Torrent getTorrent() {
		return file.getTorrent();
	}
}
