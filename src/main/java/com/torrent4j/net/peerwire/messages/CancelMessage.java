package com.torrent4j.net.peerwire.messages;

import io.netty.buffer.ByteBuf;

import com.torrent4j.net.peerwire.AbstractPeerWireMessage;

public class CancelMessage extends AbstractPeerWireMessage {
	public static final int MESSAGE_ID = 0x08;

	public int pieceIndex;
	public int begin;
	public int length;

	public CancelMessage() {
		super(MESSAGE_ID);
	}

	public CancelMessage(int pieceIndex, int begin, int length) {
		super(MESSAGE_ID);
		this.pieceIndex = pieceIndex;
		this.begin = begin;
		this.length = length;
	}

	@Override
	public void writeImpl(ByteBuf buffer) {
		buffer.writeInt(pieceIndex);
		buffer.writeInt(begin);
		buffer.writeInt(length);
	}

	@Override
	public void readImpl(ByteBuf buffer) {
		pieceIndex = buffer.readInt();
		begin = buffer.readInt();
		length = buffer.readInt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CANCEL [pieceIndex=" + pieceIndex + ", begin=" + begin
				+ ", length=" + length + "]";
	}
}
