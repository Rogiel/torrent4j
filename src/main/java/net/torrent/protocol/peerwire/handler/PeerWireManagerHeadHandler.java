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
package net.torrent.protocol.peerwire.handler;

import java.net.InetSocketAddress;

import net.torrent.protocol.peerwire.PeerWirePeer;
import net.torrent.protocol.peerwire.manager.TorrentManager;
import net.torrent.protocol.peerwire.message.HandshakeMessage;
import net.torrent.torrent.context.TorrentPeer;
import net.torrent.torrent.context.TorrentPeerID;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles pre-algoritihm handler stuff.
 * <p>
 * Updates {@link TorrentManager} state.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PeerWireManagerHeadHandler extends SimpleChannelHandler {
	/**
	 * The logger instance
	 */
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * The torrent manager
	 */
	private final TorrentManager manager;

	/**
	 * Creates a new instance
	 * 
	 * @param manager
	 *            the torrent manager
	 */
	public PeerWireManagerHeadHandler(TorrentManager manager) {
		this.manager = manager;
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		manager.getConnectionManager().add(e.getChannel());
		manager.getPeerManager().add(e.getChannel(), null);
		super.channelOpen(ctx, e);
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		if (!manager.getConnectionManager().update(e.getChannel())) {
			e.getChannel().close();
		}
		manager.getPeerManager().update(e.getChannel());
		super.channelConnected(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		final Channel channel = e.getChannel();
		final PeerWirePeer pwpeer = manager.getPeerManager().getPeer(
				e.getChannel());
		final Object msg = e.getMessage();

		if (msg instanceof HandshakeMessage) {
			final HandshakeMessage handshake = (HandshakeMessage) msg;
			final TorrentPeer peer = manager.getContext().getPeer(
					TorrentPeerID.create(handshake.getPeerId()),
					(InetSocketAddress) channel.getRemoteAddress());
			pwpeer.setTorrentPeer(peer);
			peer.setSocketAddress((InetSocketAddress) channel
					.getRemoteAddress());
			peer.getCapabilities().setCapabilities(handshake.getReserved());
			
			log.debug("Handshaked with peer {}", pwpeer);

			// TODO send bitfield
			// if (peer.getCapabilities().supports(
			// TorrentPeerCapability.FAST_PEERS)) {
			// //pwpeer.haveAll();
			// } else {
			pwpeer.bitfield(manager.getContext().getBitfield().getBits());
			// }
		}
		super.messageReceived(ctx, e);
	}
}
