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

import com.univocity.parsers.common.DataProcessingException;
import com.univocity.parsers.common.processor.ObjectRowWriterProcessor;
import com.univocity.parsers.common.processor.OutputValueSwitch;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.testng.annotations.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.testng.Assert.*;

public class Github_66 {

	private Map<String, String> newMap(String type, String data) {
		Map<String, String> out = new HashMap<String, String>();
		out.put("type", type);
		for (String pair : data.split(";")) {
			String[] kv = pair.split("=>");
			out.put(kv[0], kv[1]);
		}

		return out;
	}

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

	@Test
	public void testMultiple() {
		OutputValueSwitch writerSwitch = new OutputValueSwitch("type");
		writerSwitch.addSwitchForValue("SUPER", new ObjectRowWriterProcessor(), "type", "h1", "h2", "h3", "h4");
		writerSwitch.addSwitchForValue("SUB1", new ObjectRowWriterProcessor(), "type", "a", "b", "c", "d", "e", "f", "g");
		writerSwitch.addSwitchForValue("SUB2", new ObjectRowWriterProcessor(), "type", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setExpandIncompleteRows(true);
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderWritingEnabled(false);
		settings.setRowWriterProcessor(writerSwitch);

		StringWriter output = new StringWriter();
		CsvWriter writer = new CsvWriter(output, settings);

		writer.writeRow(newMap("SUPER", "h1=>v1;h2=>v2;h3=>v3;h4=>v4"));
		writer.writeRow(newMap("SUB1", "a=>v5;d=>v6;e=>v7;g=>v8"));
		writer.writeRow(newMap("SUB2", "q=>v9;u=>v10;w=>v11;y=>v12"));
		writer.writeRow(newMap("SUB1", "a=>v13;d=>v14;g=>v15"));
		writer.writeRow(newMap("SUB1", "a=>v16;d=>v17;f=>v18"));
		writer.close();

		assertEquals(output.toString(), "" +
				"SUPER,v1,v2,v3,v4\n" +
				"SUB1,v5,,,v6,v7,,v8\n" +
				"SUB2,,v9,,,,v10,,v11,,v12,\n" +
				"SUB1,v13,,,v14,,,v15\n" +
				"SUB1,v16,,,v17,,v18,\n");

	}

	@Test
	public void testMultiple2() {
		OutputValueSwitch writerSwitch = new OutputValueSwitch("type"); //switch based on field name
		writerSwitch.addSwitchForValue("SUPER", new ObjectRowWriterProcessor(), "type", "h1", "h2", "h3", "h4");
		writerSwitch.addSwitchForValue("SUB1", new ObjectRowWriterProcessor(), "type", "a", "b", "c", "d", "e", "f", "g");
		writerSwitch.addSwitchForValue("SUB2", new ObjectRowWriterProcessor(), "type", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
		writerSwitch.addSwitchForValue("SUB3", new ObjectRowWriterProcessor(), "type", "a", "b", "c");

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setExpandIncompleteRows(true);
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderWritingEnabled(false);
		settings.setRowWriterProcessor(writerSwitch);

		StringWriter output = new StringWriter();
		CsvWriter writer = new CsvWriter(output, settings);

		writer.writeRow(newMap("SUPER", "h1=>v1;h2=>v2;h3=>v3"));
		writer.writeRow(newMap("SUB1", "a=>v5;d=>v6;e=>v7;g=>v8"));
		writer.writeRow(newMap("SUB2", "q=>v9;u=>v10;w=>v11;y=>v12"));
		writer.writeRow(newMap("SUB1", "a=>v13;d=>v14;g=>v15"));
		writer.writeRow(newMap("SUB1", "a=>v16;d=>v17;f=>v18"));
		writer.writeRow(newMap("SUB3", "a=>v16;b=>v17"));
		writer.writeRow(newMap("SUPER", "h1=>v1;h3=>v3"));

		writer.processRecord("SUPER", "v1", null, null, "v4");
		writer.processRecord("SUB1", "v1", null, null, "v4");
		writer.processRecord("SUB2", "v1", null, null, "v4");
		writer.processRecord("SUB3", "v1", null, null, "v4"); //v4 goes beyond the number of headers

		writer.close();


		assertEquals(output.toString(), "" +
				"SUPER,v1,v2,v3,\n" +
				"SUB1,v5,,,v6,v7,,v8\n" +
				"SUB2,,v9,,,,v10,,v11,,v12,\n" +
				"SUB1,v13,,,v14,,,v15\n" +
				"SUB1,v16,,,v17,,v18,\n" +
				"SUB3,v16,v17,\n" +
				"SUPER,v1,,v3,\n" +
				"SUPER,v1,,,v4\n" +
				"SUB1,v1,,,v4,,,\n" +
				"SUB2,v1,,,v4,,,,,,,\n" +
				"SUB3,v1,,,v4\n"); //we can't lose v4
	}


	@Test
	public void testMapWithUnexpectedHeaders() {
		OutputValueSwitch writerSwitch = new OutputValueSwitch("type");
		writerSwitch.addSwitchForValue("SUPER", new ObjectRowWriterProcessor(), "type", "h1", "h2", "h3", "h4");
		writerSwitch.addSwitchForValue("DUPER", new ObjectRowWriterProcessor(), "type", "h4", "Z1", "Z3", "h1");

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setExpandIncompleteRows(true);
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderWritingEnabled(false);
		settings.setRowWriterProcessor(writerSwitch);

		StringWriter output = new StringWriter();
		CsvWriter writer = new CsvWriter(output, settings);

		writer.writeRow(newMap("SUPER", "Z1=>v1;h2=>v2;h3=>v3;h4=>v4"));
		writer.writeRow(newMap("DUPER", "Z1=>v1;h2=>v2;Z3=>v3;h4=>v4"));
		writer.close();

		assertEquals(output.toString(), "" +
				"SUPER,,v2,v3,v4\n" +
				"DUPER,v4,v1,v3,\n");
	}
}

