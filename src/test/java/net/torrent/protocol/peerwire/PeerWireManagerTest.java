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
package net.torrent.protocol.peerwire;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.LogManager;

import net.torrent.BitTorrentClient;
import net.torrent.BitTorrentClientFactory;
import net.torrent.torrent.Torrent;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.JdkLoggerFactory;
import org.junit.Test;

public class PeerWireManagerTest {
	//@Test
	public void testPeerWire() throws IOException, InterruptedException,
			URISyntaxException {
		LogManager.getLogManager().readConfiguration(
				this.getClass().getResourceAsStream("/logging.properties"));
		InternalLoggerFactory.setDefaultFactory(new JdkLoggerFactory());

		
		//GET /announce?info_hash=9uKh%e8%2a%81%a47%5b%95%24%d7%84kD-%af%a7%93&peer_id=-TR2130-0kr8c2gx1d39&port=51413&uploaded=0&downloaded=0&left=124928000&numwant=0&key=c3gvpc8w&compact=1&supportcrypto=1&event=stopped HTTP/1.1\r\n
		//GET /announce?info_hash=9uKh%E8%2a%81%A47%5B%95%24%D7%84kD-%AF%A7%93&peer_id=-TR2130-g4mvcv2iyehf&port=10254&uploaded=0&downloaded=0&left=0&compact=1&event=started HTTP/1.1

		
		final Torrent torrent = Torrent
				.load(new URL(
						"http://build.eclipse.org/technology/phoenix/torrents/indigo/eclipse-java-indigo-linux-gtk-x86_64.tar.gz.torrent"));
		System.out.println(torrent.getInfoHash());

		final BitTorrentClient client = new BitTorrentClientFactory(torrent)
				.newBitTorrentClient();
		// client.start(new InetSocketAddress("192.168.1.100", 25944));
		// client.start(new InetSocketAddress("192.168.1.110", 51413));
		client.start();

		Thread.sleep(60 * 1000 * 30);
	}
}
