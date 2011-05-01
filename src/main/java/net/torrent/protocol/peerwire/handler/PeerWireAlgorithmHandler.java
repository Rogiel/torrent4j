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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;

import net.torrent.protocol.algorithm.TorrentAlgorithm;
import net.torrent.protocol.algorithm.TorrentInterestAlgorithm;
import net.torrent.protocol.algorithm.TorrentPeerAlgorithm;
import net.torrent.protocol.algorithm.TorrentPieceDownloadAlgorithm;
import net.torrent.protocol.algorithm.TorrentPieceUploadAlgorithm;
import net.torrent.protocol.datastore.TorrentDatastore;
import net.torrent.protocol.peerwire.PeerWirePeer;
import net.torrent.protocol.peerwire.manager.TorrentManager;
import net.torrent.protocol.peerwire.message.BitfieldMessage;
import net.torrent.protocol.peerwire.message.ChokeMessage;
import net.torrent.protocol.peerwire.message.HaveMessage;
import net.torrent.protocol.peerwire.message.InterestedMessage;
import net.torrent.protocol.peerwire.message.KeepAliveMessage;
import net.torrent.protocol.peerwire.message.NotInterestedMessage;
import net.torrent.protocol.peerwire.message.PieceMessage;
import net.torrent.protocol.peerwire.message.RequestMessage;
import net.torrent.protocol.peerwire.message.UnchokeMessage;
import net.torrent.protocol.peerwire.message.fast.AllowedFastMessage;
import net.torrent.protocol.peerwire.message.fast.SuggestPieceMessage;
import net.torrent.torrent.Torrent;
import net.torrent.torrent.TorrentPart;
import net.torrent.torrent.TorrentPiece;
import net.torrent.torrent.context.TorrentPeer;
import net.torrent.torrent.context.TorrentPeer.ChokingState;
import net.torrent.torrent.context.TorrentPeer.InterestState;
import net.torrent.torrent.context.TorrentPeerCapabilities.TorrentPeerCapability;
import net.torrent.util.PeerCallback;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

