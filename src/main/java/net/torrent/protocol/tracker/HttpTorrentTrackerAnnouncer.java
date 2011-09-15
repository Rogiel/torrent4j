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

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.torrent.protocol.tracker.message.AnnounceMessage;
import net.torrent.protocol.tracker.message.AnnounceMessage.Event;
import net.torrent.torrent.Torrent;
import net.torrent.torrent.TorrentTracker;
import net.torrent.torrent.context.TorrentSwarm;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class HttpTorrentTrackerAnnouncer {
	private final ClientBootstrap client = new ClientBootstrap(
			new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()));

	private final TorrentSwarm swarm;
private final Torrent torrent;
	
	public HttpTorrentTrackerAnnouncer(TorrentSwarm swarm) {
		this.swarm = swarm;
		this.torrent = swarm.getContext().getTorrent();
		client.setPipelineFactory(new HttpTorrentTrackerPipelineFactory(swarm));
	}

	public boolean announce(TorrentTracker tracker)
			throws UnsupportedEncodingException, MalformedURLException {
		final AnnounceMessage announceMessage = new AnnounceMessage(tracker
				.getURL().toString(), torrent.getInfoHash().toByteArray(),
				torrent.getInfoHash().toByteArray(), 10254, 0, 0, 0, true,
				false, Event.STARTED);
		int port = (tracker.getURL().getPort() > 0 ? tracker.getURL().getPort()
				: tracker.getURL().getDefaultPort());
		final ChannelFuture chFuture = client.connect(new InetSocketAddress(
				tracker.getURL().getHost(), port));
		chFuture.awaitUninterruptibly(60, TimeUnit.SECONDS);
		if (!chFuture.isSuccess())
			return false;
		return chFuture.getChannel().write(announceMessage.write())
				.awaitUninterruptibly(60, TimeUnit.SECONDS);
	}
}
