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
package net.torrent.protocol.peerwire.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.torrent.protocol.peerwire.PeerWirePeer;
import net.torrent.torrent.context.TorrentContext;
import net.torrent.torrent.context.TorrentPeer;
import net.torrent.util.PeerWirePeerCallback;

import org.jboss.netty.channel.Channel;

/**
 * The peer manager is used to keep control over active peers and they
 * {@link Channel Netty Channel} used to write or read messages.
 * <p>
 * Please note that the manager actually does not make any decision nor block an
 * requested piece.
 * <p>
 * You can {@link Iterable iterate} over this manager to get <b>active</b>
 * {@link PeerWirePeer peers}.
 */
public class PeerManager implements Iterable<PeerWirePeer> {
	/**
	 * The torrent context
	 */
	private final TorrentContext context;
	/**
	 * The {@link ConnectionManager} instance
	 */
	private final ConnectionManager connectionManager;

	/**
	 * The map of active channel-peer mapping
	 */
	private final Map<Channel, PeerWirePeer> activePeers = new HashMap<Channel, PeerWirePeer>();
	/**
	 * The map of inactive channel-peer mapping
	 */
	private final Map<Channel, PeerWirePeer> inactivePeers = new HashMap<Channel, PeerWirePeer>();

	/**
	 * Creates a new instance
	 * 
	 * @param context
	 *            the torrent context
	 * @param connectionManager
	 *            the connection manager instance
	 */
	public PeerManager(TorrentContext context,
			ConnectionManager connectionManager) {
		this.context = context;
		this.connectionManager = connectionManager;
	}

	/**
	 * Tests if the given <tt>channel</tt> has an peer attached to it.
	 * 
	 * @param channel
	 *            the channel
	 * @return true if an peer is attached
	 */
	public boolean contains(Channel channel) {
		if (activePeers.containsKey(channel))
			return true;
		if (inactivePeers.containsKey(channel))
			return true;
		return false;
	}

	/**
	 * Tests if the current {@link PeerWirePeer} is registered in this manager.
	 * You will normally not have access to {@link PeerWirePeer} object, thus
	 * whis method might not be very useful outside handlers.
	 * 
	 * @param peer
	 *            the {@link PeerWirePeer} peer
	 * @return true if this <tt>peer</tt> is registered in this manager
	 */
	public boolean contains(PeerWirePeer peer) {
		if (activePeers.containsValue(peer))
			return true;
		if (inactivePeers.containsValue(peer))
			return true;
		return false;
	}

	/**
	 * Get the {@link PeerWirePeer} registered in the given <tt>channel</tt>.
	 * 
	 * @param channel
	 *            the channel
	 * @return the peer instance.
	 */
	public PeerWirePeer getPeer(Channel channel) {
		PeerWirePeer peer = activePeers.get(channel);
		if (peer == null)
			peer = inactivePeers.get(channel);
		return peer;
	}

	/**
	 * Lookup for the {@link Channel} in which the <tt>peer</tt> is attached to.
	 * 
	 * @param peer
	 *            the peer
	 * @return the {@link Channel} for the given <tt>peer</tt>
	 */
	public Channel getChannel(PeerWirePeer peer) {
		for (final Entry<Channel, PeerWirePeer> entry : activePeers.entrySet()) {
			if (entry.getValue().equals(peer))
				return entry.getKey();
		}
		for (final Entry<Channel, PeerWirePeer> entry : inactivePeers
				.entrySet()) {
			if (entry.getValue().equals(peer))
				return entry.getKey();
		}
		return null;
	}

	/**
	 * Test if there are no active peers in this manager.
	 * 
	 * @return true if no active peers
	 */
	public boolean isEmpty() {
		return activePeers.isEmpty();
	}

	/**
	 * Adds a new peer to this manager.
	 * 
	 * @param channel
	 *            the channel
	 * @param peer
	 *            the peer
	 * @return the {@link PeerWirePeer} created instance.
	 */
	public PeerWirePeer add(Channel channel, TorrentPeer peer) {
		if (channel.isConnected()) {
			return activePeers.put(channel, new PeerWirePeer(channel, peer));
		} else {
			return inactivePeers.put(channel, new PeerWirePeer(channel, peer));
		}
	}

