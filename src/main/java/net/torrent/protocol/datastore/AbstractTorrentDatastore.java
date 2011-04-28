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
package net.torrent.protocol.datastore;

import java.io.IOException;
import java.security.MessageDigest;

import net.torrent.protocol.datastore.impl.FileAwareTorrentDatastore;
import net.torrent.torrent.TorrentPiece;

/**
 * Abstract implementation of {@link TorrentDatastore}. The only implemented
 * method is {@link TorrentDatastore#checksum(TorrentPiece)} since it always the
 * same.
 * <p>
 * Since it is noticeable that this interface does not use the concept of files,
 * it might be much more useful implementing {@link FileAwareTorrentDatastore}
 * class instead of this one.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public abstract class AbstractTorrentDatastore implements TorrentDatastore {
	@Override
	public boolean checksum(TorrentPiece piece) throws IOException {
		final MessageDigest digest = piece.getHash().getType()
				.getMessageDigest();
		if (digest == null)
			throw new NullPointerException("Digest is null");
		byte[] hash = digest.digest(this.read(piece.asSinglePart()).array());
		return piece.getHash().compare(hash);
	}

}
