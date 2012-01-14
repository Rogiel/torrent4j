package com.torrent4j.model;

/**
 * An {@link AbstractTorrentPiecesContainer} dedicated to storing an
 * {@link Torrent} already downloaded pieces.
 * <p>
 * This instance overrides the {@link #addPiece(TorrentPiece)} and
 * {@link #removePiece(TorrentPiece)} in order to set
 * {@link TorrentPiece#setDownloaded(boolean)} accordingly, after that, the call
 * is redirected to the super implementation.
 * 
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class TorrentCompletePieces extends AbstractTorrentPiecesContainer {
	/**
	 * Creates a new instance
	 * 
	 * @param torrent
	 *            the torrent
	 */
	public TorrentCompletePieces(Torrent torrent) {
		super(torrent);
	}

	@Override
	public void addPiece(TorrentPiece piece) {
		super.addPiece(piece);
		piece.setDownloaded(true);
	}

	@Override
	public void removePiece(TorrentPiece piece) {
		super.removePiece(piece);
		piece.setDownloaded(false);
	}

}
