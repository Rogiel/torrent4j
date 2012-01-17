package com.torrent4j;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.torrent4j.model.Torrent;
import com.torrent4j.model.TorrentPeer;
import com.torrent4j.net.peerwire.PeerWireProtocol;
import com.torrent4j.storage.InMemoryTorrentStorage;

public class TestMain {
	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		final TorrentController controller = new TorrentController(
				new PeerWireProtocol(), new InMemoryTorrentStorage());
		controller.start(1234);
		
		final Torrent torrent = Torrent.load(Paths.get("music.torrent"));
		System.out.println("Torrent hash is " + torrent.getHash().getString());

		// controller.checkExistingData(torrent);

		controller.registerTorrent(torrent);
		final TorrentPeer peer = new TorrentPeer(torrent);
		peer.setAddress(new InetSocketAddress(Inet4Address
				.getByName("192.168.1.100"), 21958));
		torrent.getSwarm().addPeer(peer);
		
		while(true) {
			Thread.sleep(1000);
			System.out.println((peer.getTrafficControl().getCurrentDownloadSpeed() / 1024) + " kb/s");
			//peer.getTrafficControl().setDownloadSpeedLimit(32 * 1024);
			torrent.getTrafficControl().setDownloadSpeedLimit(256 * 1024);
		}

		// System.out.println(((StandardTorrentStrategy)
		// torrent.getStrategy()).getPieceSelector()
		// .selectPiece(peer));
	}
}
