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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.torrent.protocol.tracker.message.PeerListMessage.PeerInfo;
import net.torrent.torrent.TorrentPiece;
import net.torrent.util.SwarmCallback;

/**
 * An torrent swarm is an set of all connected peers.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentSwarm implements Iterable<TorrentPeer> {
	/**
	 * The torrent context
	 */
	private final TorrentContext context;

	/**
	 * The list of active peers
	 */
	private List<TorrentPeer> peers = new ArrayList<TorrentPeer>();

	/**
	 * Creates a new instance
	 * 
	 * @param context
	 *            the torrent context
	 */
	public TorrentSwarm(final TorrentContext context) {
		this.context = context;
	}

	/**
	 * Add an given peer to the swarm
	 * 
	 * @param peer
	 *            the peer
	 * @return true if was not present in swarm
	 */
	public boolean add(TorrentPeer peer) {
		return this.peers.add(peer);
	}

	/**
	 * Add an given peers to the swarm
	 * 
	 * @param peers
	 *            the peers
	 * @return true if was not present in swarm
	 */
	public boolean add(List<TorrentPeer> peers) {
		return this.peers.addAll(peers);
	}

	/**
	 * Removes an given peer from the swarm
	 * 
	 * @param peer
	 *            the peer
	 * @return true if peer was in swarm
	 */
	public boolean remove(TorrentPeer peer) {
		return this.peers.remove(peer);
	}

	/**
	 * Executes an callback on each peer in the swarm
	 * 
	 * @param callback
	 *            the callback
	 */
	public void execute(SwarmCallback callback) {
		for (final TorrentPeer peer : this) {
			callback.callback(peer);
		}
	}

	/**
	 * Select the peers which have the <tt>piece</tt>
	 * 
	 * @param piece
	 *            the piece
	 * @return list of peers with piece
	 */
	public List<TorrentPeer> getPeersWithPiece(TorrentPiece piece) {
		final List<TorrentPeer> hasPieces = new ArrayList<TorrentPeer>();
		for (final TorrentPeer peer : peers) {
			if (!peer.getBitfield().hasPiece(piece))
				continue;
			hasPieces.add(peer);
		}
		return hasPieces;
	}

	/**
	 * Get an peer by its PeerID
	 * 
	 * @param peerId
	 *            the peer id
	 * @return the found peer. Null if not found.
	 */
	public TorrentPeer getPeer(TorrentPeerID peerId) {
		for (final TorrentPeer peer : peers) {
			if (peer.getPeerID().equals(peerId))
				return peer;
		}
		return null;
	}

	/**
	 * Get an peer by its address
	 * 
	 * @param address
	 *            the address
	 * @return the found peer. Null if not found.
	 */
	public TorrentPeer getPeer(InetSocketAddress address) {
		for (final TorrentPeer peer : peers) {
			if (peer.getSocketAddress().equals(address))
				return peer;
		}
		return null;
	}

	/**
	 * Get an peer by its address
	 * 
	 * @return an random peer
	 */
	public TorrentPeer getRandomPeer() {
		if (peers.size() == 0)
			return null;
		return peers.get((int) (Math.random() * (peers.size() - 1)));
	}

	/**
	 * Get an peer by its address
	 * 
	 * @return an random peer
	 */
	public TorrentPeer getRandomOfflinePeer() {
		List<TorrentPeer> accessiblePeers = new ArrayList<TorrentPeer>();
		List<TorrentPeer> nonAccessiblePeers = new ArrayList<TorrentPeer>();
		for (final TorrentPeer peer : peers) {
			if (peer.isConnected()) {
				if (peer.isAccessible()) {
					accessiblePeers.add(peer);
				} else {
					nonAccessiblePeers.add(peer);
				}
			}
		}

		List<TorrentPeer> peerList = accessiblePeers;

		if (peerList.size() == 0)
			peerList = nonAccessiblePeers;
		if (peerList.size() == 0)
			return null;
		return peerList.get((int) (Math.random() * (peerList.size() - 1)));
	}

	/**
	 * Lookup for a peer first by its id, then by address, if still not found,
	 * creates a new entry.
	 * 
	 * @param id
	 *            the peer id
	 * @param address
	 *            the address
	 * @return the found or newly created peer
	 */
	public TorrentPeer getPeer(TorrentPeerID id, InetSocketAddress address) {
		TorrentPeer peer = getPeer(id);
		if (peer == null) {
			peer = getPeer(address);
			if (peer != null) {
				if (remove(peer))
					peer = peer.createWithID(id);
			} else {
				peer = new TorrentPeer(context, id, null);
			}
			add(peer);
		}
		return peer;
	}

	/**
	 * If this peer already exists, will update its IP.
	 * 
	 * @param peerInfo
	 *            the peer info object, returned from the tracker
	 */
	public TorrentPeer addPeerByPeerInfo(PeerInfo peerInfo) {
		final TorrentPeerID id = TorrentPeerID.create(peerInfo.getPeerId());
		final InetSocketAddress address = new InetSocketAddress(
				peerInfo.getIp(), peerInfo.getPort());
		TorrentPeer peer = getPeer(id, address);
		peer.setSocketAddress(address);
		return peer;
	}

	@Override
	public Iterator<TorrentPeer> iterator() {
		return peers.iterator();
	}

	/**
	 * Get the torrent context
	 * 
	 * @return the torrent context
	 */
	public TorrentContext getContext() {
		return context;
	}
}
