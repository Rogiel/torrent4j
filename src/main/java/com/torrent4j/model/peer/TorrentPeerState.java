package com.torrent4j.model.peer;

import java.util.Date;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPieceBlock;

public class TorrentPeerState {
	private final TorrentPeer peer;

	private TorrentPeerInterest remoteInterest = TorrentPeerInterest.NOT_INTERESTED;
	private TorrentPeerInterest localInterest = TorrentPeerInterest.NOT_INTERESTED;

	private TorrentPeerChoking remoteChoked = TorrentPeerChoking.CHOKED;
	private TorrentPeerChoking locallyChoked = TorrentPeerChoking.CHOKED;

	private TorrentPieceBlock downloadRequestedBlock;
	private Date downloadRequestedDate;
	private TorrentPieceBlock lastDownloadedBlock;
	private Date lastDownloadedBlockDate;

	private TorrentPieceBlock uploadRequestedBlock;
	private Date uploadRequestedDate;
	private TorrentPieceBlock lastUploadedBlock;
	private Date lastUploadedBlockDate;

	public TorrentPeerState(TorrentPeer peer) {
		this.peer = peer;
	}

	/**
	 * @return the remoteInterest
	 */
	public TorrentPeerInterest getRemoteInterest() {
		return remoteInterest;
	}

	/**
	 * @return the remoteInterest
	 */
	public boolean isRemotellyInterested() {
		return remoteInterest == TorrentPeerInterest.INTERESTED;
	}

	/**
	 * @param remoteInterest
	 *            the remoteInterest to set
	 */
	public void setRemoteInterest(TorrentPeerInterest remoteInterest) {
		this.remoteInterest = remoteInterest;
	}

	/**
	 * @return the localInterest
	 */
	public TorrentPeerInterest getLocalInterest() {
		return localInterest;
	}

	/**
	 * @return the localInterest
	 */
	public boolean isLocalllyInterested() {
		return localInterest == TorrentPeerInterest.INTERESTED;
	}

	/**
	 * @param localInterest
	 *            the localInterest to set
	 */
	public void setLocalInterest(TorrentPeerInterest localInterest) {
		this.localInterest = localInterest;
	}

	/**
	 * @return the remoteChoked
	 */
	public TorrentPeerChoking getRemoteChoked() {
		return remoteChoked;
	}

	/**
	 * @return the remoteChoked
	 */
	public boolean isRemotellyChoked() {
		return remoteChoked == TorrentPeerChoking.CHOKED;
	}

	/**
	 * @param remoteChoked
	 *            the remoteChoked to set
	 */
	public void setRemoteChoked(TorrentPeerChoking remoteChoked) {
		this.remoteChoked = remoteChoked;
	}

	/**
	 * @return the locallyChoked
	 */
	public TorrentPeerChoking getLocallyChoked() {
		return locallyChoked;
	}

	/**
	 * @return the locallyChoked
	 */
	public boolean isLocallyChoked() {
		return locallyChoked == TorrentPeerChoking.CHOKED;
	}

	/**
	 * @param locallyChoked
	 *            the locallyChoked to set
	 */
	public void setLocallyChoked(TorrentPeerChoking locallyChoked) {
		this.locallyChoked = locallyChoked;
	}

	/**
	 * @return the downloadRequestedBlock
	 */
	public TorrentPieceBlock getDownloadRequestedBlock() {
		return downloadRequestedBlock;
	}
	
	/**
	 * @return the downloadRequestedBlock
	 */
	public boolean hasDownloadRequestedBlock() {
		return downloadRequestedBlock != null;
	}

	/**
	 * @param downloadRequestedBlock
	 *            the downloadRequestedBlock to set
	 */
	public void setDownloadRequestedBlock(
			TorrentPieceBlock downloadRequestedBlock) {
		this.downloadRequestedBlock = downloadRequestedBlock;
	}

	/**
	 * @return the downloadRequestedDate
	 */
	public Date getDownloadRequestedDate() {
		return downloadRequestedDate;
	}

	/**
	 * @param downloadRequestedDate
	 *            the downloadRequestedDate to set
	 */
	public void setDownloadRequestedDate(Date downloadRequestedDate) {
		this.downloadRequestedDate = downloadRequestedDate;
	}

	/**
	 * @return the lastDownloadedBlock
	 */
	public TorrentPieceBlock getLastDownloadedBlock() {
		return lastDownloadedBlock;
	}

	/**
	 * @param lastDownloadedBlock
	 *            the lastDownloadedBlock to set
	 */
	public void setLastDownloadedBlock(TorrentPieceBlock lastDownloadedBlock) {
		this.lastDownloadedBlock = lastDownloadedBlock;
	}

	/**
	 * @return the lastDownloadedBlockDate
	 */
	public Date getLastDownloadedBlockDate() {
		return lastDownloadedBlockDate;
	}

	/**
	 * @param lastDownloadedBlockDate
	 *            the lastDownloadedBlockDate to set
	 */
	public void setLastDownloadedBlockDate(Date lastDownloadedBlockDate) {
		this.lastDownloadedBlockDate = lastDownloadedBlockDate;
	}

	/**
	 * @return the uploadRequestedBlock
	 */
	public TorrentPieceBlock getUploadRequestedBlock() {
		return uploadRequestedBlock;
	}
	
	/**
	 * @return the uploadRequestedBlock
	 */
	public boolean hasUploadRequestedBlock() {
		return uploadRequestedBlock != null;
	}

	/**
	 * @param uploadRequestedBlock
	 *            the uploadRequestedBlock to set
	 */
	public void setUploadRequestedBlock(TorrentPieceBlock uploadRequestedBlock) {
		this.uploadRequestedBlock = uploadRequestedBlock;
	}

	/**
	 * @return the uploadRequestedDate
	 */
	public Date getUploadRequestedDate() {
		return uploadRequestedDate;
	}

	/**
	 * @param uploadRequestedDate
	 *            the uploadRequestedDate to set
	 */
	public void setUploadRequestedDate(Date uploadRequestedDate) {
		this.uploadRequestedDate = uploadRequestedDate;
	}

	/**
	 * @return the lastUploadedBlock
	 */
	public TorrentPieceBlock getLastUploadedBlock() {
		return lastUploadedBlock;
	}

	/**
	 * @param lastUploadedBlock
	 *            the lastUploadedBlock to set
	 */
	public void setLastUploadedBlock(TorrentPieceBlock lastUploadedBlock) {
		this.lastUploadedBlock = lastUploadedBlock;
	}

	/**
	 * @return the lastUploadedBlockDate
	 */
	public Date getLastUploadedBlockDate() {
		return lastUploadedBlockDate;
	}

	/**
	 * @param lastUploadedBlockDate
	 *            the lastUploadedBlockDate to set
	 */
	public void setLastUploadedBlockDate(Date lastUploadedBlockDate) {
		this.lastUploadedBlockDate = lastUploadedBlockDate;
	}

	public TorrentPeer getPeer() {
		return peer;
	}

	public Torrent getTorrent() {
		return peer.getTorrent();
	}
}
