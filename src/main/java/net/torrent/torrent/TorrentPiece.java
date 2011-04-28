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

import net.torrent.torrent.TorrentHash.HashType;

/**
 * Torrent files are divided into small chunks of data called <tt>pieces</tt>.
 * Each piece has the same size, except for the last which can be equal or
 * smaller (enough to fit all data).
 * <p>
 * This class handles this distribution and takes care of creating
 * {@link TorrentPart parts}.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentPiece {
	/**
	 * The Torrent information
	 */
	private final TorrentInfo info;
	/**
	 * The piece hash
	 */
	private final TorrentHash hash;
	/**
	 * The piece index
	 */
	private final int index;
	/**
	 * The piece length
	 */
	private final int length;
	/**
	 * The torrent parts. All parts have the standard length (
	 * {@link TorrentPart#PART_LENGTH})
	 */
	private final TorrentPart[] parts;

	/**
	 * Create a new instance
	 * 
	 * @param info
	 *            the torrent information
	 * @param hash
	 *            the torrent hash (as {@link TorrentHash})
	 * @param index
	 *            the piece index
	 * @param length
	 *            the piece length
	 */
	public TorrentPiece(TorrentInfo info, final TorrentHash hash, int index,
			int length) {
		this.info = info;
		this.hash = hash;
		this.index = index;
		this.length = length;

		int len = TorrentPart.PART_LENGTH;
		if (len > length)
			len = length;
		int partCount = (length / len);
		if ((length % len) > 0)
			partCount += 1;

		parts = new TorrentPart[partCount];
		int remain = length;
		for (int i = 0; i < parts.length; i++) {
			int plen = remain;
			if (remain > len)
				plen = len;
			parts[i] = new TorrentPart(i, this, length - remain, plen);
			remain -= plen;
		}
	}

	/**
	 * Creates a new instance
	 * 
	 * @param info
	 *            the torrent info
	 * @param hash
	 *            the info hash (as byte array)
	 * @param index
	 *            the piece index
	 * @param length
	 *            the piece length
	 */
	public TorrentPiece(TorrentInfo info, final byte[] hash, int index,
			int length) {
		this(info, new TorrentHash(hash, HashType.SHA1), index, length);
	}

	/**
	 * Get the torrent information
	 * 
	 * @return the torrent information
	 */
	public TorrentInfo getInfo() {
		return info;
	}

	/**
	 * Get the torrent
	 * 
	 * @return the torrent
	 */
	public Torrent getTorrent() {
		return info.getTorrent();
	}

	/**
	 * Get the piece hash
	 * 
	 * @return the piece hash
	 */
	public TorrentHash getHash() {
		return hash;
	}

	/**
	 * Get the piece index
	 * 
	 * @return the piece index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Get the piece length
	 * 
	 * @return the piece length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Get the piece offset
	 * 
	 * @return the piece offset (relative to torrent)
	 */
	public long getOffset() {
		return (long) index * (long) info.getPieceLength();
	}

	/**
	 * Get the next sequential part
	 * 
	 * @return the next part
	 */
	public TorrentPiece getNextPiece() {
		return info.getPiece(index + 1);
	}

	/**
	 * Get all parts
	 * 
	 * @return all the parts
	 */
	public TorrentPart[] getParts() {
		return parts;
	}

	/**
	 * Get the first part
	 * 
	 * @return the first part
	 */
	public TorrentPart getFirstPart() {
		return parts[0];
	}

	/**
	 * Get the last part
	 * 
	 * @return the last part
	 */
	public TorrentPart getLastPart() {
		return parts[parts.length - 1];
	}

	/**
	 * The the part starting at <tt>start</tt> with <tt>length</tt>
	 * 
	 * @param start
	 * @param length
	 * @return the found or created part
	 */
	public TorrentPart getPart(int start, int length) {
		for (final TorrentPart part : parts) {
			if (part.getStart() == start && part.getLength() == length)
				return part;
		}
		return new TorrentPart(this, start, length);
	}

	/**
	 * Get this piece as an entire part
	 * 
	 * @return the part
	 */
	public TorrentPart asSinglePart() {
		return getPart(0, length);
	}

	@Override
	public String toString() {
		return "TorrentPiece [info=" + info + ", hash=" + hash + ", index="
				+ index + ", length=" + length + ", parts="
				+ (parts != null ? parts.length : null) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
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
		TorrentPiece other = (TorrentPiece) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}
}
