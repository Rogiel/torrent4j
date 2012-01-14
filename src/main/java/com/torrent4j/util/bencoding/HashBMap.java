package com.torrent4j.util.bencoding;

import java.util.HashMap;

public class HashBMap extends HashMap<String, Object> implements BMap {
	private static final long serialVersionUID = 1L;

	@Override
	public Integer getInteger(String key) {
		return (Integer) get(key);
	}

	@Override
	public BList getList(String key) {
		return (BList) get(key);
	}

	@Override
	public Long getLong(String key) {
		return (Long) get(key);
	}

	@Override
	public BMap getMap(String key) {
		return (BMap) get(key);
	}

	@Override
	public String getString(String key) {
		return (String) get(key);
	}
}
