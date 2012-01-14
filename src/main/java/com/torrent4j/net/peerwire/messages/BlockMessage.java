package com.torrent4j.net.peerwire.messages;

import io.netty.buffer.ChannelBuffer;

import java.nio.ByteBuffer;

import com.torrent4j.net.peerwire.AbstractPeerWireMessage;

public class BlockMessage extends AbstractPeerWireMessage {
	public static final int MESSAGE_ID = 0x07;

	public int pieceIndex;
	public int begin;
	public ByteBuffer data;

	public BlockMessage() {
		super(MESSAGE_ID);
	}

	public BlockMessage(int pieceIndex, int begin, ByteBuffer data) {
		super(MESSAGE_ID);
		this.pieceIndex = pieceIndex;
		this.begin = begin;
		this.data = data;
	}

	@Override
	public void writeImpl(ChannelBuffer buffer) {
		buffer.writeInt(pieceIndex);
		buffer.writeInt(begin);
		buffer.writeBytes(data);
	}

	@Override
	public void readImpl(ChannelBuffer buffer) {
		pieceIndex = buffer.readInt();
		begin = buffer.readInt();
		data = buffer.readBytes(buffer.readableBytes()).toByteBuffer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BLOCK [pieceIndex=" + pieceIndex + ", begin=" + begin
				+ ", data=" + data + "]";
	}
}
