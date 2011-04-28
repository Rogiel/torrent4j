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
package net.torrent.torrent;

import net.torrent.util.Range;

/**
 * An file inside an {@link Torrent torrent} file
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class TorrentFile {
	/**
	 * The torrent information
	 */
	private final TorrentInfo info;
	/**
	 * The filename
	 */
	private final String name;
	/**
	 * The file length
	 */
	private final long length;
	/**
	 * The file offset
	 */
	private final long offset;

	/**
	 * Creates a new instance
	 * 
	 * @param info
	 *            the torrent information
	 * @param name
	 *            the filename
	 * @param length
	 *            the file length
	 * @param offset
	 *            the file offset
	 */
	public TorrentFile(TorrentInfo info, String name, long length, long offset) {
		this.info = info;
		this.name = name;
		this.length = length;
		this.offset = offset;
	}

	/**
	 * Get the torrent informatiom
	 * 
	 * @return the torrent information
	 */
	public TorrentInfo getInfo() {
		return info;
	}

	/**
	 * Get the filename
	 * 
	 * @return the filename
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the filesize
	 * 
	 * @return the filesize
	 */
	public long getLength() {
		return length;
	}

	/**
	 * Get the file offset
	 * 
	 * @return the offset
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * Get the base directory name
	 * 
	 * @return the base directory name
	 * @see TorrentInfo#getDirectoryName()
	 */
	public String getBaseDirectoryName() {
		return info.getDirectoryName();
	}

	/**
	 * Get the relative name. This is the same as:
	 * <p>
	 * <code>
	 * String name = getBaseDirectoryName() + "/" + getName();
	 * </code>
	 * 
	 * @return the relative name
	 */
	public String getRelativePath() {
		if (getBaseDirectoryName() != null) {
			return getBaseDirectoryName() + "/" + name;
		}
		return name;
	}

	/**
	 * Get the file as torrent range
	 * 
	 * @return the torrent range representing the file
	 */
	public Range asTorrentRange() {
		return Range.getRangeByLength(offset, length);
	}

	/**
	 * Get the file as file range
	 * 
	 * @return the file range representing the file
	 */
	public Range asFileRange() {
		return Range.getRangeByLength(0, length);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (length ^ (length >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (offset ^ (offset >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TorrentFile other = (TorrentFile) obj;
		if (length != other.length)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (offset != other.offset)
			return false;
		return true;
	}
}
