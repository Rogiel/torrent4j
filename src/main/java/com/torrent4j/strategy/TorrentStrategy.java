package com.torrent4j.strategy;

/**
 * Provides an easy method to access all torrent strategies
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface TorrentStrategy {
	/**
	 * @return the download strategy
	 */
	TorrentDownloadStrategy getDownloadStrategy();

	/**
	 * @return the upload strategy
	 */
	TorrentUploadStrategy getUploadStrategy();

	/**
	 * @return the peer strategy
	 */
	TorrentPeerStrategy getPeerStrategy();
}
