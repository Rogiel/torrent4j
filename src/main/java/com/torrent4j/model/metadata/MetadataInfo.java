package com.torrent4j.model.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.torrent4j.util.bencoding.BList;
import com.torrent4j.util.bencoding.BMap;

public class MetadataInfo {
	private int pieceLength;
	private byte[] pieceHashes;
	private boolean privateTorrent;

	private String name;
	private List<MetadataFile> files = new ArrayList<>();

	public MetadataInfo(BMap dictionary) throws IOException {
		this.pieceLength = dictionary.getInteger("piece length");
		this.pieceHashes = (byte[]) dictionary.get("pieces");
		if (dictionary.get("private") != null)
			this.privateTorrent = dictionary.getInteger("private") == 1;
		this.name = dictionary.getString("name");

		if (dictionary.get("files") != null) {
			final BList files = dictionary
					.getList("files");
			for (final Object file : files) {
				this.files.add(new MetadataFile((BMap) file));
			}
		} else {
			this.files.add(new MetadataFile(dictionary));
		}
	}

	public int getPieceLength() {
		return pieceLength;
	}

	public void setPieceLength(int pieceLength) {
		this.pieceLength = pieceLength;
	}

	public byte[] getPieceHashes() {
		return pieceHashes;
	}

	public void setPieceHashes(byte[] pieceHashes) {
		this.pieceHashes = pieceHashes;
	}

	public boolean isPrivateTorrent() {
		return privateTorrent;
	}

	public void setPrivateTorrent(boolean privateTorrent) {
		this.privateTorrent = privateTorrent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MetadataFile> getFiles() {
		return files;
	}

	public void setFiles(List<MetadataFile> files) {
		this.files = files;
	}

	public long getLength() {
		long length = 0;
		for (final MetadataFile file : files) {
			length += file.getLength();
		}
		return length;
	}
}
