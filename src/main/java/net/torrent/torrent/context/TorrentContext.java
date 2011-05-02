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

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.torrent.protocol.tracker.message.PeerListMessage.PeerInfo;
import net.torrent.torrent.Torrent;
import net.torrent.torrent.context.TorrentPeerCapabilities.TorrentPeerCapability;

public class TorrentContext {
	/**
	 * The torrent metadata object
	 */
	private final Torrent torrent;
	/**
	 * The bitfield
	 */
	private final TorrentBitfield bitfield = new TorrentBitfield(this);

	/**
	 * The capabilities supported in this context
	 */
	private final TorrentPeerCapabilities capabilites = new TorrentPeerCapabilities(
			TorrentPeerCapability.DHT);

	private final TorrentSwarm swarm = new TorrentSwarm(this);
	/**
	 * Unknown peers does not have their IDs, consequently they cannot be
	 * queried using their Id and must be done through IP.
	 */
	private final Set<TorrentPeer> unknownPeers = new HashSet<TorrentPeer>();

	/**
	 * Creates a new context
	 * 
	 * @param torrent
	 *            the torrent metadata
	 */
	public TorrentContext(Torrent torrent) {
		this.torrent = torrent;
	}

	/**
	 * Get the torrent metadata object
	 * 
	 * @return metadata object
	 */
	public Torrent getTorrent() {
		return torrent;
	}

	/**
	 * Get the context bitfield
	 * 
	 * @return the bitfield
	 */
	public TorrentBitfield getBitfield() {
		return bitfield;
	}

	/**
	 * Get the capabilities of this context
	 * 
	 * @return the capabilities
	 */
	public TorrentPeerCapabilities getCapabilites() {
		return capabilites;
	}

	/**
	 * Tests if both peer and this context support an given capability.
	 * 
	 * @param peer
	 *            the peer
	 * @param capability
	 *            the capability
	 * @return true if both support this capability
	 */
	public boolean supports(TorrentPeer peer, TorrentPeerCapability capability) {
		return capabilites.supports(capability)
				&& peer.getCapabilities().supports(capability);
	}

	/**
	 * Get the list of known peers (have known peerid)
	 * 
	 * @return the list of peers
	 */
	public TorrentSwarm getSwarm() {
		return swarm;
	}

	/**
	 * Get the list of unknown peers (don't have known peerid)
	 * 
	 * @return the list of peers
	 */
	public Set<TorrentPeer> getUnknownPeers() {
		return Collections.unmodifiableSet(unknownPeers);
	}

	public TorrentPeer getPeer(TorrentPeerID peerId) {
		return swarm.getPeer(peerId);
	}

	public TorrentPeer getPeer(InetSocketAddress address) {
		return swarm.getPeer(address);
	}

	public TorrentPeer getPeer(TorrentPeerID id, InetSocketAddress address) {
		return swarm.getPeer(id, address);
	}
}
