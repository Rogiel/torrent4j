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
package net.torrent.protocol.tracker.message;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import net.torrent.protocol.tracker.codec.TorrentTrackerResponseMessage;
import net.torrent.util.bencoding.BList;
import net.torrent.util.bencoding.BMap;
import net.torrent.util.bencoding.BTypeException;

public class PeerListMessage implements TorrentTrackerResponseMessage {
	private static final String FAILURE_REASON = "failure reason";
	private static final String WARNING_MESSAGE = "warning message";
	private static final String INTERVAL = "interval";
	private static final String MIN_INTERVAL = "min interval";
	private static final String TRACKER_ID = "tracker id";
	private static final String COMPLETE = "complete";
	private static final String INCOMPLETE = "incomplete";
	private static final String PEERS = "peers";

	private String failureReason;
	private String warningMessage;

	private Integer interval;
	private Integer minInterval;
	private byte[] trackerID;
	private Integer complete;
	private Integer incomplete;
	private List<PeerInfo> peerList;
	private boolean compact;

	@Override
	public void read(BMap response) throws BTypeException {
		if (response == null)
			throw new InvalidParameterException("Tracker response is null");
		if (response.containsKey(FAILURE_REASON)) {
			failureReason = response.getString(FAILURE_REASON);
			return;
		}
		this.interval = response.getInteger(INTERVAL);

		warningMessage = response.getString(WARNING_MESSAGE);
		interval = response.getInteger(INTERVAL);
		minInterval = response.getInteger(MIN_INTERVAL);
		trackerID = (byte[]) response.get(TRACKER_ID);

		complete = response.getInteger(COMPLETE);
		incomplete = response.getInteger(INCOMPLETE);

		Object peers = response.get(PEERS);
		if (peers instanceof BList) {
			BList list = (BList) peers;
			peerList = new ArrayList<PeerInfo>(list.size());
			for (int i = 0; i < list.size(); i++) {
				peerList.add(PeerInfo.fromBMap(list.getMap(i)));
			}
		} else {
			byte[] list = (byte[]) peers;
			if (list.length % 6 != 0)
				throw new IllegalStateException(
						"Peerlist not in format IPv4:port!");
			peerList = new ArrayList<PeerInfo>(list.length / 6);
			for (int i = 0; i < list.length; i += 6) {
				peerList.add(PeerInfo.fromRawIP(list, i, 6));
			}
			compact = true;
		}
	}

	public String getFailureReason() {
		return failureReason;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public Integer getInterval() {
		return interval;
	}

	public Integer getMinInterval() {
		return minInterval;
	}

	public byte[] getTrackerID() {
		return trackerID;
	}

	public Integer getComplete() {
		return complete;
	}

	public Integer getIncomplete() {
		return incomplete;
	}

	public List<PeerInfo> getPeerList() {
		return peerList;
	}

	public boolean isCompact() {
		return compact;
	}

	public static class PeerInfo {
		private byte[] peerId;
		private String ip;
		private int port;

		public PeerInfo(byte[] peerId, String ip, int port) {
			this.peerId = peerId;
			this.ip = ip;
			this.port = port;
		}

		public byte[] getPeerId() {
			return peerId;
		}

		public void setPeerId(byte[] peerId) {
			this.peerId = peerId;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public static PeerInfo fromBMap(BMap map) throws BTypeException {
			return new PeerInfo((byte[]) map.get("peer id"),
					map.getString("ip"), map.getInteger("port"));
		}

		public static PeerInfo fromRawIP(byte[] list, int i, int j) {
			byte[] addr = new byte[4];
			System.arraycopy(list, i, addr, 0, 4);
			InetAddress address;
			try {
				address = InetAddress.getByAddress(addr);
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
			InetSocketAddress socketAddress = new InetSocketAddress(
					address.getHostAddress(), ((list[i + j - 2] & 0xFF) << 8)
							+ (list[i + j - 1] & 0xFF));
			return new PeerInfo(null, socketAddress.getHostName(),
					socketAddress.getPort());
		}
	}
}
