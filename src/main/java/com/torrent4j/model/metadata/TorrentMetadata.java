package com.torrent4j.model.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.torrent4j.util.HashType;
import com.torrent4j.util.bencoding.BEncoder;
import com.torrent4j.util.bencoding.BList;
import com.torrent4j.util.bencoding.BMap;

public class TorrentMetadata {
	private MetadataInfo info;
	private byte[] infoHash;

	private List<MetadataTracker> trackers = new ArrayList<>();
	private Date creationDate;

	public TorrentMetadata(BMap dictionary) throws IOException {
		this.trackers
				.add(new MetadataTracker(dictionary.getString("announce")));
		final BList announceListNode = dictionary.getList("announce-list");
		if (announceListNode != null) {
			for (final Object trackerGroup : announceListNode) {
				trackers.add(new MetadataTracker((BList) trackerGroup));
			}
		}
		if (dictionary.get("creation date") != null)
			this.creationDate = new Date(
					dictionary.getInteger("creation date") * 1000);

		this.infoHash = HashType.SHA1.hash(BEncoder.bencode(dictionary
				.get("info")));
		this.info = new MetadataInfo(dictionary.getMap("info"));
	}

	public byte[] getInfoHash() {
		return infoHash;
	}

	public void setInfoHash(byte[] infoHash) {
		this.infoHash = infoHash;
	}

	public MetadataInfo getInfo() {
		return info;
	}

	public void setInfo(MetadataInfo info) {
		this.info = info;
	}

	public List<MetadataTracker> getTrackers() {
		return trackers;
	}

	public MetadataTracker getMainTracker() {
		return trackers.get(0);
	}

	public void setTrackers(List<MetadataTracker> trackers) {
		this.trackers = trackers;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
