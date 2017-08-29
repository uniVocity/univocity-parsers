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

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.examples.*;
import com.univocity.parsers.tsv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

public class ProfilerTest extends Example {

	public ProfilerTest() {

	}

	public static void execute(String description, Runnable process) {
		long start = System.currentTimeMillis();

		process.run();
		System.out.println(description + " took " + (System.currentTimeMillis() - start) + " ms.");
	}

	private Runnable defaultInputReader() {
		return new Runnable() {
			CsvParserSettings options = new CsvParserSettings() {
				{
					getFormat().setLineSeparator("\n");
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
					getFormat().setLineSeparator("\n");
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

	@Test(enabled = false)
	public void runCsvWritingTest() throws Exception{
		runInLoop(100, "CSV writer", newCsvWritingProcess(1000000, getRowsToWrite()));
	}

	@Test(enabled = false)
	public void runTsvWritingTest() throws Exception{
		runInLoop(100, "TSV writer", newTsvWritingProcess(1000000, getRowsToWrite()));
	}

	private List<String[]> getRowsToWrite(){
		return new CsvParser(new CsvParserSettings()).parseAll(getReader("/examples/example.csv"));
	}

	private void runInLoop(int loops, String description, Runnable process) throws Exception{
		for(int i = 0; i < loops; i++) {
			String loop = "(" + (i + 1) + ") ";
			execute(loop + description, process);
			System.gc();
			Thread.sleep(100);
		}
	}

	private Runnable newTsvWritingProcess(final int repeats, final List<String[]> allRows){
		return new Runnable() {
			@Override
			public void run() {
				final TsvWriter writer = new TsvWriter(new StringWriter(), new TsvWriterSettings());
				for(int i = 0; i < repeats; i++) {
					writer.writeStringRows(allRows);
				}
				writer.close();
			}
		};
	}

	private Runnable newCsvWritingProcess(final int repeats, final List<String[]> allRows){
		return new Runnable() {
			@Override
			public void run() {
				final CsvWriter writer = new CsvWriter(new StringWriter(), new CsvWriterSettings());
				for(int i = 0; i < repeats; i++) {
					writer.writeStringRows(allRows);
				}
				writer.close();
			}
		};
	}
}
