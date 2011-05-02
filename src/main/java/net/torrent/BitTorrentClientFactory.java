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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.torrent.protocol.algorithm.TorrentAlgorithm;
import net.torrent.protocol.algorithm.impl.TorrentStdAlgorithm;
import net.torrent.protocol.datastore.TorrentDatastore;
import net.torrent.protocol.datastore.impl.PlainTorrentDatastore;
import net.torrent.protocol.peerwire.PeerWireManager;
import net.torrent.protocol.peerwire.manager.TorrentManager;
import net.torrent.torrent.Torrent;
import net.torrent.torrent.context.TorrentContext;
import net.torrent.torrent.piece.PieceSelector;
import net.torrent.torrent.piece.ScoredPieceSelector;

/**
 * Factory class for {@link BitTorrentClient}.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class BitTorrentClientFactory {
	/**
	 * The client's configuration
	 */
	private BitTorrentConfiguration config = new BitTorrentConfiguration();

	/**
	 * The torrent context
	 */
	private final TorrentContext context;
	/**
	 * The torrent datastore
	 */
	private TorrentDatastore datastore;
	/**
	 * The torrent manager
	 */
	private TorrentManager manager;
	/**
	 * The torrent algorithm
	 */
	private TorrentAlgorithm algorithm;
	/**
	 * The piece selector
	 */
	private PieceSelector selector;

	/**
	 * Creates a new standard {@link BitTorrentClient BitTorrent client}
	 * 
	 * @param file
	 *            the torrent file
	 * @return a new client
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static BitTorrentClient newStandardBitTorrentClient(File file)
			throws IOException, URISyntaxException {
		return new BitTorrentClientFactory(Torrent.load(file))
				.newBitTorrentClient();
	}

	/**
	 * Creates a new factory instance
	 * 
	 * @param torrent
	 *            the torrent
	 */
	public BitTorrentClientFactory(final Torrent torrent) {
		this.context = new TorrentContext(torrent);
		this.datastore = new PlainTorrentDatastore(new File("store.bin"));
		this.manager = new TorrentManager(context, datastore);
		if (this.selector == null)
			this.selector = new ScoredPieceSelector(manager);
		this.algorithm = new TorrentStdAlgorithm(manager, selector);
	}

	/**
	 * Creates a new factory instance
	 * 
	 * @param torrent
	 *            the torrent
	 * @param algorithm
	 *            the torrent algorithm
	 */
	public BitTorrentClientFactory(final Torrent torrent,
			final TorrentAlgorithm algorithm) {
		context = new TorrentContext(torrent);
		datastore = new PlainTorrentDatastore(null);
		manager = new TorrentManager(context, datastore);
		this.algorithm = algorithm;
	}

	/**
	 * Creates a new factory instance
	 * 
	 * @param torrent
	 *            the torrent
	 * @param datastore
	 *            the torrent datastore
	 */
	public BitTorrentClientFactory(final Torrent torrent,
			TorrentDatastore datastore) {
		this.context = new TorrentContext(torrent);
		this.datastore = datastore;
		this.manager = new TorrentManager(context, datastore);
		if (this.selector == null)
			this.selector = new ScoredPieceSelector(manager);
		this.algorithm = new TorrentStdAlgorithm(manager, selector);
	}

	/**
	 * Creates a new factory instance
	 * 
	 * @param torrent
	 *            the torrent
	 * @param datastore
	 *            the torrent datastore
	 * @param algorithm
	 *            the torrent algorithm
	 */
	public BitTorrentClientFactory(final Torrent torrent,
			TorrentDatastore datastore, final TorrentAlgorithm algorithm) {
		this.context = new TorrentContext(torrent);
		this.datastore = datastore;
		this.manager = new TorrentManager(context, datastore);
		this.algorithm = algorithm;
	}

	/**
	 * Create the {@link BitTorrentClient} object
	 * 
	 * @return the created {@link BitTorrentClient}
	 */
	public BitTorrentClient newBitTorrentClient() {
		final PeerWireManager peerWire = new PeerWireManager(manager, algorithm);
		return new BitTorrentClient(config, manager, peerWire, algorithm);
	}

	/**
	 * Get the client configuration
	 * 
	 * @return the client configuration
	 */
	public BitTorrentConfiguration getConfig() {
		return config;
	}

	/**
	 * Set the client configuration
	 * 
	 * @param config
	 *            the client configuration
	 */
	public void setConfig(BitTorrentConfiguration config) {
		this.config = config;
	}

	/**
	 * Get the datastore
	 * 
	 * @return the datastore
	 */
	public TorrentDatastore getDatastore() {
		return datastore;
	}

	/**
	 * Set the datastore
	 * 
	 * @param datastore
	 *            the datastore
	 */
	public void setDatastore(TorrentDatastore datastore) {
		this.datastore = datastore;
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
	 * Set the torrent manager
	 * 
	 * @param manager
	 *            the torrent manager
	 */
	public void setManager(TorrentManager manager) {
		this.manager = manager;
	}

	/**
	 * Get the torrent algorithm
	 * 
	 * @return the torrent algorithm
	 */
	public TorrentAlgorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Set the torrent algorithm
	 * 
	 * @param algorithm
	 *            the torrent algorithm
	 */
	public void setAlgorithm(TorrentAlgorithm algorithm) {
		this.algorithm = algorithm;
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
