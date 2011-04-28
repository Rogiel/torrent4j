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
package net.torrent.util.comparator;

import java.util.Comparator;
import java.util.Random;

/**
 * Random comparator, values returned on each call are always different.
 * 
 * @author <a href="http://www.rogiel.com/">Rogiel Josias Sulzbach</a>
 */
public class RandomComparator implements Comparator<Object> {
	/**
	 * Shared instance
	 */
	public static final RandomComparator SHARED_INSTANCE = new RandomComparator();

	/**
	 * Random number generator
	 */
	private final Random random = new Random();

	@Override
	public int compare(Object o1, Object o2) {
		return random.nextInt(250 * 10 * (random.nextInt(100) + 1));
	}
}
