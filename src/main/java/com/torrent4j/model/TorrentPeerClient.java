package com.torrent4j.model;

/**
 * The list of known torrent clients
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public enum TorrentPeerClient {
	/**
	 * µTorrent for Mac (<strong>-UM</strong>)
	 */
	UTORRENT_FOR_MAC("µTorrent for Mac", "-UM"),
	/**
	 * µTorrent (<strong>-UT</strong>)
	 */
	UTORRENT("µTorrent", "-UT"),
	/**
	 * Transmission (<strong>-TR</strong>)
	 */
	TRANSMISSION("Transmission", "-TR"),
	
	UNKNOWN("Unknown", "");

	/**
	 * An friendly name for the client. Can be used on an UI.
	 */
	public final String friendlyName;
	/**
	 * The PeerID prefix for the client
	 */
	public final String prefix;

	/**
	 * Initializes a new enum value
	 * 
	 * @param friendlyName
	 *            an friendly name for the client
	 * @param prefix
	 *            the PeerID prefix
	 */
	private TorrentPeerClient(String friendlyName, String prefix) {
		this.friendlyName = friendlyName;
		this.prefix = prefix;
	}
}
