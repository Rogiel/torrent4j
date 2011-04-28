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
package net.bittorrent.util.comparator;

import junit.framework.Assert;

import net.torrent.util.comparator.RandomComparator;

import org.junit.Test;

public class RandomComparatorTest {
	@Test
	public void testCompare() {
		final RandomComparator comparator = RandomComparator.SHARED_INSTANCE;

		Assert.assertNotSame(comparator.compare(null, null),
				comparator.compare(null, null));
	}

	@Test
	public void testSorting() {
		/*
		 * List<String> list1 = Arrays.asList("a", "b", "c", "d"); List<String>
		 * list2 = Arrays.asList("a", "b", "c", "d"); Collections.sort(list1,
		 * RandomComparator.SHARED_INSTANCE); Collections.sort(list2,
		 * RandomComparator.SHARED_INSTANCE);
		 * Assert.assertFalse(list1.equals(list2));
		 */
	}
}
