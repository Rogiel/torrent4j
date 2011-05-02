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

import static org.jboss.netty.channel.Channels.pipeline;
import net.torrent.protocol.peerwire.codec.PeerWireFrameDecoder;
import net.torrent.protocol.peerwire.codec.PeerWireFrameEncoder;
import net.torrent.protocol.peerwire.codec.PeerWireMessageDecoder;
import net.torrent.protocol.peerwire.codec.PeerWireMessageEncoder;
import net.torrent.protocol.peerwire.handler.PeerWireCodecHandler;
import net.torrent.protocol.peerwire.handler.PeerWireManagerHeadHandler;
import net.torrent.protocol.peerwire.handler.PeerWireManagerTailHandler;
import net.torrent.protocol.peerwire.manager.TorrentManager;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;

/**
 * The {@link ChannelPipeline} factory for all PeerWire connections.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PeerWirePipelineFactory implements ChannelPipelineFactory {
	/**
	 * The logging handler
	 */
	private final LoggingHandler loggingHandler = new LoggingHandler(
			InternalLogLevel.INFO);

	/**
	 * The algorithm handler
	 */
	private final ChannelHandler algorithmHandler;
	/**
	 * The head manager handler
	 */
	private final PeerWireManagerHeadHandler headManagerHandler;
	/**
	 * The tail manager handler
	 */
	private final PeerWireManagerTailHandler tailManagerHandler;

	/**
	 * Creates a new instance
	 * 
	 * @param manager
	 *            the torrent manager
	 * @param algorithmHandler
	 *            the algorithm handler
	 */
	public PeerWirePipelineFactory(TorrentManager manager,
			ChannelHandler algorithmHandler) {
		this.algorithmHandler = algorithmHandler;
		this.headManagerHandler = new PeerWireManagerHeadHandler(manager);
		this.tailManagerHandler = new PeerWireManagerTailHandler(manager);
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		final ChannelPipeline pipeline = pipeline();
		final PeerWireState state = new PeerWireState();

		// TODO create traffic shape handler once Netty 4.0 is released
		// TODO create firewall handler - block connections from unwanted peers.

		// pipeline.addLast("old.decoder", new PeerWireOldDecoder(state));
		// pipeline.addLast("old.encoder", new PeerWireOldEncoder());

		// frame (or header) codec
		pipeline.addLast("frame.decoder", new PeerWireFrameDecoder(state));
		pipeline.addLast("frame.encoder", new PeerWireFrameEncoder(state));

		// message codec
		pipeline.addLast("message.decoder", new PeerWireMessageDecoder(state));
		pipeline.addLast("message.encoder", new PeerWireMessageEncoder());

		pipeline.addLast("codec.handler", new PeerWireCodecHandler(state));

		// logging handler (before any other handler can take action)
		pipeline.addLast("logging", loggingHandler);

		// handlers
		pipeline.addLast("head-handler", headManagerHandler);
		pipeline.addLast("algorithm", algorithmHandler);
		pipeline.addLast("tail-handler", tailManagerHandler);

		return pipeline;
	}
}