	/**
	 * Removes an {@link Channel} and its {@link TorrentPeer} from this manager.
	 * 
	 * @param channel
	 *            the channel
	 * @return the, now removed, {@link PeerWirePeer} instance
	 */
	public PeerWirePeer remove(Channel channel) {
		PeerWirePeer peer;
		if ((peer = activePeers.remove(channel)) != null)
			return peer;
		if ((peer = inactivePeers.remove(channel)) != null)
			return peer;
		return null;
	}

	/**
	 * Removes an {@link PeerWirePeer} from this manager.
	 * 
	 * @param peer
	 *            the peer
	 * @return the, now removed, {@link PeerWirePeer} instance
	 */
	public PeerWirePeer remove(PeerWirePeer peer) {
		final Channel channel = getChannel(peer);
		PeerWirePeer peerRemoved;
		if ((peerRemoved = activePeers.remove(channel)) != null)
			return peerRemoved;
		if ((peerRemoved = inactivePeers.remove(channel)) != null)
			return peerRemoved;
		return null;
	}

	/**
	 * Updates this {@link Channel} peer state (i.e. active or inactive)
	 * 
	 * @param channel
	 *            the channel
	 * @return the {@link PeerWirePeer} instance updated
	 */
	public PeerWirePeer update(Channel channel) {
		PeerWirePeer peer;
		if ((peer = remove(channel)) == null)
			return null;
		return add(channel, peer.getTorrentPeer());
	}

	/**
	 * Get the total active peers
	 * 
	 * @return the active peers count
	 */
	public int getActivePeersCount() {
		return activePeers.size();
	}

	/**
	 * Get the total inactive peers
	 * 
	 * @return the inactive peers count
	 */
	public int getImactivePeersCount() {
		return activePeers.size();
	}

	/**
	 * Get an {@link Map} of all active peers
	 * 
	 * @return the {@link Map} of all active peers
	 */
	public Map<Channel, PeerWirePeer> getActivePeers() {
		return Collections.unmodifiableMap(activePeers);
	}

	/**
	 * Get an {@link Map} of all inactive peers
	 * 
	 * @return the {@link Map} of all inactive peers
	 */
	public Map<Channel, PeerWirePeer> getInactivePeers() {
		return Collections.unmodifiableMap(inactivePeers);
	}

	/**
	 * Get an {@link Set} of all active {@link Channel channels}
	 * 
	 * @return the {@link Set} of all active {@link Channel channels}
	 */
	public Set<Channel> getActiveChannels() {
		return Collections.unmodifiableSet(activePeers.keySet());
	}

	/**
	 * Get an {@link Set} of all inactive {@link Channel channels}
	 * 
	 * @return the {@link Set} of all inactive {@link Channel channels}
	 */
	public Set<Channel> getInactiveChannels() {
		return Collections.unmodifiableSet(inactivePeers.keySet());
	}

	/**
	 * Executes the <tt>callback</tt> for each active peer in this manager.
	 * 
	 * @param callback
	 *            the callback
	 */
	public void executeActive(PeerWirePeerCallback callback) {
		for (final Entry<Channel, PeerWirePeer> entry : this.activePeers
				.entrySet()) {
			callback.callback(entry.getValue());
		}
	}

	/**
	 * Executes the <tt>callback</tt> for each inactive peer in this manager.
	 * 
	 * @param callback
	 *            the callback
	 */
	public void executeInactive(PeerWirePeerCallback callback) {
		for (final Entry<Channel, PeerWirePeer> entry : this.inactivePeers
				.entrySet()) {
			callback.callback(entry.getValue());
		}
	}

	/**
	 * Executes the <tt>callback</tt> for each active and inactive peer in this
	 * manager. This method call firstly {@link #executeActive(PeerCallback)}
	 * and later {@link #executeInactive(PeerCallback)}.
	 * 
	 * @param callback
	 *            the callback
	 */
	public void execute(PeerWirePeerCallback callback) {
		executeActive(callback);
		executeInactive(callback);
	}

	@Override
	public Iterator<PeerWirePeer> iterator() {
		return activePeers.values().iterator();
	}

	/**
	 * Get the torrent context
	 * 
	 * @return the torrent context
	 */
	public TorrentContext getContext() {
		return context;
	}

	/**
	 * Get the connection manager
	 * 
	 * @return the connection manager
	 */
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}
}
