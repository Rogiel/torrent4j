package com.torrent4j.model.peer;

import com.torrent4j.net.peerwire.messages.HandshakeMessage;

/**
 * Enlists all recognized extensions for the bittorrent protocol
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public enum TorrentPeerCapability {
	/**
	 * These extensions serve multiple purposes. They allow a peer to more
	 * quickly bootstrap into a swarm by giving a peer a specific set of pieces
	 * which they will be allowed download regardless of choked status. They
	 * reduce message overhead by adding HaveAll and HaveNone messages and allow
	 * explicit rejection of piece requests whereas previously only implicit
	 * rejection was possible meaning that a peer might be left waiting for a
	 * piece that would never be delivered.
	 */
	FAST_PEERS(62),

	/**
	 * This extension is to allow for the tracking of peers downloading torrents
	 * without the use of a standard tracker. A peer implementing this protocol
	 * becomes a "tracker" and stores lists of other nodes/peers which can be
	 * used to locate new peers.
	 */
	DHT(64),

	/**
	 * A protocol in its own right - if two clients indicate they support the
	 * protocol, then they should switch over to using it. It allows normal
	 * BitTorrent as well extension messages to be sent over it. Currently
	 * implemented by Azureus and Transmission.
	 * <p>
	 * It is not possible to use both this protocol and the LibTorrent extension
	 * protocol at the same time - if both clients indicate they support both,
	 * then they should follow the semantics defined by the Extension
	 * Negotiation Protocol.
	 */
	AZUREUS_MESSAGING_PROTOCOL(1),

	/**
	 * This is a protocol for exchanging extension information and was derived
	 * from an early version of Azureus' extension protocol. It adds one message
	 * for exchanging arbitrary handshake information including defined
	 * extension messages, mapping extensions to specific message IDs.
	 */
	EXTENSION_PROTOCOL(44),

	/**
	 * These bits are used to allow two clients that support both the Azureus
	 * Messaging Protocol and LibTorrent's extension protocol to decide which of
	 * the two extensions should be used for communication.
	 * <p>
	 * This bit should always be set along with this
	 * {@link TorrentPeerCapability#EXTENSION_NEGOTIATION_PROTOCOL_2}
	 * 
	 * @see TorrentPeerCapability#EXTENSION_NEGOTIATION_PROTOCOL_2
	 */
	EXTENSION_NEGOTIATION_PROTOCOL_1(47),
	/**
	 * These bits are used to allow two clients that support both the Azureus
	 * Messaging Protocol and LibTorrent's extension protocol to decide which of
	 * the two extensions should be used for communication.
	 * <p>
	 * This bit should always be set along with this
	 * {@link TorrentPeerCapability#EXTENSION_NEGOTIATION_PROTOCOL_1}
	 * 
	 * @see TorrentPeerCapability#EXTENSION_NEGOTIATION_PROTOCOL_1
	 */
	EXTENSION_NEGOTIATION_PROTOCOL_2(48),

	/**
	 * A Protocol, considering peers location (in geographical terms) for better
	 * performance.
	 */
	LOCATION_AWARE_PROTOCOL(48);

	/**
	 * This bit is stored on the {@link HandshakeMessage#reserved} long. Those
	 * set of bits specify which BitTorrent extensions are known and compatible
	 * with the client. If any given extension is supported by both clients, it
	 * is enabled by default.
	 */
	public final int bit;

	/**
	 * Creates a new enum instance
	 * 
	 * @param bit
	 *            the bit
	 */
	private TorrentPeerCapability(int bit) {
		this.bit = bit;
	}
}
