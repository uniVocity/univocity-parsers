/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
package com.univocity.parsers.csv;

import java.io.*;

import org.testng.annotations.*;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;

public class ProfilerTest {
	public static void execute(String description, Runnable process) {
		long start = System.currentTimeMillis();

		process.run();
		System.out.println(description + " took " + (System.currentTimeMillis() - start) + " ms.");
	}

	private Runnable defaultInputReader() {
		return new Runnable() {
			CsvParserSettings options = new CsvParserSettings() {
				{
					setMaxCharsPerColumn(1000);
					setRowProcessor(rowProcessor());
				}
			};
			CsvParser test = new CsvParser(options);

			@Override
			public void run() {
				test.parse(input());
			}
		};
	}

	private Runnable incrementalInputReader() {
		return new Runnable() {
			CsvParserSettings options = new CsvParserSettings() {
				{
					setMaxCharsPerColumn(1000);
					setReadInputOnSeparateThread(true);
					setRowProcessor(rowProcessor());
				}
			};
			CsvParser test = new CsvParser(options);

			@Override
			public void run() {
				test.parse(input());
			}
		};

	}

	private Reader input() {
		String path = System.getProperty("user.home") + File.separator + "dev" + File.separator + "data";
		final File f = new File(path + File.separator + "worldcitiespop.txt");

		try {
			return new InputStreamReader(new FileInputStream(f), "ISO-8859-1");
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	private RowProcessor rowProcessor() {
		RowProcessor p = new RowProcessor() {
			int count = 0;

			@Override
			public void rowProcessed(String[] row, ParsingContext context) {
				count++;
			}

			@Override
			public void processStarted(ParsingContext context) {
				count = 0;
			}

			@Override
			public void processEnded(ParsingContext context) {
				System.out.println(count);
			}
		};
		return p;
	}

	@Test(enabled = false)
	public void runPerformanceComparison() throws Exception {
		final Runnable incrementalInputReader = incrementalInputReader();
		final Runnable defaultInputReader = defaultInputReader();

		for (int i = 0; i < 3; i++) {
			String loop = "(" + (i + 1) + ") ";
			execute(loop + "defaultInputReader", defaultInputReader);
			execute(loop + "incrementalInputReader", incrementalInputReader);
		}
	}
}
