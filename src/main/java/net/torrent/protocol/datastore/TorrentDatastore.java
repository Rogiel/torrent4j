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
import java.nio.ByteBuffer;

import net.torrent.protocol.datastore.impl.FileAwareTorrentDatastore;
import net.torrent.torrent.TorrentPart;
import net.torrent.torrent.TorrentPiece;

/**
 * The datastore is responsible for storing data downloaded from peers and read
 * them for further upload. The storage should be done as fast as possible,
 * since slow operations might and will slowdown download and upload rates.
 * <p>
 * Since it is noticeable that this interface does not use the concept of files,
 * it might be much more useful implementing {@link FileAwareTorrentDatastore}
 * class instead of this interface.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public interface TorrentDatastore {
	/**
	 * Read an segment of data relative to {@link TorrentPart part}.
	 * <p>
	 * <b>Warning</b>: Synchronization <b>MUST</b> be dealt by implementations.
	 * 
	 * @param part
	 *            the part readed
	 * @return {@link ByteBuffer} with the contents
	 * @throws IOException
	 *             if exception occur at IO level
	 */
	ByteBuffer read(TorrentPart part) throws IOException;

	/**
	 * Stores an segment of data relative to {@link TorrentPart part}.
	 * <p>
	 * <b>Warning</b>: Synchronization <b>MUST</b> be dealt by implementations.
	 * 
	 * @param part
	 *            the part to written
	 * @param buffer
	 *            the buffer containing the data
	 * @return {@link ByteBuffer} with the contents
	 * @throws IOException
	 *             if exception occur at IO level
	 */
	boolean write(TorrentPart part, ByteBuffer buffer) throws IOException;

	/**
	 * Calculates the checksum of an {@link TorrentPiece piece}.
	 * 
	 * @param piece
	 *            the piece to be tested
	 * @return true if checksum is correct, false otherwise.
	 * @throws IOException
	 *             if exception occur at IO level
	 */
	boolean checksum(TorrentPiece piece) throws IOException;
}
