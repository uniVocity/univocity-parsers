/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
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
import java.nio.charset.*;
import java.util.*;

import static com.univocity.parsers.common.ArgumentUtils.*;
import static com.univocity.parsers.common.input.BomInput.*;
import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/154
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_154 {

	public static class User {
		@Parsed(field = "Email")
		private String email;
	}

	private static final String INPUT = "Email\ndev@univocity.com";

	private static byte[] getInput(String encoding) {
		return INPUT.getBytes(Charset.forName(encoding));
	}

	@DataProvider
	Object[][] getFileAndEncoding() {
		return new Object[][]{
				{true, "UTF-8", null},
				{false, "UTF-8", null},
				{true, "UTF-8", UTF_8_BOM},
				{false, "UTF-8", UTF_8_BOM},

				{true, "UTF-16BE", UTF_16BE_BOM},
				{false, "UTF-16BE", UTF_16BE_BOM},

				{true, "UTF-16LE", UTF_16LE_BOM},
				{false, "UTF-16LE", UTF_16LE_BOM},

				//edge case here. Looks like UTF-32LE until the last character.
				{true, "UTF-16LE", toByteArray(0xFF, 0xFE, 0x00, ' ')},

				{true, "UTF-32BE", UTF_32BE_BOM},
				{false, "UTF-32BE", UTF_32BE_BOM},

				{true, "UTF-32LE", UTF_32LE_BOM},
				{false, "UTF-32LE", UTF_32LE_BOM},
		};
	}

	@Test(dataProvider = "getFileAndEncoding")
	public void readWithBom(boolean extractFromBom, String encoding, byte[] prepend) {
		final CsvParserSettings parserSettings = new CsvParserSettings();
		final BeanListProcessor<User> rowProcessor = new BeanListProcessor<User>(User.class);

		parserSettings.setProcessor(rowProcessor);
		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setSkipEmptyLines(false);
		parserSettings.setReadInputOnSeparateThread(false);

		final CsvParser parser = new CsvParser(parserSettings);

		byte[] bytes = getInput(encoding);
		if (extractFromBom) {
			encoding = null;
		}
		if (prepend != null) {
			byte[] newBytes = new byte[bytes.length + prepend.length];
			System.arraycopy(prepend, 0, newBytes, 0, prepend.length);
			System.arraycopy(bytes, 0, newBytes, prepend.length, bytes.length);

			bytes = newBytes;
		}
		parser.beginParsing(new ByteArrayInputStream(bytes), encoding);
		String[] row = parser.parseNext();
		parser.stopParsing();

		if(prepend != null && prepend[prepend.length -1] == ' '){
			assertEquals(parser.getContext().headers()[0], " Email");
			assertEquals(row[0], "dev@univocity.com");

		} else {
			assertEquals(parser.getContext().headers()[0], "Email");
			assertEquals(row[0], "dev@univocity.com");
			final List<User> actual = rowProcessor.getBeans();
			assertEquals(actual.get(0).email, "dev@univocity.com");
		}
	}
}
