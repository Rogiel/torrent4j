package com.torrent4j.model;

import com.torrent4j.util.Hash;
import com.torrent4j.util.HashType;

public class TorrentPieceHash extends Hash {
	private final TorrentPiece piece;

	public TorrentPieceHash(TorrentPiece piece, byte[] hash) {
		super(HashType.SHA1, hash);
		this.piece = piece;
	}

	public TorrentPiece getPiece() {
		return piece;
	}

	public Torrent getTorrent() {
		return piece.getTorrent();
	}
}
