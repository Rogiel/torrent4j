package com.torrent4j.model;

import java.nio.file.Path;
import java.util.List;

import com.torrent4j.storage.TorrentStorage;
import com.torrent4j.util.Range;

/**
 * An file that can be downloaded from a torrent peer.
 * <p>
 * Though protocol implementations does not know about files, files themselves
 * are composed from several pieces (multiple files can be represented by a
 * single piece and (normally) a file is represented by several pieces -- both
 * are valid, even simultaneously) that are downloaded and can later be
 * assembled into a file by an {@link TorrentStorage}.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class TorrentFile {
	/**
	 * The torrent containing this file
	 */
	private final Torrent torrent;
	/**
	 * The file hash, if present at the metadata file
	 */
	private TorrentFileHash hash;

	/**
	 * The file offset (relative to the torrent)
	 */
	private final long offset;
	/**
	 * The file length
	 */
	private final long length;

	/**
	 * The list of pieces that contains at least one byte of the file data
	 */
	private final List<TorrentPiece> pieces;

	/**
	 * The file path relative to the torrent
	 */
	private Path path;

	/**
	 * Creates a new instance
	 * 
	 * @param torrent
	 *            the torrent
	 * @param offset
	 *            the file offset (relative to the torrent)
	 * @param length
	 *            the file length
	 * @param pieces
	 *            the list of pieces that contains at least one byte of the file
	 *            data
	 * @param path
	 *            the file path relative to the torrent
	 * @param hash
	 *            the file hash, if present at the metadata file
	 */
	public TorrentFile(Torrent torrent, long offset, long length,
			List<TorrentPiece> pieces, Path path, byte[] hash) {
		this.torrent = torrent;
		this.offset = offset;
		this.length = length;
		this.pieces = pieces;
		this.path = path;
		if (hash != null)
			this.hash = new TorrentFileHash(this, hash);
	}

	/**
	 * @return the file hash, if present at the metadata file
	 */
	public TorrentFileHash getHash() {
		return hash;
	}

	/**
	 * @param hash
	 *            the file hash
	 */
	public void setHash(TorrentFileHash hash) {
		this.hash = hash;
	}

	/**
	 * @return the file offset (relative to the torrent)
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * @return the file length
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @return the range of this file inside the torrent
	 */
	public Range getTorrentRange() {
		return Range.getRangeByLength(offset, length);
	}

	/**
	 * @return the range of this file as a single file
	 */
	public Range getFileRange() {
		return Range.getRangeByLength(0, length);
	}

	/**
	 * @return the list of pieces that contains at least one byte of the file
	 *         data
	 */
	public List<TorrentPiece> getPieces() {
		return pieces;
	}

	/**
	 * @return the file path relative to the torrent
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the file path relative to the torrent
	 */
	public void setPath(Path path) {
		this.path = path;
	}

	/**
	 * @return the file name
	 */
	public String getFileName() {
		return path.getFileName().toString();
	}

	/**
	 * @return the parent torrent
	 */
	public Torrent getTorrent() {
		return torrent;
	}

	@Override
	public String toString() {
		return "TorrentFile [torrent=" + torrent + ", hash=" + hash
				+ ", length=" + length + ", path=" + path + "]";
	}
}
