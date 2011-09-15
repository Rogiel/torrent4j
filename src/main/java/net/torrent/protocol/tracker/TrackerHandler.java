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
package net.torrent.protocol.tracker;

import java.net.Inet4Address;
import java.net.InetSocketAddress;

import net.torrent.protocol.tracker.message.PeerListMessage;
import net.torrent.protocol.tracker.message.PeerListMessage.PeerInfo;
import net.torrent.torrent.context.TorrentPeer;
import net.torrent.torrent.context.TorrentPeerID;
import net.torrent.torrent.context.TorrentSwarm;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class TrackerHandler extends SimpleChannelHandler {
	private final TorrentSwarm swarm;

	/**
	 * @param torrent
	 */
	public TrackerHandler(TorrentSwarm swarm) {
		this.swarm = swarm;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (e.getMessage() instanceof PeerListMessage) {
			final PeerListMessage message = (PeerListMessage) e.getMessage();

			for (final PeerInfo peerInfo : message.getPeerList()) {
				final TorrentPeer peer = new TorrentPeer(swarm.getContext(),
						TorrentPeerID.create(peerInfo.getPeerId()),
						new InetSocketAddress(Inet4Address.getByName(peerInfo
								.getIp()), peerInfo.getPort()));
				swarm.add(peer);
			}
		}
		super.messageReceived(ctx, e);
	}
}
