package net.torrent.util.bencoding;

import java.io.IOException;

/**
 * BDecodingException: an error has occured when decoding bencoded data.
 * 
 * @author Dennis "Bytekeeper" Waldherr
 */
public class BDecodingException extends IOException {
	private static final long serialVersionUID = 1L;

	public BDecodingException(Exception e) {
		super(e);
	}

	public BDecodingException(String message) {
		super(message);
	}

}
