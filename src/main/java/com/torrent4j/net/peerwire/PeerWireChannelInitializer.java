package com.torrent4j.net.peerwire;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.ScheduledExecutorService;

import com.torrent4j.TorrentController;
import com.torrent4j.net.peerwire.codec.PeerWireFrameDecoder;
import com.torrent4j.net.peerwire.codec.PeerWireFrameEncoder;
import com.torrent4j.net.peerwire.codec.PeerWireMessageDecoder;
import com.torrent4j.net.peerwire.codec.PeerWireMessageEncoder;
import com.torrent4j.net.peerwire.traffic.PeerTrafficShapingHandler;
import com.torrent4j.net.peerwire.traffic.TorrentTrafficShapingHandler;

public class PeerWireChannelInitializer extends ChannelInitializer<Channel> {
	private final TorrentController controller;
	private final ScheduledExecutorService executor;

	public PeerWireChannelInitializer(TorrentController controller,
			ScheduledExecutorService executor) {
		this.controller = controller;
		this.executor = executor;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		final ChannelPipeline p = ch.pipeline();
		
		p.addLast("torrent-shaper", new TorrentTrafficShapingHandler(executor));
		p.addLast("traffic-shaper", new PeerTrafficShapingHandler());

		p.addLast("frame-decoder", new PeerWireFrameDecoder());
		p.addLast("frame-encoder", new PeerWireFrameEncoder());

		p.addLast("message-decoder", new PeerWireMessageDecoder());
		p.addLast("message-encoder", new PeerWireMessageEncoder());

		p.addLast("logging", new LoggingHandler(LogLevel.WARN));

		p.addLast("in-handler", new PeerWireInboundHandler(controller));
		p.addLast("out-handler", new PeerWireOutboundHandler(controller));
	}
}
