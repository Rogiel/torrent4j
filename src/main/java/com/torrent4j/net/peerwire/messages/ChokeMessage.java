package com.torrent4j.net.peerwire.messages;

import com.torrent4j.net.peerwire.AbstractPeerWireMessage;

public class ChokeMessage extends AbstractPeerWireMessage {
	public static final int MESSAGE_ID = 0x00;

	public ChokeMessage() {
		super(MESSAGE_ID);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CHOKE []";
	}
}
