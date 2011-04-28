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

import net.torrent.protocol.peerwire.PeerWirePeer;
import net.torrent.protocol.peerwire.manager.TorrentManager;
import net.torrent.protocol.peerwire.message.CancelMessage;
import net.torrent.protocol.peerwire.message.PieceMessage;
import net.torrent.protocol.peerwire.message.RequestMessage;
import net.torrent.protocol.peerwire.message.fast.RejectMessage;
import net.torrent.torrent.Torrent;
import net.torrent.torrent.TorrentPart;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * Handles post-algorithm handler stuff.
 * <p>
 * Updates {@link TorrentManager} state.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PeerWireManagerTailHandler extends SimpleChannelHandler {
	/**
	 * The torrent manager
	 */
	private final TorrentManager manager;

	/**
	 * Creates a new instance
	 * 
	 * @param manager
	 *            the torrent manager
	 */
	public PeerWireManagerTailHandler(TorrentManager manager) {
		this.manager = manager;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		final Object msg = e.getMessage();

		if (msg instanceof PieceMessage) {
			final PieceMessage pieceMsg = (PieceMessage) msg;
			final Torrent torrent = manager.getTorrent();
			final TorrentPart part = torrent.getPart(pieceMsg.getIndex(),
					pieceMsg.getStart(), pieceMsg.getLength());
			manager.getDownloadManager().remove(part);
		} else if (msg instanceof RejectMessage) {
			final RejectMessage reject = (RejectMessage) msg;
			final Torrent torrent = manager.getTorrent();
			final TorrentPart part = torrent.getPart(reject.getIndex(),
					reject.getStart(), reject.getLength());
			manager.getDownloadManager().remove(part);
		}
		super.messageReceived(ctx, e);
	}

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		final PeerWirePeer peer = manager.getPeerManager().getPeer(
				e.getChannel());
		final Object msg = e.getMessage();

		if (msg instanceof PieceMessage) {
			final PieceMessage message = (PieceMessage) msg;
			final Torrent torrent = manager.getContext().getTorrent();
			final TorrentPart part = torrent.getPart(message.getIndex(),
					message.getStart(), message.getLength());
			manager.getUploadManager().add(part, peer.getTorrentPeer());
			e.getFuture().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					manager.getUploadManager().remove(part);
				}
			});
		} else if (msg instanceof RequestMessage) {
			final RequestMessage message = (RequestMessage) msg;
			final Torrent torrent = manager.getContext().getTorrent();
			final TorrentPart part = torrent.getPart(message.getIndex(),
					message.getStart(), message.getLength());
			manager.getDownloadManager().add(part, peer.getTorrentPeer());
		} else if (msg instanceof CancelMessage) {
			final CancelMessage message = (CancelMessage) msg;
			final Torrent torrent = manager.getContext().getTorrent();
			final TorrentPart part = torrent.getPart(message.getIndex(),
					message.getStart(), message.getLength());
			e.getFuture().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					manager.getDownloadManager().remove(part);
				}
			});
		}
		super.writeRequested(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		manager.getConnectionManager().update(e.getChannel());
		final PeerWirePeer peer = manager.getPeerManager().update(
				e.getChannel());
		if (peer.getTorrentPeer() != null) {
			manager.getDownloadManager().remove(peer.getTorrentPeer());
			manager.getUploadManager().remove(peer.getTorrentPeer());
		}
		super.channelDisconnected(ctx, e);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		manager.getConnectionManager().remove(e.getChannel());
		final PeerWirePeer peer = manager.getPeerManager().remove(
				e.getChannel());
		if (peer.getTorrentPeer() != null) {
			manager.getDownloadManager().remove(peer.getTorrentPeer());
			manager.getUploadManager().remove(peer.getTorrentPeer());
		}
		super.channelClosed(ctx, e);
	}
}
