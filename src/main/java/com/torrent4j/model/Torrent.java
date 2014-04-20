package com.torrent4j.model;

import static com.torrent4j.util.HashType.MD5;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.torrent4j.TorrentController;
import com.torrent4j.model.metadata.MetadataFile;
import com.torrent4j.model.metadata.MetadataInfo;
import com.torrent4j.model.metadata.TorrentMetadata;
import com.torrent4j.net.TorrentProtocol;
import com.torrent4j.strategy.TorrentStrategy;
import com.torrent4j.strategy.standard.StandardTorrentStrategy;
import com.torrent4j.util.Range;
import com.torrent4j.util.bencoding.BDecoder;
import com.torrent4j.util.bencoding.BMap;

/**
 * The main Torrent instance class. With this class, all torrent state is stored
 * and external interaction can be performed.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class Torrent {
	/**
	 * The torrent strategy
	 */
	private final TorrentStrategy strategy;

	/**
	 * The torrent meta data
	 */
	private final TorrentMetadata metadata;
	/**
	 * The torrent hash
	 */
	private final TorrentHash hash;

	/**
	 * The torrent pieces
	 */
	private final List<TorrentPiece> pieces = new ArrayList<>();
	/**
	 * The torrent files
	 */
	private final List<TorrentFile> files = new ArrayList<>();

	/**
	 * The torrent traffic control
	 */
	private final TorrentTrafficControl trafficControl = new TorrentTrafficControl(this);
	/**
	 * The torrent swarm (list of all peers)
	 */
	private final TorrentSwarm swarm = new TorrentSwarm(this);
	/**
	 * The torrent already downloaded pieces
	 */
	private final TorrentCompletePieces completedPieces;

	/**
	 * The current controller for this torrent object. If not attached to any
	 * controller, its value is <code>null</code>.
	 */
	private TorrentController controller;

	/**
	 * Creates a new torrent instance
	 * 
	 * @param strategy
	 *            the torrent strategy
	 * @param metadata
	 *            the torrent meta data
	 */
	public Torrent(TorrentStrategy strategy, TorrentMetadata metadata) {
		this.strategy = strategy;
		this.metadata = metadata;
		this.hash = new TorrentHash(this, metadata.getInfoHash());

		// parse file list
		final MetadataInfo info = metadata.getInfo();
		final byte[] pieceHashes = info.getPieceHashes();
		final int pieces = pieceHashes.length / 20;
		long piecesLength = 0;
		for (int i = 0; i < pieces; i++) {
			final byte[] hash = Arrays.copyOfRange(pieceHashes, i * 20,
					(i + 1) * 20);
			final int length = (int) (i + 1 == pieces ? (info.getLength() - piecesLength)
					: info.getPieceLength());

			this.pieces.add(new TorrentPiece(this, hash, i, info
					.getPieceLength() * i, length));
			piecesLength += length;
		}

		final Path torrentPath = Paths.get(info.getName());
		long offset = 0;
		for (final MetadataFile metaFile : info.getFiles()) {
			byte[] hash = null;
			if (metaFile.getHash() != null)
				hash = MD5.fromString(metaFile.getHash());
			final List<TorrentPiece> filePieces = getPieces(Range
					.getRangeByLength(offset, metaFile.getLength()));
			final TorrentFile file = new TorrentFile(this, offset,
					metaFile.getLength(), filePieces,
					torrentPath.resolve(metaFile.getFileName()), hash);
			files.add(file);
			for (final TorrentPiece piece : filePieces) {
				piece.addFile(file);
			}
			offset += metaFile.getLength();
		}

		completedPieces = new TorrentCompletePieces(this);

		// try {
		// this.localPeer.setPeerID(new String(Hex
		// .decodeHex("851054102530302d9c640cd409c769266ad3a04f"
		// .toCharArray())));
		// } catch (DecoderException e) {
		// }
	}

	/**
	 * Creates a new torrent instance with {@link StandardTorrentStrategy} as
	 * default strategy
	 * 
	 * @param metadata
	 *            the torrent meta data
	 */
	public Torrent(TorrentMetadata metadata) {
		this(new StandardTorrentStrategy(), metadata);
	}

	/**
	 * @return this torrent's strategy
	 */
	public TorrentStrategy getStrategy() {
		return strategy;
	}

	/**
	 * @return this torrent's meta data
	 */
	public TorrentMetadata getMetadata() {
		return metadata;
	}

	/**
	 * @return this torrent's hash
	 */
	public TorrentHash getHash() {
		return hash;
	}

	/**
	 * @return this torrent's pieces
	 */
	public List<TorrentPiece> getPieces() {
		return pieces;
	}

	/**
	 * Determines all the pieces that contains at least one byte inside the
	 * requested range.
	 * 
	 * @param range
	 *            the range to look for pieces
	 * @return the pieces inside the requested <code>range</code>
	 */
	public List<TorrentPiece> getPieces(Range range) {
		final List<TorrentPiece> pieces = new ArrayList<>();
		for (final TorrentPiece piece : this.pieces) {
			if (piece.getTorrentRange().intersects(range))
				pieces.add(piece);
		}
		return pieces;
	}

	/**
	 * @param index
	 *            the piece index
	 * @return the piece at the requested index, if any.
	 */
	public TorrentPiece getPiece(int index) {
		if ((index + 1) > pieces.size())
			return null;
		return pieces.get(index);
	}

	/**
	 * @return all the files for this torrent
	 */
	public List<TorrentFile> getFiles() {
		return files;
	}

	/**
	 * Determines all the files that contains at least one byte inside the
	 * requested range.
	 * 
	 * @param range
	 *            the range to search for files
	 * @return the list of files inside the requested <code>range</code>
	 */
	public List<TorrentFile> getFiles(Range range) {
		final List<TorrentFile> files = new ArrayList<>();
		for (final TorrentFile file : this.files) {
			if (file.getTorrentRange().intersects(range))
				files.add(file);
		}
		return files;
	}

	/**
	 * @return the sum of all file sizes
	 */
	public long getTorrentSize() {
		long size = 0;
		for (final TorrentFile file : files) {
			size += file.getLength();
		}
		return size;
	}

	/**
	 * @return the trafficControl
	 */
	public TorrentTrafficControl getTrafficControl() {
		return trafficControl;
	}

	/**
	 * @return the torrent swarm
	 */
	public TorrentSwarm getSwarm() {
		return swarm;
	}

	/**
	 * @return the downloaded pieces
	 */
	public TorrentCompletePieces getCompletedPieces() {
		return completedPieces;
	}

	/**
	 * @return this torrent's controller, if any.
	 */
	public TorrentController getController() {
		return controller;
	}

	/**
	 * Sets the current controller. <b>This method should not be invoked
	 * manually!</b>
	 * 
	 * @param controller
	 *            the controller to set
	 */
	public void setController(TorrentController controller) {
		this.controller = controller;
	}

	/**
	 * @return the torrent protocol
	 */
	public TorrentProtocol getProtocol() {
		return controller.getProtocol();
	}

	@Override
	public String toString() {
		return "Torrent [hash=" + hash + "]";
	}

	/**
	 * Load an torrent from an {@link InputStream}
	 * 
	 * @param in
	 *            the {@link InputStream}
	 * @return the loaded {@link Torrent} instance
	 * @throws IOException
	 *             if any error occur while reading the torrent file
	 */
	public static Torrent load(InputStream in) throws IOException {
		try(BDecoder bin = new BDecoder(in)) {
			final Object node = bin.readElement();
			final TorrentMetadata metadata = new TorrentMetadata((BMap) node);
			return new Torrent(metadata);
		}
	}

	/**
	 * Load an torrent from an {@link InputStream}
	 * 
	 * @param file
	 *            the file {@link Path}
	 * @return the loaded {@link Torrent} instance
	 * @throws IOException
	 *             if any error occur while reading the torrent file
	 */
	public static Torrent load(Path file) throws IOException {
		return load(Files.newInputStream(file));
	}

	/**
	 * Load an torrent from an {@link File}
	 * 
	 * @param file
	 *            the {@link File}
	 * @return the loaded {@link Torrent} instance
	 * @throws IOException
	 *             if any error occur while reading the torrent file
	 */
	public static Torrent load(File file) throws IOException {
		if (file == null)
			throw new InvalidParameterException("File cannot be null");
		return load(new FileInputStream(file));
	}

	/**
	 * Load an torrent from an {@link Byte} array
	 * 
	 * @param content
	 *            the {@link Byte} array
	 * @return the loaded {@link Torrent} instance
	 * @throws IOException
	 *             if any error occur while reading the torrent file
	 */
	public static Torrent load(byte[] content) throws IOException {
		return load(new ByteArrayInputStream(content));
	}

	/**
	 * Load an torrent from an {@link File}
	 * 
	 * @param url
	 *            the {@link URL}
	 * @return the loaded {@link Torrent} instance
	 * @throws IOException
	 *             if any error occur while reading the torrent file
	 */
	public static Torrent load(URL url) throws IOException {
		if (url == null)
			throw new InvalidParameterException("File cannot be null");
		return load(url.openStream());
	}
}
