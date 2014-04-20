package com.torrent4j.net.peerwire.messages;

import io.netty.buffer.ByteBuf;

import java.util.BitSet;

import com.torrent4j.net.peerwire.AbstractPeerWireMessage;

public class BitFieldMessage extends AbstractPeerWireMessage {
	public static final int MESSAGE_ID = 0x05;

	public BitSet bitSet;

	public BitFieldMessage() {
		super(MESSAGE_ID);
	}

	public BitFieldMessage(BitSet bitSet) {
		super(MESSAGE_ID);
		this.bitSet = bitSet;
	}

	@Override
	public void writeImpl(ByteBuf buffer) {
		for (int i = 0; i < bitSet.size();) {
			byte data = 0;
			for (int j = 128; i < bitSet.size() && j > 0; j >>= 1, i++) {
				if (bitSet.get(i)) {
					data |= j;
				}
			}
			buffer.writeByte(data);
		}
	}

	@Override
	public void readImpl(ByteBuf buffer) {
		bitSet = new BitSet();
		int i = 0;
		while (buffer.isReadable()) {
			byte b = buffer.readByte();
			for (int j = 128; j > 0; j >>= 1) {
				bitSet.set(i++, (b & j) != 0);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BITFIELD [bitSet=" + bitSet + "]";
	}
}
