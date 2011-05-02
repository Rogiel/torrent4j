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
package net.torrent.torrent.context;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.torrent.protocol.peerwire.message.header.PeerWireFastPeersMessageHeaderManager;
import net.torrent.protocol.peerwire.message.header.PeerWireMessageHeaderManager;

/**
 * Object containing peers support for certain capabilities.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentPeerCapabilities {
	/**
	 * BitSet containing available capabilities
	 */
	private BitSet capabilities = new BitSet(64);

	/**
	 * Create a new instance
	 */
	public TorrentPeerCapabilities() {
	}

	/**
	 * Create a new instance with the given <tt>capabilities</tt>
	 * 
	 * @param capabilities
	 *            the capabilities supported
	 */
	public TorrentPeerCapabilities(TorrentPeerCapability... capabilities) {
		for (final TorrentPeerCapability capability : capabilities) {
			this.capabilities.set(capability.bit);
		}
	}

	/**
	 * Create a new instance with the given <tt>capabilities</tt>
	 * 
	 * @param capabilities
	 *            the capabilities {@link BitSet} supported
	 */
	public TorrentPeerCapabilities(BitSet capabilitites) {
		this.capabilities = capabilitites;
	}

	/**
	 * Tests if the given <tt>capability</tt> is supported.
	 * 
	 * @param capability
	 *            the capability
	 * @return true if capability is supported, false otherwise.
	 */
	public boolean supports(TorrentPeerCapability capability) {
		return capabilities.get(capability.bit);
	}

	/**
	 * Get an {@link List} of capabilities supported.
	 * 
	 * @return the list os capabilities
	 */
	public List<TorrentPeerCapability> getCapabilities() {
		final List<TorrentPeerCapability> capabilitites = new ArrayList<TorrentPeerCapability>();
		for (final TorrentPeerCapability capability : TorrentPeerCapability
				.values()) {
			if (supports(capability))
				capabilitites.add(capability);
		}
		return capabilitites;
	}

	/**
	 * Set the capabilities {@link BitSet}.
	 * 
	 * @param capabilitites
	 *            the bitset
	 */
	public void setCapabilities(BitSet capabilitites) {
		this.capabilities = capabilitites;
	}

	/**
	 * Convert this matrix of capabilties to a {@link BitSet}.
	 * 
	 * @return an {@link BitSet}.
	 */
	public BitSet toBitSet() {
		return capabilities;
	}

	/**
	 * Enumeration of known capabilities.
	 * 
	 * @author Rogiel Josias Sulzbach (<a
	 *         href="http://www.rogiel.com/">http://www.rogiel.com/</a>)
	 */
	public enum TorrentPeerCapability {
		/**
		 * Location aware protocol
		 */
		LOCATION_AWARE_PROTOCOL(21),
		/**
		 * Extension protocol
		 */
		EXTENSION_PROTOCOL(44),
		/**
		 * Fast peers support
		 */
		FAST_PEERS(62, PeerWireFastPeersMessageHeaderManager.SHARED_INSTANCE),
		/**
		 * DHT Support
		 */
		DHT(64);

		/**
		 * The bit index for this capability
		 */
		public final int bit;
		/**
		 * The header manager for this extension
		 */
		public final PeerWireMessageHeaderManager headerManager;

		/**
		 * Creates a new capability
		 * 
		 * @param bit
		 *            the bit marking this capability
		 * @param headerManager
		 *            the header manager for this extension
		 */
		TorrentPeerCapability(int bit,
				PeerWireMessageHeaderManager headerManager) {
			this.bit = bit;
			this.headerManager = headerManager;
		}

		/**
		 * Creates a new capability will a <tt>null</tt> <tt>handlerManager</tt>
		 * 
		 * @param bit
		 *            the bit marking this capability
		 */
		TorrentPeerCapability(int bit) {
			this(bit, null);
		}
	}
}
