/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.DataProcessingException;
import com.univocity.parsers.common.processor.ObjectRowWriterProcessor;
import com.univocity.parsers.common.processor.OutputValueSwitch;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.testng.annotations.Test;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Github_66 {


	@Test(expectedExceptions = DataProcessingException.class)
	public void testMapRecords() {
		final ObjectRowWriterProcessor clientProcessor = new ObjectRowWriterProcessor();
		final ObjectRowWriterProcessor accountProcessor = new ObjectRowWriterProcessor();

		OutputValueSwitch writerSwitch = new OutputValueSwitch();
		writerSwitch.addSwitchForValue("Account", accountProcessor, "type", "balance", "bank", "account", "swift");
		writerSwitch.addSwitchForValue("Client", clientProcessor);

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setExpandIncompleteRows(true);
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderWritingEnabled(false);
		settings.setRowWriterProcessor(writerSwitch);

		StringWriter output = new StringWriter();
		CsvWriter writer = new CsvWriter(output, settings);

		Map<String, Object> rowData = new LinkedHashMap<String, Object>();
		rowData.put("balance", "sp2");
		rowData.put("type", "Account"); //account NOT in first position to force an exception to happen.
		rowData.put("bank", "sp3");
		rowData.put("acount", "sp4");
		rowData.put("swift", "sp5");
		writer.processRecord(rowData);
		writer.close();

	}
}
