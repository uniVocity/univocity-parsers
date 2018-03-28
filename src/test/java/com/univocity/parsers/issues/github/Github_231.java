/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.nio.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/231
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_231 {

	@Test
	public void testStopParsingConcurrencyIssue() {
		CsvParserSettings s = new CsvParserSettings();
		s.setHeaderExtractionEnabled(true);
		final CsvParser parser = new CsvParser(s);

		parser.beginParsing(new StringReader("LALALA"){
			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				parser.stopParsing();
				return super.read(cbuf, off, len);
			}
		});


		String[] parsedHeaders;
		try {
			parsedHeaders = parser.getContext().parsedHeaders();
		} finally {
			parser.stopParsing();
		}

	}
}
