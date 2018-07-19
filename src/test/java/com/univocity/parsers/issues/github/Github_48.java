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

import static org.testng.Assert.*;

import org.testng.annotations.*;

import java.io.*;
import java.util.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/48
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_48 {

	@Headers(sequence = {"id", "timestamp", "symbol", "quantity", "isComplete", "datetime", "number"})
	public static class BasicTypes {
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

		@Override
		public String toString() {
			return "{id=" + id +
					", quantity=" + quantity +
					", timestamp=" + timestamp +
					", symbol='" + symbol + '\'' +
					", isComplete=" + isComplete +
					", number=" + number +
					'}';
		}
	}

	private interface ParserTest {
		void configure(CsvParserSettings settings);
	}


	private List<Github_48.BasicTypes> runWithSettings(ParserTest test) {
		BeanListProcessor<BasicTypes> processor = new BeanListProcessor<BasicTypes>(BasicTypes.class);

		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowProcessor(processor);

		test.configure(settings);

		CsvParser p = new CsvParser(settings);
		p.parseAll(new StringReader("quantity,symbol,id\n" +
				"23.4,IBM,1\n" +
				"9.55,WMT,9\n" +
				"79.7,the quick brown fox,100\n"));

		List<BasicTypes> out = processor.getBeans();
		return out;
	}

	@Test
	public void parseBeanWithSomeHeaders() {
		List<Github_48.BasicTypes> result = runWithSettings(new ParserTest() {
			@Override
			public void configure(CsvParserSettings settings) {
				settings.setHeaderExtractionEnabled(true);
			}
		});
		assertFalse(result.isEmpty());

		assertEquals(result.get(0).toString(), "{id=1, quantity=23.4, timestamp=33, symbol='IBM', isComplete=true, number=1}");
		assertEquals(result.get(1).toString(), "{id=9, quantity=9.55, timestamp=33, symbol='WMT', isComplete=true, number=1}");
		assertEquals(result.get(2).toString(), "{id=100, quantity=79.7, timestamp=33, symbol='the quick brown fox', isComplete=true, number=1}");
	}


	@Test
	public void parseBeanWithSomeHeadersAndFieldSelection() {
		List<Github_48.BasicTypes> result = runWithSettings(new ParserTest() {
			@Override
			public void configure(CsvParserSettings settings) {
				settings.setHeaderExtractionEnabled(true);
				settings.selectFields("symbol");
			}
		});
		assertFalse(result.isEmpty());
		assertEquals(result.get(0).toString(), "{id=2, quantity=2.4, timestamp=33, symbol='IBM', isComplete=true, number=1}");
		assertEquals(result.get(1).toString(), "{id=2, quantity=2.4, timestamp=33, symbol='WMT', isComplete=true, number=1}");
		assertEquals(result.get(2).toString(), "{id=2, quantity=2.4, timestamp=33, symbol='the quick brown fox', isComplete=true, number=1}");
	}

	@Test
	public void parseBeanWithSomeHeadersAndIndexSelection() {
		List<Github_48.BasicTypes> result = runWithSettings(new ParserTest() {
			@Override
			public void configure(CsvParserSettings settings) {
				settings.setHeaderExtractionEnabled(true);
				settings.selectIndexes(2, 0); //id, quantity
			}
		});
		assertFalse(result.isEmpty());
		assertEquals(result.get(0).toString(), "{id=1, quantity=23.4, timestamp=33, symbol='S', isComplete=true, number=1}");
		assertEquals(result.get(1).toString(), "{id=9, quantity=9.55, timestamp=33, symbol='S', isComplete=true, number=1}");
		assertEquals(result.get(2).toString(), "{id=100, quantity=79.7, timestamp=33, symbol='S', isComplete=true, number=1}");
	}


}
