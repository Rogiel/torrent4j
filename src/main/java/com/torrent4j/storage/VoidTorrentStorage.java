package com.torrent4j.storage;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPiece;
import com.torrent4j.util.Hash;
import com.torrent4j.util.Range;

/**
 * This {@link TorrentStorage} implementation is similar to a UNIX
 * <code>/dev/null</code> for writing and <code>/dev/zero</code> for reading.
 * Data on this storage are never persisted and are discarded immediately. All
 * reads return a {@link ByteBuffer} composed entirely from <code>0</code>.
 * <p>
 * Also, in order for checksums to match, when checksumming an piece, the piece
 * hash itself is returned. This means that the checksums will <b>never</b>
 * fail, no matter what, independent from data contents.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class VoidTorrentStorage implements TorrentStorage {
	@Override
	public boolean write(Torrent torrent, Range range, ByteBuffer data)
			throws IOException {
		return true;
	}

	@Override
	public ByteBuffer read(Torrent torrent, Range range) throws IOException {
		return ByteBuffer.allocate((int) range.getLength());
	}

	@Override
	public Hash checksum(TorrentPiece piece) {
		return piece.getHash();
	}
}
