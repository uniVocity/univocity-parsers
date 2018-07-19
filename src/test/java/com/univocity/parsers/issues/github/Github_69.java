/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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

import com.univocity.parsers.common.TextWritingException;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.testng.annotations.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class Github_69 {

	@Test(expectedExceptions = TextWritingException.class)
	public void testException(){
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setHeaders("col2", "col5", "col3", "col4", "col1", "col6");
		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, settings);

		Map<String, Object> rowData = new HashMap<String, Object>();
		for (int i = 1; i <= 4; i++) {
			rowData.put("col" + i, "inh" + i);
		}

		writer.processRecord(rowData);
	}
}
