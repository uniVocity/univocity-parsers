/*
 * Copyright (c) 2019 Univocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.issues.github;


import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/305
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_305 {

	@Test
	public void testSeparatorDetectionCrLfAndMultilineComment() {
		String csv = "#created at 2019-01-22T11:39:43.312Z\r\n" +
				"#CSV export\r\n" +
				"Timestamp;Value;Metric;Entity;host\r\n" +
				"2019-01-21T11:39:53.763Z;160527072;jvm_memory_used;dev;LOCALHOST\r\n" +
				"2019-01-21T11:40:08.765Z;1.6270228E+8;jvm_memory_used;dev;LOCALHOST\r\n" +
				"2019-01-21T11:40:23.765Z;454336496;jvm_memory_used;dev;LOCALHOST\r\n";
		final CsvParserSettings settings = new CsvParserSettings();
		settings.setReadInputOnSeparateThread(false);
		settings.setQuoteDetectionEnabled(true);
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setDelimiterDetectionEnabled(true, ',', ';', '\t', '|', ' ');
		final CsvParser csvParser = new CsvParser(settings);

		csvParser.beginParsing(new StringReader(csv));

		assertEquals(csvParser.getDetectedFormat().getDelimiterString(), ";");

		for (String[] columns : csvParser.parseAll()) {
			assertEquals(columns.length, 5);
		}

	}

}
