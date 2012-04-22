package com.torrent4j.net.peerwire;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MessageEvent;
import io.netty.channel.SimpleChannelHandler;

import java.net.InetSocketAddress;
import java.util.Date;

import com.torrent4j.TorrentController;
import com.torrent4j.model.Torrent;
import com.torrent4j.model.peer.TorrentPeer;
import com.torrent4j.model.peer.TorrentPeerChoking;
import com.torrent4j.model.peer.TorrentPeerInterest;
import com.torrent4j.model.TorrentPiece;
import com.torrent4j.model.TorrentPieceBlock;
import com.torrent4j.net.peerwire.codec.PeerWireFrameDecoder;
import com.torrent4j.net.peerwire.codec.PeerWireFrameEncoder;
import com.torrent4j.net.peerwire.codec.PeerWireMessageDecoder;
import com.torrent4j.net.peerwire.codec.PeerWireMessageEncoder;
import com.torrent4j.net.peerwire.messages.BitFieldMessage;
import com.torrent4j.net.peerwire.messages.BlockMessage;
import com.torrent4j.net.peerwire.messages.CancelMessage;
import com.torrent4j.net.peerwire.messages.ChokeMessage;
import com.torrent4j.net.peerwire.messages.HandshakeMessage;
import com.torrent4j.net.peerwire.messages.HaveMessage;
import com.torrent4j.net.peerwire.messages.InterestedMessage;
import com.torrent4j.net.peerwire.messages.NotInterestedMessage;
import com.torrent4j.net.peerwire.messages.RequestMessage;
import com.torrent4j.net.peerwire.messages.UnchokeMessage;
import com.torrent4j.net.peerwire.traffic.PeerTrafficShapingHandler;
import com.torrent4j.net.peerwire.traffic.TorrentTrafficShapingHandler;
import com.torrent4j.util.Hash;
import com.torrent4j.util.HashType;

public class PeerWireHandler extends SimpleChannelHandler {
	private final TorrentController controller;
	private PeerWireProtocolPeer peer;

