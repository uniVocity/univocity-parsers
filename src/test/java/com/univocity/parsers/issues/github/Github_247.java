/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.issues.github;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.testng.annotations.Test;

import static com.univocity.parsers.ParserTestCase.newReader;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/247
 *
 * @author camerondavison
 */
public class Github_247 {

	@Test(expectedExceptions = TextParsingException.class)
	public void test100Cols() throws Exception {
		final CsvParserSettings s = new CsvParserSettings();
		s.setHeaderExtractionEnabled(true);
		s.setMaxColumns(99);
		s.setLineSeparatorDetectionEnabled(true);

		final RowListProcessor rowListProcessor = new RowListProcessor();
		s.setProcessor(rowListProcessor);

		final CsvParser parser = new CsvParser(s);
		parser.parse(newReader("/issues/github_247/input-100.txt"));
		fail("should have exception");
	}
}
