package com.torrent4j.strategy.standard;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPeer;
import com.torrent4j.model.TorrentPiece;
import com.torrent4j.model.TorrentPieceBlock;
import com.torrent4j.model.TorrentSwarm.SwarmBroadcastHandler;
import com.torrent4j.strategy.TorrentDownloadStrategy;

public class StandardTorrentDownloadStrategy implements TorrentDownloadStrategy {
	private final StandardTorrentStrategy strategy;

	public StandardTorrentDownloadStrategy(StandardTorrentStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public void blockReceived(Torrent torrent, TorrentPieceBlock block,
			TorrentPeer peer) {
		TorrentPieceBlock next = block.getNextBlock();
		if (next == null) {
			final TorrentPiece piece = strategy.getPieceSelector().selectPiece(
					peer);
			if (piece == null) {
				peer.withdrawInterest();
				return;
			}
			next = piece.getFirstBlock();
		}
		peer.requestBlock(next);
	}

	@Override
	public void pieceComplete(Torrent torrent,
			final TorrentPiece completePiece, TorrentPeer peer) {
		torrent.getSwarm().broadcast(new SwarmBroadcastHandler() {
			@Override
			public void broadcast(TorrentPeer peer) {
				peer.have(completePiece);
			}

			@Override
			public boolean exception(Exception e) {
				return false;
			}
		});

		final TorrentPiece piece = strategy.getPieceSelector()
				.selectPiece(peer);
		if (piece == null) {
			peer.withdrawInterest();
			return;
		}
		peer.requestBlock(piece.getFirstBlock());
	}

	@Override
	public void pieceChecksumFailed(Torrent torrent, TorrentPiece piece,
			TorrentPeer peer) {
		System.out.println("Checksum has failed!");
		strategy.banPeer(peer);
	}
}
