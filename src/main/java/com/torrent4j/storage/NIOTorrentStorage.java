package com.torrent4j.storage;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.torrent4j.model.TorrentFile;
import com.torrent4j.util.Range;

/**
 * This {@link TorrentStorage} instance implements
 * {@link FileAwareTorrentStorage}, and thus, stores data into separated files
 * backed by an NIO.2 {@link SeekableByteChannel} available on Java 7. The
 * implementation tries to respect as much as possible from the original file
 * and folder structure, however, if any invalid name is found, beware that this
 * implementation might override it.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class NIOTorrentStorage extends FileAwareTorrentStorage {
	/**
	 * The place where data files are stored
	 */
	private final Path root;

	/**
	 * Creates a new instance
	 * 
	 * @param root
	 *            the place where torrent data is stored
	 */
	public NIOTorrentStorage(Path root) {
		this.root = root;
	}

	/**
	 * Creates a new instance which stores data into the current working
	 * directory
	 */
	public NIOTorrentStorage() {
		this(Paths.get(".").toAbsolutePath());
	}

	@Override
	protected synchronized boolean write(TorrentFile file, Range range,
			ByteBuffer data) throws IOException {
		try (final SeekableByteChannel channel = openChannel(file, CREATE,
				WRITE)) {
			if (channel == null)
				return false;
			channel.position(range.getStart());
			return channel.write(data) > 0;
		}
	}

	@Override
	protected synchronized boolean read(TorrentFile file, Range range,
			ByteBuffer data) throws IOException {
		try (final SeekableByteChannel channel = openChannel(file, READ)) {
			if (channel == null)
				return false;
			channel.position(range.getStart());
			return channel.read(data) > 0;
		}
	}

	/**
	 * Opens a new channel ready for writing or reading (defines by
	 * <code>modes</code>) data into the torrent <code>file</code>.
	 * 
	 * @param file
	 *            the torrent file
	 * @param modes
	 *            the file open mode
	 * @return an {@link SeekableByteChannel}
	 * @throws IOException
	 *             if any error is thrown by NIO.2
	 */
	private SeekableByteChannel openChannel(TorrentFile file,
			OpenOption... modes) throws IOException {
		final Path filePath = root.resolve(file.getFileName());
		try {
			SeekableByteChannel channel = Files.newByteChannel(filePath, modes);
			return channel;
		} catch (NoSuchFileException e) {
			return null;
		}
	}
}
