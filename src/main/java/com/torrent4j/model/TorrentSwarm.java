package com.torrent4j.model;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.torrent4j.model.peer.TorrentPeer;

public class TorrentSwarm {
	private final Torrent torrent;
	private final List<TorrentPeer> peers = new ArrayList<>();

	public TorrentSwarm(Torrent torrent) {
		this.torrent = torrent;
	}

	public void broadcast(SwarmBroadcastHandler handler) {
		for (final TorrentPeer peer : getConnectedPeers()) {
			try {
				handler.broadcast(peer);
			} catch (Exception e) {
				if (handler.exception(e))
					throw new RuntimeException(e);
			}
		}
	}

	public interface SwarmBroadcastHandler {
		void broadcast(TorrentPeer peer);

		boolean exception(Exception e);
	}

	public List<TorrentPeer> getPeers() {
		return Collections.unmodifiableList(peers);
	}

	public List<TorrentPeer> getConnectedPeers() {
		final List<TorrentPeer> list = new ArrayList<>();
		for (final TorrentPeer peer : peers) {
			if (peer.isConnected())
				list.add(peer);
		}
		return Collections.unmodifiableList(list);
	}

	public void addPeer(TorrentPeer peer) {
		peers.add(peer);
		torrent.getStrategy().getPeerStrategy().peerDiscovered(torrent, peer);
	}

	public void removePeer(TorrentPeer peer) {
		peers.remove(peer);
		torrent.getStrategy().getPeerStrategy().peerRemoved(torrent, peer);
	}

	public TorrentPeer findPeer(InetSocketAddress address, String peerID) {
		for (final TorrentPeer peer : peers) {
			if (peerID != null && peerID.equals(peer.getPeerID()))
				return peer;
			if (address != null && address.equals(peer.getAddress()))
				return peer;
		}
		return null;
	}

	public Torrent getTorrent() {
		return torrent;
	}
}
