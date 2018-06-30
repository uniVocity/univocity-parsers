/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.issues.github;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.util.*;

import static com.univocity.parsers.ParserTestCase.*;
import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/247
 *
 * @author camerondavison
 */
public class Github_250 {

	@Test
	public void testParseLineNoIndexesSelected() throws Exception {
		final CsvParserSettings s = new CsvParserSettings();
		s.selectIndexes();
		final CsvParser parser = new CsvParser(s);

		System.out.println(Arrays.toString(parser.parseLine("0")));
		System.out.println(parser.getContext().currentChar());

		System.out.println(Arrays.toString(parser.parseLine("1")));
		System.out.println(parser.getContext().currentChar());

		System.out.println(Arrays.toString(parser.parseLine("2")));
		System.out.println(parser.getContext().currentChar());

		System.out.println(Arrays.toString(parser.parseLine("3")));
		System.out.println(parser.getContext().currentChar());

	}
}
