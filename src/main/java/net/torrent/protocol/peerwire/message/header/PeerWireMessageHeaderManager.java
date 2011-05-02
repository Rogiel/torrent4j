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

/**
 * The message header manager return the message to each given message id (aka
 * opcode).
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public interface PeerWireMessageHeaderManager {
	/**
	 * Return the message for an given <tt>id</tt>. If this handler cannot
	 * handle the message id, <b>null</b> must be returned.
	 * 
	 * @param id
	 *            the message id
	 * @return the message, <tt>null</tt> if <tt>id</tt> is unknown.
	 */
	PeerWireReadableMessage getMessage(byte id);
}
