package com.torrent4j.model;

public class TorrentTrafficControl {
	private final Torrent torrent;

	private long downloadSpeedLimit = 0;
	private long uploadSpeedLimit = 0;

	private long currentDownloadSpeed = 0;
	private long currentUploadSpeed = 0;

	public TorrentTrafficControl(Torrent torrent) {
		this.torrent = torrent;
	}

	/**
	 * @return the downloadSpeedLimit
	 */
	public long getDownloadSpeedLimit() {
		return downloadSpeedLimit;
	}

	/**
	 * @param downloadSpeedLimit
	 *            the downloadSpeedLimit to set
	 */
	public void setDownloadSpeedLimit(long downloadSpeedLimit) {
		this.downloadSpeedLimit = downloadSpeedLimit;
	}

	/**
	 * @return the uploadSpeedLimit
	 */
	public long getUploadSpeedLimit() {
		return uploadSpeedLimit;
	}

	/**
	 * @param uploadSpeedLimit
	 *            the uploadSpeedLimit to set
	 */
	public void setUploadSpeedLimit(long uploadSpeedLimit) {
		this.uploadSpeedLimit = uploadSpeedLimit;
	}

	/**
	 * @return the currentDownloadSpeed
	 */
	public long getCurrentDownloadSpeed() {
		return currentDownloadSpeed;
	}

	/**
	 * @param currentDownloadSpeed
	 *            the currentDownloadSpeed to set
	 */
	public void setCurrentDownloadSpeed(long currentDownloadSpeed) {
		this.currentDownloadSpeed = currentDownloadSpeed;
	}

	/**
	 * @return the currentUploadSpeed
	 */
	public long getCurrentUploadSpeed() {
		return currentUploadSpeed;
	}

	/**
	 * @param currentUploadSpeed
	 *            the currentUploadSpeed to set
	 */
	public void setCurrentUploadSpeed(long currentUploadSpeed) {
		this.currentUploadSpeed = currentUploadSpeed;
	}

	@Override
	public String toString() {
		return "TorrentTrafficControl [torrent=" + torrent
				+ ", downloadSpeedLimit=" + downloadSpeedLimit
				+ ", uploadSpeedLimit=" + uploadSpeedLimit
				+ ", currentDownloadSpeed=" + currentDownloadSpeed
				+ ", currentUploadSpeed=" + currentUploadSpeed + "]";
	}

	/**
	 * @return the torrent
	 */
	public Torrent getTorrent() {
		return torrent;
	}
}
