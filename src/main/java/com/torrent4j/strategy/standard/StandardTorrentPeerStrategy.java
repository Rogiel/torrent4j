package com.torrent4j.strategy.standard;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPeer;
import com.torrent4j.model.TorrentPiece;
import com.torrent4j.strategy.TorrentPeerStrategy;

public class StandardTorrentPeerStrategy implements TorrentPeerStrategy {
	private final StandardTorrentStrategy strategy;

	public StandardTorrentPeerStrategy(StandardTorrentStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public void peerDiscovered(Torrent torrent, TorrentPeer peer) {
		peer.connect();
		peer.handshake();
	}

	@Override
	public void peerConnected(Torrent torrent, TorrentPeer peer) {
		peer.bitField();
	}

	@Override
	public void bitField(Torrent torrent, TorrentPeer peer) {
		if (peer.getPieces().isSeeder()
				&& torrent.getCompletedPieces().isSeeder()) {
			peer.disconnect();
			return;
		}
		if (peer.getPieces().hasMissingPieces(torrent.getCompletedPieces())) {
			peer.declareInterest();
			return;
		}
	}

	@Override
	public void havePiece(Torrent torrent, TorrentPeer peer, TorrentPiece piece) {
		if (torrent.getCompletedPieces().hasPiece(piece))
			return;
		if (peer.getState().hasDownloadRequestedBlock())
			return;
		if (peer.getState().isRemotellyChoked()) {
			peer.declareInterest();
		} else {
			peer.requestBlock(piece.getFirstBlock());
		}
	}

	@Override
	public void unchoked(Torrent torrent, TorrentPeer peer) {
		final TorrentPiece piece = strategy.getPieceSelector()
				.selectPiece(peer);
		if (piece == null) {
			peer.withdrawInterest();
			return;
		}
		peer.requestBlock(piece.getFirstBlock());
	}

	@Override
	public void choked(Torrent torrent, TorrentPeer peer) {
		if (peer.getPieces().isSeeder()
				&& torrent.getCompletedPieces().isSeeder()) {
			peer.disconnect();
			return;
		}
	}

	@Override
	public void interested(Torrent torrent, TorrentPeer peer) {
		peer.unchoke();
	}

	@Override
	public void notInterested(Torrent torrent, TorrentPeer peer) {
		peer.choke();
		if (peer.getPieces().isSeeder()
				&& torrent.getCompletedPieces().isSeeder()) {
			peer.disconnect();
			return;
		}
	}

	@Override
	public void peerIdle(Torrent torrent, TorrentPeer peer, long idleTime) {
		peer.keepAlive();
	}

	@Override
	public void peerDisconnected(Torrent torrent, TorrentPeer peer) {
	}

	@Override
	public void peerRemoved(Torrent torrent, TorrentPeer peer) {
		if (peer.isConnected())
			peer.disconnect();
	}
}
