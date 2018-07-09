/*
 * Copyright (c) 2018. uniVocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/3
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_8 {
	@Test
	public void testCarriageReturn() throws Exception {
		byte[] bytes = "a,b,c,d\r1,2,3,4\r5,6,7,8".getBytes("UTF-8");
		InputStream is = new ByteArrayInputStream(bytes);
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\r");
		CsvParser parser = new CsvParser(settings);
		List<String[]> rows = parser.parseAll(new InputStreamReader(is));
		Assert.assertEquals(rows.size(), 3);
		Assert.assertEquals(rows.get(0).length, 4);
	}
}
