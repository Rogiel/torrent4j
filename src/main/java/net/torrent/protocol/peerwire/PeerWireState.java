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
package net.torrent.protocol.peerwire;

import org.jboss.netty.channel.Channel;

/**
 * Maintain an per {@link Channel} information about the connection state.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PeerWireState {
	/**
	 * The peer handshake state
	 */
	private boolean peerHandshaked;

	/**
	 * The handshake state
	 */
	private boolean handshaked;

	/**
	 * Returns true if this connection has already been handshaked by the peer
	 * end.
	 * 
	 * @return true if connection was handshaked
	 */
	public boolean hasPeerHandshaked() {
		return peerHandshaked;
	}

	/**
	 * Set the handshake state of this connection by the peer end.
	 * 
	 * @param peerHandshaked
	 *            the handshake state
	 */
	public void setPeerHandshaked(boolean peerHandshaked) {
		this.peerHandshaked = peerHandshaked;
	}

	/**
	 * Returns true if this connection has already been handshaked by the
	 * library end.
	 * 
	 * @return true if connection was handshaked
	 */
	public boolean hasHandshaked() {
		return handshaked;
	}

	/**
	 * Set the handshake state of this connection by the library end.
	 * 
	 * @param handshaked
	 *            the handshake state
	 */
	public void setHandshaked(boolean handshaked) {
		this.handshaked = handshaked;
	}
}
