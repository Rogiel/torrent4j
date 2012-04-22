package com.torrent4j.strategy.standard;

import java.io.IOException;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPieceBlock;
import com.torrent4j.model.peer.TorrentPeer;
import com.torrent4j.storage.TorrentStorage;
import com.torrent4j.strategy.TorrentUploadStrategy;

public class StandardTorrentUploadStrategy implements TorrentUploadStrategy {
	@SuppressWarnings("unused")
	private final StandardTorrentStrategy strategy;

	public StandardTorrentUploadStrategy(StandardTorrentStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public void blockRequested(Torrent torrent, TorrentPieceBlock block,
			TorrentPeer peer) {
		final TorrentStorage storage = torrent.getController().getStorage();
		try {
			peer.sendBlock(block,
					storage.read(torrent, block.getTorrentRange()));
		} catch (IOException e) {
			peer.disconnect();
			return;
		}
	}

	@Override
	public void blockRequestCancelled(Torrent torrent, TorrentPieceBlock block,
			TorrentPeer peer) {
		// we don't queue uploads, so we don't need to worry about this
	}
}
