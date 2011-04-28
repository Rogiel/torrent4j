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

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;

import net.torrent.BitTorrentClient;
import net.torrent.BitTorrentClientFactory;
import net.torrent.torrent.Torrent;

public class TorrentProfile {
	public static void main(String[] args) throws IOException,
			InterruptedException, URISyntaxException {
		final Torrent torrent = Torrent.load(new File(
				"src/test/resources/oth.s01e13.avi.torrent"));
		System.out.println(torrent.getInfoHash());

		// System.out.println(torrent.getPiece(700).getParts()[3]);
		// for (final TorrentPart part : torrent.getPiece(700).getParts()) {
		// System.out.println(part);
		// }

		final BitTorrentClient client = new BitTorrentClientFactory(torrent)
				.newBitTorrentClient();
		client.start(new InetSocketAddress("192.168.1.100", 25944));
		// client.start(new InetSocketAddress("192.168.1.110", 51413));

		Thread.sleep(60 * 1000 * 30);
	}
}
