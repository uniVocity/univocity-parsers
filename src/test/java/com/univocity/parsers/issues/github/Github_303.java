/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.issues.github;


import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/303
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_303 {

	@Test
	public void testAutoClosingDisabledSingleThread() {
		testAutoClosingDisabled(false);
	}

	@Test
	public void testAutoClosingDisabledMultiThread() {
		testAutoClosingDisabled(true);
	}


	public void testAutoClosingDisabled(boolean separateThread) {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setAutoClosingEnabled(false);
		settings.setReadInputOnSeparateThread(separateThread);

		final boolean[] closed = new boolean[1];

		StringReader reader = new StringReader("a\nb\nc"){
			@Override
			public void close() {
				closed[0] = true;
				super.close();
			}
		};

		CsvParser parser = new CsvParser(settings);
		parser.parseAll(reader);
		assertFalse(closed[0]);

		reader.close();
		assertTrue(closed[0]);
	}

}
