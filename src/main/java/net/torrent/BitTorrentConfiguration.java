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
package net.torrent;

/**
 * Configurations for an {@link BitTorrentClient BitTorrent client} instance.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class BitTorrentConfiguration {
	/**
	 * Default peerwire listen port
	 */
	private int listenPort = 58462;
	/**
	 * Default peer id
	 */
	private byte[] peerID = null;

	/**
	 * Get the port
	 * 
	 * @return the port
	 */
	public int getListenPort() {
		return listenPort;
	}

	/**
	 * Set the listening port for the server.<br />
	 * 0 will disable server.
	 * 
	 * @param listenPort
	 *            the port
	 */
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	/**
	 * Get the peer id.
	 * 
	 * @return the peer id
	 */
	public byte[] getPeerID() {
		return peerID;
	}

	/**
	 * Set the peer id. If null a random peerid will be generated.
	 * 
	 * @param peerID
	 *            the peerid. Can be null.
	 */
	public void setPeerID(byte[] peerID) {
		this.peerID = peerID;
	}
}
