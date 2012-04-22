package com.torrent4j.net.peerwire;

import io.netty.bootstrap.ClientBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioClientSocketChannelFactory;
import io.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.torrent4j.TorrentController;
import com.torrent4j.model.peer.TorrentPeer;
import com.torrent4j.net.TorrentProtocol;

public class PeerWireProtocol implements TorrentProtocol {
	private final Executor threadPool = Executors.newCachedThreadPool();

	private final ServerBootstrap serverBootstrap = new ServerBootstrap(
			new NioServerSocketChannelFactory(threadPool, threadPool));
	private final ClientBootstrap clientBootstrap = new ClientBootstrap(
			new NioClientSocketChannelFactory(threadPool, threadPool));

	private Channel serverChannel;

	@Override
	public void start(TorrentController controller, int listenPort) {
		serverBootstrap.setPipelineFactory(new PeerWirePipelineFactory(
				controller, threadPool));
		clientBootstrap.setPipelineFactory(new PeerWirePipelineFactory(
				controller, threadPool));

		try {
			serverChannel = serverBootstrap.bind(new InetSocketAddress(
					Inet4Address.getByName("0.0.0.0"), listenPort));
		} catch (UnknownHostException e) {
		}
	}

	@Override
	public void stop() {
		clientBootstrap.releaseExternalResources();
		serverChannel.close();
		serverBootstrap.releaseExternalResources();
	}

	@Override
	public boolean connect(TorrentPeer peer) {
		final ChannelFuture future = clientBootstrap.connect(peer.getAddress())
				.awaitUninterruptibly();
		if (future.isSuccess()) {
			final PeerWireProtocolPeer protocolPeer = new PeerWireProtocolPeer(
					future.getChannel());
			protocolPeer.setTorrentPeer(peer);
			peer.setProtocolPeer(protocolPeer);
			return true;
		} else {
			return false;
		}
	}
}
