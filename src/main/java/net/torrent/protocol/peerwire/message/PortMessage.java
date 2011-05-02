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
 * port: [len=0003][id=9][listen-port]
 * </pre>
 * 
 * The port message is sent by newer versions of the Mainline that implements a
 * DHT tracker. The listen port is the port this peer's DHT node is listening
 * on. This peer should be inserted in the local routing table (if DHT tracker
 * is supported).
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 * @source BitTorrent documentation
 */
public class PortMessage implements PeerWireWritableMessage,
		PeerWireReadableMessage {
	public static final byte MESSAGE_ID = 0x09;

	/**
	 * The DHT port
	 */
	private short port;

	public PortMessage() {
	}

	public PortMessage(short port) {
		this.port = port;
	}

	@Override
	public void read(ChannelBuffer buffer) throws IOException {
		this.port = buffer.readShort();
	}

	@Override
	public void write(ChannelBuffer buffer) throws IOException {
		buffer.writeByte(MESSAGE_ID);
		buffer.writeShort(port);
	}

	@Override
	public int length() {
		return 3;
	}

	public short getPort() {
		return port;
	}

	public void setPort(short port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return "PortMessage [port=" + port + "]";
	}
}
