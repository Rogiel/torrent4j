package com.torrent4j.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.torrent4j.model.Torrent;
import com.torrent4j.util.Range;

/**
 * An simple {@link TorrentStorage} implementation that stores data into huge
 * in-memory direct buffers. Please note that though it provides really fast
 * write and reading, it should not be used in real world implementations.
 * Storing data in-memory consumes a lot of memory and may cause the JVM to be
 * terminated by some OSes.
 * <p>
 * <b>This class is intended for benchmarking and testing!</b>
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class InMemoryTorrentStorage extends AbstractTorrentStorage implements
		TorrentStorage {
	/**
	 * The map containing all buffers for all active torrents
	 */
	private final Map<Torrent, ByteBuffer> buffers = new HashMap<>();

	@Override
	public boolean write(Torrent torrent, Range range, ByteBuffer data)
			throws IOException {
		final ByteBuffer buffer = getBuffer(torrent);
		synchronized (buffer) {
			buffer.position((int) range.getStart());
			buffer.put(data);
		}
		return true;
	}

	@Override
	public ByteBuffer read(Torrent torrent, Range range) throws IOException {
		final ByteBuffer store = getBuffer(torrent);
		synchronized (store) {
			return (ByteBuffer) ((ByteBuffer) store.position((int) range
					.getStart())).slice().limit((int) range.getLength());
		}
	}

	/**
	 * Tries to return an existing buffer for the <code>torrent</code>. If none
	 * is found, creates a new buffer and returns it.
	 * 
	 * @param torrent
	 *            the torrent instance
	 * @return an {@link ByteBuffer} allocated with enough size to store all the
	 *         torrent data into it
	 */
	private ByteBuffer getBuffer(Torrent torrent) {
		ByteBuffer buffer = buffers.get(torrent);
		if (buffer == null) {
			buffer = ByteBuffer.allocateDirect((int) torrent.getTorrentSize());
			buffers.put(torrent, buffer);
		}
		return buffer;
	}
}
