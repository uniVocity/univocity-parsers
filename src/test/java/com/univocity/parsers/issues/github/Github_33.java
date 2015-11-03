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
import java.math.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/33
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_33 {

	@Test
	public void testMultipleConversionsWork() {
		ObjectRowListProcessor rowProcessor = new ObjectRowListProcessor();

		FormattedBigDecimalConversion conversion = new FormattedBigDecimalConversion();
		conversion.addFormat("0.00", "decimalSeparator=.");
		conversion.addFormat("0,00", "decimalSeparator=,");

		rowProcessor.convertFields(conversion).set("Amount", "Tax", "Total");

		CsvParserSettings parserSettings = new CsvParserSettings();

		parserSettings.getFormat().setDelimiter('|');

		parserSettings.setRowProcessor(rowProcessor);

		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);

		parser.parse(new StringReader("Amount|Tax|Total\n1.99|10.0|2.189\n1,99|10,0|2,189"));

		List<Object[]> rows = rowProcessor.getRows();

		assertEquals(rows.get(0)[0], new BigDecimal("1.99"));
		assertEquals(rows.get(0)[1], new BigDecimal("10.0"));
		assertEquals(rows.get(0)[2], new BigDecimal("2.189"));
		assertEquals(rows.get(1)[0], new BigDecimal("1.99"));
		assertEquals(rows.get(1)[1], new BigDecimal("10.0"));
		assertEquals(rows.get(1)[2], new BigDecimal("2.189"));

	}
}
