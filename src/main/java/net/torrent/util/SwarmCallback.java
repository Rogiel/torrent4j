/**
 * 
 */
package net.torrent.util;

import net.torrent.torrent.context.TorrentPeer;
import net.torrent.torrent.context.TorrentSwarm;

/**
 * Callback used in {@link TorrentSwarm#execute(SwarmCallback)}
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public interface SwarmCallback {
	/**
	 * Execute the desired action for <tt>peer</tt>
	 * 
	 * @param peer
	 *            the peer
	 */
	void callback(TorrentPeer peer);
}
