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
import java.util.HashMap;
import java.util.Map;

import net.torrent.torrent.TorrentFile;
import net.torrent.util.Range;

/**
 * An simple implementation for torrent. Stores files in local file system.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class SimpleTorrentDatastore extends FileAwareTorrentDatastore {
	private final Map<TorrentFile, FileChannel> cache = new HashMap<TorrentFile, FileChannel>();
	private final File root;

	public SimpleTorrentDatastore(File root) {
		this.root = root;
	}

	public SimpleTorrentDatastore() {
		this(new File("."));
	}

	@Override
	protected void read(ByteBuffer buffer, TorrentFile file, Range range)
			throws IOException {
		final FileChannel channel = cache(file);
		synchronized (channel) {
			channel.position(file.getOffset() + range.getStart());
			while (buffer.hasRemaining()) {
				if (channel.read(buffer) == -1) // EOF
					break;
			}
		}
		// don't flip the buffer!
	}

	@Override
	protected boolean write(ByteBuffer buffer, TorrentFile file, Range range)
			throws IOException {
		final FileChannel channel = cache(file);
		synchronized (channel) {
			channel.position(file.getOffset() + range.getStart());
			while (buffer.hasRemaining()) {
				if (channel.write(buffer) == -1)
					return false;
			}
		}
		return true;
	}

	/**
	 * Retrieve an cached {@link FileChannel channel}. If not cached, a new will
	 * be open in read/write mode and cached.
	 * 
	 * @param file
	 *            the {@link TorrentFile file}
	 * @return the cached {@link FileChannel channel}
	 * @throws FileNotFoundException
	 *             if parent folder does not exists.
	 */
	private FileChannel cache(TorrentFile file) throws FileNotFoundException {
		FileChannel channel = cache.get(file);
		if (channel == null) {
			channel = new RandomAccessFile(new File(root,
					file.getRelativePath()), "rw").getChannel();
			cache.put(file, channel);
		}
		return channel;
	}
}
