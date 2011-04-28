/*
 * Copyright 2011 Rogiel Josias Sulzbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.torrent.protocol.peerwire.manager;

import net.torrent.protocol.datastore.TorrentDatastore;
import net.torrent.torrent.Torrent;
import net.torrent.torrent.context.TorrentContext;

public class TorrentManager {
	private final TorrentContext context;

	private final ConnectionManager connectionManager;
	private final PeerManager peerManager;
	private final DownloadManager downloadManager;
	private final UploadManager uploadManager;

	private final TorrentDatastore datastore;

	public TorrentManager(TorrentContext context, TorrentDatastore datastore) {
		this.context = context;
		this.datastore = datastore;
		connectionManager = new ConnectionManager(context);
		peerManager = new PeerManager(context, connectionManager);
		downloadManager = new DownloadManager(context);
		uploadManager = new UploadManager(context);
	}

	public TorrentContext getContext() {
		return context;
	}

	public TorrentDatastore getDatastore() {
		return datastore;
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public PeerManager getPeerManager() {
		return peerManager;
	}

	public DownloadManager getDownloadManager() {
		return downloadManager;
	}

	public UploadManager getUploadManager() {
		return uploadManager;
	}

	public Torrent getTorrent() {
		return context.getTorrent();
	}
}
