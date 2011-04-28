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

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.torrent.protocol.algorithm.TorrentAlgorithm;
import net.torrent.protocol.peerwire.handler.PeerWireAlgorithmHandler;
import net.torrent.protocol.peerwire.manager.TorrentManager;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * Manager for the peerwire protocol. Can be used to start or stop the server
 * and initiate new connections.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PeerWireManager {
	/**
	 * The Netty client's {@link ClientBootstrap bootstrap}
	 */
	private final ClientBootstrap client = new ClientBootstrap(
			new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()));
	/**
	 * The Netty server's {@link ServerBootstrap bootstrap}
	 */
	private final ServerBootstrap server = new ServerBootstrap(
			new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()));

	/**
	 * The server's listen address
	 */
	private InetSocketAddress listenAddress;
	/**
	 * The server's channel
	 */
	private Channel serverChannel;

	/**
	 * The pipeline channel factory
	 */
	private final PeerWirePipelineFactory pipelineFactory;

	/**
	 * Creates a new instance
	 * 
	 * @param manager
	 *            the torrent manager
	 * @param algorithm
	 *            the torrent algorithm
	 * @param listenAddress
	 *            the server's listen address
	 */
	public PeerWireManager(TorrentManager manager, ChannelHandler algorithm,
			InetSocketAddress listenAddress) {
		pipelineFactory = new PeerWirePipelineFactory(manager, algorithm);

		client.setPipelineFactory(pipelineFactory);
		server.setPipelineFactory(pipelineFactory);
	}

	/**
	 * Creates a new instance
	 * 
	 * @param manager
	 *            the torrent manager
	 * @param algorithm
	 *            the torrent algorithm
	 */
	public PeerWireManager(TorrentManager manager, TorrentAlgorithm algorithm) {
		this(manager, new PeerWireAlgorithmHandler(manager, algorithm), null);
	}

	/**
	 * Listen the server to the given port
	 * 
	 * @param port
	 *            the port
	 * @return the server {@link Channel}.
	 */
	public Channel listen(int port) {
		if (listenAddress == null)
			listenAddress = new InetSocketAddress(port);
		return (serverChannel = server.bind(listenAddress));
	}

	/**
	 * Connect to a new client
	 * 
	 * @param address
	 *            the address
	 * @return the {@link ChannelFuture} for monitoring the connection progress
	 */
	public ChannelFuture connect(InetSocketAddress address) {
		return client.connect(address);
	}

	/**
	 * Connect to a new client and wait until is complete.
	 * 
	 * @param address
	 *            the peer address
	 * @param wait
	 *            wait time
	 * @param unit
	 *            unit of <tt>wait</tt>
	 * @return the newly created {@link Channel}
	 */
	public Channel connectWait(InetSocketAddress address, long wait,
			TimeUnit unit) {
		final ChannelFuture future = connect(address);
		if (future.awaitUninterruptibly(wait, unit)) {
			if (future.isSuccess())
				return future.getChannel();
		}
		return null;
	}

	/**
	 * Close the server and the client, and then release the external resources.
	 */
	public void close() {
		if (serverChannel != null)
			serverChannel.close().awaitUninterruptibly();
		client.releaseExternalResources();
	}

	/**
	 * Get the server's listening channel
	 * 
	 * @return
	 */
	public Channel getServerChannel() {
		return serverChannel;
	}

	/**
	 * Get the Netty client's {@link ClientBootstrap bootstrap}
	 * 
	 * @return the {@link ClientBootstrap}
	 */
	protected ClientBootstrap getClientBootstrap() {
		return client;
	}

	/**
	 * Get the Netty server's {@link ServerBootstrap bootstrap}
	 * 
	 * @return the {@link ServerBootstrap}
	 */
	protected ServerBootstrap getServerBootstrap() {
		return server;
	}

	/**
	 * Return the current server's listening address
	 * 
	 * @return the listen address
	 */
	public InetSocketAddress getListenAddress() {
		return listenAddress;
	}

	/**
	 * Get the current server's listening address
	 * 
	 * @param listenAddress
	 *            the listen address
	 */
	public void setListenAddress(InetSocketAddress listenAddress) {
		this.listenAddress = listenAddress;
	}
}
