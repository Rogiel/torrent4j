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

import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Represents an {@link TorrentPiece} hash.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentHash {
	/**
	 * An table containing hexadecimal characters
	 */
	private static final char[] HEX_TABLE = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	/**
	 * The hash
	 */
	private final byte[] hash;
	/**
	 * The hash type
	 */
	private final HashType type;

	/**
	 * The hashes type
	 * 
	 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
	 */
	public enum HashType {
		/**
		 * SHA1 -> 20 bytes
		 */
		SHA1(20, "SHA1"),

		/**
		 * MD5 -> 16 bytes
		 */
		MD5(16, "MD5");

		/**
		 * The hash length
		 */
		private final int length;
		/**
		 * The digest algorithm
		 */
		private final String digestAlgorithm;
		/**
		 * The {@link MessageDigest} instance
		 */
		private MessageDigest digest;

		/**
		 * Creates a new instance
		 * 
		 * @param length
		 *            the hash length
		 * @param digestAlgorithm
		 *            the algorithm
		 */
		HashType(int length, String digestAlgorithm) {
			this.length = length;
			this.digestAlgorithm = digestAlgorithm;
			try {
				this.digest = MessageDigest.getInstance(digestAlgorithm);
			} catch (NoSuchAlgorithmException e) {
				this.digest = null;
			}
		}

		/**
		 * Get the hash length
		 * 
		 * @return the hash length
		 */
		public int getLength() {
			return length;
		}

		/**
		 * Get the hash algorithm
		 * 
		 * @return the hash algorithm
		 */
		public String getDigestAlgorithm() {
			return digestAlgorithm;
		}

		/**
		 * Get the {@link MessageDigest}
		 * 
		 * @return the {@link MessageDigest}
		 */
		public MessageDigest getMessageDigest() {
			return digest;
		}
	}

	/**
	 * Creates a new instance
	 * 
	 * @param hash
	 *            the hash
	 * @param type
	 *            the hash type
	 */
	public TorrentHash(byte[] hash, HashType type) {
		if (hash == null)
			throw new IllegalArgumentException("hash is null");
		if (type == null)
			throw new IllegalArgumentException("type is null");
		if (hash.length != type.length)
			throw new IllegalArgumentException("hash does not have "
					+ type.length + " bytes");
		this.hash = Arrays.copyOf(hash, hash.length);
		this.type = type;
	}

	/**
	 * Get hash as an hexadecimal string
	 * 
	 * @return
	 */
	public String asHexString() {
		StringBuilder b = new StringBuilder(hash.length * 2);
		for (int i = 0; i < hash.length; i++) {
			b.append(HEX_TABLE[((hash[i] & 0xff) >> 4)]);
			b.append(HEX_TABLE[hash[i] & 15]);
		}
		return b.toString();
	}

	/**
	 * Compare the two hashes
	 * 
	 * @param hash
	 *            the hash
	 * @return true if are equal
	 */
	public boolean compare(byte[] hash) {
		if (hash == null)
			throw new InvalidParameterException("Hash is null");
		return Arrays.equals(this.hash, hash);
	}

	/**
	 * Get the hash byte array
	 * 
	 * @return the hash byte array
	 */
	public byte[] toByteArray() {
		return Arrays.copyOf(hash, hash.length);
	}

	/**
	 * Get the hash length
	 * 
	 * @return the hash length
	 */
	public int getHashLength() {
		return hash.length;
	}

	/**
	 * Get the hash type
	 * 
	 * @return the hash type
	 */
	public HashType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(hash);
		result = prime * result + type.hashCode();
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
		TorrentHash other = (TorrentHash) obj;
		if (!Arrays.equals(hash, other.hash))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return type + ":" + asHexString();
	}
}
