package com.torrent4j.util;

public class PeerIDGenerator {
	public static String generateRandomPeerID() {
		byte[] id = new byte[20];
		for (int i = 0; i < id.length; i++) {
			id[i] = (byte) (Math.random() * Byte.MAX_VALUE);
		}
		return new String(id);
	}
}
