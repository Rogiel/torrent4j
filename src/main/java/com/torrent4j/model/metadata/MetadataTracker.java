package com.torrent4j.model.metadata;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.torrent4j.util.bencoding.BList;

public class MetadataTracker {
	private String url;
	private List<String> backupUrls;

	public MetadataTracker(String url, List<String> backupUrls) {
		this.url = url;
		this.backupUrls = backupUrls;
	}

	public MetadataTracker(String url, String... backupUrls) {
		this(url, Arrays.asList(backupUrls));
	}

	public MetadataTracker(String url) {
		this.url = url;
	}

	public MetadataTracker(BList urls) throws IOException {
		this.url = urls.getString(0);
		if(urls.size() > 1) {
			//TODO
		}
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public List<String> getBackupURLs() {
		return backupUrls;
	}

	public void setBackupURLs(List<String> backupUrls) {
		this.backupUrls = backupUrls;
	}
}
