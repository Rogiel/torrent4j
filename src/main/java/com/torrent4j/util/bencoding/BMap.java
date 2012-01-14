package com.torrent4j.util.bencoding;

import java.util.Map;

/**
 * Representation of a bencoded map.
 * 
 * @author Bytekeeper
 * 
 */
public interface BMap extends Map<String, Object> {
	String getString(String key) throws BTypeException;

	Integer getInteger(String key) throws BTypeException;

	Long getLong(String key) throws BTypeException;

	BList getList(String key) throws BTypeException;

	BMap getMap(String key) throws BTypeException;
}
