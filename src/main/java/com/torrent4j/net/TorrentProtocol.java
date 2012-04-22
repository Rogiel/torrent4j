package com.torrent4j.net;

import com.torrent4j.TorrentController;
import com.torrent4j.model.peer.TorrentPeer;

public interface TorrentProtocol {
	void start(TorrentController controller, int listenPort);

	void stop();

	boolean connect(TorrentPeer peer);
}
