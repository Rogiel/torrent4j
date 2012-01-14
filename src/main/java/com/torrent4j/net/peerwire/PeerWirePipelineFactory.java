package com.torrent4j.net.peerwire;

import static io.netty.channel.Channels.pipeline;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineFactory;

import java.util.concurrent.Executor;

import com.torrent4j.TorrentController;
import com.torrent4j.net.peerwire.codec.PeerWireFrameDecoder;
import com.torrent4j.net.peerwire.codec.PeerWireFrameEncoder;
import com.torrent4j.net.peerwire.codec.PeerWireMessageDecoder;
import com.torrent4j.net.peerwire.codec.PeerWireMessageEncoder;
import com.torrent4j.net.peerwire.traffic.TorrentTrafficShapingHandler;
import com.torrent4j.net.peerwire.traffic.PeerTrafficShapingHandler;

public class PeerWirePipelineFactory implements ChannelPipelineFactory {
	private final TorrentController controller;
	private final Executor executor;

	public PeerWirePipelineFactory(TorrentController controller,
			Executor executor) {
		this.controller = controller;
		this.executor = executor;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		final ChannelPipeline p = pipeline();

		p.addLast("torrent-shaper", new TorrentTrafficShapingHandler(executor));
		p.addLast("traffic-shaper", new PeerTrafficShapingHandler(executor));

		p.addLast("frame-decoder", new PeerWireFrameDecoder());
		p.addLast("frame-encoder", new PeerWireFrameEncoder());

		p.addLast("message-decoder", new PeerWireMessageDecoder());
		p.addLast("message-encoder", new PeerWireMessageEncoder());

		// p.addLast("logging", new LoggingHandler(InternalLogLevel.WARN));

		p.addLast("handler", new PeerWireHandler(controller));

		return p;
	}
}