/**
 * Standard handler responsible for forwarding calls to {@link TorrentAlgorithm}
 * methods. This class handles low-level protocol specific behavior.
 * <p>
 * The general guide line for this handler is to abstract <b>ALL</b> protocol
 * specific models and use an abstracted algorithm for download. This will
 * obviously limit the complexity of algorithm implementations. If that is the
 * case, you are free to implement a new handler.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PeerWireAlgorithmHandler extends IdleStateAwareChannelHandler {
	/**
	 * The torrent manager
	 */
	private final TorrentManager manager;
	/**
	 * The torrent datastore
	 */
	private final TorrentDatastore datastore;

	/**
	 * The peer algorithm
	 */
	private final TorrentPeerAlgorithm peerAlgorithm;
	/**
	 * The interest algorithm
	 */
	private final TorrentInterestAlgorithm interestAlgorithm;
	/**
	 * The download algorithm
	 */
	private final TorrentPieceDownloadAlgorithm downloadAlgorithm;
	/**
	 * The upload algorithm
	 */
	private final TorrentPieceUploadAlgorithm uploadAlgorithm;

	/**
	 * Creates a new handler
	 * 
	 * @param manager
	 *            the torrent manager
	 * @param algorithm
	 *            the algorithm
	 */
	public PeerWireAlgorithmHandler(TorrentManager manager,
			final TorrentAlgorithm algorithm) {
		this.manager = manager;
		this.datastore = manager.getDatastore();
		this.peerAlgorithm = algorithm.getPeerAlgorithm();
		this.interestAlgorithm = algorithm.getInterestAlgorithm();
		this.downloadAlgorithm = algorithm.getDownloadAlgorithm();
		this.uploadAlgorithm = algorithm.getUploadAlgorithm();
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		final PeerWirePeer peer = manager.getPeerManager().getPeer(
				e.getChannel());
		// TODO handshake with random peer id
		peer.handshake(manager.getTorrent().getInfoHash().toByteArray(),
				"-TR2050-mcm14ye4h2mq".getBytes(), manager.getContext()
						.getCapabilites().toBitSet());
		peer.port((short) 1541);
		super.channelConnected(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		final PeerWirePeer peer = manager.getPeerManager().getPeer(
				e.getChannel());
		final Object msg = e.getMessage();

		if (msg instanceof BitfieldMessage) {
			final BitfieldMessage bitfield = (BitfieldMessage) msg;
			final BitSet bitset = bitfield.getBitfield();
			peer.getTorrentPeer().getBitfield().setBits(bitset);
			testInterest(peer);
		} else if (msg instanceof InterestedMessage) {
			peer.getTorrentPeer()
					.setPeerInterestState(InterestState.INTERESTED);
			peerIntrestUpdate(peer, peer.getTorrentPeer()
					.getPeerInterestState());
		} else if (msg instanceof NotInterestedMessage) {
			peer.getTorrentPeer().setPeerInterestState(
					InterestState.UNINTERESTED);
			peerIntrestUpdate(peer, peer.getTorrentPeer()
					.getPeerInterestState());
		} else if (msg instanceof UnchokeMessage) {
			peer.getTorrentPeer().setPeerChokingState(ChokingState.UNCHOKED);
			peerChokeUpdate(peer, peer.getTorrentPeer().getPeerChokingState());
		} else if (msg instanceof ChokeMessage) {
			peer.getTorrentPeer().setPeerChokingState(ChokingState.CHOKED);
			peerChokeUpdate(peer, peer.getTorrentPeer().getPeerChokingState());
		} else if (msg instanceof RequestMessage) {
			final RequestMessage request = (RequestMessage) msg;
			final Torrent torrent = manager.getTorrent();
			final TorrentPart part = torrent.getPart(request.getIndex(),
					request.getStart(), request.getLength());
			processRequest(peer, part);
		} else if (msg instanceof PieceMessage) {
			final PieceMessage pieceMsg = (PieceMessage) msg;
			final Torrent torrent = manager.getTorrent();
			final TorrentPart part = torrent.getPart(pieceMsg.getIndex(),
					pieceMsg.getStart(), pieceMsg.getLength());
			processDownload(peer, part, pieceMsg.getBlock());
		} else if (msg instanceof HaveMessage) {
			final HaveMessage have = (HaveMessage) msg;
			final Torrent torrent = manager.getTorrent();
			final TorrentPiece piece = torrent.getPiece(have.getPiece());
			peer.getTorrentPeer().getBitfield().setPiece(piece, true);
			testInterest(peer);
		} else if (msg instanceof KeepAliveMessage) {
			keepAlive(peer);
		} else if (msg instanceof AllowedFastMessage) {
			final AllowedFastMessage fast = (AllowedFastMessage) msg;
			final Torrent torrent = manager.getTorrent();
			for (final int index : fast.getPieces()) {
				final TorrentPiece piece = torrent.getPiece(index);
				allowedFast(peer, piece);
			}
		} else if (msg instanceof SuggestPieceMessage) {
			final SuggestPieceMessage suggest = (SuggestPieceMessage) msg;
			final Torrent torrent = manager.getTorrent();
			final TorrentPiece piece = torrent.getPiece(suggest.getPiece());
			suggested(peer, piece);
		}
		super.messageReceived(ctx, e);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		// any exception thrown, the channel is disconnected.
		e.getCause().printStackTrace();
		e.getChannel().disconnect();
		super.exceptionCaught(ctx, e);
	}

	@Override
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
			throws Exception {
		final PeerWirePeer peer = manager.getPeerManager().getPeer(
				e.getChannel());
		keepAlive(peer);
		super.channelIdle(ctx, e);
	}

	/**
	 * Test the interest in the peer
	 * 
	 * @param peer
	 *            the peer
	 */
	private void testInterest(PeerWirePeer peer) {
		switch (interestAlgorithm.interested(peer.getTorrentPeer())) {
		case INTERESTED:
			peer.interested();
			return;
		case UNINTERESTED:
			testChoke(peer);
			return;
		}
	}

	/**
	 * Test choke interest to this peer
	 * 
	 * @param peer
	 *            the peer
	 */
	private void testChoke(PeerWirePeer peer) {
		switch (interestAlgorithm.choke(peer.getTorrentPeer())) {
		case CHOKED:
			peer.choke();
			return;
		case UNCHOKED:
			peer.unchoke();
			return;
		}
	}

	/**
	 * Handles the peer choke interest change.
	 * 
	 * @param peer
	 *            the peer
	 * @param state
	 *            the new interest state
	 */
	private void peerIntrestUpdate(PeerWirePeer peer, InterestState state) {
		switch (peerAlgorithm.interested(peer.getTorrentPeer(), state)) {
		case CHOKED:
			peer.choke();
			return;

		case UNCHOKED:
			peer.unchoke();
			return;
		}
	}

	/**
	 * Handles the peer choke change.
	 * 
	 * @param peer
	 *            the peer
	 * @param state
	 *            the choke state
	 */
	private void peerChokeUpdate(PeerWirePeer peer, ChokingState state) {
		switch (peerAlgorithm.choked(peer.getTorrentPeer(), state)) {
		case DISCONNECT:
			peer.disconnect();
			break;

		case CONNECT_NEW_PEER:
			peer.disconnect();
			connect(peerAlgorithm.connect());
			return;

		case DOWNLOAD:
			download(peer,
					downloadAlgorithm.getNextPart(peer.getTorrentPeer(), null));
			return;
		}
	}

	/**
	 * Process the upload request
	 * 
	 * @param peer
	 *            the peer
	 * @param part
	 *            the part
	 * @throws IOException
	 */
	private void processRequest(PeerWirePeer peer, TorrentPart part)
			throws IOException {
		switch (uploadAlgorithm.request(peer.getTorrentPeer(), part)) {
		case DISCONNECT:
			peer.disconnect();
			break;
		case REJECT:
			if (!peer.getTorrentPeer().getCapabilities()
					.supports(TorrentPeerCapability.FAST_PEERS))
				return;
			peer.reject(part.getPiece().getIndex(), part.getStart(),
					part.getLength());
			break;
		case CONNECT_NEW_PEER:
			peer.disconnect();
			connect(peerAlgorithm.connect());
			break;
		case CHOKE:
			peer.choke();
			break;
		case UPLOAD:
			upload(peer, part);
			break;
		}
	}

	/**
	 * Processes an download
	 * 
	 * @param peer
	 *            the uploader peer
	 * @param part
	 *            the part
	 * @param data
	 *            the downloaded content
	 * @throws IOException
	 */
	private void processDownload(final PeerWirePeer peer,
			final TorrentPart part, ByteBuffer data) throws IOException {
		final TorrentPart nextPart = downloadAlgorithm.getNextPart(
				peer.getTorrentPeer(), part);
		boolean complete = downloadAlgorithm.isComplete(peer.getTorrentPeer(),
				part.getPiece());
		final ChannelFuture future = download(peer, nextPart);

		// store piece
		if (!datastore.write(part, data))
			return;
		if (!complete)
			return;

		if (datastore.checksum(part.getPiece())) {
			manager.getContext().getBitfield().setPiece(part.getPiece(), true);
			manager.getPeerManager().executeActive(new PeerCallback() {
				@Override
				public void callback(PeerWirePeer peer) {
					peer.have(part.getPiece().getIndex());
				}
			});
		} else {
			System.exit(0);
			manager.getContext().getBitfield().setPiece(part.getPiece(), false);
			switch (downloadAlgorithm.corrupted(peer.getTorrentPeer(),
					part.getPiece())) {
			case CHOKE:
				if (future != null && !future.cancel())
					peer.cancel(nextPart.getPiece().getIndex(),
							nextPart.getStart(), nextPart.getLength());
				peer.choke();
				break;
			case DISCONNECT:
				peer.disconnect();
				break;
			case CONNECT_NEW_PEER:
				peer.disconnect();
				connect(peerAlgorithm.connect());
				break;
			case CONTINUE:
				break;
			case CANCEL:
				if (future != null && !future.cancel())
					peer.cancel(nextPart.getPiece().getIndex(),
							nextPart.getStart(), nextPart.getLength());
				return;
			}
		}
	}

	/**
	 * Check keep alive interest in this peer.
	 * 
	 * @param peer
	 *            the peer
	 */
	private void keepAlive(PeerWirePeer peer) {
		switch (peerAlgorithm.keepAlive(peer.getTorrentPeer())) {
		case DISCONNECT:
			peer.disconnect();
			break;
		case CONNECT_NEW_PEER:
			peer.disconnect();
			connect(peerAlgorithm.connect());
			break;
		case KEEP_ALIVE:
			peer.keepAlive();
			break;
		}
	}

	/**
	 * Do the part request
	 * 
	 * @param peer
	 *            the uploader peer
	 * @param part
	 *            the part
	 * @return an {@link ChannelFuture}. Can be used to cancel the message send.
	 */
	private ChannelFuture download(PeerWirePeer peer, TorrentPart part) {
		if (part == null) {
			testInterest(peer);
			return null;
		}
		return peer.request(part.getPiece().getIndex(), part.getStart(),
				part.getLength());
	}

	/**
	 * Do the part upload
	 * 
	 * @param peer
	 *            the downloader peer
	 * @param part
	 *            the part
	 */
	private void upload(PeerWirePeer peer, TorrentPart part) throws IOException {
		if (part == null) {
			testChoke(peer);
			return;
		}
		final ByteBuffer data = datastore.read(part);
		peer.upload(part.getPiece().getIndex(), part.getStart(),
				part.getLength(), data);
	}

	/**
	 * Tries to establish an connection to <tt>peer</tt>
	 * 
	 * @param peer
	 *            the peer to be connected.
	 */
	private void connect(TorrentPeer peer) {
		if (peer == null)
			return;
		// TODO
	}

	/**
	 * Processes the suggested piece
	 * 
	 * @param peer
	 *            the suggesting peer
	 * @param piece
	 *            the suggested piece
	 */
	private void suggested(PeerWirePeer peer, TorrentPiece piece) {
		final TorrentPart part = downloadAlgorithm.sugested(
				peer.getTorrentPeer(), piece);
		if (part == null)
			return;
		download(peer, part);
	}

	/**
	 * Processes the allowed fast piece
	 * 
	 * @param peer
	 *            the allowing peer
	 * @param piece
	 *            the allowed piece
	 */
	private void allowedFast(PeerWirePeer peer, TorrentPiece piece) {
		final TorrentPart part = downloadAlgorithm.allowedFast(
				peer.getTorrentPeer(), piece);
		if (part == null)
			return;
		download(peer, part);
	}
}