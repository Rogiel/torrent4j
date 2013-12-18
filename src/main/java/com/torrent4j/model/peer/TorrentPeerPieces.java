package com.torrent4j.model.peer;

import java.util.BitSet;

import com.torrent4j.model.AbstractTorrentPiecesContainer;
import com.torrent4j.net.peerwire.messages.BitFieldMessage;
import com.torrent4j.net.peerwire.messages.HaveMessage;

/**
 * This class represents a set of pieces that a given peer has on their side.
 * Please note that some peers make use of a delayed bitfield, which sets random
 * pieces of the bitfield set on {@link BitFieldMessage} and the rest is
 * received with {@link HaveMessage}s. This is called "lazy bitfield".
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class TorrentPeerPieces extends AbstractTorrentPiecesContainer {
	private final TorrentPeer peer;

	/**
	 * Loads an existing bitset to this container
	 * 
	 * @param bitSet
	 *            the bitset
	 */
	public void load(BitSet bitSet) {
		this.bitSet.set(0, this.bitSet.size(), false);
		for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
			this.bitSet.set(i, true);
		}
	}

	public TorrentPeerPieces(TorrentPeer peer) {
		super(peer.getTorrent());
		this.peer = peer;
	}

	public TorrentPeer getPeer() {
		return peer;
	}
}
