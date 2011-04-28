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
package net.torrent.torrent;

import java.io.File;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.torrent.torrent.TorrentHash.HashType;
import net.torrent.util.bencoding.BEncodedOutputStream;
import net.torrent.util.bencoding.BList;
import net.torrent.util.bencoding.BMap;
import net.torrent.util.bencoding.BTypeException;

public class TorrentInfo {
	private static final String PIECE_LENGTH = "piece length";
	private static final String PIECES = "pieces";
	private static final String NAME = "name";
	private static final String LENGTH = "length";
	private static final String FILES = "files";
	private static final String PATH = "path";
	private static final String PRIVATE = "private";

	private final Torrent torrent;
	private final TorrentHash infohash;

	private final List<TorrentFile> files = new ArrayList<TorrentFile>();
	private final TorrentPiece[] pieces;

	private long size;
	private final int pieceLength;
	private final boolean privTracker;
	private final TorrentType type;

	public enum TorrentType {
		SINGLE_FILE, DIRECTORY;
	}

	private final String directoryName;

	public TorrentInfo(Torrent torrent, TorrentHash infohash,
			TorrentPiece[] pieces, int pieceLength, boolean privTracker,
			TorrentType type, String directoryName) {
		this.torrent = torrent;
		this.infohash = infohash;
		this.pieces = pieces;
		this.pieceLength = pieceLength;
		this.privTracker = privTracker;
		this.type = type;
		this.directoryName = directoryName;
	}

	public TorrentInfo(Torrent torrent, BMap info) throws BTypeException {
		if (torrent == null)
			throw new InvalidParameterException("Torrent is null");
		if (info == null)
			throw new InvalidParameterException("Info BMap is null");
		this.torrent = torrent;

		infohash = calculateInfoHash(info);
		privTracker = Integer.valueOf(1).equals(info.getInteger(PRIVATE));
		pieceLength = ((BigInteger) info.get(PIECE_LENGTH)).intValue();

		String base = info.getString(NAME);
		if (base == null)
			throw new IllegalStateException("Missing a required key: " + NAME);
		if (info.containsKey(LENGTH)) { // single file mode
			type = TorrentType.SINGLE_FILE;
			directoryName = null;
			files.add(new TorrentFile(this, base, ((BigInteger) info
					.get(LENGTH)).longValue(), 0));
		} else {
			type = TorrentType.DIRECTORY;
			directoryName = base;

			long offset = 0;
			BList fList = info.getList(FILES);
			for (int i = 0; i < fList.size(); i++) {
				BMap file = fList.getMap(i);
				StringBuilder path = new StringBuilder();
				BList elements = file.getList(PATH);
				for (int j = 0; j < elements.size(); j++) {
					String element = elements.getString(j);
					if (path.length() > 0) {
						path.append(File.separatorChar);
					}
					path.append(element);
				}
				if (files.add(new TorrentFile(this, path.toString(),
						((BigInteger) file.get(LENGTH)).longValue(), offset))) {
					offset += ((BigInteger) file.get(LENGTH)).longValue();
				}
			}
		}
		calculateSize();

		byte[] hashes = (byte[]) info.get(PIECES);
		if (hashes == null)
			throw new IllegalStateException("Pieces hash is null");
		if (hashes.length % 20 != 0)
			throw new IllegalStateException(
					"Pieces hash dictionary has an invalid size: "
							+ hashes.length);
		pieces = new TorrentPiece[(hashes.length / 20)];
		for (int index = 0; index < pieces.length; index++) {
			int len = pieceLength;
			if (pieces.length - 1 == index) // last piece
				len = (int) (size - (pieceLength * (pieces.length - 1)));

			pieces[index] = new TorrentPiece(this, Arrays.copyOfRange(hashes,
					index * 20, index * 20 + 20), index, len);
		}
		// Arrays.sort(pieces, PieceIndexComparator.SHARED_INSTANCE);
	}

	private void calculateSize() {
		for (TorrentFile file : files) {
			size += file.getLength();
		}
	}

	private TorrentHash calculateInfoHash(Map<String, ?> info) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			sha1.update(BEncodedOutputStream.bencode(info));
			return new TorrentHash(sha1.digest(), HashType.SHA1);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public Torrent getTorrent() {
		return torrent;
	}

	public TorrentHash getInfoHash() {
		return infohash;
	}

	public List<TorrentFile> getFiles() {
		return files;
	}

	public TorrentPiece[] getPieces() {
		return pieces;
	}

	public int getPieceLength() {
		return pieceLength;
	}

	public boolean isPrivTracker() {
		return privTracker;
	}

	public TorrentType getType() {
		return type;
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public TorrentPiece getPiece(int index) {
		for (final TorrentPiece piece : pieces) {
			if (piece.getIndex() == index)
				return piece;
		}
		return null;
	}

	public TorrentPart getPart(TorrentPiece piece, int start, int len) {
		return piece.getPart(start, len);
	}

	public TorrentPart getPart(int index, int start, int len) {
		return getPart(getPiece(index), start, len);
	}
}
