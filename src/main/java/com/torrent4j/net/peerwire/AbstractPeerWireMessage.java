package com.torrent4j.net.peerwire;

import io.netty.buffer.ByteBuf;

public abstract class AbstractPeerWireMessage implements PeerWireMessage {
	public final int messageID;

	public AbstractPeerWireMessage(int messageID) {
		this.messageID = messageID;
	}

	@Override
	public final void write(ByteBuf buffer) {
		buffer.writeByte(messageID);
		writeImpl(buffer);
	}

	public void writeImpl(ByteBuf buffer) {
	}

	@Override
	public final void read(ByteBuf buffer) {
		readImpl(buffer);
	}

	public void readImpl(ByteBuf buffer) {
	}
}
