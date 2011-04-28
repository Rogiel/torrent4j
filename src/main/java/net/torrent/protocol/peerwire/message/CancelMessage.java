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

import net.torrent.protocol.peerwire.codec.PeerWireReadableMessage;
import net.torrent.protocol.peerwire.codec.PeerWireWritableMessage;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * <pre>
 * cancel: [len=0013][id=8][index][begin][length]
 * </pre>
 * 
 * The cancel message is fixed length, and is used to cancel block requests. The
 * payload is identical to that of the "request" message. It is typically used
 * during "End Game" (check for algorithms).
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source BitTorrent documentation
 */
public class CancelMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	public static final byte MESSAGE_ID = 0x08;

	/**
	 * The piece index
	 */
	private int index;
	/**
	 * The start offset
	 */
	private int start;
	/**
	 * The length
	 */
	private int length;

	public CancelMessage() {
	}

	public CancelMessage(int index, int start, int length) {
		this.index = index;
		this.start = start;
		this.length = length;
	}

	@Override
	public void read(ChannelBuffer buffer) throws IOException {
		this.index = buffer.readInt();
		this.start = buffer.readInt();
		this.length = buffer.readInt();
	}

	@Override
	public void write(ChannelBuffer buffer) throws IOException {
		buffer.writeByte(MESSAGE_ID);
		buffer.writeInt(index);
		buffer.writeInt(start);
		buffer.writeInt(length);
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

	@Override
	public String toString() {
		return "CancelMessage [index=" + index + ", start=" + start
				+ ", length=" + length + "]";
	}
}
