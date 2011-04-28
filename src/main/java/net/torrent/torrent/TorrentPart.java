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

import net.torrent.protocol.peerwire.message.RequestMessage;
import net.torrent.util.Range;

/**
 * BitTorrent files are divided in small chunks of hashes, each hash represent
 * an <b>piece</b> and each of those pieces are divided into smaller parts. Each
 * part can be requested to an peer and received afterwards.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @see RequestMessage RequestMessage for more about requests and parts
 */
public class TorrentPart {
	/**
	 * The standard part length -- most clients respect this!
	 */
	public static final int PART_LENGTH = 16 * 1024;

	/**
	 * The piece index, only for internal management
	 */
	private int index; // internal use only
	/**
	 * The piece of this part
	 */
	private final TorrentPiece piece;
	/**
	 * The start offset of data
	 */
	private final int start;
	/**
	 * The length of data
	 */
	private final int length;

	/**
	 * Creates a new instance
	 * 
	 * @param piece
	 *            the piece
	 * @param start
	 *            the start offset
	 * @param length
	 *            the length
	 */
	public TorrentPart(TorrentPiece piece, int start, int length) {
		this.index = start / length;
		this.piece = piece;
		this.start = start;
		this.length = length;
	}

	/**
	 * Creates a new instance
	 * 
	 * @param index
	 *            the part index. <b>Note</b>: internal use only!
	 * @param piece
	 *            the piece
	 * @param start
	 *            the start offset
	 * @param length
	 *            the length
	 */
	public TorrentPart(int index, TorrentPiece piece, int start, int length) {
		this.index = index;
		this.piece = piece;
		this.start = start;
		this.length = length;
	}

	/**
	 * Get the piece
	 * 
	 * @return the piece
	 */
	public TorrentPiece getPiece() {
		return piece;
	}

	/**
	 * Get the torrent
	 * 
	 * @return the torrent
	 */
	public Torrent getTorrent() {
		return piece.getTorrent();
	}

	/**
	 * Get the data start offset
	 * 
	 * @return the start offset
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Get the data length
	 * 
	 * @return the data length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Get the offset of part inside the torrent.
	 * 
	 * @return the part's torrent offset
	 */
	public long getOffset() {
		return piece.getOffset() + start;
	}

	/**
	 * Check if this is the last part in this piece.
	 * <p>
	 * <code>
	 * TorrentPart part = ...;<br>
	 * boolean first = (part.getStart() + part.getLength() == part.getPiece().getLength());
	 * </code>
	 * 
	 * @return true if last
	 */
	public boolean isLast() {
		return (start + length == piece.getLength());
	}

	/**
	 * Check if this is the first part in this piece.
	 * <p>
	 * <code>
	 * TorrentPart part = ...;<br>
	 * boolean first = (part.getStart() == 0);
	 * </code>
	 * 
	 * @return true if first
	 */
	public boolean isFirst() {
		return start == 0;
	}

	/**
	 * Get the next part. Null if last.
	 * 
	 * @return the next part. Might be null.
	 */
	public TorrentPart getNextPart() {
		if (isLast())
			return null;
		return piece.getParts()[index + 1];
	}

	/**
	 * Get the range inside the piece.
	 * 
	 * @return the range in piece
	 */
	public Range asPieceRange() {
		return Range.getRangeByLength(start, length);
	}

	/**
	 * Get the range inside the torrent
	 * 
	 * @return the range in torrent
	 */
	public Range asTorrentRange() {
		return Range.getRangeByLength(piece.getOffset() + start, length);
	}

	/**
	 * Get the range inside the file
	 * 
	 * @param file
	 *            the file
	 * @return the range in file
	 */
	public Range asFileRange(TorrentFile file) {
		return Range.getRangeByLength(
				piece.getOffset() - file.getOffset() + start, length)
				.intersection(file.asFileRange());
	}

	@Override
	public String toString() {
		return "TorrentPart [piece=" + piece + ", start=" + start + ", length="
				+ length + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
		result = prime * result + ((piece == null) ? 0 : piece.hashCode());
		result = prime * result + start;
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
		TorrentPart other = (TorrentPart) obj;
		if (length != other.length)
			return false;
		if (piece == null) {
			if (other.piece != null)
				return false;
		} else if (!piece.equals(other.piece))
			return false;
		if (start != other.start)
			return false;
		return true;
	}
}
