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
package net.torrent.protocol.peerwire.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * An readable message in the BitTorrent protocol
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public interface PeerWireReadableMessage {
	/**
	 * Read the content of the message contained in this buffer.
	 * 
	 * @param buffer
	 *            the buffer
	 * @throws IOException
	 */
	void read(ChannelBuffer buffer) throws IOException;
}
