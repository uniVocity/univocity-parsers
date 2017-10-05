/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.issues.github;

import com.univocity.parsers.common.input.*;
import com.univocity.parsers.common.input.concurrent.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.examples.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/194
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_194 extends Example {

	@Test
	public void testReadInputOnSeparateThreadIssue() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(5);
		try {
			final List<Throwable> errors = new ArrayList<Throwable>();
			final CountDownLatch latch = new CountDownLatch(500);
			for (int i = 0; i < 500; i++) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							CsvParserSettings s = new CsvParserSettings();
							s.setHeaderExtractionEnabled(true);
							s.getFormat().setLineSeparator("\r");

							//s.setReadInputOnSeparateThread(false);
							List<String[]> rows = new CsvParser(s).parseAll(getClass().getResourceAsStream("/issues/github_194/uk-500.csv"));
							assertEquals(rows.size(), 500);
						} catch (Throwable e) {
							synchronized (errors) {
								errors.add(e);
							}
						} finally {
							latch.countDown();
						}
					}
				});
			}

			latch.await();

			if (!errors.isEmpty()) {
				System.err.println("Got " + errors.size() + " errors");
				int i = 0;
				for (Throwable e : errors) {
					System.err.println((++i) + "\t " + e.getMessage());
				}
			}

			assertTrue(errors.isEmpty());
		} finally {
			executor.shutdown();
		}
	}

}
