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
package net.torrent.protocol.peerwire.message.fast;

import java.io.IOException;

import net.torrent.protocol.peerwire.codec.PeerWireReadableMessage;
import net.torrent.protocol.peerwire.codec.PeerWireWritableMessage;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * <pre>
 * Reject: [len=0013][id=0x10][index][begin][length]
 * </pre>
 * 
 * Reject Request notifies a requesting peer that its request will not be
 * satisfied.
 * 
 * If the fast extension is disabled and a peer receives a reject request then
 * the peer MUST close the connection.
 * <p>
 * When the fast extension is enabled:
 * <ul>
 * <li>If a peer receives a reject for a request that was never sent then the
 * peer SHOULD close the connection.</li>
 * <li>If a peer sends a choke, it MUST reject all requests from the peer to
 * whom the choke was sent except it SHOULD NOT reject requests for pieces that
 * are in the allowed fast set. A peer SHOULD choke first and then reject
 * requests so that the peer receiving the choke does not re-request the pieces.
 * </li>
 * <li>If a peer receives a request from a peer its choking, the peer receiving
 * the request SHOULD send a reject unless the piece is in the allowed fast set.
 * </li>
 * <li>If a peer receives an excessive number of requests from a peer it is
 * choking, the peer receiving the requests MAY close the connection rather than
 * reject the request. However, consider that it can take several seconds for
 * buffers to drain and messages to propagate once a peer is choked.
 * </ul>
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source http://www.bittorrent.org/beps/bep_0006.html
 */
public class RejectMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	public static final byte MESSAGE_ID = 0x06;

	/**
	 * The piece index
	 */
	private int index;
	/**
	 * The piece start offset
	 */
	private int start;
	/**
	 * The part length
	 */
	private int length;

	public RejectMessage(int index, int start, int length) {
		this.index = index;
		this.start = start;
		this.length = length;
	}

	public RejectMessage() {
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
		return "RejectMessage [index=" + index + ", start=" + start
				+ ", length=" + length + "]";
	}
}
