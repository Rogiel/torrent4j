package com.torrent4j.net;

import java.nio.ByteBuffer;
import java.util.BitSet;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPeer;

public interface TorrentProtocolPeer {
	public TorrentPeer getTorrentPeer();

	public Torrent getTorrent();

	boolean connect();

	boolean isConnected();

	boolean disconnect();

	void handshake(byte[] torrentHash, String peerID);

	void requestBlock(int pieceIndex, int start, int length);

	void cancelRequestedBlock(int pieceIndex, int start, int length);

	void sendBlock(int pieceIndex, int start, ByteBuffer data);

	void bitField(BitSet bitSet);

	void have(int pieceIndex);

	void choke();

	void unchoke();

	void interested();

	void notInterested();

	void port(int dhtPort);

	void keepAlive();
}
