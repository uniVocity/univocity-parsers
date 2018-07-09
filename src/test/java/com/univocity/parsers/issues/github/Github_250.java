/*
 * Copyright (c) 2018. uniVocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.util.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/251
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
