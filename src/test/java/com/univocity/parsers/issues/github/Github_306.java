/*******************************************************************************
 * Copyright 2019 Univocity Software Pty Ltd
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
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/306
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_306 {

	@Test
	public void testWithMultilineRecordsAndFooter() {
		final String csv = "Timestamp,Value,Metric,Entity,host\n" +
				"2019-01-21T11:39:53.763Z,160527072,jvm_memory_used,dev,\"LOCAL\n" +
				"HOST\"\n" +
				"2019-01-21T11:40:08.765Z,1.6270228E+8,jvm_memory_used,dev,\"LOCAL\n" +
				"HOST\"\n" +
				"#created at 2019-01-22T11:39:43.312Z";
		final CsvParserSettings settings = new CsvParserSettings();
		settings.setReadInputOnSeparateThread(false);
		settings.setQuoteDetectionEnabled(true);
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setHeaderExtractionEnabled(true);
		settings.setDelimiterDetectionEnabled(true);

		final CsvParser csvParser = new CsvParser(settings);
		csvParser.beginParsing(new StringReader(csv));

		assertEquals(csvParser.getDetectedFormat().getDelimiterString(), ",");

		final List<String[]> dataRows = csvParser.parseAll(new StringReader(csv));
		assertEquals(dataRows.size(), 2);

		for (String[] columns : csvParser.parseAll()) {
			assertEquals(columns.length, 5);
		}

	}

}
