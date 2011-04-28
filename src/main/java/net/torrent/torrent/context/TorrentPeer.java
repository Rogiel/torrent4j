/*
 * Copyright 2011 Rogiel Josias Sulzbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.torrent.torrent.context;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * Object representing a peer in the swarm.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentPeer {
	/**
	 * The torrent context
	 */
	private final TorrentContext context;
	/**
	 * The peer id
	 */
	private final TorrentPeerID peerID;

	/**
	 * Set of peer capabilities
	 */
	private final TorrentPeerCapabilities capabilities = new TorrentPeerCapabilities();

	/**
	 * The bitfield of the peer
	 */
	private final TorrentBitfield bitfield;
	/**
	 * Local choking state to this peer. By default is
	 * {@link ChokingState#CHOKED choked}.
	 */
	private ChokingState chokingState = ChokingState.CHOKED;
	/**
	 * Remote choking state of this peer. By default is
	 * {@link ChokingState#CHOKED choked}.
	 */
	private ChokingState peerChokingState = ChokingState.CHOKED;

	/**
	 * The choking states
	 * 
	 * @author Rogiel Josias Sulzbach (<a
	 *         href="http://www.rogiel.com/">http://www.rogiel.com/</a>)
	 */
	public enum ChokingState {
		/**
		 * Choked: communication is "blocked". No pieces can be requested nor
		 * sent. By stantard, this is the default interest once the connection
		 * is made.
		 */
		CHOKED,
		/**
		 * Unchoked: communication is "open": pieces can be requested and
		 * uploaded.
		 */
		UNCHOKED;
	}

	/**
	 * Local interest to this peer. By default is
	 * {@link InterestState#UNINTERESTED not interested}
	 */
	private InterestState interestState = InterestState.UNINTERESTED;
	/**
	 * Remote interest of this peer. By default is
	 * {@link InterestState#UNINTERESTED not interested}
	 */
	private InterestState peerInterestState = InterestState.UNINTERESTED;

	/**
	 * The interest states
	 * 
	 * @author Rogiel Josias Sulzbach (<a
	 *         href="http://www.rogiel.com/">http://www.rogiel.com/</a>)
	 */
	public enum InterestState {
		/**
		 * The peer is interested in the other pieces. If the peer is willing to
		 * upload, the remote peer is unchoked and data transfer can begin.
		 */
		INTERESTED,

		/**
		 * The peer has not interest in the remote peer pieces. By stantard,
		 * this is the default interest once the connection is made.
		 */
		UNINTERESTED;
	}

	/**
	 * The peer socket address. For incoming connections.
	 */
	private InetSocketAddress socketAddress;
	/**
	 * Last time the peer was found online.
	 */
	private Date lastReached = null;
	/**
	 * State of the peer connectivity. Peer behind firewall or NAT will have
	 * false. By default all peers are accessible.
	 */
	private boolean accessible = true;

	/**
	 * Creates a new peer
	 * 
	 * @param context
	 *            the torrent context
	 * @param peerID
	 *            the peer id
	 * @param socketAddress
	 *            the peer address
	 */
	public TorrentPeer(TorrentContext context, TorrentPeerID peerID,
			InetSocketAddress socketAddress) {
		if (peerID == null && socketAddress == null)
			throw new IllegalArgumentException(
					"PeerID or SocketAddress must be not null");
		if (context == null)
			throw new IllegalArgumentException("Context is null");

		this.context = context;
		this.peerID = peerID;
		this.socketAddress = socketAddress;
		this.bitfield = new TorrentBitfield(context);
	}

	/**
	 * Get the peer id
	 * 
	 * @return the peer id
	 */
	public TorrentPeerID getPeerID() {
		return peerID;
	}

	/**
	 * Set the peer address
	 * 
	 * @param address
	 *            the address
	 */
	public void setSocketAddress(InetSocketAddress address) {
		this.socketAddress = address;
	}

	/**
	 * Get the peer address
	 * 
	 * @return the address
	 */
	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}

	/**
	 * Get the peer bitfield
	 * 
	 * @return the bitfield
	 */
	public TorrentBitfield getBitfield() {
		return bitfield;
	}

	/**
	 * Get the peer capabilities
	 * 
	 * @return the capabilities
	 */
	public TorrentPeerCapabilities getCapabilities() {
		return capabilities;
	}

	/**
	 * Get the local choking state to this peer. By default is
	 * {@link ChokingState#CHOKED choked}.
	 * 
	 * @return the choking state
	 */
	public ChokingState getChokingState() {
		return chokingState;
	}

	/**
	 * Set the local choking state to this peer. By default is
	 * {@link ChokingState#CHOKED choked}.
	 * 
	 * @param chokingState
	 *            the choking state
	 */
	public void setChokingState(ChokingState chokingState) {
		this.chokingState = chokingState;
	}

	/**
	 * 
	 * Get the remote choking state of this peer. By default is
	 * {@link ChokingState#CHOKED choked}.
	 * 
	 * @return the peer choking state
	 */
	public ChokingState getPeerChokingState() {
		return peerChokingState;
	}

	/**
	 * Set the remote choking state of this peer. By default is
	 * {@link ChokingState#CHOKED choked}.
	 * 
	 * @param peerChokingState
	 *            the peer choking state
	 */
	public void setPeerChokingState(ChokingState peerChokingState) {
		this.peerChokingState = peerChokingState;
	}

	/**
	 * Get the local interest to this peer. By default is
	 * {@link InterestState#UNINTERESTED not interested}
	 * 
	 * @return the local interest
	 */
	public InterestState getInterestState() {
		return interestState;
	}

	/**
	 * Set the local interest to this peer. By default is
	 * {@link InterestState#UNINTERESTED not interested}
	 * 
	 * @param interestState
	 *            the local interest
	 */
	public void setInterestState(InterestState interestState) {
		this.interestState = interestState;
	}

	/**
	 * Get the peer remote interest of this peer. By default is
	 * {@link InterestState#UNINTERESTED not interested}
	 * 
	 * @return the peer remote interest
	 */
	public InterestState getPeerInterestState() {
		return peerInterestState;
	}

	/**
	 * Set the peer remote interest of this peer. By default is
	 * {@link InterestState#UNINTERESTED not interested}
	 * 
	 * @param peerInterestState
	 *            the peer remote interest
	 */
	public void setPeerInterestState(InterestState peerInterestState) {
		this.peerInterestState = peerInterestState;
	}

	/**
	 * Get the last time the peer has been reached by an connection
	 * 
	 * @return the date of last connection
	 */
	public Date getLastReached() {
		return lastReached;
	}

	/**
	 * Set the last time the peer has been reached by an connection
	 * 
	 * @param lastReached
	 *            the date of last connection
	 */
	public void setLastReached(Date lastReached) {
		this.lastReached = lastReached;
	}

	/**
	 * State of the peer connectivity. Peer behind firewall or NAT will have
	 * false. By default all peers are accessible.
	 * 
	 * @return true if is accessible, false otherwise.
	 */
	public boolean isAccessible() {
		return accessible;
	}

	/**
	 * State of the peer connectivity. Peer behind firewall or NAT will have
	 * false. By default all peers are accessible.
	 * 
	 * @param accessible
	 *            true if is accessible, false otherwise.
	 */
	public void setAccessible(boolean accessible) {
		this.accessible = accessible;
	}

	/**
	 * Get the torrent context
	 * 
	 * @return the torrent context
	 */
	public TorrentContext getContext() {
		return context;
	}

	/**
	 * Create an clone of this peer with the new {@link TorrentPeerID id}.
	 * 
	 * @param id
	 *            the new peer id
	 * @return the new peer with id.
	 */
	public TorrentPeer createWithID(TorrentPeerID id) {
		return new TorrentPeer(context, id, socketAddress);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TorrentPeer other = (TorrentPeer) obj;
		if (peerID == null) {
			if (other.peerID != null)
				return false;
			if (socketAddress == null) {
				if (other.socketAddress != null)
					return false;
			} else if (!socketAddress.equals(other.socketAddress))
				return false;
		} else if (!peerID.equals(other.peerID))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (peerID != null) {
			result = prime * result + peerID.hashCode();
		} else if (socketAddress != null) {
			result = prime * result + socketAddress.hashCode();
		}
		return result;
	}
}