	public PeerWireHandler(TorrentController controller) {
		this.controller = controller;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		try {
			final Object msg = e.getMessage();
			if (!(msg instanceof PeerWireMessage))
				return;
			if (msg instanceof HandshakeMessage) {
				final HandshakeMessage message = (HandshakeMessage) msg;

				((PeerWireFrameDecoder) e.getChannel().getPipeline()
						.get("frame-decoder")).setHandshaked(true);
				((PeerWireMessageDecoder) e.getChannel().getPipeline()
						.get("message-decoder")).setHandshaked(true);

				final Hash hash = new Hash(HashType.SHA1, message.torrentHash);
				final Torrent torrent = controller.findTorrent(hash);

				if (torrent == null) {
					e.getChannel().disconnect();
					return;
				}

				TorrentPeer peer = torrent.getSwarm().findPeer(
						(InetSocketAddress) e.getChannel().getRemoteAddress(),
						message.peerID);
				if (peer == null) {
					peer = new TorrentPeer(torrent);
					peer.setAddress((InetSocketAddress) e.getChannel()
							.getRemoteAddress());
				}
				peer.setPeerID(message.peerID);
				
				this.peer = (PeerWireProtocolPeer) peer.getProtocolPeer();

				e.getChannel().getPipeline()
						.get(PeerTrafficShapingHandler.class).setPeer(peer);
				e.getChannel().getPipeline()
						.get(TorrentTrafficShapingHandler.class)
						.setTorrent(torrent);

				peer.resetState();
				this.peer.getStrategy().getPeerStrategy()
						.peerConnected(torrent, peer);
			} else if (msg instanceof HaveMessage) {
				peer.getTorrentPeer()
						.getPieces()
						.addPiece(
								peer.getTorrent().getPiece(
										((HaveMessage) msg).pieceIndex));
				peer.getStrategy()
						.getPeerStrategy()
						.havePiece(
								peer.getTorrent(),
								peer.getTorrentPeer(),
								peer.getTorrent().getPiece(
										((HaveMessage) msg).pieceIndex));
			} else if (msg instanceof UnchokeMessage) {
				peer.getTorrentPeer().getState()
						.setRemoteChoked(TorrentPeerChoking.UNCHOKED);
				peer.getStrategy().getPeerStrategy()
						.unchoked(peer.getTorrent(), peer.getTorrentPeer());
			} else if (msg instanceof ChokeMessage) {
				peer.getTorrentPeer().getState()
						.setRemoteChoked(TorrentPeerChoking.CHOKED);
				peer.getStrategy().getPeerStrategy()
						.choked(peer.getTorrent(), peer.getTorrentPeer());
			} else if (msg instanceof InterestedMessage) {
				peer.getTorrentPeer().getState()
						.setRemoteInterest(TorrentPeerInterest.INTERESTED);
				peer.getStrategy().getPeerStrategy()
						.interested(peer.getTorrent(), peer.getTorrentPeer());
			} else if (msg instanceof NotInterestedMessage) {
				peer.getTorrentPeer().getState()
						.setRemoteInterest(TorrentPeerInterest.NOT_INTERESTED);
				peer.getStrategy()
						.getPeerStrategy()
						.notInterested(peer.getTorrent(), peer.getTorrentPeer());
			} else if (msg instanceof BitFieldMessage) {
				peer.getTorrentPeer().getPieces()
						.load(((BitFieldMessage) msg).bitSet);
				peer.getStrategy().getPeerStrategy()
						.bitField(peer.getTorrent(), peer.getTorrentPeer());
			} else if (msg instanceof RequestMessage) {
				final RequestMessage message = (RequestMessage) msg;

				final TorrentPiece piece = peer.getTorrent().getPiece(
						message.pieceIndex);
				final TorrentPieceBlock block = piece.getBlock(message.begin,
						message.length);
				
				if(peer.getTorrentPeer().getState().hasUploadRequestedBlock()) {
					peer.disconnect();
					return;
				}

				peer.getTorrentPeer().getState().setUploadRequestedBlock(block);
				peer.getTorrentPeer().getState()
						.setUploadRequestedDate(new Date());

				peer.getStrategy()
						.getUploadStrategy()
						.blockRequested(peer.getTorrent(), block,
								peer.getTorrentPeer());
			} else if (msg instanceof CancelMessage) {
				final CancelMessage message = (CancelMessage) msg;

				final TorrentPiece piece = peer.getTorrent().getPiece(
						message.pieceIndex);
				final TorrentPieceBlock block = piece.getBlock(message.begin,
						message.length);

				peer.getTorrentPeer().getState().setUploadRequestedBlock(null);
				peer.getTorrentPeer().getState().setUploadRequestedDate(null);

				peer.getStrategy()
						.getUploadStrategy()
						.blockRequestCancelled(peer.getTorrent(), block,
								peer.getTorrentPeer());
			} else if (msg instanceof BlockMessage) {
				final BlockMessage message = (BlockMessage) msg;

				final TorrentPiece piece = peer.getTorrent().getPiece(
						message.pieceIndex);
				final TorrentPieceBlock block = piece.getBlock(message.begin,
						message.data.remaining());

				peer.getTorrentPeer().getState().setLastDownloadedBlock(block);
				peer.getTorrentPeer().getState()
						.setLastDownloadedBlockDate(new Date());

				peer.getTorrentPeer().getState()
						.setDownloadRequestedBlock(null);
				peer.getTorrentPeer().getState().setDownloadRequestedDate(null);

				controller.getStorage().write(piece.getTorrent(),
						block.getTorrentRange(), message.data);
				block.setDownloaded(true);
				if (piece.isDownloaded()) {
					final Hash pieceHash = controller.getStorage().checksum(
							piece);
					if (!piece.getHash().equals(pieceHash)) {
						piece.getTorrent()
								.getStrategy()
								.getDownloadStrategy()
								.pieceChecksumFailed(piece.getTorrent(), piece,
										peer.getTorrentPeer());
					} else {
						piece.getTorrent().getCompletedPieces().addPiece(piece);
						piece.getTorrent()
								.getStrategy()
								.getDownloadStrategy()
								.pieceComplete(piece.getTorrent(), piece,
										peer.getTorrentPeer());
					}
				} else {
					piece.getTorrent()
							.getStrategy()
							.getDownloadStrategy()
							.blockReceived(block.getTorrent(), block,
									peer.getTorrentPeer());
				}
			} else {
				System.out.println(msg);
			}
		} finally {
			ctx.sendUpstream(e);
		}
	}

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		try {
			final Object msg = e.getMessage();
			if (!(msg instanceof PeerWireMessage))
				return;
			if (msg instanceof HandshakeMessage) {
				e.getFuture().addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						((PeerWireFrameEncoder) future.getChannel()
								.getPipeline().get("frame-encoder"))
								.setHandshaked(true);
						((PeerWireMessageEncoder) future.getChannel()
								.getPipeline().get("message-encoder"))
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

				e.getFuture().addListener(new ChannelFutureListener() {
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
				e.getFuture().addListener(new ChannelFutureListener() {
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
			ctx.sendDownstream(e);
		}
	}

	// @Override
	// public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent
	// e)
	// throws Exception {
	// try {
	// peer = new PeerWireProtocolPeer(e.getChannel());
	// } finally {
	// ctx.sendUpstream(e);
	// }
	// }
}
