package com.torrent4j.net;

public interface TorrentTrafficShaper {
	void update(long writeLimit, long readLimit);
	
	long getDownloadSpeed();
	long getUploadSpeed();
}
