package com.torrent4j.util.bencoding;

import java.io.IOException;

public class BTypeException extends IOException {
	private static final long serialVersionUID = 1L;

	public BTypeException(String msg) {
		super(msg);
	}
}
