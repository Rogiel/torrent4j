package com.torrent4j.net.peerwire.traffic;

import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

import java.util.concurrent.Executor;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentTrafficControl;

public class TorrentTrafficShapingHandler extends GlobalTrafficShapingHandler {
	private Torrent torrent;

	private long writeLimit;
	private long readLimit;

	public TorrentTrafficShapingHandler(Executor executor) {
		super(executor, 0, 0);
	}

	private void reconfigure() {
		if (torrent == null)
			return;
		final TorrentTrafficControl traffic = torrent.getTrafficControl();
		long readLimit = traffic.getDownloadSpeedLimit();
		long writeLimit = traffic.getUploadSpeedLimit();
		if (readLimit != this.readLimit || writeLimit != this.writeLimit) {
			this.writeLimit = writeLimit;
			this.readLimit = readLimit;
			configure(writeLimit, readLimit);
		}
	}

	@Override
	protected void doAccounting(TrafficCounter counter) {
		if (torrent == null)
			return;
		reconfigure();
		torrent.getTrafficControl().setCurrentDownloadSpeed(
				counter.getLastReadThroughput());
		torrent.getTrafficControl().setCurrentUploadSpeed(
				counter.getLastWriteThroughput());
	}

	/**
	 * @return the torrent
	 */
	public Torrent getTorrent() {
		return torrent;
	}

	/**
	 * @param torrent
	 *            the torrent to set
	 */
	public void setTorrent(Torrent torrent) {
		this.torrent = torrent;
		reconfigure();
	}
}
