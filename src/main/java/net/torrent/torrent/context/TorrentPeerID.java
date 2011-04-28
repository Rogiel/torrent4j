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

import java.util.Arrays;

/**
 * Creates a new Peer id for an peer.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentPeerID {
	/**
	 * The peer id in bytes (20 bytes)
	 */
	private byte[] peerID;

	/**
	 * Creates a new PeerID.
	 * 
	 * @param peerID
	 *            the bytes of the peer id
	 */
	protected TorrentPeerID(byte[] peerID) {
		this.peerID = peerID;
	}

	@Override
	public String toString() {
		return new String(peerID);
	}

	/**
	 * Convert to a byte array
	 * 
	 * @return the id in bytes
	 */
	public byte[] toByteArray() {
		return Arrays.copyOf(peerID, peerID.length);
	}

	/**
	 * Creates a new ID
	 * 
	 * @param peerID
	 *            the id byte array
	 * @return the new instance of {@link TorrentPeerID peer id}
	 */
	public static final TorrentPeerID create(byte[] peerID) {
		return new TorrentPeerID(peerID);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(peerID);
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
		TorrentPeerID other = (TorrentPeerID) obj;
		if (!Arrays.equals(peerID, other.peerID))
			return false;
		return true;
	}
}