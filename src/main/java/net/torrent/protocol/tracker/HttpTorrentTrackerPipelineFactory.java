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

import static org.jboss.netty.channel.Channels.pipeline;
import net.torrent.protocol.tracker.codec.ISO8859HttpRequestEncoder;
import net.torrent.protocol.tracker.codec.TorrentTrackerBDecoder;
import net.torrent.protocol.tracker.codec.TorrentTrackerDecoder;
import net.torrent.protocol.tracker.codec.TorrentTrackerEncoder;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;

public class HttpTorrentTrackerPipelineFactory implements
		ChannelPipelineFactory {
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		final ChannelPipeline pipeline = pipeline();

		// log binary data input and object output
		// pipeline.addFirst("logging", new LoggingHandler());

		pipeline.addLast("tracker.encoder", new TorrentTrackerEncoder());
		pipeline.addLast("encoder", new ISO8859HttpRequestEncoder());

		pipeline.addLast("interceptor", new Interceptor());

		pipeline.addLast("decoder", new HttpResponseDecoder());
		pipeline.addLast("bdecoder", new TorrentTrackerBDecoder());
		pipeline.addLast("tracker.decoder", new TorrentTrackerDecoder());

		pipeline.addLast("handler", new TrackerHandler());

		pipeline.addLast("logging", new LoggingHandler(InternalLogLevel.WARN));

		return pipeline;
	}
}
