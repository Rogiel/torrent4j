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
package net.torrent.torrent.context;

import java.security.InvalidParameterException;
import java.util.BitSet;
import java.util.Iterator;

import net.torrent.torrent.TorrentPiece;

/**
 * This is a part bitfield. The only way to check if there is a full piece is to
 * test all parts.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentBitfield implements Iterable<TorrentPiece> {
	/**
	 * The torrent context
	 */
	private final TorrentContext context;
	/**
	 * {@link BitSet} containing all completed pieces
	 */
	private final BitSet bits;

	/**
	 * Creates a new instance given the {@link BitSet}.
	 * 
	 * @param context
	 *            the torrent context
	 * @param bits
	 *            the {@link BitSet}.
	 */
	public TorrentBitfield(TorrentContext context, BitSet bits) {
		this.context = context;
		this.bits = bits;
	}

	/**
	 * Creates a new instance
	 * 
	 * @param context
	 *            the torrent context
	 */
	public TorrentBitfield(TorrentContext context) {
		this(context, new BitSet((context.getTorrent() != null ? context
				.getTorrent().getPieces().length : 0)));
	}

	/**
	 * Test if this bitfield contains the given piece. Redirect the call to
	 * {@link TorrentBitfield#hasPiece(int)}.
	 * 
	 * @param piece
	 *            the piece to test
	 * @return true if piece is set, false otherwise
	 */
	public boolean hasPiece(TorrentPiece piece) {
		if (piece == null)
			throw new InvalidParameterException("Piece is null");
		return hasPiece(piece.getIndex());
	}

	/**
	 * Test if this bitfield contains the given piece <tt>index</tt>
	 * 
	 * @param index
	 *            the piece index
	 * @return true if piece is set, false otherwise
	 */
	public boolean hasPiece(int index) {
		return bits.get(index);
	}

	/**
	 * Set the state of the given piece.
	 * 
	 * @param piece
	 *            the piece
	 * @param state
	 *            the state
	 */
	public void setPiece(TorrentPiece piece, boolean state) {
		if (piece == null)
			throw new InvalidParameterException("Piece is null");
		bits.set(piece.getIndex(), state);
	}

	/**
	 * Set the bits into this bitfield.
	 * 
	 * @param bitSet
	 *            the bits
	 */
	public void setBits(BitSet bitSet) {
		bits.xor(bitSet);
	}

	/**
	 * Get the torrent context
	 * 
	 * @return the torrent context
	 */
	public TorrentContext getContext() {
		return context;
	}

	/**
	 * Get the backing {@link BitSet}.
	 * 
	 * @return the bit set
	 */
	public BitSet getBits() {
		return bits;
	}

	/**
	 * Return an Bitfield representing remaining pieces.
	 * 
	 * @return the remain bitfield
	 */
	public TorrentBitfield getRemainingPieces() {
		BitSet set = (BitSet) bits.clone();
		set.flip(0, bits.size());
		return new TorrentBitfield(context, set);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bits == null) ? 0 : bits.hashCode());
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
		TorrentBitfield other = (TorrentBitfield) obj;
		if (bits == null) {
			if (other.bits != null)
				return false;
		} else if (!bits.equals(other.bits))
			return false;
		return true;
	}

	@Override
	public Iterator<TorrentPiece> iterator() {
		return iterator(true);
	}

	/**
	 * Iterate to all pieces with <tt>type</tt>. True if set, false if not.
	 * 
	 * @param type
	 *            the type
	 * @return {@link Iterator} for those pieces
	 */
	public Iterator<TorrentPiece> iterator(final boolean type) {
		return new BitfieldIterator(type);
	}

	/**
	 * Iterate to all pieces with <tt>type</tt>. True if set, false if not.
	 * 
	 * @param type
	 *            the type
	 * @return {@link Iterable} for those pieces
	 * @see TorrentBitfield#iterator(boolean)
	 */
	public Iterable<TorrentPiece> iterate(final boolean type) {
		return new Iterable<TorrentPiece>() {
			@Override
			public Iterator<TorrentPiece> iterator() {
				return TorrentBitfield.this.iterator(type);
			}
		};
	}

	/**
	 * The bitfield iterator
	 * 
	 * @author Rogiel Josias Sulzbach (<a
	 *         href="http://www.rogiel.com/">http://www.rogiel.com/</a>)
	 */
	public class BitfieldIterator implements Iterator<TorrentPiece> {
		/**
		 * The desired value
		 */
		private boolean value = true;

		/**
		 * The current index
		 */
		private int index = 0;

		/**
		 * Creates a new instance
		 * 
		 * @param value
		 *            the desired state
		 */
		public BitfieldIterator(boolean value) {
			this.value = value;
		}

		@Override
		public boolean hasNext() {
			return getIndex() >= 0;
		}

		@Override
		public TorrentPiece next() {
			int index = -2;
			try {
				index = getIndex();
				return context.getTorrent().getPiece(getIndex());
			} finally {
				this.index = index + 1;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Find the next index, without increasing it.
		 * 
		 * @return the next index, -1 if non existent.
		 */
		private int getIndex() {
			if (value)
				return index = bits.nextSetBit(index);
			else
				return bits.nextClearBit(index);
		}
	}
}
