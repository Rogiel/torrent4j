package com.torrent4j.net.peerwire.messages;

import io.netty.buffer.ByteBuf;

import com.torrent4j.net.peerwire.AbstractPeerWireMessage;

public class RequestMessage extends AbstractPeerWireMessage {
	public static final int MESSAGE_ID = 0x06;

	public int pieceIndex;
	public int begin;
	public int length;

	public RequestMessage() {
		super(MESSAGE_ID);
	}

	public RequestMessage(int pieceIndex, int begin, int length) {
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

	@Override
	public String toString() {
		return "REQUEST [pieceIndex=" + pieceIndex + ", begin=" + begin
				+ ", length=" + length + "]";
	}
}
