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
package net.torrent.protocol.tracker.codec;

import java.io.ByteArrayInputStream;

import net.torrent.util.bencoding.BEncodedInputStream;
import net.torrent.util.bencoding.BMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class TorrentTrackerBDecoder extends OneToOneDecoder {
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (!(msg instanceof HttpChunk))
			return msg;
		final HttpChunk message = (HttpChunk) msg;

		System.out.println(new String(message.getContent().array()));
		if (message.getContent().readableBytes() <= 0)
			return null;

		final BEncodedInputStream in = new BEncodedInputStream(
				new ByteArrayInputStream(message.getContent().array()));
		final BMap map = (BMap) in.readElement();
		return map;
	}
}
