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
package net.torrent.protocol.datastore.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.torrent.protocol.datastore.AbstractTorrentDatastore;
import net.torrent.torrent.TorrentPart;
import net.torrent.torrent.TorrentPiece;

/**
 * This is much like the <tt>/dev/null</tt> and <tt>/dev/zero</tt> pseudo-file
 * in UNIX systems. All data written is discarded and all readed data is
 * composed of <tt>NULL</tt> (<tt>0x00</tt>). Checksums always succeed.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class VoidTorrentDatastore extends AbstractTorrentDatastore {
	@Override
	public ByteBuffer read(TorrentPart part) throws IOException {
		return ByteBuffer.allocate(part.getLength());
	}

	@Override
	public boolean write(TorrentPart part, ByteBuffer buffer) {
		return true;
	}

	@Override
	public boolean checksum(TorrentPiece piece) throws IOException {
		return true;
	}
}
