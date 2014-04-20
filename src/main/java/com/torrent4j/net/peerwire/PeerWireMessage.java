package com.torrent4j.net.peerwire;

import io.netty.buffer.ByteBuf;

public interface PeerWireMessage {
	void write(ByteBuf buffer);

	void read(ByteBuf buffer);
}
