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
import java.util.BitSet;

import net.torrent.protocol.peerwire.codec.PeerWireReadableMessage;
import net.torrent.protocol.peerwire.codec.PeerWireWritableMessage;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * <pre>
 * bitfield: [len=0001+X][id=5][bitfield]
 * </pre>
 * 
 * The bitfield message may only be sent immediately after the handshaking
 * sequence is completed, and before any other messages are sent. It is
 * optional, and need not be sent if a client has no pieces. The bitfield
 * message is variable length, where X is the length of the bitfield. The
 * payload is a bitfield representing the pieces that have been successfully
 * downloaded. The high bit in the first byte corresponds to piece index 0. Bits
 * that are cleared indicated a missing piece, and set bits indicate a valid and
 * available piece. Spare bits at the end are set to zero. A bitfield of the
 * wrong length is considered an error. Clients should drop the connection if
 * they receive bitfields that are not of the correct size, or if the bitfield
 * has any of the spare bits set.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source BitTorrent documentation
 */
public class BitfieldMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	public static final byte MESSAGE_ID = 0x05;

	/**
	 * A set of bits with the following scheme:
	 * <ul>
	 * <li><b>true</b>: has the piece</li>
	 * </li><b>false</b>: piece is missing</li>
	 * </ul>
	 */
	private BitSet bitfield;

	public BitfieldMessage(BitSet bitfield) {
		this.bitfield = bitfield;
	}

	public BitfieldMessage() {
	}

	@Override
	public void read(ChannelBuffer buffer) throws IOException {
		buffer.readerIndex(buffer.readerIndex() - 5);
		int len = buffer.readInt() - 2;
		buffer.readByte(); // unk

		bitfield = new BitSet(len * 8);
		int i = 0;
		int read = 0;
		while (read <= len) {
			byte b = buffer.readByte();
			for (int j = 128; j > 0; j >>= 1) {
				bitfield.set(i++, (b & j) != 0);
			}
			read++;
		}
	}

	@Override
	public void write(ChannelBuffer buffer) throws IOException {
		buffer.writeByte(MESSAGE_ID);
		// byte[] bytes = new byte[bitfield.size() / 8 - 4];
		// for (int i = 0; i < bitfield.size(); i++) {
		// if((bytes.length - i / 8 - 1) > bytes.length)
		// break;
		// if (bitfield.get(i)) {
		// bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
		// }
		// }
		// buffer.put(bytes);
		for (int i = 0; i < bitfield.size();) {
			byte data = 0;
			for (int j = 128; i < bitfield.size() && j > 0; j >>= 1, i++) {
				if (bitfield.get(i)) {
					data |= j;
				}
			}
			buffer.writeByte(data);
		}
	}

	@Override
	public int length() {
		return bitfield.size() / 8 + 1;
	}

	public BitSet getBitfield() {
		return bitfield;
	}

	public void setBitfield(BitSet bitfield) {
		this.bitfield = bitfield;
	}

	@Override
	public String toString() {
		return "BitfieldMessage [bitfield=" + bitfield + "]";
	}
}
