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
import net.torrent.util.PeerCallback;

import org.jboss.netty.channel.Channel;

public class PeerManager implements Iterable<PeerWirePeer> {
	private final TorrentContext context;
	private final ConnectionManager connectionManager;

	private final Map<Channel, PeerWirePeer> activePeers = new HashMap<Channel, PeerWirePeer>();
	private final Map<Channel, PeerWirePeer> inactivePeers = new HashMap<Channel, PeerWirePeer>();

	public PeerManager(TorrentContext context,
			ConnectionManager connectionManager) {
		this.context = context;
		this.connectionManager = connectionManager;
	}

	public boolean contains(Channel channel) {
		if (activePeers.containsKey(channel))
			return true;
		if (inactivePeers.containsKey(channel))
			return true;
		return false;
	}

	public boolean contains(PeerWirePeer peer) {
		if (activePeers.containsValue(peer))
			return true;
		if (inactivePeers.containsValue(peer))
			return true;
		return false;
	}

	public PeerWirePeer getPeer(Channel channel) {
		PeerWirePeer peer = activePeers.get(channel);
		if (peer == null)
			peer = inactivePeers.get(channel);
		return peer;
	}

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

	public boolean isEmpty() {
		return activePeers.isEmpty();
	}

	public PeerWirePeer add(Channel channel, TorrentPeer peer) {
		if (channel.isConnected()) {
			return activePeers.put(channel, new PeerWirePeer(channel, peer));
		} else {
			return inactivePeers.put(channel, new PeerWirePeer(channel, peer));
		}
	}

	public PeerWirePeer remove(Channel channel) {
		PeerWirePeer peer;
		if ((peer = activePeers.remove(channel)) != null)
			return peer;
		if ((peer = inactivePeers.remove(channel)) != null)
			return peer;
		return null;
	}

	public PeerWirePeer remove(PeerWirePeer peer) {
		final Channel channel = getChannel(peer);
		PeerWirePeer peerRemoved;
		if ((peerRemoved = activePeers.remove(channel)) != null)
			return peerRemoved;
		if ((peerRemoved = inactivePeers.remove(channel)) != null)
			return peerRemoved;
		return null;
	}

	public PeerWirePeer update(Channel channel) {
		PeerWirePeer peer;
		if ((peer = remove(channel)) == null)
			return null;
		return add(channel, peer.getTorrentPeer());
	}

	public int getActivePeersCount() {
		return activePeers.size();
	}

	public int getImactivePeersCount() {
		return activePeers.size();
	}

	public Map<Channel, PeerWirePeer> getActivePeers() {
		return Collections.unmodifiableMap(activePeers);
	}

	public Map<Channel, PeerWirePeer> getInactivePeers() {
		return Collections.unmodifiableMap(inactivePeers);
	}

	public Set<Channel> getActiveChannels() {
		return Collections.unmodifiableSet(activePeers.keySet());
	}

	public Set<Channel> getInactiveChannels() {
		return Collections.unmodifiableSet(inactivePeers.keySet());
	}

	public void executeActive(PeerCallback callback) {
		for (final Entry<Channel, PeerWirePeer> entry : this.activePeers
				.entrySet()) {
			callback.callback(entry.getValue());
		}
	}

	public void executeInactive(PeerCallback callback) {
		for (final Entry<Channel, PeerWirePeer> entry : this.inactivePeers
				.entrySet()) {
			callback.callback(entry.getValue());
		}
	}

	public void execute(PeerCallback callback) {
		executeActive(callback);
		executeInactive(callback);
	}

	@Override
	public Iterator<PeerWirePeer> iterator() {
		return activePeers.values().iterator();
	}

	public TorrentContext getContext() {
		return context;
	}

	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}
}
