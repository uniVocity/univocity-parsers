/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.issues.github;


import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/253
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_254 {

	@Test
	public void testParse() {
		CsvParserSettings s = new CsvParserSettings();
		s.setColumnReorderingEnabled(false);
		s.selectFields("r1", "r2");

		List<String[]> rows = new CsvParser(s).parseAll(new StringReader("r1,r2\nref1\nref1,\nref1"));
		assertEquals(rows.size(), 4);

		for (String[] row : rows) {
			assertEquals(row.length, 2);
		}
	}

}
