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
package com.univocity.parsers;

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;

import java.io.*;
import java.net.*;
import java.util.*;

import static org.testng.Assert.*;

public abstract class ParserTestCase {

	protected RowListProcessor processor = newRowListProcessor();

	protected RowListProcessor newRowListProcessor() {
		return new RowListProcessor();
	}

	public static Reader newReader(String path) throws UnsupportedEncodingException {
		Reader reader = new InputStreamReader(ParserTestCase.class.getResourceAsStream(path), "UTF-8");
		return reader;
	}

	public void assertHeadersAndValuesMatch(RowListProcessor processor, String[] expectedHeaders, Object[][] expectedResult) {
		String[] headers = processor.getHeaders();
		TestUtils.assertEquals(headers, expectedHeaders);

		List<String[]> rows = processor.getRows();
		assertEquals(rows.size(), expectedResult.length);

		for (int i = 0; i < expectedResult.length; i++) {
			String[] row = rows.get(i);
			Object[] expectedRow = expectedResult[i];
			assertEquals(row, expectedRow);
		}
	}

	public void assertHeadersAndValuesMatch(String[] expectedHeaders, Object[][] expectedResult) {
		assertHeadersAndValuesMatch(processor, expectedHeaders, expectedResult);
	}

	public String readFileContent(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		StringBuilder fileContent = new StringBuilder();
		while((line = reader.readLine()) != null) {
			fileContent.append(line).append('\n');
		}

		return fileContent.toString();
	}
}
