package com.torrent4j.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import com.torrent4j.util.BitField;

public abstract class AbstractTorrentPiecesContainer implements Iterable<TorrentPiece> {
	protected final Torrent torrent;
	protected final BitField bitSet;

	public AbstractTorrentPiecesContainer(Torrent torrent) {
		this.torrent = torrent;
		this.bitSet = new BitField(torrent.getPieces().size());
	}

	public boolean hasPiece(TorrentPiece piece) {
		return bitSet.get(piece.getIndex());
	}

	public void addPiece(TorrentPiece piece) {
		bitSet.set(piece.getIndex(), true);
	}

	public void removePiece(TorrentPiece piece) {
		bitSet.set(piece.getIndex(), false);
	}
	
	public boolean isSeeder() {
		return bitSet.cardinality() == torrent.getPieces().size();
	}
	
	public boolean isLeecher() {
		return !isSeeder();
	}

	public void clear() {
		bitSet.clear();
	}

	public List<TorrentPiece> getPieces() {
		return createList(bitSet);
	}

	public boolean hasMissingPieces(AbstractTorrentPiecesContainer other) {
		final BitSet difference = (BitSet) bitSet.clone();
		difference.andNot(other.bitSet);
		return difference.cardinality() != 0;
	}

	public List<TorrentPiece> getMissingPieces(AbstractTorrentPiecesContainer other) {
		final BitSet difference = (BitSet) bitSet.clone();
		difference.andNot(other.bitSet);
		return createList(difference);
	}

	public List<TorrentPiece> createList(BitSet bits) {
		final List<TorrentPiece> list = new ArrayList<>();
		for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
			list.add(getTorrent().getPiece(i));
		}
		return list;
	}

	@Override
	public Iterator<TorrentPiece> iterator() {
		return new Iterator<TorrentPiece>() {
			private int lastIndex = 0;
			private int index = 0;

			@Override
			public boolean hasNext() {
				return bitSet.nextSetBit(lastIndex) >= 0;
			}

			@Override
			public TorrentPiece next() {
				lastIndex = index;
				return getTorrent().getPiece(
						(index = bitSet.nextSetBit(lastIndex)));
			}

			@Override
			public void remove() {
				removePiece(getTorrent().getPiece(lastIndex));
			}
		};
	}

	public BitField getBitSet() {
		return bitSet;
	}

	public Torrent getTorrent() {
		return torrent;
	}
}
