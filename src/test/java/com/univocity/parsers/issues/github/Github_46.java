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

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/46
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_46 {

	@Headers(sequence = {"id", "timestamp", "symbol", "quantity", "isComplete", "datetime", "number"})
	class BasicTypes {
		@Parsed
		int id = 2;
		@Parsed
		double quantity = 2.4;
		@Parsed
		long timestamp = 33L;
		@Parsed
		String symbol = "S";
		@Parsed
		boolean isComplete = true;
		@Parsed
		int number = 1;
	}


	@Test
	public void testFieldSelectionWithOverriddenHeadersAnnotation() {
		BeanWriterProcessor<BasicTypes> processor = new BeanWriterProcessor<BasicTypes>(BasicTypes.class);

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowWriterProcessor(processor);

		settings.setHeaders("id", "symbol", "timestamp");
		settings.selectFields("timestamp", "id");


		StringWriter out = new StringWriter();
		CsvWriter w = new CsvWriter(out, settings);
		w.writeHeaders();
		w.processRecord(new BasicTypes());
		w.processRecord(new BasicTypes());
		w.close();

		assertEquals(out.toString(), "id,symbol,timestamp\n2,,33\n2,,33\n");
	}

	@Test
	public void testIndexSelectionWithOverriddenHeadersAnnotation() {
		BeanWriterProcessor<BasicTypes> processor = new BeanWriterProcessor<BasicTypes>(BasicTypes.class);

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowWriterProcessor(processor);

		settings.setHeaders("id", "symbol", "timestamp");
		settings.selectIndexes(2, 0);

		StringWriter out = new StringWriter();
		CsvWriter w = new CsvWriter(out, settings);
		w.writeHeaders();
		w.processRecord(new BasicTypes());
		w.processRecord(new BasicTypes());
		w.close();

		assertEquals(out.toString(), "id,symbol,timestamp\n2,,33\n2,,33\n");
	}
}