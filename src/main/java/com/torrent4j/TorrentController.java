package com.torrent4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPiece;
import com.torrent4j.net.TorrentProtocol;
import com.torrent4j.net.peerwire.PeerWireProtocol;
import com.torrent4j.storage.NIOTorrentStorage;
import com.torrent4j.storage.TorrentStorage;
import com.torrent4j.util.Hash;

/**
 * The main class for starting torrent transfers. Each torrent instance need to
 * be registered into an controller. Once registered, connections can be
 * established to start downloading the file (or files) from other peers in the
 * BitTorrent network.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class TorrentController {
	/**
	 * The controller configuration
	 */
	private final TorrentControllerConfiguration config = new TorrentControllerConfiguration();
	/**
	 * The torrent protocol
	 */
	private final TorrentProtocol protocol;
	/**
	 * The torrent storage
	 */
	private final TorrentStorage storage;
	/**
	 * The list of torrents controlled on this controller
	 */
	private final List<Torrent> torrents = new ArrayList<>();

	/**
	 * Creates a new controller
	 * 
	 * @param protocol
	 *            the protocol to use
	 * @param storage
	 *            the storage to use
	 */
	public TorrentController(TorrentProtocol protocol, TorrentStorage storage) {
		this.protocol = protocol;
		this.storage = storage;
	}

	/**
	 * Creates a new controller with {@link NIOTorrentStorage} as default
	 * storage.
	 * 
	 * @param protocol
	 *            thw protocol to use
	 */
	public TorrentController(TorrentProtocol protocol) {
		this(protocol, new NIOTorrentStorage());
	}

	/**
	 * Creates a new controller with {@link PeerWireProtocol} as default
	 * protocol.
	 * 
	 * @param storage
	 *            the storage to use
	 */
	public TorrentController(TorrentStorage storage) {
		this(new PeerWireProtocol(), storage);
	}

	/**
	 * Creates a new controller with {@link PeerWireProtocol} as default
	 * protocol and {@link NIOTorrentStorage} as default storage.
	 */
	public TorrentController() {
		this(new PeerWireProtocol(), new NIOTorrentStorage());
	}

	/**
	 * Retrieves data from the storage and validate all piece information
	 * already downloaded, if any. Automatically updates the torrent internal
	 * state to match the real download progress.
	 * 
	 * @param torrent
	 *            the torrent to check existing data
	 * @throws IOException
	 *             if any IO error occur
	 */
	public void checkExistingData(Torrent torrent) throws IOException {
		for (final TorrentPiece piece : torrent.getPieces()) {
			final Hash hash = storage.checksum(piece);
			if (!piece.getHash().equals(hash))
				continue;
			torrent.getCompletedPieces().addPiece(piece);
		}
	}

	/**
	 * Registers a new torrent on this controller
	 * 
	 * @param torrent
	 *            the torrent to be registered
	 */
	public void registerTorrent(Torrent torrent) {
		torrents.add(torrent);
		torrent.setController(this);
	}

	/**
	 * Removes an already registered torrent from this controller
	 * 
	 * @param torrent
	 *            the torrent to be removed
	 */
	public void removeTorrent(Torrent torrent) {
		torrents.remove(torrent);
		torrent.setController(null);
	}

	/**
	 * Tries to locate the torrent represented by <code>hash</code>.
	 * 
	 * @param hash
	 *            the torrent hash to look for
	 * @return the torrent with the given <code>hash</code>, if any
	 */
	public Torrent findTorrent(Hash hash) {
		for (final Torrent torrent : torrents) {
			if (torrent.getHash().equals(hash))
				return torrent;
		}
		return null;
	}

	/**
	 * Stars the controller
	 * 
	 * @param port
	 *            the listen port
	 */
	public void start(int port) {
		protocol.start(this, port);
	}

	/**
	 * Stops the controller
	 */
	public void stop() {
		protocol.stop();
	}

	/**
	 * @return this controller configuration object
	 */
	public TorrentControllerConfiguration getConfig() {
		return config;
	}

	/**
	 * @return this controller protocol
	 */
	public TorrentProtocol getProtocol() {
		return protocol;
	}

	/**
	 * @return this controller storage
	 */
	public TorrentStorage getStorage() {
		return storage;
	}

	/**
	 * @return this controller torrent list
	 */
	public List<Torrent> getTorrents() {
		return torrents;
	}
}
