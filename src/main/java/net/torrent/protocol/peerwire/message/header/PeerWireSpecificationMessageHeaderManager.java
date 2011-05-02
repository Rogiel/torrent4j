/*
 * Copyright 2011 Rogiel Josias Sulzbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.torrent.protocol.peerwire.message.header;

import net.torrent.protocol.peerwire.codec.PeerWireReadableMessage;
import net.torrent.protocol.peerwire.message.BitfieldMessage;
import net.torrent.protocol.peerwire.message.CancelMessage;
import net.torrent.protocol.peerwire.message.ChokeMessage;
import net.torrent.protocol.peerwire.message.HaveMessage;
import net.torrent.protocol.peerwire.message.InterestedMessage;
import net.torrent.protocol.peerwire.message.NotInterestedMessage;
import net.torrent.protocol.peerwire.message.PieceMessage;
import net.torrent.protocol.peerwire.message.PortMessage;
import net.torrent.protocol.peerwire.message.RequestMessage;
import net.torrent.protocol.peerwire.message.UnchokeMessage;

/**
 * The default PeerWire message handler
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public final class PeerWireSpecificationMessageHeaderManager implements
		PeerWireMessageHeaderManager {
	public static final PeerWireSpecificationMessageHeaderManager SHARED_INSTANCE = new PeerWireSpecificationMessageHeaderManager();

	@Override
	public PeerWireReadableMessage getMessage(byte id) {
		switch (id) {
		case BitfieldMessage.MESSAGE_ID:
			return new BitfieldMessage();
		case CancelMessage.MESSAGE_ID:
			return new CancelMessage();
		case ChokeMessage.MESSAGE_ID:
			return new ChokeMessage();
		case HaveMessage.MESSAGE_ID:
			return new HaveMessage();
		case InterestedMessage.MESSAGE_ID:
			return new InterestedMessage();
		case NotInterestedMessage.MESSAGE_ID:
			return new NotInterestedMessage();
		case PieceMessage.MESSAGE_ID:
			return new PieceMessage();
		case PortMessage.MESSAGE_ID:
			return new PortMessage();
		case RequestMessage.MESSAGE_ID:
			return new RequestMessage();
		case UnchokeMessage.MESSAGE_ID:
			return new UnchokeMessage();
		}
		return null;
	}
}
