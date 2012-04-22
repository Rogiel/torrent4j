package com.torrent4j.net.peerwire;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.nio.ByteBuffer;
import java.util.BitSet;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.peer.TorrentPeer;
import com.torrent4j.net.TorrentProtocolPeer;
import com.torrent4j.net.peerwire.messages.BitFieldMessage;
import com.torrent4j.net.peerwire.messages.BlockMessage;
import com.torrent4j.net.peerwire.messages.CancelMessage;
import com.torrent4j.net.peerwire.messages.ChokeMessage;
import com.torrent4j.net.peerwire.messages.HandshakeMessage;
import com.torrent4j.net.peerwire.messages.HaveMessage;
import com.torrent4j.net.peerwire.messages.InterestedMessage;
import com.torrent4j.net.peerwire.messages.KeepAliveMessage;
import com.torrent4j.net.peerwire.messages.NotInterestedMessage;
import com.torrent4j.net.peerwire.messages.PortMessage;
import com.torrent4j.net.peerwire.messages.RequestMessage;
import com.torrent4j.net.peerwire.messages.UnchokeMessage;
import com.torrent4j.strategy.TorrentStrategy;

public class PeerWireProtocolPeer implements TorrentProtocolPeer {
	private final Channel channel;
	private TorrentPeer peer;

	public PeerWireProtocolPeer(Channel channel) {
		this.channel = channel;
	}

	@Override
	public boolean connect() {
		return channel.connect(channel.getRemoteAddress())
				.awaitUninterruptibly().isSuccess();
	}

	@Override
	public boolean isConnected() {
		return channel.isConnected();
	}

	@Override
	public boolean disconnect() {
		return channel.disconnect().awaitUninterruptibly().isSuccess();
	}

	@Override
	public void handshake(byte[] torrentHash, String peerID) {
		write(new HandshakeMessage(torrentHash, peerID));
	}

	@Override
	public void requestBlock(int pieceIndex, int start, int length) {
		write(new RequestMessage(pieceIndex, start, length));
	}

	@Override
	public void cancelRequestedBlock(int pieceIndex, int start, int length) {
		write(new CancelMessage(pieceIndex, start, length));
	}

	@Override
	public void sendBlock(int pieceIndex, int start, ByteBuffer data) {
		write(new BlockMessage(pieceIndex, start, data));
	}

	@Override
	public void bitField(BitSet bitSet) {
		write(new BitFieldMessage(bitSet));
	}

	@Override
	public void have(int pieceIndex) {
		write(new HaveMessage(pieceIndex));
	}

	@Override
	public void choke() {
		write(new ChokeMessage());
	}

	@Override
	public void unchoke() {
		write(new UnchokeMessage());
	}

	@Override
	public void interested() {
		write(new InterestedMessage());
	}

	@Override
	public void notInterested() {
		write(new NotInterestedMessage());
	}

	@Override
	public void port(int dhtPort) {
		write(new PortMessage(dhtPort));
	}

	@Override
	public void keepAlive() {
		write(new KeepAliveMessage());
	}

	public ChannelFuture write(PeerWireMessage message) {
		return channel.write(message);
	}

	public TorrentPeer getTorrentPeer() {
		return peer;
	}

	public void setTorrentPeer(TorrentPeer peer) {
		this.peer = peer;
	}

	public Torrent getTorrent() {
		return peer.getTorrent();
	}

	public TorrentStrategy getStrategy() {
		return peer.getTorrent().getStrategy();
	}
}
