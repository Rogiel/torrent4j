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
import net.torrent.protocol.peerwire.message.fast.AllowedFastMessage;
import net.torrent.protocol.peerwire.message.fast.HaveAllMessage;
import net.torrent.protocol.peerwire.message.fast.HaveNoneMessage;
import net.torrent.protocol.peerwire.message.fast.RejectMessage;
import net.torrent.protocol.peerwire.message.fast.SuggestPieceMessage;
import net.torrent.torrent.context.TorrentPeerCapabilities.TorrentPeerCapability;

/**
 * The message header manager for {@link TorrentPeerCapability#FAST_PEERS Fast
 * Peers extension}.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public final class PeerWireFastPeersMessageHeaderManager implements
		PeerWireMessageHeaderManager {
	public static final PeerWireFastPeersMessageHeaderManager SHARED_INSTANCE = new PeerWireFastPeersMessageHeaderManager();

	@Override
	public PeerWireReadableMessage getMessage(byte id) {
		switch (id) {
		case AllowedFastMessage.MESSAGE_ID:
			return new AllowedFastMessage();
		case HaveAllMessage.MESSAGE_ID:
			return new HaveAllMessage();
		case HaveNoneMessage.MESSAGE_ID:
			return new HaveNoneMessage();
		case RejectMessage.MESSAGE_ID:
			return new RejectMessage();
		case SuggestPieceMessage.MESSAGE_ID:
			return new SuggestPieceMessage();
		}
		return null;
	}
}
