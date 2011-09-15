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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.torrent.torrent.TorrentInfo.TorrentType;
import net.torrent.util.bencoding.BEncodedInputStream;
import net.torrent.util.bencoding.BList;
import net.torrent.util.bencoding.BMap;
import net.torrent.util.bencoding.BTypeException;

/**
 * An class representing an .torrent file.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class Torrent {
	/**
	 * Dictionary key for <tt>announce</tt>
	 */
	private static final String ANNOUNCE = "announce";
	/**
	 * Dictionary key for <tt>announce-list</tt>
	 */
	private static final String ANNOUNCE_LIST = "announce-list";
	/**
	 * Dictionary key for <tt>info</tt>
	 */
	private static final String INFO = "info";
	/**
	 * Dictionary key for <tt>comment</tt>
	 */
	private static final String COMMENT = "comment";
	/**
	 * Dictionary key for <tt>creation date</tt>
	 */
	private static final String CREATION_DATE = "creation date";
	/**
	 * Dictionary key for <tt>created by</tt>
	 */
	private static final String CREATED_BY = "created by";

	/**
	 * The torrent information
	 */
	private final TorrentInfo info;
	/**
	 * The list of trackers
	 */
	private final Set<TorrentTracker> trackers = new HashSet<TorrentTracker>();

	/**
	 * The torrent comment
	 */
	private final String comment;
	/**
	 * The creation date of the torrent
	 */
	private final Date creationDate;
	/**
	 * The torrent creator
	 */
	private final String createdBy;

	/**
	 * Creates a new instance
	 * 
	 * @param info
	 *            the torrent information
	 * @param comment
	 *            the comment
	 * @param creationDate
	 *            the date of creation
	 * @param createdBy
	 *            the creator
	 * @param trackers
	 *            the list of trackers
	 */
	public Torrent(TorrentInfo info, String comment, Date creationDate,
			String createdBy, TorrentTracker... trackers) {
		if (info == null)
			throw new InvalidParameterException("Torrent info is null");
		if (trackers == null || trackers.length == 0)
			throw new InvalidParameterException(
					"Trackers null or empty: DHT is not yet implemented, a tracker is needed.");

		Collections.addAll(this.trackers, trackers);

		this.info = info;
		this.comment = comment;
		this.creationDate = creationDate;
		this.createdBy = createdBy;
	}

	/**
	 * Creates a new instance
	 * 
	 * @param info
	 *            the torrent information
	 * @param trackers
	 *            the list o trackers
	 */
	public Torrent(TorrentInfo info, TorrentTracker... trackers) {
		this(info, null, null, null, trackers);
	}

	/**
	 * Creates a new instance
	 * 
	 * @param torrent
	 *            the torrent bencoded map
	 * @throws BTypeException
	 * @throws URISyntaxException
	 */
	protected Torrent(BMap torrent) throws BTypeException, URISyntaxException {
		this.comment = torrent.getString(COMMENT);

		final Long time = torrent.getLong(CREATION_DATE);
		this.creationDate = (time != null ? new Date(time * 1000) : null);

		this.createdBy = torrent.getString(CREATED_BY);

		BList aLists = torrent.getList(ANNOUNCE_LIST);
		if (aLists != null) {
			for (int i = 0; i < aLists.size(); i++) {
				BList list = aLists.getList(i);
				URI primary = null;
				URI[] backup = new URI[(list.size() > 1 ? list.size() - 1 : 0)];
				int backups = 0;
				for (int j = 0; j < list.size(); j++) {
					if (j == 0) {
						final String url = list.getString(j);
						if (!url.startsWith("http"))
							break;
						primary = new URI(url);
					} else {
						final String url = list.getString(j);
						if (url.startsWith("http"))
							backup[backups++] = new URI(url);
					}
				}
				this.trackers.add(new TorrentTracker(this, primary, Arrays
						.copyOf(backup, backups)));
			}
		} else {
			if (torrent.containsKey(ANNOUNCE)) {
				this.trackers.add(new TorrentTracker(this, new URI(torrent
						.getString(ANNOUNCE))));
			}
		}

		info = new TorrentInfo(this, torrent.getMap(INFO));
	}

	/**
	 * Get torrent information
	 * 
	 * @return the torrent information
	 */
	public TorrentInfo getInfo() {
		return info;
	}

	/**
	 * The list of trackers
	 * 
	 * @return the trackers
	 */
	public Set<TorrentTracker> getTrackers() {
		return Collections.unmodifiableSet(trackers);
	}

	/**
	 * Get the torrent comment
	 * 
	 * @return the torrent comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Get the creation adte
	 * 
	 * @return the creation date
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Get the torrent creator
	 * 
	 * @return the torrent creator
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * Get the torrent info hash
	 * 
	 * @return the torrent info hash
	 */
	public TorrentHash getInfoHash() {
		return info.getInfoHash();
	}

	/**
	 * Get list of files
	 * 
	 * @return the file list
	 */
	public List<TorrentFile> getFiles() {
		return info.getFiles();
	}

	/**
	 * Get the list of pieces
	 * 
	 * @return the list of pieces
	 */
	public TorrentPiece[] getPieces() {
		return info.getPieces();
	}

	/**
	 * Get the torrent type
	 * 
	 * @return the torrent type
	 */
	public TorrentType getType() {
		return info.getType();
	}

	/**
	 * Get the piece by index
	 * 
	 * @param index
	 *            the piece index
	 * @return the found piece
	 */
	public TorrentPiece getPiece(int index) {
		return info.getPiece(index);
	}

	/**
	 * Get an certain part
	 * 
	 * @param piece
	 *            the piece
	 * @param start
	 *            the part start
	 * @param len
	 *            the part length
	 * @return the found part
	 */
	public TorrentPart getPart(TorrentPiece piece, int start, int len) {
		return info.getPart(piece, start, len);
	}

	/**
	 * Get an certain part
	 * 
	 * @param piece
	 *            the piece index
	 * @param start
	 *            the part start
	 * @param len
	 *            the part length
	 * @return the found part
	 */
	public TorrentPart getPart(int index, int start, int len) {
		return info.getPart(index, start, len);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((info == null) ? 0 : info.getInfoHash().hashCode());
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
		Torrent other = (Torrent) obj;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		return true;
	}

	/**
	 * Load an torrent from an {@link InputStream}
	 * 
	 * @param in
	 *            the {@link InputStream}
	 * @return the loaded {@link Torrent} instance
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static Torrent load(InputStream in) throws IOException,
			URISyntaxException {
		if (in == null)
			throw new InvalidParameterException("InputStream cannot be null");

		try {
			final BMap map = (BMap) new BEncodedInputStream(in).readElement();
			return new Torrent(map);
		} finally {
			in.close();
		}
	}

	/**
	 * Load an torrent from an {@link File}
	 * 
	 * @param file
	 *            the {@link File}
	 * @return the loaded {@link Torrent} instance
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static Torrent load(File file) throws IOException,
			URISyntaxException {
		if (file == null)
			throw new InvalidParameterException("File cannot be null");
		return load(new FileInputStream(file));
	}

	/**
	 * Load an torrent from an {@link File}
	 * 
	 * @param url
	 *            the {@link URL}
	 * @return the loaded {@link Torrent} instance
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static Torrent load(URL url) throws IOException, URISyntaxException {
		if (url == null)
			throw new InvalidParameterException("File cannot be null");
		return load(url.openStream());
	}
}
