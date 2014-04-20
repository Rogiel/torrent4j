package com.torrent4j.net.peerwire.messages;

import io.netty.buffer.ByteBuf;

import com.torrent4j.net.peerwire.AbstractPeerWireMessage;

public class HaveMessage extends AbstractPeerWireMessage {
	public static final int MESSAGE_ID = 0x04;

	public int pieceIndex;

	public HaveMessage() {
		super(MESSAGE_ID);
	}

	public HaveMessage(int pieceIndex) {
		super(MESSAGE_ID);
		this.pieceIndex = pieceIndex;
	}

	@Override
	public void writeImpl(ByteBuf buffer) {
		buffer.writeInt(pieceIndex);
	}

	@Override
	public void readImpl(ByteBuf buffer) {
		pieceIndex = buffer.readInt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HAVE [pieceIndex=" + pieceIndex + "]";
	}
}
