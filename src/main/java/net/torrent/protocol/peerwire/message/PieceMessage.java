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
package net.torrent.protocol.peerwire.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.torrent.protocol.peerwire.codec.PeerWireReadableMessage;
import net.torrent.protocol.peerwire.codec.PeerWireWritableMessage;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * <pre>
 * piece: [len=0009+X][id=7][index][begin][block]
 * </pre>
 * 
 * The piece message is variable length, where X is the length of the block. The
 * payload contains the following information:<br>
 * index: integer specifying the zero-based piece index<br>
 * begin: integer specifying the zero-based byte offset within the piece<br>
 * block: block of data, which is a subset of the piece specified by index.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source BitTorrent documentation
 */
public class PieceMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	public static final byte MESSAGE_ID = 0x07;

	/**
	 * The piece index
	 */
	private int index;
	/**
	 * The part start offset
	 */
	private int start;
	/**
	 * The part length
	 */
	private int length;
	/**
	 * The downloaded/uploaded data
	 */
	private ByteBuffer block;

	public PieceMessage(RequestMessage request, ByteBuffer block) {
		this.index = request.getIndex();
		this.start = request.getStart();
		this.length = request.getLength();
		this.block = block;
	}

	public PieceMessage(int index, int start, int length, ByteBuffer block) {
		this.index = index;
		this.start = start;
		this.length = length;
		this.block = block;
	}

	public PieceMessage() {
	}

	@Override
	public void read(ChannelBuffer buffer) throws IOException {
		this.index = buffer.readInt();
		this.start = buffer.readInt();
		this.length = buffer.readableBytes();
		this.block = buffer.readBytes(length).toByteBuffer();
	}

	@Override
	public void write(ChannelBuffer buffer) throws IOException {
		buffer.writeByte(MESSAGE_ID);
		buffer.writeInt(index);
		buffer.writeInt(start);
		buffer.writeBytes(block);
	}
	
	@Override
	public int length() {
		return 9 + block.capacity();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public ByteBuffer getBlock() {
		return block;
	}

	public void setBlock(ByteBuffer block) {
		this.block = block;
	}

	@Override
	public String toString() {
		return "PieceMessage [index=" + index + ", start=" + start
				+ ", length=" + length + ", block=" + block + "]";
	}
}
