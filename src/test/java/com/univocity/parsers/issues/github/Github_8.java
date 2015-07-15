/*******************************************************************************
 * Copyright (c) 2015 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 ******************************************************************************/
package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/3
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_8 {
	@Test
	public void testCarriageReturn() throws Exception {
		byte[] bytes = "a,b,c,d\r1,2,3,4\r5,6,7,8".getBytes("UTF-8");
		InputStream is = new ByteArrayInputStream(bytes);
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\r");
		CsvParser parser = new CsvParser(settings);
		List<String[]> rows = parser.parseAll(new InputStreamReader(is));
		Assert.assertEquals(rows.size(), 3);
		Assert.assertEquals(rows.get(0).length, 4);
	}
}
