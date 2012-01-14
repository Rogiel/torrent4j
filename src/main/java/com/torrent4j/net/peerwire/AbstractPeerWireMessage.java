package com.torrent4j.net.peerwire;

import io.netty.buffer.ChannelBuffer;

public abstract class AbstractPeerWireMessage implements PeerWireMessage {
	public final int messageID;

	public AbstractPeerWireMessage(int messageID) {
		this.messageID = messageID;
	}

	@Override
	public final void write(ChannelBuffer buffer) {
		buffer.writeByte(messageID);
		writeImpl(buffer);
	}

	public void writeImpl(ChannelBuffer buffer) {
	}

	@Override
	public final void read(ChannelBuffer buffer) {
		readImpl(buffer);
	}

	public void readImpl(ChannelBuffer buffer) {
	}
}
