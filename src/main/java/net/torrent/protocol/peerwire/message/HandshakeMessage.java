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
import java.util.Arrays;
import java.util.BitSet;

import net.torrent.protocol.peerwire.codec.PeerWireReadableMessage;
import net.torrent.protocol.peerwire.codec.PeerWireWritableMessage;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * TODO documentation
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class HandshakeMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	/**
	 * Length of "BitTorrent protocol" string.
	 */
	private int pstrlen = 19;
	/**
	 * The protocol string, must match to validate the protocol connection
	 */
	private String pstr = "BitTorrent protocol";
	/**
	 * Addons supported by the client (bitfield)
	 */
	private BitSet reserved = new BitSet(64);
	/**
	 * The torrent's hash
	 */
	private byte[] infohash;
	/**
	 * The peer id
	 */
	private byte[] peerId;

	public HandshakeMessage(byte[] infohash, byte[] peerId, BitSet reserved) {
		this.infohash = infohash;
		this.peerId = peerId;
		this.reserved = reserved;
	}

	public HandshakeMessage() {
	}

	@Override
	public void read(ChannelBuffer buffer) throws IOException {
		this.pstrlen = buffer.readByte();
		this.pstr = new String(buffer.readBytes(pstrlen).array());

		for (int i = 0; i < 8; i++) {
			byte b = buffer.readByte();
			for (int j = 0; j < 8; j++) {
				if ((b & (1 << j)) > 0) {
					reserved.set(i * 8 + j);
				}
			}
		}

		this.infohash = buffer.readBytes(20).array();
		this.peerId = buffer.readBytes(20).array();
	}

	@Override
	public void write(ChannelBuffer buffer) throws IOException {
		buffer.writeByte((byte) this.pstr.length());
		buffer.writeBytes(this.pstr.getBytes());

		for (int i = 0; i < 64;) {
			byte data = 0;
			for (int j = 128; i < reserved.size() && j > 0; j >>= 1, i++) {
				if (reserved.get(i)) {
					data |= j;
				}
			}
			buffer.writeByte(data);
		}

		// buffer.writeBytes(reserved);
		buffer.writeBytes(this.infohash);
		buffer.writeBytes(this.peerId);
	}

	@Override
	public int length() {
		return 1 + pstrlen + pstr.length() + 8 + infohash.length + peerId.length;
	}

	public int getPstrlen() {
		return pstrlen;
	}

	public void setPstrlen(int pstrlen) {
		this.pstrlen = pstrlen;
	}

	public String getPstr() {
		return pstr;
	}

	public void setPstr(String pstr) {
		this.pstr = pstr;
	}

	public BitSet getReserved() {
		return reserved;
	}

	public void setReserved(BitSet reserved) {
		this.reserved = reserved;
	}

	public byte[] getInfohash() {
		return infohash;
	}

	public void setInfohash(byte[] infohash) {
		this.infohash = infohash;
	}

	public byte[] getPeerId() {
		return peerId;
	}

	public void setPeerId(byte[] peerId) {
		this.peerId = peerId;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return "HandshakeMessage [pstrlen="
				+ pstrlen
				+ ", pstr="
				+ pstr
				+ ", reserved="
				+ reserved
				+ ", infohash="
				+ (infohash != null ? Arrays.toString(Arrays.copyOf(infohash,
						Math.min(infohash.length, maxLen))) : null)
				+ ", peerId="
				+ (peerId != null ? Arrays.toString(Arrays.copyOf(peerId,
						Math.min(peerId.length, maxLen))) : null) + "]";
	}
}
