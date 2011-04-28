package net.torrent.util.bencoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Helper class to "BEncode" data.
 * 
 * @author Dennis "Bytekeeper" Waldherr
 * 
 */
public final class BEncodedOutputStream extends OutputStream {
	static final Comparator<String> BYTE_COMPARATOR = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			byte b1[] = bytesOf(o1);
			byte b2[] = bytesOf(o2);
			int len = Math.min(b1.length, b2.length);
			for (int i = 0; i < len; i++) {
				if (b1[i] > b2[i]) {
					return 1;
				}
				if (b1[i] < b2[i]) {
					return -1;
				}
			}
			return b1.length - b2.length;
		}
	};

	private final OutputStream out;

	/**
	 * Converts a string into the raw byte array representation used to store
	 * into bencoded data.
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] bytesOf(String s) {
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	private BEncodedOutputStream(OutputStream out) {
		if (out == null)
			throw new InvalidParameterException("output strean is null");
		this.out = out;
	}

	/**
	 * @param obj
	 * @return
	 */
	public static byte[] bencode(Object obj) {
		if (obj == null)
			throw new InvalidParameterException("Object to encode is null!");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			BEncodedOutputStream bout = new BEncodedOutputStream(out);
			bout.writeElement(obj);
			bout.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toByteArray();
	}

	/**
	 * BEncodes data and outputs it to an OutputStream.
	 * 
	 * @param out
	 * @param obj
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void writeElement(Object obj) throws IOException {
		if (obj == null)
			throw new InvalidParameterException("Object to encode is null!");

		if (obj instanceof String) {
			bencodeString((String) obj);
		} else if (obj instanceof byte[]) {
			bencodeString((byte[]) obj);
		} else if (obj instanceof Integer) {
			bencodeInteger(BigInteger.valueOf((Integer) obj));
		} else if (obj instanceof Long) {
			bencodeInteger(BigInteger.valueOf((Long) obj));
		} else if (obj instanceof BigInteger) {
			bencodeInteger((BigInteger) obj);
		} else if (obj instanceof Collection) {
			bencodeList((Collection<?>) obj);
		} else if (obj instanceof Map) {
			bencodeDictionary((Map<String, ?>) obj);
		} else {
			throw new IllegalArgumentException("Type " + obj.getClass()
					+ " is not bencodable.");
		}
	}

	private void bencodeDictionary(Map<String, ?> dict) throws IOException {
		assert out != null;
		assert dict != null;

		out.write('d');
		List<String> sorted = new ArrayList<String>(dict.keySet());
		Collections.sort(sorted, BYTE_COMPARATOR);
		for (String key : sorted) {
			writeElement(key);
			writeElement(dict.get(key));
		}
		out.write('e');
	}

	private void bencodeList(Collection<?> list) throws IOException {
		assert out != null;
		assert list != null;
		out.write('l');
		for (Object child : list) {
			writeElement(child);
		}
		out.write('e');
	}

	private void bencodeInteger(BigInteger integer) throws IOException {
		assert integer != null;
		out.write('i');
		out.write(bytesOf(integer.toString()));
		out.write('e');
	}

	private void bencodeString(String string) throws IOException {
		assert string != null;
		byte[] bytes = bytesOf(string);
		bencodeString(bytes);
	}

	private void bencodeString(byte[] data) throws IOException {
		assert data != null;
		out.write(bytesOf(Integer.toString(data.length)));
		out.write(':');
		out.write(data);
	}

	@Override
	public void write(int arg0) throws IOException {
		out.write(arg0);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

}
