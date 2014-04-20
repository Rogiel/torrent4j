package com.torrent4j.net.peerwire.messages;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.nio.charset.Charset;
import java.util.Arrays;

import com.torrent4j.net.peerwire.PeerWireMessage;

public class HandshakeMessage implements PeerWireMessage {
	public int protocolStringLength = 19;
	public String protocolString = "BitTorrent protocol";
	public long reserved = 0;
	public byte[] torrentHash;
	public String peerID;

	public HandshakeMessage() {
	}

	public HandshakeMessage(byte[] torrentHash, String peerID) {
		this.torrentHash = torrentHash;
		this.peerID = peerID;
	}

	@Override
	public void write(ByteBuf buffer) {
		buffer.writeByte(protocolStringLength);
		buffer.writeBytes(protocolString.getBytes());
		buffer.writeLong(reserved);
		buffer.writeBytes(torrentHash, 0, 20);
		buffer.writeBytes(peerID.getBytes(), 0, 20);
	}

	@Override
	public void read(ByteBuf buffer) {
		protocolStringLength = buffer.readByte();
		byte[] str = new byte[protocolStringLength];
		buffer.readBytes(str);
		protocolString = new String(str);
		//protocolString = buffer.readBytes
		reserved = buffer.readLong();
		torrentHash = buffer.readBytes(20).array();
		peerID = buffer.readBytes(20).toString(Charset.forName("UTF-8"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HANDSHAKE [protocolStringLength=" + protocolStringLength
				+ ", protocolString=" + protocolString + ", reserved="
				+ reserved + ", torrentHash=" + Arrays.toString(torrentHash)
				+ ", peerID=" + peerID + "]";
	}
}
