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
import net.torrent.torrent.context.TorrentContext;
import net.torrent.torrent.context.TorrentPeer;

/**
 * The upload manager keep an track over current parts being uploaded at the
 * moment. Notice that the part is remove right after the last byte is written,
 * this is not a guarantee that the peer has received data nor that it is still
 * alive.
 * <p>
 * Please note that the manager actually does not make any decision nor declines
 * an requested piece.
 * <p>
 * You can {@link Iterable iterate} over this manager to get <b>current</b>
 * uploading {@link TorrentPart parts}.
 * <p>
 * FIXME <b>BUG NOTICE</b>: Note that this manager has an huge issue. When two
 * or more peers request the very same part, only the latter will be maintained
 * under control by this manager. This should be fixed soon and changes to the
 * manager signature can occur. The affected methods are:
 * {@link UploadManager#getPeer(TorrentPart)},
 * {@link UploadManager#remove(TorrentPart)} and
 * {@link UploadManager#getActiveUploads()}.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
// TODO track pieces which have been requested but the upload is not interesting
// right now, but might be later.
// FIXME this manager has an issue: if two peers request the same piece, only
// the latter one will be managed! UploadManager#getPeer(TorrentPart part) needs
// a fix.
public class UploadManager implements Iterable<TorrentPart> {
	/**
	 * The torrent context
	 */
	private final TorrentContext context;
	/**
	 * An map of requested pieces. Only accepted pieces are registered.
	 */
	private final Map<TorrentPart, TorrentPeer> activeParts = new HashMap<TorrentPart, TorrentPeer>();

	/**
	 * Creates a new instance
	 * 
	 * @param context
	 *            the torrent context
	 */
	public UploadManager(TorrentContext context) {
		this.context = context;
	}

	/**
	 * Test if the given <tt>part</tt> is being uploaded.
	 * 
	 * @param part
	 *            the part
	 * @return true if piece has been requested <b>AND</b> accepted.
	 */
	public boolean isUploading(TorrentPart part) {
		return activeParts.containsKey(part);
	}

	/**
	 * Test if uploads are being made to the given <tt>peer</tt>
	 * 
	 * @param peer
	 *            the peer
	 * @return true if at least one piece has been requested an piece <b>AND</b>
	 *         the request was accepted
	 */
	public boolean isUploading(TorrentPeer peer) {
		return activeParts.containsValue(peer);
	}

	/**
	 * This method retrieve the peer which has requested the given
	 * <tt>piece</tt>.
	 * <p>
	 * Note that this method has an huge issue: when two or more peers request
	 * the very same part (whichever piece), only the latter will be maintained
	 * under control by this manager. This need to be fixed soon and this method
	 * deprecated. You should avoid relying on it!
	 * 
	 * @param part
	 * @return
	 */
	public TorrentPeer getPeer(TorrentPart part) {
		return activeParts.get(part);
	}

	/**
	 * Get the pieces being uploaded to the given <tt>peer</tt>
	 * 
	 * @param peer
	 *            the peer
	 * @return an {@link Set} of all parts being uploaded to this peer.
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
	 * Tests if this manager has no active uploads.
	 * 
	 * @return true if no uploads are being done
	 */
	public boolean isInactive() {
		return activeParts.isEmpty();
	}

	/**
	 * Adds a new part to this manager
	 * 
	 * @param part
	 *            the uploaded part
	 * @param peer
	 *            the downloading peer
	 * @return the downloding {@link TorrentPeer}
	 */
	public TorrentPeer add(TorrentPart part, TorrentPeer peer) {
		return activeParts.put(part, peer);
	}

	/**
	 * Removes the given <tt>part</tt> from this manager
	 * <p>
	 * Note that this method has an huge issue: when two or more peers request
	 * the very same part (whichever piece), only the latter will be maintained
	 * under control by this manager. This need to be fixed soon and this method
	 * deprecated. You should avoid relying on it!
	 * 
	 * @param part
	 *            the part
	 * @return the downloading peer for the <tt>part</tt>
	 */
	public TorrentPeer remove(TorrentPart part) {
		return activeParts.remove(part);
	}

	/**
	 * Removes all uploads for the given <tt>peer</tt>
	 * 
	 * @param peer
	 *            the peer
	 * @return the set of {@link TorrentPart parts} being uploaded to that peer
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
	 * Get the amount of active uploads
	 * 
	 * @return the count of active uploads
	 */
	public int getActiveUploadsCount() {
		return activeParts.size();
	}

	/**
	 * Get an {@link Map} of active uploads
	 * <p>
	 * Note that this method has an huge issue: when two or more peers request
	 * the very same part (whichever piece), only the latter will be maintained
	 * under control by this manager. This need to be fixed soon and this method
	 * deprecated. You should avoid relying on it!
	 * 
	 * @return an {@link Map} of active uploads
	 */
	public Map<TorrentPart, TorrentPeer> getActiveUploads() {
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
