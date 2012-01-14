package com.torrent4j.storage;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.torrent4j.model.TorrentPiece;
import com.torrent4j.util.Hash;

/**
 * Abstract {@link TorrentStorage} that implements universal that methods that
 * very likely don't need to be override by implementations.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public abstract class AbstractTorrentStorage implements TorrentStorage {
	@Override
	public Hash checksum(TorrentPiece piece) throws IOException {
		final ByteBuffer buffer = read(piece.getTorrent(),
				piece.getTorrentRange());
		if (buffer == null)
			return null;
		return new Hash(piece.getHash().getType(), piece.getHash().getType()
				.hash(buffer));
	}
}
