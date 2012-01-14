package com.torrent4j.storage;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPiece;
import com.torrent4j.util.Hash;
import com.torrent4j.util.Range;

/**
 * Provides an easy interface to access torrent data in an linear way. All
 * torrent data is accessed by an single addressing, starting from the first
 * piece to that last one. Implementations can create abstractions to support
 * files (see {@link FileAwareTorrentStorage}) or can store data into a huge
 * in-memory buffer (see {@link InMemoryTorrentStorage}).
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface TorrentStorage {
	/**
	 * Writes data into the underlying storage
	 * 
	 * @param torrent
	 *            the torrent
	 * @param torrentRange
	 *            the range in which the data is located
	 * @param data
	 *            the data itself
	 * @return <code>true</code> if the write was successful
	 * @throws IOException
	 *             if any error occur while writing
	 */
	boolean write(Torrent torrent, Range torrentRange, ByteBuffer data)
			throws IOException;

	/**
	 * Reads data from the underlying storage
	 * 
	 * @param torrent
	 *            the torrent
	 * @param torrentRange
	 *            the range in which the data should be read from
	 * @return an {@link ByteBuffer}
	 * @throws IOException
	 *             if any error occur while reading
	 */
	ByteBuffer read(Torrent torrent, Range torrentRange) throws IOException;

	/**
	 * Calculates the checksum of an given piece
	 * 
	 * @param piece
	 *            the piece
	 * @return the {@link Hash} for the requested piece
	 * @throws IOException
	 *             if any error occur while reading data or calculating the
	 *             checksum
	 */
	Hash checksum(TorrentPiece piece) throws IOException;
}
