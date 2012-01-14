package com.torrent4j.model;

import com.torrent4j.util.Range;

public class TorrentPieceBlock {
	public static final int BLOCK_LENGTH = 16 * 1024;

	private final TorrentPiece piece;
	private final int offset;
	private final int length;

	private boolean downloaded = false;

	public TorrentPieceBlock(TorrentPiece piece, int offset, int length) {
		this.piece = piece;
		this.offset = offset;
		this.length = length;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public Range getTorrentRange() {
		return Range.getRangeByLength(piece.getOffset() + offset, length);
	}

	/**
	 * Get the range inside the piece.
	 * 
	 * @return the range in piece
	 */
	public Range getPieceRange() {
		return Range.getRangeByLength(offset, length);
	}

	/**
	 * Get the range inside the file
	 * 
	 * @param file
	 *            the file
	 * @return the range in file
	 */
	public Range getFileRange(TorrentFile file) {
		if (piece.getOffset() - file.getOffset() + offset < 0)
			return null;
		return Range.getRangeByLength(
				piece.getOffset() - file.getOffset() + offset, length)
				.intersection(file.getFileRange());
	}

	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	public TorrentPieceBlock getNextBlock() {
		return piece.getNextBlock(this);
	}

	public TorrentPiece getPiece() {
		return piece;
	}

	public Torrent getTorrent() {
		return piece.getTorrent();
	}

	@Override
	public String toString() {
		return "TorrentPieceBlock [piece=" + piece + ", offset=" + offset
				+ ", length=" + length + "]";
	}
}
