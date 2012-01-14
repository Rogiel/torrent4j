package com.torrent4j.strategy.standard;

import java.util.ArrayList;
import java.util.List;

import com.torrent4j.model.TorrentPeer;
import com.torrent4j.strategy.TorrentDownloadStrategy;
import com.torrent4j.strategy.TorrentPeerStrategy;
import com.torrent4j.strategy.TorrentStrategy;
import com.torrent4j.strategy.TorrentUploadStrategy;

public class StandardTorrentStrategy implements TorrentStrategy {
	private final TorrentDownloadStrategy downloadStrategy = new StandardTorrentDownloadStrategy(
			this);
	private final TorrentUploadStrategy uploadStrategy = new StandardTorrentUploadStrategy(
			this);
	private final TorrentPeerStrategy peerStrategy = new StandardTorrentPeerStrategy(
			this);
	private final PieceSelector pieceSelector;

	private final List<TorrentPeer> bannedPeers = new ArrayList<>();

	public StandardTorrentStrategy(PieceSelector pieceSelector) {
		this.pieceSelector = pieceSelector;
	}

	public StandardTorrentStrategy() {
		this.pieceSelector = new RandomPieceSelector();
	}

	@Override
	public TorrentDownloadStrategy getDownloadStrategy() {
		return downloadStrategy;
	}

	@Override
	public TorrentUploadStrategy getUploadStrategy() {
		return uploadStrategy;
	}

	@Override
	public TorrentPeerStrategy getPeerStrategy() {
		return peerStrategy;
	}

	public PieceSelector getPieceSelector() {
		return pieceSelector;
	}

	public void banPeer(TorrentPeer peer) {
		bannedPeers.add(peer);
	}

	public void unbanPeer(TorrentPeer peer) {
		bannedPeers.remove(peer);
	}
	
	public boolean isBanned(TorrentPeer peer) {
		return bannedPeers.contains(peer);
	}
}
