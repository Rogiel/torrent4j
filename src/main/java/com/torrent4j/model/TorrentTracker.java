package com.torrent4j.model;

public class TorrentTracker {
	private final Torrent torrent;

	public TorrentTracker(Torrent torrent) {
		this.torrent = torrent;
	}

	public Torrent getTorrent() {
		return torrent;
	}
}
