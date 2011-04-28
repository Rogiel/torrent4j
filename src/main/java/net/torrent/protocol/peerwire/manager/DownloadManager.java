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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.torrent.torrent.TorrentPart;
import net.torrent.torrent.TorrentPiece;
import net.torrent.torrent.context.TorrentContext;
import net.torrent.torrent.context.TorrentPeer;

public class DownloadManager implements Iterable<TorrentPart> {
	private final TorrentContext context;

	private final Map<TorrentPart, TorrentPeer> activeParts = new HashMap<TorrentPart, TorrentPeer>();

	public DownloadManager(TorrentContext context) {
		this.context = context;
	}

	public boolean isDownloading(TorrentPart torrentPart) {
		return activeParts.containsKey(torrentPart);
	}

	public boolean isDownloading(TorrentPiece piece) {
		for (final TorrentPart part : activeParts.keySet()) {
			if (part.getPiece().equals(piece))
				return true;
		}
		return false;
	}

	public boolean isDownloading(TorrentPeer peer) {
		return activeParts.containsValue(peer);
	}

	public TorrentPeer getPeer(TorrentPart torrentPart) {
		return activeParts.get(torrentPart);
	}

	public Set<TorrentPart> getTorrentParts(TorrentPeer peer) {
		final Set<TorrentPart> parts = new HashSet<TorrentPart>();
		for (final Entry<TorrentPart, TorrentPeer> entry : activeParts
				.entrySet()) {
			if (entry.getValue().equals(peer))
				parts.add(entry.getKey());
		}
		return parts;
	}

	public boolean isInactive() {
		return activeParts.isEmpty();
	}

	public TorrentPeer add(TorrentPart torrentPart, TorrentPeer peer) {
		return activeParts.put(torrentPart, peer);
	}

	public TorrentPeer remove(TorrentPart torrentPart) {
		return activeParts.remove(torrentPart);
	}

	public Set<TorrentPart> remove(TorrentPeer peer) {
		final Set<TorrentPart> parts = new HashSet<TorrentPart>();
		for (TorrentPart part : getTorrentParts(peer)) {
			if (activeParts.remove(part) != null)
				parts.add(part);
		}
		return parts;
	}

	public int getActiveDownloadsCount() {
		return activeParts.size();
	}

	public Map<TorrentPart, TorrentPeer> getActiveDownloads() {
		return Collections.unmodifiableMap(activeParts);
	}

	@Override
	public Iterator<TorrentPart> iterator() {
		return activeParts.keySet().iterator();
	}

	public TorrentContext getContext() {
		return context;
	}
}
