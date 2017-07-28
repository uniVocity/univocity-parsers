/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/165
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_176 {

	@DataProvider
	Object[][] threadProvider() {
		return new Object[][]{
				{true},
				{false}
		};
	}

	@Test(enabled = false, dataProvider = "threadProvider")
	public void testPerformance(boolean readInputOnSeparateThread) {
		CsvParserSettings s = new CsvParserSettings();
		s.setReadInputOnSeparateThread(readInputOnSeparateThread);
		CsvParser p = new CsvParser(s);

		String path = System.getProperty("user.home") + File.separator + "dev" + File.separator + "data" + File.separator + "Sample-Spreadsheet-500000-rows.csv";

		for (int i = 0; i < 10; i++) {
			long start = System.currentTimeMillis();
			p.parse(new File(path));
			System.out.println(p.getContext().currentLine() + " rows parsed in " + (System.currentTimeMillis() - start) + " ms");
		}
	}
}
