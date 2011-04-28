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
package net.torrent.torrent;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An tracker has the responsibility to discover new peers. Each peer, once
 * begin downloading, send its own peer id and got registered into an peer table
 * in the tracker. Once the next peer starts, it receives an copy of that peer
 * table and connect to some or all peers in that table.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentTracker {
	/**
	 * The torrent
	 */
	private final Torrent torrent;
	/**
	 * The tracker URI. There are several types of trackers:
	 * <ul>
	 * <li>HTTP</li>
	 * <li>HTTPS</li>
	 * <li>UDP</li>
	 * </ul>
	 * Each type have its own protocol and methods to retrieve peers.
	 */
	private final URI uri;
	/**
	 * The tracker backups uris
	 */
	private final Set<URI> backup = new HashSet<URI>();

	/**
	 * Creates a new instance
	 * 
	 * @param torrent
	 *            the torrent
	 * @param uri
	 *            the tracker uri
	 * @param backup
	 *            the tracker's backup uris
	 */
	public TorrentTracker(Torrent torrent, URI uri, URI... backup) {
		this.torrent = torrent;
		this.uri = uri;
		if (backup != null && backup.length > 0)
			Collections.addAll(this.backup, backup);
	}

	/**
	 * Creates a new instance
	 * 
	 * @param torrent
	 *            the torrent
	 * @param uri
	 *            the tracker uri
	 */
	public TorrentTracker(Torrent torrent, URI uri) {
		this(torrent, uri, (URI[]) null);
	}

	/**
	 * Get the torrent
	 * 
	 * @return the torrent
	 */
	public Torrent getTorrent() {
		return torrent;
	}

	/**
	 * Get the tracker {@link URL}
	 * 
	 * @return the url
	 */
	public URL getURL() {
		try {
			return uri.toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * Get the tracker {@link URI}
	 * 
	 * @return the tracker uri
	 */
	public URI getURI() {
		return uri;
	}

	/**
	 * Get the backup {@link URI}s
	 * 
	 * @return the tracker's backup URIS
	 */
	public Set<URI> getBackup() {
		return Collections.unmodifiableSet(backup);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TorrentTracker other = (TorrentTracker) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}
