/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.issues.github;


import com.univocity.parsers.common.record.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/299
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_299 {

	@Test
	public void shouldDetectNewLine() {
		CsvFormat format = new CsvFormat();
		format.setLineSeparator("\r\n");
		CsvParserSettings settings = new CsvParserSettings();
		settings.setFormat(format);

		settings.setNormalizeLineEndingsWithinQuotes(false);
		CsvParser parser = new CsvParser(settings);

		String[] values = parser.parseRecord("foo,\" bar \",qix\r\n").getValues();

		assertEquals(values.length , 3);
		assertEquals(values[0], "foo");
		assertEquals(values[1], " bar ");
		assertEquals(values[2], "qix");

		values = parser.parseRecord("foo,\" bar \"\r\n").getValues();

		assertEquals(values.length , 2);
		assertEquals(values[0], "foo");
		assertEquals(values[1], " bar ");

		values = parser.parseRecord("foo,\" bar \",\r\n").getValues();

		assertEquals(values.length , 3);
		assertEquals(values[0], "foo");
		assertEquals(values[1], " bar ");
		assertEquals(values[2], null);
	}

}
