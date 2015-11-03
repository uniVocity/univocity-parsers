/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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

import com.univocity.parsers.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.examples.*;
import org.testng.annotations.*;

import java.math.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/3
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_3 extends ParserTestCase {

	@Test
	public void parseNextWithRowReaderTest() throws Exception {
		BeanListProcessor<TestBean> rowProcessor = new BeanListProcessor<TestBean>(TestBean.class);

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setRowProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		parser.beginParsing(newReader("/examples/bean_test.csv"));

		String[][] expectedCsvRows = new String[][]{
			{"10-oct-2001", "555.999", "1", "yEs", "?"},
			{"2001-10-10", null, "?", "N", "  \" something \"  "}
		};

		//quantity, comments, amount, pending
		Object[][] expecteBeanValues = new Object[][]{
			{1, "?", new BigDecimal("555.999"), true},
			{0, "\" something \"", null, false}
		};

		List<String[]> rows = new ArrayList<String[]>();
		String[] row = null;
		while ((row = parser.parseNext()) != null) {
			rows.add(row);
		}

		assertEquals(rows.size(), expectedCsvRows.length);

		for (int i = 0; i < expectedCsvRows.length; i++) {
			assertEquals(rows.get(i), expectedCsvRows[i]);
		}

		// The BeanListProcessor provides a list of objects extracted from the input.
		List<TestBean> beans = rowProcessor.getBeans();

		assertEquals(beans.size(), expecteBeanValues.length);

		for (int i = 0; i < expecteBeanValues.length; i++) {
			TestBean bean = beans.get(i);
			Object[] values = new Object[]{
				bean.getQuantity(), bean.getComments(), bean.getAmount(), bean.getPending()
			};

			assertEquals(values, expecteBeanValues[i]);
		}
	}
}
