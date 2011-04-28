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
import net.torrent.protocol.datastore.TorrentDatastore;
import net.torrent.torrent.Torrent;
import net.torrent.torrent.TorrentFile;
import net.torrent.torrent.TorrentPart;
import net.torrent.util.Range;

/**
 * Since {@link TorrentDatastore} does not uses the concept of files when
 * storing and/or retrieving data, this abstract class provides abstraction for
 * that issue and allows, easily to store and/or retrieve data in separated
 * files.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public abstract class FileAwareTorrentDatastore extends
		AbstractTorrentDatastore {
	/**
	 * Read data contained withing <tt>file</tt> in the given <tt>range</tt>
	 * 
	 * @param buffer
	 *            the buffer in which data should be readed.
	 * @param file
	 *            the file to be read
	 * @param range
	 *            range of data being readed.
	 * @throws IOException
	 *             if any exception occur at IO level
	 */
	protected abstract void read(ByteBuffer buffer, TorrentFile file,
			Range range) throws IOException;

	/**
	 * Write data contained in <tt>buffer</tt> to <tt>file</tt> in the given
	 * <tt>range</tt>
	 * 
	 * @param buffer
	 *            the buffer in which data should be written.
	 * @param file
	 *            the file to be written
	 * @param range
	 *            range of data being written.
	 * @throws IOException
	 *             if any exception occur at IO level
	 */
	protected abstract boolean write(ByteBuffer buffer, TorrentFile file,
			Range range) throws IOException;

	@Override
	public ByteBuffer read(TorrentPart part) throws IOException {
		final ByteBuffer buffer = allocate(part);

		final Torrent torrent = part.getTorrent();
		Range partRange = part.asTorrentRange();

		for (final TorrentFile file : torrent.getFiles()) {
			final Range fileRange = file.asTorrentRange();
			if (fileRange.intersects(partRange)) {
				final Range range = part.asFileRange(file).intersection(
						fileRange);
				buffer.limit((int) (buffer.position() + range.getLength()));
				this.read(buffer, file, range);
			}
		}

		buffer.flip();
		return buffer;
	}

	@Override
	public boolean write(TorrentPart part, ByteBuffer buffer)
			throws IOException {
		final Torrent torrent = part.getTorrent();
		Range partRange = part.asTorrentRange();

		for (final TorrentFile file : torrent.getFiles()) {
			final Range fileRange = file.asFileRange();
			if (fileRange.intersects(partRange)) {
				final Range range = part.asFileRange(file);
				buffer.limit((int) (buffer.position() + range.getLength()));
				if (!this.write(buffer, file, range))
					return false;
			}
		}
		return true;
	}

	/**
	 * Allocates a new {@link ByteBuffer}.
	 * 
	 * @param part
	 *            the {@link TorrentPart}s
	 * @return a newly allocated ByteBuffer
	 */
	private ByteBuffer allocate(TorrentPart part) {
		return ByteBuffer.allocate(part.getLength());
	}
}
