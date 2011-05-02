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

import net.torrent.protocol.peerwire.PeerWireState;
import net.torrent.protocol.peerwire.message.HandshakeMessage;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * Handler used to manage the codecs' state
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PeerWireCodecHandler extends SimpleChannelHandler {
	/**
	 * This connection state. This need to be shared with other encoders,
	 * decoders or handlers. But more importantly <b>NEVER</b> share the same
	 * instance across more than one {@link Channel}.
	 */
	private final PeerWireState state;

	/**
	 * Creates a new instance
	 * 
	 * @param state
	 *            the connection state
	 */
	public PeerWireCodecHandler(final PeerWireState state) {
		this.state = state;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (e.getMessage() instanceof HandshakeMessage) {
			if (state.hasPeerHandshaked())
				throw new IllegalStateException(
						"Peer is trying to handshaked twice");
			state.setPeerHandshaked(true);
		}
		super.messageReceived(ctx, e);
	}

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (e.getMessage() instanceof HandshakeMessage) {
			if (state.hasHandshaked())
				throw new IllegalStateException(
						"Handshake has already been sent");
			e.getFuture().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					state.setHandshaked(true);
				}
			});
		}
		super.writeRequested(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		state.setHandshaked(false);
		state.setPeerHandshaked(false);
	}
}