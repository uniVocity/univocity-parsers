/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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

import java.util.*;

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/univocity/univocity-parsers/issues/35
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_35 {

	@Test
	public void testConversionWithoutHeaders() {
		CsvWriterSettings writerSettings = new CsvWriterSettings();

		ObjectRowWriterProcessor writerProcessor = new ObjectRowWriterProcessor();
		writerProcessor.convertAll(Conversions.toBoolean("T", "F")); // will write "T" and "F" instead of "true" and "false"

		writerSettings.setRowWriterProcessor(writerProcessor);

		CsvWriter writer = new CsvWriter(writerSettings);
		String line1 = writer.processRecordToString(true, false, false, true);
		String line2 = writer.processRecordToString(false, false, true, true);

		assertEquals(line1, "T,F,F,T");
		assertEquals(line2, "F,F,T,T");

		// Now, let's read these lines

		CsvParserSettings parserSettings = new CsvParserSettings();

		ObjectRowListProcessor readerProcessor = new ObjectRowListProcessor();
		readerProcessor.convertAll(Conversions.toBoolean("T", "F")); //reads "T" and "F" back to true and false

		parserSettings.setRowProcessor(readerProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parseLine(line1);
		parser.parseLine(line2);

		List<Object[]> rows = readerProcessor.getRows();
		assertEquals(rows.get(0)[0], true);
		assertEquals(rows.get(0)[1], false);
		assertEquals(rows.get(0)[2], false);
		assertEquals(rows.get(0)[3], true);
		assertEquals(rows.get(1)[0], false);
		assertEquals(rows.get(1)[1], false);
		assertEquals(rows.get(1)[2], true);
		assertEquals(rows.get(1)[3], true);
	}
}
