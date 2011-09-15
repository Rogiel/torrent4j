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

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;

import net.torrent.protocol.algorithm.TorrentAlgorithm;
import net.torrent.protocol.peerwire.PeerWireManager;
import net.torrent.protocol.peerwire.manager.TorrentManager;
import net.torrent.protocol.tracker.HttpTorrentTrackerAnnouncer;
import net.torrent.torrent.context.TorrentContext;
import net.torrent.torrent.context.TorrentPeer;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main class used to controll your torrent transfer. It is not
 * recommended to directly instantiate this class, instead use
 * {@link BitTorrentClientFactory} to create new instances.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class BitTorrentClient implements Runnable {
	/**
	 * The logger instance
	 */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Configuration of an BitTorrentClient.
	 */
	private final BitTorrentConfiguration config;

	/**
	 * The torrent context
	 */
	private final TorrentContext context;
	/**
	 * The torrent manager
	 */
	private final TorrentManager manager;
	/**
	 * The peer wire protocol manager
	 */
	private final PeerWireManager peerWire;
	/**
	 * The torrent algorithm
	 */
	private final TorrentAlgorithm algorithm;

	/**
	 * Timer used to create new connections
	 */
	private final Timer connectorTimer = new Timer();

	/**
	 * Creates a new instance
	 * 
	 * @param config
	 *            the configuration
	 * @param manager
	 *            the torrent manager
	 * @param peerWire
	 *            the peer wire protocol manager
	 * @param algorithm
	 *            the torrent algorithm
	 */
	public BitTorrentClient(final BitTorrentConfiguration config,
			TorrentManager manager, PeerWireManager peerWire,
			TorrentAlgorithm algorithm) {
		this.config = config;
		this.context = manager.getContext();
		this.manager = manager;
		this.peerWire = peerWire;
		this.algorithm = algorithm;
	}

	/**
	 * Start this torrent
	 */
	public void start() {
		start((InetSocketAddress[]) null);
	}

	/**
	 * Start this torrent. Once network is up, tries to connect to all the peers
	 * in <tt>addrs</tt>.
	 * 
	 * @param addrs
	 *            addresses
	 */
	public void start(InetSocketAddress... addrs) {
		if (config.getListenPort() > 0)
			peerWire.listen(config.getListenPort());

		final HttpTorrentTrackerAnnouncer announcer = new HttpTorrentTrackerAnnouncer(
				context.getSwarm());
		try {
			announcer.announce(context.getTorrent().getTrackers().iterator()
					.next());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// run every 10 seconds - only 1 connection per turn
				connectorTimer.schedule(new ConnectorTimerTask(), 0 * 1000, 2 * 1000);

		if (addrs != null)
			for (final InetSocketAddress addr : addrs) {
				peerWire.connect(addr);
			}
	}

	/**
	 * Task that creates new connections at a certain repeat rate. Only one
	 * connection per turn.
	 * 
	 * @author Rogiel Josias Sulzbach (<a
	 *         href="http://www.rogiel.com/">http://www.rogiel.com/</a>)
	 */
	public class ConnectorTimerTask extends TimerTask {
		@Override
		public void run() {
			TorrentPeer peer = null;
			while ((peer = algorithm.getPeerAlgorithm().connect()) != null) {
				log.debug("Connecting to {}", peer);
				try {
					if (!peerWire.connect(peer.getSocketAddress()).await()
							.isSuccess()) {
						peer.setAccessible(false);
					}
				} catch (InterruptedException e) {
				}
				return;
			}
			log.debug("No new peers to connect");
		}
	}

	@Override
	public void run() {
		this.start();
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
	 * Get the torrent manager
	 * 
	 * @return the torrent manager
	 */
	public TorrentManager getManager() {
		return manager;
	}

	/**
	 * The peerwire manager
	 * 
	 * @return the peerwire manager
	 */
	public PeerWireManager getPeerWire() {
		return peerWire;
	}

	/**
	 * The torret
	 * 
	 * @return
	 */
	public BitTorrentConfiguration getConfig() {
		return config;
	}

	public TorrentAlgorithm getAlgorithm() {
		return algorithm;
	}
}
