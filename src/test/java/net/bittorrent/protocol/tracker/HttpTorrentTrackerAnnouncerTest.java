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
package net.bittorrent.protocol.tracker;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.torrent.protocol.tracker.HttpTorrentTrackerAnnouncer;
import net.torrent.torrent.Torrent;

import org.junit.Test;

public class HttpTorrentTrackerAnnouncerTest {
	@Test
	public void testAnnounce() throws IOException, URISyntaxException,
			InterruptedException {
		final Torrent torrent = Torrent
				.load(new File(
						"src/test/resources/Tim Besamusca - Running Away EP Urban Sickness Audio USA1008.torrent"));
		final HttpTorrentTrackerAnnouncer announcer = new HttpTorrentTrackerAnnouncer();
		System.out.println(announcer.announce(torrent, torrent.getTrackers()
				.iterator().next()));

		Thread.sleep(10 * 60 * 1000);
	}
}
