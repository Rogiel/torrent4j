package com.torrent4j.net.peerwire.traffic;

import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

import com.torrent4j.model.peer.TorrentPeer;
import com.torrent4j.model.peer.TorrentPeerTrafficControl;

public class PeerTrafficShapingHandler extends ChannelTrafficShapingHandler {
	private long writeLimit;
	private long readLimit;

	private TorrentPeer peer;

	public PeerTrafficShapingHandler() {
		super(0, 0);
	}

	private void reconfigure() {
		if (peer == null)
			return;
		final TorrentPeerTrafficControl peerTraffic = peer.getTrafficControl();
		long readLimit = peerTraffic.getDownloadSpeedLimit();
		long writeLimit = peerTraffic.getUploadSpeedLimit();
		if (readLimit != this.readLimit || writeLimit != this.writeLimit) {
			this.writeLimit = writeLimit;
			this.readLimit = readLimit;
			configure(writeLimit, readLimit);
		}
	}

	@Override
	protected void doAccounting(TrafficCounter counter) {
		if (peer == null)
			return;
		reconfigure();
		peer.getTrafficControl().setCurrentDownloadSpeed(
				counter.lastReadThroughput());
		peer.getTrafficControl().setCurrentUploadSpeed(
				counter.lastWriteThroughput());
	}

	/**
	 * @return the peer
	 */
	public TorrentPeer getPeer() {
		return peer;
	}

	/**
	 * @param peer
	 *            the peer to set
	 */
	public void setPeer(TorrentPeer peer) {
		this.peer = peer;
		reconfigure();
	}
}
