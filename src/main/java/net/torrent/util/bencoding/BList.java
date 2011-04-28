package net.torrent.util.bencoding;

import java.util.List;

/**
 * Representation of a bencoded list.
 * 
 * @author Bytekeeper
 * 
 */
public interface BList extends List<Object> {
	String getString(int index) throws BTypeException;

	Integer getInteger(int index) throws BTypeException;

	Long getLong(int index) throws BTypeException;

	BList getList(int index) throws BTypeException;

	BMap getMap(int index) throws BTypeException;
}
