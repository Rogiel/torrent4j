package com.torrent4j.net.peerwire.messages;

import io.netty.buffer.ByteBuf;

import com.torrent4j.net.peerwire.PeerWireMessage;

public class KeepAliveMessage implements PeerWireMessage {
	public KeepAliveMessage() {
	}

	@Override
	public void write(ByteBuf buffer) {
	}

	@Override
	public void read(ByteBuf buffer) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KEEP_ALIVE []";
	}
}
