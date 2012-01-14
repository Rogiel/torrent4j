package com.torrent4j.model.metadata;

import java.io.IOException;

import com.torrent4j.util.bencoding.BList;
import com.torrent4j.util.bencoding.BMap;

public class MetadataFile {
	private String fileName;
	private long length;
	private String hash;

	public MetadataFile(BMap file) throws IOException {
		if (file.get("path") != null) {
			final BList path = file.getList("path");
			final StringBuilder builder = new StringBuilder();
			for (final Object pathPart : path) {
				builder.append(new String((byte[]) pathPart)).append("/");
			}
			this.fileName = builder.substring(0, builder.length() - 1);
		} else {
			this.fileName = file.getString("name");
		}
		this.length = file.getLong("length");
		this.hash = file.getString("md5sum");
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
