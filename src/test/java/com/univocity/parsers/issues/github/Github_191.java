/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
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

import com.univocity.parsers.csv.*;
import com.univocity.parsers.examples.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/191
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_191 extends Example {

	@Test
	public void testQuotedColumnSelection() throws Exception {
		String input = "\"bbb\",\"\",,10,\"15\",\"aaa\"\n";

		CsvParserSettings parseSettings = new CsvParserSettings();
		parseSettings.getFormat().setLineSeparator("\n");
		CsvParser parse = new CsvParser(parseSettings);
		//Original: "bbb","",,10,"15","aaa"
		List<String[]> records = parse.parseAll(new StringReader(input));

		StringWriter writer = new StringWriter();
		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\n");
		writerSettings.quoteIndexes(0, 1, 4, 5);

		CsvWriter csvWriter = new CsvWriter(writer, writerSettings);
		for (String[] record : records) {
			csvWriter.writeRow(record);
		}
		csvWriter.close();

		assertEquals(writer.toString(), input);


		writer = new StringWriter();
		writerSettings.setHeaders("A", "B", "C", "D", "E", "F");
		writerSettings.quoteFields("C", "D");
		csvWriter = new CsvWriter(writer, writerSettings);
		for (String[] record : records) {
			csvWriter.writeRow(record);
		}
		csvWriter.close();

		assertEquals(writer.toString(), "bbb,,\"\",\"10\",15,aaa\n");
	}

}
