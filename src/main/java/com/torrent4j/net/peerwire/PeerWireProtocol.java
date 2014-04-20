package com.torrent4j.net.peerwire;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.torrent4j.TorrentController;
import com.torrent4j.model.peer.TorrentPeer;
import com.torrent4j.net.TorrentProtocol;

public class PeerWireProtocol implements TorrentProtocol {
	private final ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(1);

	private final EventLoopGroup bossGroup = new NioEventLoopGroup();
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();

	private final ServerBootstrap serverBootstrap = new ServerBootstrap();
	private final Bootstrap clientBootstrap = new Bootstrap();

	private NioServerSocketChannel serverChannel;

	@Override
	public void start(TorrentController controller, int listenPort) {
		serverBootstrap
				.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.localAddress(listenPort)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childHandler(
						new PeerWireChannelInitializer(controller, threadPool)).validate();
		clientBootstrap
				.group(workerGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100)
				//.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new PeerWireChannelInitializer(controller, threadPool)).validate();

		try {
			serverChannel = (NioServerSocketChannel) serverBootstrap.bind(new InetSocketAddress(
					Inet4Address.getByName("0.0.0.0"), listenPort)).channel();
		} catch (UnknownHostException e) {
		}
	}

	@Override
	public void stop() {
		serverChannel.close();
	}

	@Override
	public boolean connect(TorrentPeer peer) {
		final ChannelFuture future = clientBootstrap.connect(peer.getAddress())
				.awaitUninterruptibly();
		if (future.isSuccess()) {
			final PeerWireProtocolPeer protocolPeer = new PeerWireProtocolPeer(
					future.channel());
			protocolPeer.setTorrentPeer(peer);
			peer.setProtocolPeer(protocolPeer);
			return true;
		} else {
			return false;
		}
	}
}
