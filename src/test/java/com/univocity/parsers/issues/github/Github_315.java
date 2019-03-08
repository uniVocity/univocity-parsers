/*******************************************************************************
 * Copyright 2019 Univocity Software Pty Ltd
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


import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static com.univocity.parsers.common.ArgumentUtils.*;
import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/315
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_315 {

	static final String[][] rows = {
			{"Start newline", "\nabc123", "Trailing"},
			{"Middle newline", "abc\n123", "Trailing"},
			{"End newline", "abc123\n", "Trailing"},
			{"Start cr", "\rabc123", "Trailing"},
			{"Middle cr", "abc\r123", "Trailing"},
			{"End cr", "abc123\r", "Trailing"},
			{"Start both", "\n\rabc123", "Trailing"},
			{"Middle both", "abc\n\r123", "Trailing"},
			{"End both", "abc123\n\r", "Trailing"},
	};

	static final String csv(){
		StringBuilder csv = new StringBuilder();
		for (String[] row : rows) {
			csv.append(row[0]).append(',').append('"').append(row[1]).append('"').append(',').append(row[2]).append("\r\n----\r\n");
		}
		return csv.toString();
	}

	@Test
	public void testCrIsQuotedWhenWritingExcel() {

		StringWriter out = new StringWriter();
		CsvWriterSettings settings = Csv.writeExcel();
		settings.trimValues(false);
		settings.setNormalizeLineEndingsWithinQuotes(false);

		final CsvWriter csv = new CsvWriter(out, settings);

		try {
			for (String[] row : rows) {
				csv.writeRow(row);
				csv.writeRow(new String[]{"----"});
			}
		} finally {
			csv.close();
		}

		String result = displayLineSeparators(out.toString(), true);
		String expected = displayLineSeparators(csv(), true);

		assertEquals(result, expected);
	}

	@Test
	public void testDisplayLineSeparatorsMultiLine(){
		String csv = csv().replace('"', '\'');
		assertEquals(displayLineSeparators(csv, true), "" +
				"Start newline,'[lf]\nabc123',Trailing[crlf]\r\n" +
				"----[crlf]\r\n" +
				"Middle newline,'abc[lf]\n123',Trailing[crlf]\r\n" +
				"----[crlf]\r\n" +
				"End newline,'abc123[lf]\n',Trailing[crlf]\r\n" +
				"----[crlf]\r\n" +
				"Start cr,'[cr]\rabc123',Trailing[crlf]\r\n" +
				"----[crlf]\r\n" +
				"Middle cr,'abc[cr]\r123',Trailing[crlf]\r\n" +
				"----[crlf]\r\n" +
				"End cr,'abc123[cr]\r',Trailing[crlf]\r\n" +
				"----[crlf]\r\n" +
				"Start both,'[lfcr]\n\rabc123',Trailing[crlf]\r\n" +
				"----[crlf]\r\n" +
				"Middle both,'abc[lfcr]\n\r123',Trailing[crlf]\r\n" +
				"----[crlf]\r\n" +
				"End both,'abc123[lfcr]\n\r',Trailing[crlf]\r\n" +
				"----[crlf]\r\n"
		);
	}

	@Test
	public void testDisplayLineSeparatorsSingleLine(){
		String csv = csv().replace('"', '\'');
		assertEquals(displayLineSeparators(csv, false), "" +
				"Start newline,'[lf]abc123',Trailing[crlf]" +
				"----[crlf]" +
				"Middle newline,'abc[lf]123',Trailing[crlf]" +
				"----[crlf]" +
				"End newline,'abc123[lf]',Trailing[crlf]" +
				"----[crlf]" +
				"Start cr,'[cr]abc123',Trailing[crlf]" +
				"----[crlf]" +
				"Middle cr,'abc[cr]123',Trailing[crlf]" +
				"----[crlf]" +
				"End cr,'abc123[cr]',Trailing[crlf]" +
				"----[crlf]" +
				"Start both,'[lfcr]abc123',Trailing[crlf]" +
				"----[crlf]" +
				"Middle both,'abc[lfcr]123',Trailing[crlf]" +
				"----[crlf]" +
				"End both,'abc123[lfcr]',Trailing[crlf]" +
				"----[crlf]"
		);
	}
}
