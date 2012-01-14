package com.torrent4j.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public enum HashType {
	SHA1(new SimpleHashDelegate("SHA1")),

	SHA256(new SimpleHashDelegate("SHA-256")),

	SHA384(new SimpleHashDelegate("SHA-384")),

	SHA512(new SimpleHashDelegate("SHA-512")),

	MD5(new SimpleHashDelegate("MD5")),

	MD2(new SimpleHashDelegate("MD2"));

	private final HashDelegate delegate;

	HashType(HashDelegate delegate) {
		this.delegate = delegate;
	}

	public byte[] hash(byte[] data) {
		return delegate.hash(data);
	}

	public byte[] hash(String string) {
		return hash(string.getBytes());
	}

	public byte[] hash(ByteBuffer buffer) {
		final byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		return hash(data);
	}

	public String hashAsString(byte[] data) {
		return toString(hash(data));
	}

	public String hashAsString(String string) {
		return hashAsString(string.getBytes());
	}

	public String hashAsString(ByteBuffer buffer) {
		final byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		return hashAsString(data);
	}

	public String toString(byte[] hash) {
		return delegate.toString(hash);
	}

	public byte[] fromString(String hash) {
		return delegate.fromString(hash);
	}

	private interface HashDelegate {
		byte[] hash(byte[] data);

		String toString(byte[] hash);

		byte[] fromString(String hash);
	}

	private static class SimpleHashDelegate implements HashDelegate {
		private final String name;

		protected SimpleHashDelegate(String name) {
			this.name = name;
		}

		@Override
		public byte[] hash(byte[] data) {
			try {
				final MessageDigest hasher = MessageDigest.getInstance(name);
				hasher.update(data);
				return hasher.digest();
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
		}

		@Override
		public String toString(byte[] hash) {
			return new String(Hex.encodeHex(hash));
		}

		@Override
		public byte[] fromString(String hash) {
			try {
				return Hex.decodeHex(hash.toCharArray());
			} catch (DecoderException e) {
				return null;
			}
		}
	}
}
