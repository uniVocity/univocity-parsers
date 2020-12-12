/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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

import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class CsvFormatDetectorTest {

	@DataProvider
	public Object[][] getInputsAndOutputs() {
		return new Object[][]{
				{"A,B,C\n1,2,3\n1,2,3\n1,2,3",
						Arrays.asList(new String[]{"A", "B", "C"}, new String[]{"1", "2", "3"}, new String[]{"1", "2", "3"}, new String[]{"1", "2", "3"})},
				{"\"A\";'B';\"C\"\n\"1\\\" and \\\"2\";\"3\\\"A\";'B';\"C\"\n\"A\";'B';\"C\"\n\"A\";'B';\"C\"\n",
						Arrays.asList(new String[]{"A", "'B'", "C"}, new String[]{"1\" and \"2", "3\"A", "'B'", "C"}, new String[]{"A", "'B'", "C"}, new String[]{"A", "'B'", "C"})},
				{"\"A\";'B';\"C\"\n\"1\\\" and \\\"2\";\"3' and '4\";\"5\\\" and \\\"6\"\n\"A\";'B';\"C\"\n\"A\";'B';\"C\"\n",
						Arrays.asList(new String[]{"A", "'B'", "C"}, new String[]{"1\" and \"2", "3' and '4", "5\" and \"6"}, new String[]{"A", "'B'", "C"}, new String[]{"A", "'B'", "C"})},
				{"1,2;2,3;3,4;a\n1,2;2,3;3,4;b\n1,2;2,3;3,4;c\n1,2;2,3;3,4;d\n",
						Arrays.asList(new String[]{"1,2", "2,3", "3,4", "a"}, new String[]{"1,2", "2,3", "3,4", "b"}, new String[]{"1,2", "2,3", "3,4", "c"}, new String[]{"1,2", "2,3", "3,4", "d"})},
				{"A;B;C;D;E\n$1.2;$2.3;$3.4\n$1.2;$2.3;$3.4\n$1.2;$2.3;$3.4\n$1.2;$2.3;$3.4\n",
						Arrays.asList(new String[]{"A", "B", "C", "D", "E"}, new String[]{"$1.2", "$2.3", "$3.4"}, new String[]{"$1.2", "$2.3", "$3.4"}, new String[]{"$1.2", "$2.3", "$3.4"},
								new String[]{"$1.2", "$2.3", "$3.4"})},
				{"\"A'A\",\"BB\",\"CC\"\n\"11\",\"22\",\"33\"\n\"11\",\"22\",\"33\"\n\"11\",\"22\",\"33\"\n",
						Arrays.asList(new String[]{"A'A", "BB", "CC"}, new String[]{"11", "22", "33"}, new String[]{"11", "22", "33"}, new String[]{"11", "22", "33"})}

		};
	}

	private CsvParserSettings newSettings() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setDelimiterDetectionEnabled(true);
		settings.setQuoteDetectionEnabled(true);
		settings.setParseUnescapedQuotes(false);

		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setDelimiter('x');
		settings.getFormat().setQuote('x');
		settings.getFormat().setQuoteEscape('x');
		return settings;
	}

	@Test(dataProvider = "getInputsAndOutputs")
	public void testDelimiterDiscovery(String input, List<String[]> expectedOutput) {
		CsvParserSettings settings = newSettings();

		CsvParser parser = new CsvParser(settings);

		List<String[]> rows = parser.parseAll(new StringReader(input));

		assertEquals(rows.size(), expectedOutput.size());
		for (int i = 0; i < rows.size(); i++) {
			assertEquals(expectedOutput.get(i), rows.get(i));
		}
	}


	@Test
	public void testAutodetection() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.detectFormatAutomatically();
		CsvParser parser = new CsvParser(settings);

		String s = "" +
				"1;2001-01-01;First row;1.1\n" +
				"2;2002-02-02;Second row;2.2\n" +
				"3;2003-03-03;Third row;3.3\n" +
				"4;2004-04-04;Fourth row;4.4";

		List<String[]> rows = parser.parseAll(new StringReader(s));

		CsvFormat format = parser.getDetectedFormat();
		assertEquals(format.getDelimiter(), ';');
		assertEquals(rows.size(), 4);

		s = "" +
				"1;2001-01-01;First row;1.1\n" +
				"2;2002-02-02;Second row;2\n" +
				"3;2003-03-03;Third row;3.3\n" +
				"4;2004-04-04;Fourth row;4.4";

		rows = parser.parseAll(new StringReader(s));

		format = parser.getDetectedFormat();
		assertEquals(format.getDelimiter(), ';');
		assertEquals(rows.size(), 4);
	}

	@Test
	public static void testDelimitersDetectedUsingOrderOfPreference() {
		String input = "HEADER1, HEADER2, HEADER3\n" +
				"11, 12, 13\n" +
				"21, 22, 23\n" +
				"31, 32, 33\n";

		CsvParserSettings settings = new CsvParserSettings();
		settings.setDelimiterDetectionEnabled(true, ',', ' ');
		CsvParser parser = new CsvParser(settings);
		parser.parseAll(new StringReader(input));
		CsvFormat format = parser.getDetectedFormat();
		assertEquals(format.getDelimiter(), ',');
	}

	@Test
	public static void testDelimitersDetectedUsingOrderOfPreference1() {
		String input = "HEADER 1,HEADER 2,HEADER 3\n" +
						"SOME TEXT 1,SOME TEXT 2,SOME TEXT 3,";

		CsvParserSettings settings = new CsvParserSettings();
		settings.setDelimiterDetectionEnabled(true, ',', ' ');
		settings.setFormatDetectorRowSampleCount(2);
		CsvParser parser = new CsvParser(settings);
		parser.parseAll(new StringReader(input));
		CsvFormat format = parser.getDetectedFormat();
		assertEquals(format.getDelimiter(), ',');
	}
}
