package com.torrent4j.net.peerwire;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.util.Date;

import com.torrent4j.TorrentController;
import com.torrent4j.model.TorrentPiece;
import com.torrent4j.model.TorrentPieceBlock;
import com.torrent4j.net.peerwire.codec.PeerWireFrameEncoder;
import com.torrent4j.net.peerwire.codec.PeerWireMessageEncoder;
import com.torrent4j.net.peerwire.messages.BlockMessage;
import com.torrent4j.net.peerwire.messages.CancelMessage;
import com.torrent4j.net.peerwire.messages.HandshakeMessage;
import com.torrent4j.net.peerwire.messages.RequestMessage;

public class PeerWireOutboundHandler extends ChannelOutboundHandlerAdapter {
	@SuppressWarnings("unused")
	private final TorrentController controller;
	private PeerWireProtocolPeer peer;

	public PeerWireOutboundHandler(TorrentController controller) {
		this.controller = controller;
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		try {
			if (!(msg instanceof PeerWireMessage))
				return;
			if (msg instanceof HandshakeMessage) {
				promise.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						((PeerWireFrameEncoder) future.channel()
								.pipeline().get("frame-encoder"))
								.setHandshaked(true);
						((PeerWireMessageEncoder) future.channel()
								.pipeline().get("message-encoder"))
								.setHandshaked(true);
					}
				});
			} else if (msg instanceof BlockMessage) {
				final BlockMessage message = (BlockMessage) msg;

				final TorrentPiece piece = peer.getTorrent().getPiece(
						message.pieceIndex);
				final TorrentPieceBlock block = piece.getBlock(message.begin,
						message.data.remaining());

				peer.getTorrentPeer().getState().setLastUploadedBlock(block);
				peer.getTorrentPeer().getState()
						.setLastUploadedBlockDate(new Date());

				promise.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						if (!future.isSuccess())
							return;
						peer.getTorrentPeer().getState()
								.setUploadRequestedBlock(null);
						peer.getTorrentPeer().getState()
								.setUploadRequestedDate(null);
					}
				});
			} else if (msg instanceof RequestMessage) {
				final RequestMessage message = (RequestMessage) msg;

				final TorrentPiece piece = peer.getTorrent().getPiece(
						message.pieceIndex);
				final TorrentPieceBlock block = piece.getBlock(message.begin,
						message.length);

				peer.getTorrentPeer().getState()
						.setDownloadRequestedBlock(block);
				peer.getTorrentPeer().getState()
						.setDownloadRequestedDate(new Date());
			} else if (msg instanceof CancelMessage) {
				promise.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						if (!future.isSuccess())
							return;
						peer.getTorrentPeer().getState()
								.setDownloadRequestedBlock(null);
						peer.getTorrentPeer().getState()
								.setDownloadRequestedDate(null);
					}
				});
			}
		} finally {
			super.write(ctx, msg, promise);
		}
	}
}
