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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import net.torrent.protocol.tracker.codec.TorrentTrackerRequestMessage;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class AnnounceMessage implements TorrentTrackerRequestMessage {
	private String url;
	private byte[] infoHash;
	private byte[] peerId;
	private int port;

	private long uploaded;
	private long downloaded;
	private long left;

	private boolean compact;
	private boolean noPeerId;

	private Event event;

	public enum Event {
		UPDATE(null), STARTED("started"), STOPPED("stopped"), COMPLETED(
				"completed");
		private String urlArg;

		Event(String arg) {
			urlArg = arg;
		}

		public String urlArg() {
			return urlArg;
		}
	}

	private String ip;
	private Integer numWant = 100;
	private String key = "avtbyit8";
	private String trackerId;

	public AnnounceMessage(String url, byte[] infoHash, byte[] peerId,
			int port, long uploaded, long downloaded, long left,
			boolean compact, boolean noPeerId, Event event, String ip,
			Integer numWant, String key, String trackerId) {
		this.url = url;
		this.infoHash = infoHash;
		this.peerId = peerId;
		this.port = port;
		this.uploaded = uploaded;
		this.downloaded = downloaded;
		this.left = left;
		this.compact = compact;
		this.noPeerId = noPeerId;
		this.event = event;
		this.ip = ip;
		this.numWant = numWant;
		this.key = key;
		this.trackerId = trackerId;
	}

	public AnnounceMessage(String url, byte[] infoHash, byte[] peerId,
			int port, long uploaded, long downloaded, long left,
			boolean compact, boolean noPeerId, Event event) {
		this.url = url;
		this.infoHash = infoHash;
		this.peerId = peerId;
		this.port = port;
		this.uploaded = uploaded;
		this.downloaded = downloaded;
		this.left = left;
		this.compact = compact;
		this.noPeerId = noPeerId;
		this.event = event;
	}

	@Override
	public HttpRequest write() throws UnsupportedEncodingException,
			MalformedURLException {
		StringBuilder builder = new StringBuilder(url);
		if (url.contains("?")) {
			builder.append("&");
		} else {
			builder.append("?");
		}

		addLowerCase(builder, "info_hash", infoHash);
		add(builder, "peer_id", "-TR2130-g4mvcv2iyehf");
		add(builder, "port", port);

		add(builder, "uploaded", Long.toString(uploaded));
		add(builder, "downloaded", Long.toString(downloaded));
		add(builder, "left", Long.toString(left));

		//add(builder, "compact", compact);
		//add(builder, "no_peer_id", noPeerId);
		if (event != Event.UPDATE)
			add(builder, "event", event.urlArg());

		add(builder, "ip", ip);
		add(builder, "numwant", numWant);
		//add(builder, "key", key);
		//add(builder, "trackerid", trackerId);

		builder.setLength(builder.length() - 1);// trim last character it is an
												// unnecessary &.
		final URL url = new URL(builder.toString());
		System.out.println(builder.toString());

		return new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
				url.getPath() + "?" + url.getQuery());
	}

	private void add(StringBuilder builder, String key, String value)
			throws UnsupportedEncodingException {
		if (value == null)
			return;
		builder.append(key + "=" + URLEncoder.encode(value, "ISO-8859-1").replace("*", "%2a")).append("&");
	}

	private void add(StringBuilder builder, String key, byte[] value)
			throws UnsupportedEncodingException {
		if (value == null)
			return;
		add(builder, key, new String(value, "ISO-8859-1"));
	}

	private void addLowerCase(StringBuilder builder, String key, byte[] value)
			throws UnsupportedEncodingException {
		if (value == null)
			return;
		add(builder, key, new String(value, "ISO-8859-1"));
	}

	private void add(StringBuilder builder, String key, Number value)
			throws UnsupportedEncodingException {
		if (value == null)
			return;
		add(builder, key, value.toString());
	}

	private void add(StringBuilder builder, String key, Boolean value)
			throws UnsupportedEncodingException {
		if (value == null)
			return;
		add(builder, key, (value ? "1" : "0"));
	}
}
