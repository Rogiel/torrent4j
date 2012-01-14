package com.torrent4j.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentFile;
import com.torrent4j.util.Range;

/**
 * Provides an abstraction to storage implementations that allows data to be
 * stored into multiple files, respecting data stored into the original .torrent
 * meta data file. {@link #write(Torrent, Range, ByteBuffer)} and
 * {@link #read(Torrent, Range)} detect the file (or files) contained in the
 * requested range and for each file that needs to be written or redden, a call
 * to {@link #write(TorrentFile, Range, ByteBuffer)} and
 * {@link #read(TorrentFile, Range, ByteBuffer)} is made. On all cases, the same
 * buffer is sent for several calls.
 * <p>
 * If any error occur while reading any of the files, the whole reading process
 * fails.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public abstract class FileAwareTorrentStorage extends AbstractTorrentStorage
		implements TorrentStorage {
	@Override
	public final boolean write(Torrent torrent, Range dataRange, ByteBuffer data)
			throws IOException {
		final List<TorrentFile> files = torrent.getFiles(dataRange);
		for (final TorrentFile file : files) {
			final Range rangeOnFile = file.getTorrentRange().intersection(
					dataRange);
			final Range range = file.getFileRange().intersection(
					Range.getRangeByLength(
							rangeOnFile.getStart() - file.getOffset(),
							rangeOnFile.getLength()));
			data.limit((int) (data.position() + range.getLength()));
			if (!this.write(file, range, data))
				return false;
		}
		return true;
	}

	@Override
	public final ByteBuffer read(Torrent torrent, Range dataRange)
			throws IOException {
		final List<TorrentFile> files = torrent.getFiles(dataRange);
		final ByteBuffer data = ByteBuffer
				.allocate((int) dataRange.getLength());
		for (final TorrentFile file : files) {
			final Range rangeOnFile = file.getTorrentRange().intersection(
					dataRange);
			final Range range = file.getFileRange().intersection(
					Range.getRangeByLength(
							rangeOnFile.getStart() - file.getOffset(),
							rangeOnFile.getLength()));
			data.limit((int) (data.position() + range.getLength()));
			if (!this.read(file, range, data))
				return null;
		}
		data.flip();
		return data;
	}

	/**
	 * Writes <code>data</code> into the given <code>file</code> at the
	 * requested <code>range</code>.
	 * 
	 * @param file
	 *            the file to write the data to
	 * @param range
	 *            the range to write the data to
	 * @param data
	 *            the data to be written
	 * @return <code>true</code> if write was successful
	 * @throws IOException
	 *             if any exception occur while writing data
	 */
	protected abstract boolean write(TorrentFile file, Range range,
			ByteBuffer data) throws IOException;

	/**
	 * Reads <code>data</code> from the given <code>file</code> in the requested
	 * <code>range</code>.
	 * 
	 * @param file
	 *            the file to read the data from
	 * @param range
	 *            the range to read the data from
	 * @param data
	 *            the data to be redden
	 * @return <code>true</code> if read was successful
	 * @throws IOException
	 *             if any exception occur while reading data
	 */
	protected abstract boolean read(TorrentFile file, Range range,
			ByteBuffer data) throws IOException;
}
