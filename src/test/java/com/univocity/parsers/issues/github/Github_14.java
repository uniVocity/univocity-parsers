/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/14
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_14 {

	private static final Conversion<Boolean, Double> boolenToDoubleConversion = new Conversion<Boolean, Double>() {
		@Override
		public Double execute(Boolean input) {
			if (input == null || input == Boolean.FALSE) {
				return 0.0;
			}
			return 1.0;
		}

		@Override
		public Boolean revert(Double input) {
			if (input == null || input == 0.0 || input.isNaN()) {
				return false;
			}
			return Boolean.TRUE;
		}
	};

	@DataProvider
	public Object[][] conversionProvider() {
		return new Object[][]{{true}, {false}};
	}

	@Test(dataProvider = "conversionProvider")
	public void testConversion(boolean convertAll) {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setHeaderExtractionEnabled(true);

		ObjectColumnProcessor objectColumnProcessor = new ObjectColumnProcessor();
		if (convertAll) {
			objectColumnProcessor.convertAll(Conversions.toBoolean("T", "F"), boolenToDoubleConversion);
		} else {
			objectColumnProcessor.convertIndexes(Conversions.toBoolean("T", "F"), boolenToDoubleConversion).set(0, 1);
		}

		parserSettings.setRowProcessor(objectColumnProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader("A,B\nT,F\nF,T"));

		Map<String, List<Object>> columnValues = objectColumnProcessor.getColumnValuesAsMapOfNames();
		assertEquals(columnValues.size(), 2);
		List<Object> values;
		values = columnValues.get("A");
		assertNotNull(values);
		assertEquals(values.size(), 2);
		assertEquals(Arrays.asList(1.0, 0.0), values);

		values = columnValues.get("B");
		assertNotNull(values);
		assertEquals(values.size(), 2);
		assertEquals(Arrays.asList(0.0, 1.0), values);
	}
}
