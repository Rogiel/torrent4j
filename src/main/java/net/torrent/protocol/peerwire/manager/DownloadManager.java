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

/**
 * The download manager keep an track over current parts being downloaded at the
 * moment.
 * <p>
 * Please note that the manager actually does not make any decision nor block an
 * requested piece.
 * <p>
 * You can {@link Iterable iterate} over this manager to get <b>current</b>
 * downloading {@link TorrentPart parts}.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
// TODO track pieces which have been requested for some time but never got an
// REJECT (fast peer extensions) nor PIECE message (standard torrent
// implementation does nothing when the peer is not uploading the piece)
// TODO allow more then one peer per piece request
public class DownloadManager implements Iterable<TorrentPart> {
	/**
	 * The torrent context
	 */
	private final TorrentContext context;
	/**
	 * The requested pieces not yet attended.
	 */
	private final Map<TorrentPart, TorrentPeer> activeParts = new HashMap<TorrentPart, TorrentPeer>();

	/**
	 * Creates a new instance
	 * 
	 * @param context
	 *            the torrent context
	 */
	public DownloadManager(TorrentContext context) {
		this.context = context;
	}

	/**
	 * Check if the given <tt>part</tt> has been requested.
	 * 
	 * @param part
	 *            the torrent part
	 * @return true if request message have been sent
	 */
	public boolean isDownloading(TorrentPart part) {
		return activeParts.containsKey(part);
	}

	/**
	 * Test if the given <tt>piece</tt> has been requested.
	 * 
	 * @param piece
	 *            the piece
	 * @return true if request message have been sent
	 */
	public boolean isDownloading(TorrentPiece piece) {
		for (final TorrentPart part : activeParts.keySet()) {
			if (part.getPiece().equals(piece))
				return true;
		}
		return false;
	}

	/**
	 * Check if the given <tt>peer</tt> is uploading something.
	 * 
	 * @param peer
	 *            the peer
	 * @return true if downloading from this peer
	 */
	public boolean isDownloading(TorrentPeer peer) {
		return activeParts.containsValue(peer);
	}

	/**
	 * Get the peer uploading <tt>part</tt>
	 * 
	 * @param part
	 *            the part
	 * @return the peer uploading the part
	 */
	public TorrentPeer getPeer(TorrentPart part) {
		return activeParts.get(part);
	}

	/**
	 * Get all parts being downloaded from the given <tt>peer</tt>
	 * 
	 * @param peer
	 *            the peer
	 * @return a set of pieces
	 */
	public Set<TorrentPart> getTorrentParts(TorrentPeer peer) {
		final Set<TorrentPart> parts = new HashSet<TorrentPart>();
		for (final Entry<TorrentPart, TorrentPeer> entry : activeParts
				.entrySet()) {
			if (entry.getValue().equals(peer))
				parts.add(entry.getKey());
		}
		return parts;
	}

	/**
	 * Test if there are no active pieces requests.
	 * 
	 * @return true if no pending parts
	 */
	public boolean isInactive() {
		return activeParts.isEmpty();
	}

	/**
	 * Add a new part request
	 * 
	 * @param part
	 *            the part
	 * @param peer
	 *            the remote peer
	 * @return the peer (java collections thing)
	 */
	public TorrentPeer add(TorrentPart part, TorrentPeer peer) {
		return activeParts.put(part, peer);
	}

	/**
	 * Removes an part from this manager.
	 * 
	 * @param part
	 *            the part
	 * @return true if part was present before remove
	 */
	public TorrentPeer remove(TorrentPart part) {
		return activeParts.remove(part);
	}

	/**
	 * Remove all pieces request from the given <tt>peer</tt>.
	 * <p>
	 * Note that since this implementation is decoupled from any protocol
	 * implementation, it will not cancel the request! This piece might arrive
	 * some time later.
	 * 
	 * @param peer
	 *            the peer
	 * @return a set containing pieces removed
	 */
	public Set<TorrentPart> remove(TorrentPeer peer) {
		final Set<TorrentPart> parts = new HashSet<TorrentPart>();
		for (TorrentPart part : getTorrentParts(peer)) {
			if (activeParts.remove(part) != null)
				parts.add(part);
		}
		return parts;
	}

	/**
	 * Get the current active requests
	 * 
	 * @return the current active requests
	 */
	public int getActiveDownloadsCount() {
		return activeParts.size();
	}

	/**
	 * Get an map containing each piece mapped to the peer uploading it.
	 * 
	 * @return the map containing each piece mapped to the peer uploading it.
	 */
	public Map<TorrentPart, TorrentPeer> getActiveDownloads() {
		return Collections.unmodifiableMap(activeParts);
	}

	@Override
	public Iterator<TorrentPart> iterator() {
		return activeParts.keySet().iterator();
	}

	/**
	 * Get the torrent context
	 * 
	 * @return the torrent context
	 */
	public TorrentContext getContext() {
		return context;
	}
}
