package com.torrent4j.model;

import java.util.ArrayList;
import java.util.List;

import com.torrent4j.util.Range;

public class TorrentPiece {
	private final Torrent torrent;
	private final TorrentPieceHash hash;

	private final int index;
	private final int offset;
	private final int length;

	private final List<TorrentPieceBlock> blocks = new ArrayList<>();
	private final List<TorrentFile> files = new ArrayList<>();

	public TorrentPiece(Torrent torrent, byte[] hash, int index, int offset,
			int length) {
		this.torrent = torrent;
		this.hash = new TorrentPieceHash(this, hash);
		this.index = index;
		this.offset = offset;
		this.length = length;

		final int blocks = (int) Math.ceil((double) length
				/ TorrentPieceBlock.BLOCK_LENGTH);
		for (int i = 0; i < blocks; i++) {
			final int blockOffset = TorrentPieceBlock.BLOCK_LENGTH * i;
			int len = TorrentPieceBlock.BLOCK_LENGTH;
			if (i == blocks - 1 && length % TorrentPieceBlock.BLOCK_LENGTH != 0)
				len = length - blockOffset;
			this.blocks.add(new TorrentPieceBlock(this, blockOffset, len));
		}
	}

	public TorrentPieceHash getHash() {
		return hash;
	}

	public int getIndex() {
		return index;
	}

	public long getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public Range getTorrentRange() {
		return Range.getRangeByLength(offset, length);
	}

	public boolean isFirst() {
		return index == 0;
	}

	public boolean isLast() {
		return torrent.getPieces().size() == index + 1;
	}

	public List<TorrentPieceBlock> getBlocks() {
		return blocks;
	}

	public TorrentPieceBlock getBlock(int offset, int length) {
		for (final TorrentPieceBlock block : blocks) {
			if (block.getOffset() == offset && block.getLength() == length)
				return block;
		}
		return null;
	}

	public TorrentPieceBlock getFirstBlock() {
		return blocks.get(0);
	}

	public TorrentPieceBlock getLastBlock() {
		return blocks.get(blocks.size() - 1);
	}

	public TorrentPieceBlock getNextBlock(TorrentPieceBlock block) {
		int next = blocks.indexOf(block) + 1;
		if (blocks.size() == next)
			return null;
		return blocks.get(next);
	}

	public boolean isDownloaded() {
		for (final TorrentPieceBlock block : blocks) {
			if (!block.isDownloaded())
				return false;
		}
		return true;
	}
	
	public void setDownloaded(boolean downloaded) {
		for (final TorrentPieceBlock block : blocks) {
			block.setDownloaded(downloaded);
		}
	}

	public List<TorrentFile> getFiles() {
		return files;
	}

	/* package protected! */void addFile(TorrentFile file) {
		files.add(file);
	}

	public TorrentPiece getNextPiece() {
		return torrent.getPiece(index + 1);
	}

	public Torrent getTorrent() {
		return torrent;
	}

	@Override
	public String toString() {
		return "TorrentPiece [torrent=" + torrent + ", hash=" + hash
				+ ", index=" + index + ", length=" + length + "]";
	}
}
