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
 * Algorithm that processes peer interest, choking state and connections.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public interface TorrentPeerAlgorithm {
	/**
	 * Get an peer to be connected. This method is invoked in many situations.
	 * 
	 * @return the new peer to be connected
	 * @see TorrentPeer
	 */
	TorrentPeer connect();

	/**
	 * Once a new peer is discovered, this method is called to test if we want
	 * to connect to it or not.
	 * 
	 * @param peer
	 *            the new discovered peer
	 * @return the desired action
	 */
	PeerDiscoveredAction discovered(TorrentPeer peer);

	/**
	 * Action to be performed when an peer is idle.
	 * 
	 * @author Rogiel Josias Sulzbach (<a
	 *         href="http://www.rogiel.com/">http://www.rogiel.com/</a>)
	 */
	public enum PeerDiscoveredAction {
		/**
		 * Try to establish an connection to this new peer
		 */
		CONNECT,

		/**
		 * Remove this peer from list
		 */
		REMOVE,

		/**
		 * Nothing is done.
		 */
		NONE;
	}

	/**
	 * Test to keep this connection alive. The value sent represents the action
	 * which the handler will do with the idle peer.
	 * 
	 * @param peer
	 *            the peer
	 * @return the action to be done
	 * @see KeepAliveAction KeepAliveAction for a list of actions
	 */
	KeepAliveAction keepAlive(TorrentPeer peer);

	/**
	 * Action to be performed when an peer is idle.
	 * 
	 * @author Rogiel Josias Sulzbach (<a
	 *         href="http://www.rogiel.com/">http://www.rogiel.com/</a>)
	 */
	public enum KeepAliveAction {
		/**
		 * Keep this connection alive
		 */
		KEEP_ALIVE,

		/**
		 * Disconnect the peer. No new connection is made.
		 */
		DISCONNECT,

		/**
		 * Disconnect the peer AND connects another peer.
		 */
		CONNECT_NEW_PEER,

		/**
		 * Nothing is done.
		 */
		NONE;
	}

	/**
	 * Called when the peer interested in us changes. Return the desired choke
	 * or unchoked action.
	 * 
	 * @param peer
	 *            the peer
	 * @param interest
	 *            the new interest (it is also already set in the peer object)
	 * @return the desired choke/unchoke action
	 * @see ChokingState
	 */
	ChokingState interested(TorrentPeer peer, InterestState interest);

	/**
	 * Called when the peer choke state change with us (that is, <b>when the
	 * peer choke or unchoke</b>!).
	 * 
	 * @param peer
	 *            the peer
	 * @param state
	 *            the new choking state
	 * @return the desired action to be taken
	 * @see PeerChokedAction
	 */
	PeerChokedAction choked(TorrentPeer peer, ChokingState state);

	/**
	 * Action to be performed when the peer changes it's choke state
	 * 
	 * @author Rogiel Josias Sulzbach (<a
	 *         href="http://www.rogiel.com/">http://www.rogiel.com/</a>)
	 */
	public enum PeerChokedAction {
		/**
		 * Disconnects the current peer and connects a new one
		 */
		CONNECT_NEW_PEER,

		/**
		 * Only disconnects the peer, does not initiate a new connection with
		 * anyone.
		 */
		DISCONNECT,

		/**
		 * Download a new piece (this is only valid if unchoked)
		 */
		DOWNLOAD,

		/**
		 * Do nothing, ignore.
		 */
		NONE;
	}
}
