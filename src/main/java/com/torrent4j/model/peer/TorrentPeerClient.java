package com.torrent4j.model.peer;

/**
 * The list of known torrent clients
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public enum TorrentPeerClient {
	/**
	 * µTorrent for Mac (<strong>-UM</strong>)
	 */
	UTORRENT_FOR_MAC("µTorrent for Mac", "-UM", true),
	/**
	 * µTorrent (<strong>-UT</strong>)
	 */
	UTORRENT("µTorrent", "-UT", true),
	/**
	 * Transmission (<strong>-TR</strong>)
	 */
	TRANSMISSION("Transmission", "-TR", true),

	UNKNOWN("Unknown", "", false);

	/**
	 * An friendly name for the client. Can be used on an UI.
	 */
	public final String friendlyName;
	/**
	 * The PeerID prefix for the client
	 */
	public final String prefix;
	/**
	 * Whether the PeerID has an version attribute
	 */
	public final boolean versioned;

	/**
	 * Initializes a new enum value
	 * 
	 * @param friendlyName
	 *            an friendly name for the client
	 * @param prefix
	 *            the PeerID prefix
	 */
	private TorrentPeerClient(String friendlyName, String prefix) {
		this(friendlyName, prefix, false);
	}

	/**
	 * Initializes a new enum value
	 * 
	 * @param friendlyName
	 *            an friendly name for the client
	 * @param prefix
	 *            the PeerID prefix
	 * @param versioned
	 *            whether the PeerID has an version attribute
	 * 
	 */
	private TorrentPeerClient(String friendlyName, String prefix,
			boolean versioned) {
		this.friendlyName = friendlyName;
		this.prefix = prefix;
		this.versioned = versioned;
	}
}
