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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.torrent.torrent.context.TorrentContext;

import org.jboss.netty.channel.Channel;

/**
 * Connection manager: keep control over active and inactive {@link Channel
 * channel} connections.
 * <p>
 * Please note that the manager actually does not make any decision nor create
 * or block an connection.
 * <p>
 * You can {@link Iterable iterate} over this manager to get <b>active</b>
 * {@link Channel} instances.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class ConnectionManager implements Iterable<Channel> {
	/**
	 * The torrent context
	 */
	private final TorrentContext context;

	/**
	 * The list of active channels
	 */
	private final Set<Channel> activeChannels = new HashSet<Channel>();
	/**
	 * The list of inactive channels
	 */
	private final Set<Channel> inactiveChannels = new HashSet<Channel>();

	/**
	 * Creates a new instance
	 * 
	 * @param context
	 *            the torrent context
	 */
	public ConnectionManager(TorrentContext context) {
		this.context = context;
	}

	/**
	 * Registers a new channel
	 * 
	 * @param channel
	 *            the channel
	 * @return true if channel was added. False if was already in list.
	 */
	public boolean add(Channel channel) {
		if (channel.isConnected()) {
			return activeChannels.add(channel);
		} else {
			return inactiveChannels.add(channel);
		}
	}

	/**
	 * Test if contains the channel.
	 * 
	 * @param channel
	 *            the channel
	 * @return true if channel is registered
	 */
	public boolean contains(Channel channel) {
		if (activeChannels.contains(channel))
			return true;
		if (inactiveChannels.contains(channel))
			return true;
		return false;
	}

	/**
	 * Remove the channel
	 * 
	 * @param channel
	 *            the channel
	 * @return true if channel was registered
	 */
	public boolean remove(Channel channel) {
		if (activeChannels.remove(channel))
			return true;
		if (inactiveChannels.remove(channel))
			return true;
		return false;
	}

	/**
	 * Updates the state of the channel. In practice, remove and adds again the
	 * channel.
	 * 
	 * @param channel
	 *            the channel
	 * @return true if channel has been added successfully.
	 */
	public boolean update(Channel channel) {
		if (!remove(channel))
			return false;
		return add(channel);
	}

	/**
	 * Get to count of active connections
	 * 
	 * @return the amount of active connections
	 */
	public int getActiveConnections() {
		return activeChannels.size();
	}

	/**
	 * Get to count of inactive connections
	 * 
	 * @return the amount of inactive connections
	 */
	public int getInactiveConnections() {
		return inactiveChannels.size();
	}

	/**
	 * Get the list of active channels
	 * 
	 * @return list of active channels
	 */
	public Set<Channel> getActiveChannels() {
		return Collections.unmodifiableSet(activeChannels);
	}

	/**
	 * Get the list of inactive channels
	 * 
	 * @return list of inactive channels
	 */
	public Set<Channel> getInactiveChannels() {
		return Collections.unmodifiableSet(inactiveChannels);
	}

	@Override
	public Iterator<Channel> iterator() {
		return activeChannels.iterator();
	}

	/**
	 * Get the torent context
	 * 
	 * @return the torrent context
	 */
	public TorrentContext getContext() {
		return context;
	}
}
