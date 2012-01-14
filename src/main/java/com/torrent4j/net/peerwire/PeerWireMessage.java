package com.torrent4j.net.peerwire;

import io.netty.buffer.ChannelBuffer;

public interface PeerWireMessage {
	void write(ChannelBuffer buffer);

	void read(ChannelBuffer buffer);
}
