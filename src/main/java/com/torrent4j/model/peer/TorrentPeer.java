package com.torrent4j.model.peer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPiece;
import com.torrent4j.model.TorrentPieceBlock;
import com.torrent4j.net.TorrentProtocolPeer;

/**
 * Represents a peer on the BitTorrent network. This object contains information
 * necessary to locate and connect to a peer.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class TorrentPeer {
	/**
	 * The parent torrent for this peer
	 */
	private final Torrent torrent;
	/**
	 * The pieces this peer has complete
	 */
	private final TorrentPeerPieces pieces;

	/**
	 * The peer ID
	 */
	private TorrentPeerID peerID;

	/**
	 * The protocol peer used to communicate with it
	 */
	private TorrentProtocolPeer protocolPeer;
	/**
	 * The socket address
	 */
	private InetSocketAddress address;
	/**
	 * Whether the peer can be contacted directly (has public accessible IP)
	 */
	private boolean accessible;

	/**
	 * This peer's traffic control
	 */
	private final TorrentPeerTrafficControl trafficControl = new TorrentPeerTrafficControl(
			this);
	/**
	 * The peer state
	 */
	private TorrentPeerState state = new TorrentPeerState(this);

	/**
	 * Creates a new instance
	 * 
	 * @param torrent
	 *            the torrent
	 */
	public TorrentPeer(Torrent torrent) {
		this.torrent = torrent;
		this.pieces = new TorrentPeerPieces(this);
	}

	public TorrentPeerPieces getPieces() {
		return pieces;
	}

	public TorrentPeerID getPeerID() {
		return peerID;
	}

	public void setPeerID(TorrentPeerID peerID) {
		this.peerID = peerID;
	}

	public void setPeerID(String peerID) {
		this.peerID = new TorrentPeerID(this, peerID);
	}

	/**
	 * Tries to detect the peer's client. If unknown,
	 * {@link TorrentPeerClient#UNKNOWN} is returned.
	 * 
	 * @return the peer torrent client
	 */
	public TorrentPeerClient getClient() {
		return peerID.getClient();
	}

	/**
	 * @return <code>true</code> if the client version is known
	 */
	public boolean isClientVersionKnown() {
		return peerID.isClientVersionKnown();
	}

	/**
	 * @return the client version as by the PeerID
	 */
	public String getClientVersion() {
		return peerID.getClientVersion();
	}

	public TorrentProtocolPeer getProtocolPeer() {
		return protocolPeer;
	}

	public void setProtocolPeer(TorrentProtocolPeer protocolPeer) {
		this.protocolPeer = protocolPeer;
	}

	public boolean connect() {
		if (protocolPeer == null) {
			if (torrent.getProtocol() == null)
				return false;
			return torrent.getProtocol().connect(this);
		} else {
			return protocolPeer.connect();
		}
	}

	public boolean isConnectable() {
		return address != null;
	}

	public boolean isConnected() {
		if (protocolPeer == null)
			return false;
		return protocolPeer.isConnected();
	}

	public boolean disconnect() {
		if (protocolPeer == null)
			return false;
		return protocolPeer.disconnect();
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

	public final InetAddress getIP() {
		return address.getAddress();
	}

	public final int getPort() {
		return address.getPort();
	}

	public final String getHostName(boolean resolve) {
		return resolve ? address.getHostName() : address.getHostString();
	}

	public final String getHostName() {
		return getHostName(false);
	}

	public boolean isAccessible() {
		return accessible;
	}

	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}

	/**
	 * @return the traffic shaper
	 */
	public TorrentPeerTrafficControl getTrafficControl() {
		return trafficControl;
	}

	public TorrentPeerState getState() {
		return state;
	}

	public void resetState() {
		state = new TorrentPeerState(this);
	}

	// NETWORK RELATED THINGS!
	public void handshake() {
		// FIXME send another peer id
		protocolPeer.handshake(torrent.getHash().getHash(), torrent
				.getController().getConfig().getPeerID());
	}

	public void declareInterest() {
		if (state.isLocalllyInterested())
			return;
		protocolPeer.interested();
		state.setLocalInterest(TorrentPeerInterest.INTERESTED);
	}

	public void withdrawInterest() {
		if (!state.isLocalllyInterested())
			return;
		protocolPeer.notInterested();
		state.setLocalInterest(TorrentPeerInterest.NOT_INTERESTED);
	}

	public void choke() {
		if (state.isLocallyChoked())
			return;
		protocolPeer.choke();
		state.setLocallyChoked(TorrentPeerChoking.CHOKED);
	}

	public void unchoke() {
		if (!state.isLocallyChoked())
			return;
		protocolPeer.unchoke();
		state.setLocallyChoked(TorrentPeerChoking.UNCHOKED);
	}

	public void have(TorrentPiece piece) {
		protocolPeer.have(piece.getIndex());
	}

	public void bitField() {
		protocolPeer.bitField(torrent.getCompletedPieces().getBitSet());
	}

	public void requestBlock(TorrentPieceBlock block) {
		protocolPeer.requestBlock(block.getPiece().getIndex(),
				block.getOffset(), block.getLength());
	}

	public void cancelRequestedBlock(TorrentPieceBlock block) {
		protocolPeer.cancelRequestedBlock(block.getPiece().getIndex(),
				block.getOffset(), block.getLength());
	}

	public void sendBlock(TorrentPieceBlock block, ByteBuffer data) {
		protocolPeer.sendBlock(block.getPiece().getIndex(), block.getOffset(),
				data);
	}

	public void sendPort(int port) {
		protocolPeer.port(port);
	}

	public void keepAlive() {
		protocolPeer.keepAlive();
	}

	public Torrent getTorrent() {
		return torrent;
	}

	@Override
	public String toString() {
		return "TorrentPeer [torrent=" + torrent + ", address=" + address + "]";
	}
}
