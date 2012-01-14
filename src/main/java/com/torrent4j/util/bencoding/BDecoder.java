package com.torrent4j.util.bencoding;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Helper class to decode "bencoded" data.
 * 
 * @author Dennis "Bytekeeper" Waldherr
 * 
 */
public class BDecoder extends InputStream {
	private static final int MAX_STRING_LENGTH = 1024 * 1024;

	private final PushbackInputStream in;

	public BDecoder(InputStream in) {
		this.in = new PushbackInputStream(in);
	}

	/**
	 * Utility method to decode exactly one bencoded element.
	 * 
	 * @param bencode
	 * @return
	 * @throws BDecodingException
	 * @throws IOException
	 */
	public static Object bdecode(byte[] bencode) throws BDecodingException {
		BDecoder in = new BDecoder(new ByteArrayInputStream(bencode));
		try {
			return in.readElement();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads one bencoded element.
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws BDecodingException
	 */
	public Object readElement() throws IOException, BDecodingException {
		return bdecode();
	}

	/**
	 * Converts a string result from bencoding to a java String. If the data is
	 * already a String or null it is simply returned.
	 * 
	 * @param data
	 * @return
	 * @throws BTypeException
	 */
	private static String stringOf(Object data) throws BTypeException {
		if (data == null) {
			return null;
		}
		try {
			if (data instanceof byte[]) {
				return new String((byte[]) data, "UTF-8");
			} else if (data instanceof String) {
				return (String) data;
			} else {
				throw new BTypeException("Unsupported type: " + data.getClass());
			}
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	/**
	 * Converts a constructed or decoded integer to a java Integer. If the data
	 * is already an Integer or null it is simply returned.
	 * 
	 * @param data
	 * @return
	 * @throws BTypeException
	 */
	private static Integer intOf(Object data) throws BTypeException {
		if (data == null) {
			return null;
		}
		if (data instanceof BigInteger) {
			return ((BigInteger) data).intValue();
		} else if (data instanceof Integer) {
			return (Integer) data;
		} else {
			throw new BTypeException("Unsupported type: " + data.getClass());
		}
	}

	/**
	 * Converts a constructed or decoded integer to a java Integer. If the data
	 * is already an Integer or null it is simply returned.
	 * 
	 * @param data
	 * @return
	 * @throws BTypeException
	 */
	private static Long longOf(Object data) throws BTypeException {
		if (data == null) {
			return null;
		}
		if (data instanceof BigInteger) {
			return ((BigInteger) data).longValue();
		} else if (data instanceof Integer) {
			return Long.valueOf((Integer) data);
		} else if (data instanceof Long) {
			return (Long) data;
		} else {
			throw new BTypeException("Unsupported type: " + data.getClass());
		}
	}

	private Object bdecode() throws IOException, BDecodingException {
		int head = in.read();
		switch (head) {
		case -1:
			throw new EOFException();
		case 'l':
			return bdecodeList();
		case 'i':
			return bdecodeInteger();
		case 'd':
			return bdecodeDictionary();
		default:
			if (Character.isDigit(head)) {
				in.unread(head);
				return bdecodeString();
			}
		}
		throw new BDecodingException("Parameter is not bencoded data.");
	}

	private BMap bdecodeDictionary() throws IOException, BDecodingException {
		assert in != null;

		BMap map = new BMapImpl();
		int head;
		while ((head = in.read()) != 'e') {
			if (head < 0) {
				throw new EOFException();
			}
			in.unread(head);
			String key;
			try {
				key = stringOf(bdecodeString());
			} catch (BTypeException e) {
				throw new BDecodingException(e);
			}
			map.put(key, bdecode());
		}
		return map;
	}

	private byte[] bdecodeString() throws IOException, BDecodingException {
		assert in != null;
		int len = 0;
		int head;
		while ((head = in.read()) != ':') {
			if (head < 0) {
				throw new EOFException();
			}
			len = len * 10 + (head - '0');
			if (len > MAX_STRING_LENGTH) {
				throw new BDecodingException("Encoded string length exceeds "
						+ MAX_STRING_LENGTH + " bytes!");
			}
		}
		byte data[] = new byte[len];
		int off = 0;
		while (len > 0) {
			int read = in.read(data, off, len);
			len -= read;
			off += read;
		}
		return data;
	}

	private Object bdecodeInteger() throws IOException, BDecodingException {
		assert in != null;

		StringBuilder b = new StringBuilder();
		int head;
		while ((head = in.read()) != 'e') {
			if (head < 0) {
				throw new EOFException();
			}
			if (!Character.isDigit(head)) {
				throw new BDecodingException("Expected digit but got: " + head);
			}
			b.append((char) head);
		}
		return new BigInteger(b.toString());
	}

	private BList bdecodeList() throws IOException, BDecodingException {
		assert in != null;

		BList list = new BListImpl();
		int head;
		while ((head = in.read()) != 'e') {
			if (head < 0) {
				throw new EOFException();
			}
			in.unread(head);
			list.add(bdecode());
		}
		return list;
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	private static class BListImpl extends ArrayList<Object> implements BList {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Integer getInteger(int index) throws BTypeException {
			return intOf(get(index));
		}

		@Override
		public Long getLong(int index) throws BTypeException {
			return longOf(get(index));
		}

		@Override
		public String getString(int index) throws BTypeException {
			return stringOf(get(index));
		}

		@Override
		public BList getList(int index) throws BTypeException {
			Object tmp = get(index);
			if (tmp instanceof BList) {
				return (BList) tmp;
			}
			throw new BTypeException("Unexpected type: " + tmp.getClass());
		}

		@Override
		public BMap getMap(int index) throws BTypeException {
			Object tmp = get(index);
			if (tmp instanceof BMap) {
				return (BMap) tmp;
			}
			throw new BTypeException("Unexpected type: " + tmp.getClass());
		}

	}

	private static class BMapImpl extends HashMap<String, Object> implements
			BMap {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Integer getInteger(String key) throws BTypeException {
			return intOf(get(key));
		}

		@Override
		public Long getLong(String key) throws BTypeException {
			return longOf(get(key));
		}

		@Override
		public String getString(String key) throws BTypeException {
			return stringOf(get(key));
		}

		@Override
		public BList getList(String key) throws BTypeException {
			Object tmp = get(key);
			if (tmp instanceof BList) {
				return (BList) tmp;
			}
			if (tmp != null)
				throw new BTypeException("Unexpected type: " + tmp.getClass());
			return null;
		}

		@Override
		public BMap getMap(String key) throws BTypeException {
			Object tmp = get(key);
			if (tmp instanceof BMap) {
				return (BMap) tmp;
			}
			throw new BTypeException("Unexpected type: " + tmp.getClass());
		}
	}

	public static final Object decode(String encoded) throws IOException {
		try(final ByteArrayInputStream in = new ByteArrayInputStream(
				encoded.getBytes())) {
			return new BDecoder(in).readElement();
		}
	}
}
