/**
 * Copyright 2017 Ambud Sharma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.srotya.sidewinder.core.storage.disk;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.srotya.sidewinder.core.storage.mem.MemTagIndex;

/**
 * @author ambud
 */
public class TestDiskTagIndex {

	@Test
	public void testDiskTagIndexBasic() {
		MemTagIndex index = new MemTagIndex();
		for (int i = 0; i < 1000; i++) {
			String idx = index.createEntry("tag" + (i + 1));
			index.index(idx, "test212");
		}
		for (int i = 0; i < 1000; i++) {
			String entry = index.createEntry("tag" + (i + 1));
			assertEquals("tag" + (i + 1), index.getEntry(entry));
			assertEquals("test212", index.searchRowKeysForTag(entry).iterator().next());
		}
	}

	@Test
	public void testTagIndexThreaded() throws InterruptedException, IOException {
		final DiskTagIndex index = new DiskTagIndex("target/index", "db2", "m2");
		ExecutorService es = Executors.newCachedThreadPool();
		for (int k = 0; k < 10; k++) {
			es.submit(() -> {
				try {
					for (int i = 0; i < 1000; i++) {
						String idx = index.createEntry("tag" + (i + 1));
						index.index(idx, "test212");
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
		es.shutdown();
		es.awaitTermination(10, TimeUnit.SECONDS);
		for (int i = 0; i < 1000; i++) {
			String entry = index.createEntry("tag" + (i + 1));
			assertEquals("tag" + (i + 1), index.getEntry(entry));
			assertEquals("test212", index.searchRowKeysForTag(entry).iterator().next());
		}

		DiskTagIndex index2 = new DiskTagIndex("target/index", "db2", "m2");
		for (int i = 0; i < 1000; i++) {
			String entry = index2.createEntry("tag" + (i + 1));
			assertEquals("tag" + (i + 1), index2.getEntry(entry));
			assertEquals("test212", index2.searchRowKeysForTag(entry).iterator().next());
		}
	}

}