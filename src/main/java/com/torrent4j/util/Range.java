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
package com.torrent4j.util;

import java.io.Serializable;

/**
 * This class represents an interval.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public final class Range implements Serializable {
	private static final long serialVersionUID = 1L;

	private final long start, length;

	/**
	 * Creates a range with the given parameters
	 * 
	 * @param start
	 *            the beginning of the range
	 * @param length
	 *            the length of the range
	 * @return a range
	 */
	public static Range getRangeByLength(long start, long length) {
		return new Range(start, length);
	}

	/**
	 * Creates the smallest possible range containing the given numbers.
	 * 
	 * @param number1
	 *            number to be within the range
	 * @param number2
	 *            number to be within the range
	 * @return
	 */
	public static Range getRangeByNumbers(long number1, long number2) {
		long s = Math.min(number1, number2);
		return new Range(s, Math.max(number1, number2) - s + 1);
	}

	/**
	 * Creates a new range
	 * 
	 * @param start
	 * @param length
	 */
	private Range(long start, long length) {
		if (start < 0)
			throw new IllegalArgumentException("start must be >= 0");
		if (length < 0)
			throw new IllegalArgumentException("length must be >= 0");

		this.start = start;
		this.length = length;
	}

	/**
	 * @param range
	 * @return true if the given range is contained within this range
	 */
	public boolean contains(Range range) {
		return getStart() <= range.getStart() && getEnd() >= range.getEnd();
	}

	/**
	 * @param pos
	 * @return true if the given point is contained within this range
	 */
	public boolean contains(long pos) {
		return getStart() <= pos && getEnd() >= pos;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Range) {
			Range r = (Range) obj;
			return getStart() == r.getStart() && getLength() == r.getLength();
		}
		return false;
	}

	/**
	 * @return the last index contained in this range
	 */
	public long getEnd() {
		return start + length - 1;
	}

	/**
	 * @return the length of this range
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @return the first index contained in this range
	 */
	public long getStart() {
		return start;
	}

	/**
	 * Test if this range defines a zero-sized range.
	 * 
	 * @return true if zero-sized.
	 */
	public boolean isEmpty() {
		return length == 0;
	}

	@Override
	public int hashCode() {
		return (int) ((getStart() * 13) & (getEnd() * 137));
	}

	/**
	 * Creates a range which contains only the indices contained in the
	 * intersection of this range and the given range.
	 * 
	 * @param range
	 *            the range to intersect with
	 * @return the intersected range or null if the ranges don't overlap
	 */
	public Range intersection(Range range) {
		if (!intersects(range)) {
			return null;
		}
		return getRangeByNumbers(Math.max(getStart(), range.getStart()),
				Math.min(getEnd(), range.getEnd()));
	}

	/**
	 * Returns the number of indices which are in this range and the given
	 * range.
	 * 
	 * @param r
	 * @return 0 if the ranges don't overlap, the length of the intersection
	 *         between them otherwise
	 */
	public long intersectionLength(Range r) {
		if (!intersects(r)) {
			return 0;
		}
		return Math.min(getEnd(), r.getEnd())
				- Math.max(getStart(), r.getStart()) + 1;
	}

	/**
	 * @param range
	 *            the range to intersect test with
	 * @return true if the ranges overlap
	 */
	public boolean intersects(Range range) {
		return getStart() <= range.getEnd() && getEnd() >= range.getStart();
	}

	@Override
	public String toString() {
		return "[" + getStart() + " - " + getEnd() + "]";
	}
}
