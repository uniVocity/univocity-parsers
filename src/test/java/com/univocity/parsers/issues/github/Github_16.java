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

import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/16
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_16 {

	@Test
	public void testAlignment() {
		FixedWidthFields lengths = new FixedWidthFields(15, 8, 15);
		lengths.setAlignment(FieldAlignment.CENTER, 1);
		lengths.setAlignment(FieldAlignment.RIGHT, 2);
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(lengths);

		settings.setHeaders("date", "quantity", "comments");

		FixedWidthWriter writer = new FixedWidthWriter(settings);

		String headers = writer.writeHeadersToString();
		String line1 = writer.writeRowToString(new String[]{"AAAA", "BBBB", "12"});
		String line2 = writer.writeRowToString(new String[]{"CC", "DD", "222212"});

		assertEquals(headers, "date           quantity       comments");
		assertEquals(line1, "AAAA             BBBB               12");
		assertEquals(line2, "CC                DD            222212");
	}
}
