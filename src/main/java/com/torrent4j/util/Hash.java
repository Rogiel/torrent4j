package com.torrent4j.util;

import java.util.Arrays;

public class Hash {
	private final HashType type;
	private final byte[] hash;

	public Hash(HashType type, byte[] hash) {
		this.type = type;
		this.hash = hash;
	}

	public HashType getType() {
		return type;
	}

	public byte[] getHash() {
		return hash;
	}

	public String getString() {
		return type.toString(hash);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(hash);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Hash))
			return false;
		Hash other = (Hash) obj;
		if (!Arrays.equals(hash, other.hash))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return type.name() + ":" + getString();
	}
}
