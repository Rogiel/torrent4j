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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.torrent.protocol.datastore.AbstractTorrentDatastore;
import net.torrent.torrent.TorrentPart;

/**
 * Stores data into a single plain file, ignoring any file metadata into the
 * original .torrent file.
 * <p>
 * Unless for a single torrent file, data might (and possibly will) not be
 * readable outside this library.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class PlainTorrentDatastore extends AbstractTorrentDatastore {
	/**
	 * The target file
	 */
	private final File file;
	/**
	 * The file channel
	 */
	private FileChannel channel;

	/**
	 * Creates a new datastore instance
	 * 
	 * @param file
	 *            the single file to be used
	 */
	public PlainTorrentDatastore(File file) {
		this.file = file;
		try {
			this.channel = new RandomAccessFile(this.file, "rw").getChannel();
		} catch (FileNotFoundException e) {
		}
	}

	@Override
	public synchronized ByteBuffer read(TorrentPart part) throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocate(part.getLength());
		synchronized (channel) {
			channel.position(part.getOffset());
			while (buffer.hasRemaining()) {
				if (channel.read(buffer) == -1) // EOF
					break;
			}
		}
		buffer.flip();
		return buffer;
	}

	@Override
	public synchronized boolean write(TorrentPart part, ByteBuffer buffer)
			throws IOException {
		synchronized (channel) {
			channel.position(part.getOffset());
			while (buffer.hasRemaining()) {
				if (channel.write(buffer) == -1)
					return false;
			}
		}
		return true;
	}
}
