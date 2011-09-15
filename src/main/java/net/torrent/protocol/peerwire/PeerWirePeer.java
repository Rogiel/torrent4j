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
package net.torrent.protocol.peerwire;

import java.nio.ByteBuffer;
import java.util.BitSet;

import net.torrent.protocol.peerwire.codec.PeerWireWritableMessage;
import net.torrent.protocol.peerwire.message.BitfieldMessage;
import net.torrent.protocol.peerwire.message.CancelMessage;
import net.torrent.protocol.peerwire.message.ChokeMessage;
import net.torrent.protocol.peerwire.message.HandshakeMessage;
import net.torrent.protocol.peerwire.message.HaveMessage;
import net.torrent.protocol.peerwire.message.InterestedMessage;
import net.torrent.protocol.peerwire.message.KeepAliveMessage;
import net.torrent.protocol.peerwire.message.NotInterestedMessage;
import net.torrent.protocol.peerwire.message.PieceMessage;
import net.torrent.protocol.peerwire.message.PortMessage;
import net.torrent.protocol.peerwire.message.RequestMessage;
import net.torrent.protocol.peerwire.message.UnchokeMessage;
import net.torrent.protocol.peerwire.message.fast.AllowedFastMessage;
import net.torrent.protocol.peerwire.message.fast.HaveAllMessage;
import net.torrent.protocol.peerwire.message.fast.HaveNoneMessage;
import net.torrent.protocol.peerwire.message.fast.RejectMessage;
import net.torrent.protocol.peerwire.message.fast.SuggestPieceMessage;
import net.torrent.torrent.context.TorrentPeer;
import net.torrent.torrent.context.TorrentPeer.ChokingState;
import net.torrent.torrent.context.TorrentPeer.InterestState;
import net.torrent.torrent.context.TorrentPeerCapabilities.TorrentPeerCapability;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An PeerWire Peer manages the {@link Channel channel} (the current peer
 * connection) and the {@link TorrentPeer torrent peer} object.
 * <p>
 * This object contains handy methods to send messages to the peer.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PeerWirePeer {
	/**
	 * The logger instance
	 */
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * The active {@link Channel}
	 */
	private final Channel channel;
	/**
	 * The active {@link TorrentPeer}. It might no be available until handshake.
	 */
	private TorrentPeer peer;

	/**
	 * Creates a new instance
	 * 
	 * @param channel
	 *            the channel
	 * @param peer
	 *            the peer. Can be null.
	 */
	public PeerWirePeer(Channel channel, TorrentPeer peer) {
		this.channel = channel;
		this.peer = peer;
	}

	/**
	 * Send an handshake message
	 * 
	 * @param infohash
	 *            the info hash
	 * @param peerId
	 *            the peer id
	 * @param reserved
	 *            the capabilities {@link BitSet}
	 * @return the {@link ChannelFuture} for the message
	 */
	public ChannelFuture handshake(byte[] infohash, byte[] peerId,
			BitSet reserved) {
		return write(new HandshakeMessage(infohash, peerId, reserved));
	}

	/**
	 * Send an bitfield message
	 * 
	 * @param bitset
	 *            the bitfield bits
	 * @return the {@link ChannelFuture} for the message
	 */
	public ChannelFuture bitfield(BitSet bitset) {
		return write(new BitfieldMessage(bitset));
	}

	/**
	 * Send an choke message
	 */
	public void choke() {
		if (peer.getChokingState() == ChokingState.CHOKED)
			return;
		log.debug("Chocking peer {}", this);

		write(new ChokeMessage()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					log.debug("Chocked peer {}", this);
					peer.setChokingState(ChokingState.CHOKED);
				}
			}
		});
	}

	/**
	 * Send an unchoke message
	 */
	public void unchoke() {
		if (peer.getChokingState() == ChokingState.UNCHOKED)
			return;
		log.debug("Unchocking peer {}", this);

		write(new UnchokeMessage()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					log.debug("Unchocked peer {}", this);
					peer.setChokingState(ChokingState.UNCHOKED);
				}
			}
		});
	}

	/**
	 * Send an interest message
	 */
	public void interested() {
		if (peer.getInterestState() == InterestState.INTERESTED)
			return;
		log.debug("Informing interest in peer {}", this);

		write(new InterestedMessage()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (future.isSuccess()) {
					log.debug("Interest informed to peer {}", this);
					peer.setInterestState(InterestState.INTERESTED);
				}
			}
		});
	}

	/**
	 * Send an not interest message
	 */
	public void uninterested() {
		if (peer.getInterestState() == InterestState.UNINTERESTED)
			return;
		log.debug("Informing no interest to peer {}", this);

		write(new NotInterestedMessage()).addListener(
				new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						if (future.isSuccess()) {
							log.debug("No interest informed to peer {}", this);
							peer.setInterestState(InterestState.UNINTERESTED);
						}
					}
				});
	}

	/**
	 * Send an request message
	 * 
	 * @param index
	 *            the piece index
	 * @param start
	 *            the part start
	 * @param length
	 *            the part length
	 * @return the {@link ChannelFuture} for this message
	 */
	public ChannelFuture request(int index, int start, int length) {
		log.debug("Requesting piece {} part {} with {} length to peer {}",
				new Object[] { index, start, length, peer });
		return write(new RequestMessage(index, start, length));
	}

	/**
	 * Send an upload message
	 * 
	 * @param index
	 *            the index
	 * @param start
	 *            the start
	 * @param length
	 *            the length
	 * @param data
	 *            the upload {@link ByteBuffer content}
	 */
	public void upload(int index, int start, int length, ByteBuffer data) {
		this.unchoke();
		log.debug("Sending piece {} part {} with {} length to {}, buffer: {}",
				new Object[] { index, start, length, this, data });
		write(new PieceMessage(index, start, length, data));
	}

	/**
	 * Send an have message
	 * 
	 * @param index
	 *            the piece index
	 */
	public void have(int index) {
		log.debug("Notifying have piece {} to peer {}", index, this);
		write(new HaveMessage(index));
	}

	/**
	 * Send an cancel message
	 * 
	 * @param index
	 *            the index
	 * @param start
	 *            the start
	 * @param length
	 *            the length
	 */
	public void cancel(int index, int start, int length) {
		log.debug(
				"Cancelling piece {} reuquest, part {} with {} length from peer {}",
				new Object[] { index, start, length, this });
		write(new CancelMessage(index, start, length));
	}

	/**
	 * Send an keep alive message
	 */
	public void keepAlive() {
		log.debug("Keeping alive peer {}", this);
		write(new KeepAliveMessage());
	}

	// //////////////////////////////////////////////////////////////////////

	/**
	 * *** REQUIRES {@link TorrentPeerCapability#DHT DHT Protocol} ***
	 * <p>
	 * Send and port message. The port is used for UDP.
	 * 
	 * @param port
	 *            the port number
	 */
	public void port(short port) {
		log.debug("Sending DHT port {} to peer {}", port, this);
		write(new PortMessage(port));
	}

	/**
	 * *** REQUIRES {@link TorrentPeerCapability#EXTENSION_PROTOCOL Extension
	 * Protocol} ***
	 * <p>
	 * Send an have none message.
	 */
	public void haveNone() {
		log.debug("Sendind NO-PIECES message to peer {}", this);
		write(new HaveNoneMessage());
	}

	/**
	 * *** REQUIRES {@link TorrentPeerCapability#EXTENSION_PROTOCOL Extension
	 * Protocol} ***
	 * <p>
	 * Send an have all message.
	 */
	public void haveAll() {
		log.debug("Sending HAVE-ALL message to peer {}", this);
		write(new HaveAllMessage());
	}

	/**
	 * *** REQUIRES {@link TorrentPeerCapability#EXTENSION_PROTOCOL Extension
	 * Protocol} ***
	 * <p>
	 * Send an reject message.
	 * 
	 * @param index
	 *            the piece index
	 * @param start
	 *            the part start
	 * @param length
	 *            the part length
	 */
	public void reject(int index, int start, int length) {
		this.choke();
		log.debug(
				"Rejecting part {}, starting at {} with {] length from peer {}",
				new Object[] { index, start, length, this });
		write(new RejectMessage(index, start, length));
	}

	/**
	 * *** REQUIRES {@link TorrentPeerCapability#EXTENSION_PROTOCOL Extension
	 * Protocol} ***
	 * <p>
	 * Send an suggest message
	 * 
	 * @param index
	 *            the piece index
	 */
	public void suggest(int index) {
		log.debug("Suggesting piece {} to peer {}", index, this);
		write(new SuggestPieceMessage(index));
	}

	/**
	 * *** REQUIRES {@link TorrentPeerCapability#EXTENSION_PROTOCOL Extension
	 * Protocol} ***
	 * <p>
	 * Send an list of pieces allowed to download fast (download even when
	 * choked)
	 * 
	 * @param indexes
	 *            the piece indexes
	 */
	public void allowedFast(int... indexes) {
		log.debug("Allowing {} fast pieces to peer {}", indexes.length, this);
		write(new AllowedFastMessage(indexes));
	}

	// //////////////////////////////////////////////////////////////////////

	/**
	 * Write an message to the peer
	 * 
	 * @return an {@link ChannelFuture} to monitor write progress
	 * @see Channel#write(Object)
	 */
	public ChannelFuture write(PeerWireWritableMessage message) {
		return channel.write(message);
	}

	/**
	 * Disconnect the peer
	 * 
	 * @return an {@link ChannelFuture} to monitor disconnect progress
	 * @see Channel#diconnect()
	 */
	public ChannelFuture disconnect() {
		return channel.disconnect();
	}

	/**
	 * Close the peer {@link Channel channel}
	 * 
	 * @return an {@link ChannelFuture} to monitor close progress
	 * @see Channel#close()
	 */
	public ChannelFuture close() {
		return channel.close();
	}

	/**
	 * Get the peer {@link Channel channel}
	 * 
	 * @return the peer active {@link Channel}
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * @return
	 * @see org.jboss.netty.channel.Channel#isOpen()
	 */
	public boolean isConnected() {
		if (channel == null)
			return false;
		return channel.isOpen();
	}

	/**
	 * Get the active {@link TorrentPeer}
	 * 
	 * @return the active peer
	 */
	public TorrentPeer getTorrentPeer() {
		return peer;
	}

	/**
	 * Set the active peer
	 * 
	 * @param peer
	 *            the active peer
	 */
	public void setTorrentPeer(TorrentPeer peer) {
		this.peer = peer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeerWirePeer other = (PeerWirePeer) obj;
		if (channel == null) {
			if (other.channel != null)
				return false;
		} else if (!channel.equals(other.channel))
			return false;
		return true;
	}
}
