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
 * request: [len=0013][id=6][index][begin][length]
 * </pre>
 * 
 * The request message is fixed length, and is used to request a block. The
 * payload contains the following information:<br>
 * index: integer specifying the zero-based piece index<br>
 * begin: integer specifying the zero-based byte offset within the piece<br>
 * length: integer specifying the requested length.<br>
 * View #1 According to the official specification, "All current implementations
 * use 2^15 (32KB), and close connections which request an amount greater than
 * 2^17 (128KB)." As early as version 3 or 2004, this behavior was changed to
 * use 2^14 (16KB) blocks. As of version 4.0 or mid-2005, the mainline
 * disconnected on requests larger than 2^14 (16KB); and some clients have
 * followed suit. Note that block requests are smaller than pieces (>=2^18
 * bytes), so multiple requests will be needed to download a whole piece.<br>
 * Strictly, the specification allows 2^15 (32KB) requests. The reality is near
 * all clients will now use 2^14 (16KB) requests. Due to clients that enforce
 * that size, it is recommended that implementations make requests of that size.
 * Due to smaller requests resulting in higher overhead due to tracking a
 * greater number of requests, implementers are advised against going below 2^14
 * (16KB). The choice of request block size limit enforcement is not nearly so
 * clear cut. With mainline version 4 enforcing 16KB requests, most clients will
 * use that size.<br>
 * At the same time 2^14 (16KB) is the semi-official (only semi because the
 * official protocol document has not been updated) limit now, so enforcing that
 * isn't wrong. At the same time, allowing larger requests enlarges the set of
 * possible peers, and except on very low bandwidth connections (<256kbps)
 * multiple blocks will be downloaded in one choke-timeperiod, thus merely
 * enforcing the old limit causes minimal performance degradation. Due to this
 * factor, it is recommended that only the older 2^17 (128KB) maximum size limit
 * be enforced. View #2 This section has contained falsehoods for a large
 * portion of the time this page has existed. This is the third time I (uau) am
 * correcting this same section for incorrect information being added, so I
 * won't rewrite it completely since it'll probably be broken again... Current
 * version has at least the following errors: Mainline started using 2^14
 * (16384) byte requests when it was still the only client in existence; only
 * the "official specification" still talked about the obsolete 32768 byte value
 * which was in reality neither the default size nor maximum allowed. In version
 * 4 the request behavior did not change, but the maximum allowed size did
 * change to equal the default size. In latest mainline versions the max has
 * changed to 32768 (note that this is the first appearance of 32768 for either
 * default or max size since the first ancient versions).<br>
 * "Most older clients use 32KB requests" is false. Discussion of larger
 * requests fails to take latency effects into account.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source BitTorrent documentation
 */
public class RequestMessage implements PeerWireWritableMessage,
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

	public RequestMessage(int index, int start, int length) {
		this.index = index;
		this.start = start;
		this.length = length;
	}

	public RequestMessage() {
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
	
	@Override
	public int length() {
		return 13;
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
		return "RequestMessage [index=" + index + ", start=" + start
				+ ", length=" + length + "]";
	}
}
