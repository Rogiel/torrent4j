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
package net.torrent.protocol.algorithm;

import net.torrent.torrent.context.TorrentPeer;
import net.torrent.torrent.context.TorrentPeer.ChokingState;
import net.torrent.torrent.context.TorrentPeer.InterestState;

/**
 * Algorithm used to determine the interest and choking in peers.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public interface TorrentInterestAlgorithm {
	/**
	 * Test if we are interested in this peer pieces. Interest is for download
	 * only.
	 * 
	 * @param peer
	 *            the peer
	 * @return our interest
	 * @see InterestState
	 */
	InterestState interested(TorrentPeer peer);

	/**
	 * Test if we want to choke this peer. This is normally invoked when we have
	 * no more interest in the peer pieces.
	 * 
	 * @param peer
	 *            the peer
	 * @return the choking state
	 * @see ChokingState
	 */
	ChokingState choke(TorrentPeer peer);
}
