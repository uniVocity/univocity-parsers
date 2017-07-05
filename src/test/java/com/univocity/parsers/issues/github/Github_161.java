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

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/161
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_161 {

	@Test
	public void testCsvFormatAutodetection() throws IOException {
		String input = "\"Date\",\"Col2\",\"Col3\",\"Col4\",\"Col5\",\"Col6\",\"Col7\",\"Col7\",\"Col8\"\n" +
				"\"2017-05-23\",\"String\",\"lo rem ipsum\",\"dolor sit amet\",\"mcdonalds.com/online.html\",\"\",\"-\",\"-\",\"-\"\n" +
				"\"2017-05-23\",\"String\",\"lo rem ipsum\",\"dolor sit amet\",\"burgerking.com\",\"https://burgerking.com/\",\"20\",\"2\",\"fake\"\n" +
				"\"2017-05-23\",\"String\",\"lo rem ipsum\",\"dolor sit amet\",\"wendys.com\",\"\",\"-\",\"-\",\"-\"\n" +
				"\"2017-05-23\",\"String\",\"lo rem ipsum\",\"dolor sit amet\",\"buggagump.com\",\"\",\"-\",\"-\",\"-\"\n" +
				"\"2017-05-23\",\"String\",\"cheese\",\"ad eum\",\"mcdonalds.com/online.html\",\"\",\"-\",\"-\",\"-\"\n" +
				"\"2017-05-23\",\"String\",\"burger\",\"ludus dissentiet\",\"www.mcdonalds.com\",\"https://www.mcdonalds.com/\",\"25\",\"3\",\"fake\"\n" +
				"\"2017-05-23\",\"String\",\"wine\",\"id erat utamur\",\"bubbagump.com\",\"https://buggagump.com/\",\"25\",\"3\",\"fake\"";

		CsvParserSettings s = new CsvParserSettings();
		s.detectFormatAutomatically();

		CsvParser parser = new CsvParser(s);
		parser.beginParsing(new StringReader(input));

		CsvFormat format = parser.getDetectedFormat();
		assertEquals(format.getQuoteEscape(), '"');
	}
}
