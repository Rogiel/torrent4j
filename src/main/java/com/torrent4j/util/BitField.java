package com.torrent4j.util;

import java.util.BitSet;

public class BitField extends BitSet {
	private static final long serialVersionUID = 1L;

	private final int length;

	public BitField(int length) {
		super(length);
		this.length = length;
	}

	private void ensureBitFieldLength(int index) {
		if (index > length)
			throw new ArrayIndexOutOfBoundsException(index + " is bigger than "
					+ length);
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public void flip(int bitIndex) {
		ensureBitFieldLength(bitIndex);
		super.flip(bitIndex);
	}

	@Override
	public void flip(int fromIndex, int toIndex) {
		ensureBitFieldLength(fromIndex);
		ensureBitFieldLength(toIndex);
		super.flip(fromIndex, toIndex);
	}

	@Override
	public void set(int bitIndex) {
		ensureBitFieldLength(bitIndex);
		super.set(bitIndex);
	}

	@Override
	public void set(int bitIndex, boolean value) {
		ensureBitFieldLength(bitIndex);
		super.set(bitIndex, value);
	}

	@Override
	public void set(int fromIndex, int toIndex) {
		ensureBitFieldLength(fromIndex);
		ensureBitFieldLength(toIndex);
		super.set(fromIndex, toIndex);
	}

	@Override
	public void set(int fromIndex, int toIndex, boolean value) {
		ensureBitFieldLength(fromIndex);
		ensureBitFieldLength(toIndex);
		super.set(fromIndex, toIndex, value);
	}

	@Override
	public void clear(int bitIndex) {
		ensureBitFieldLength(bitIndex);
		super.clear(bitIndex);
	}

	@Override
	public void clear(int fromIndex, int toIndex) {
		ensureBitFieldLength(fromIndex);
		ensureBitFieldLength(toIndex);
		super.clear(fromIndex, toIndex);
	}

	@Override
	public boolean get(int bitIndex) {
		ensureBitFieldLength(bitIndex);
		return super.get(bitIndex);
	}

	@Override
	public BitSet get(int fromIndex, int toIndex) {
		ensureBitFieldLength(fromIndex);
		ensureBitFieldLength(toIndex);
		return super.get(fromIndex, toIndex);
	}

	@Override
	public int nextSetBit(int fromIndex) {
		ensureBitFieldLength(fromIndex);
		return super.nextSetBit(fromIndex);
	}

	@Override
	public int nextClearBit(int fromIndex) {
		ensureBitFieldLength(fromIndex);
		return super.nextClearBit(fromIndex);
	}
}
