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
package com.univocity.parsers.examples;


import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

public class FixedWidthWriterExamples extends Example {

	@Test
	public void example001WriteWithAlignmentAndPadding() throws Exception {

		//##CODE_START
		FixedWidthFields fields = new FixedWidthFields();
		//"id" has length of 5 characters, is aligned to the right and unwritten spaces should be represented as 0
		fields.addField("id", 5, FieldAlignment.RIGHT, '0');

		//"code" is aligned to the center, and padded with '_'
		fields.addField("code", 20, FieldAlignment.CENTER, '_');

		//name and quantity use the default padding defined in the settings (further below).
		fields.addField("name", 15, FieldAlignment.LEFT);
		fields.addField("quantity", 5, FieldAlignment.CENTER); //"quantity" has more than 5 characters. This header will be truncated.
		fields.addField("total", 5, FieldAlignment.RIGHT, '0');

		FixedWidthWriterSettings writerSettings = new FixedWidthWriterSettings(fields);

		//this is the default padding to use to represent unwritten spaces.
		writerSettings.getFormat().setPadding('.');

		//The following settings will override the individual column padding and alignment when writing headers only.
		//we want to write header rows, but use the default padding for them.
		writerSettings.setUseDefaultPaddingForHeaders(true);
		//we also want to align headers to the left.
		writerSettings.setDefaultAlignmentForHeaders(FieldAlignment.LEFT);

		//Let's create the writer
		FixedWidthWriter writer = new FixedWidthWriter(writerSettings);

		//Writing the headers into a formatted String.
		String headers = writer.writeHeadersToString();

		//And a few records
		String line1 = writer.writeRowToString(new String[]{"45", "ABC", "cool thing", "3", "135"});
		String line2 = writer.writeRowToString(new String[]{"8000", "XYZ", "expensive thing", "1", "8000"});

		//Let's see how they look like:
		println(headers);
		println(line1);
		println(line2);

		//We should be able to parse these records as well. Let's give this a try
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(fields);
		parserSettings.setFormat(writerSettings.getFormat());

		FixedWidthParser parser = new FixedWidthParser(parserSettings);
		String[] record1 = parser.parseLine(line1);
		String[] record2 = parser.parseLine(line2);

		println("\nParsed:");
		println(Arrays.toString(record1));
		println(Arrays.toString(record2));

		//##CODE_END
		printAndValidate();
	}


	@Test
	public void example002WriteWithLookahead() throws Exception {
		//##CODE_START
		//Here's the format used for client accounts:
		FixedWidthFields accountFields = new FixedWidthFields();
		accountFields.addField("ID", 10); //account value includes the lookahead value.
		accountFields.addField("Bank", 8);
		accountFields.addField("AccountNumber", 15);
		accountFields.addField("Swift", 12);

		//Format for clients' records
		FixedWidthFields clientFields = new FixedWidthFields();
		clientFields.addField("Lookahead", 5); //clients have their lookahead in a separate column
		clientFields.addField("ClientID", 15, FieldAlignment.RIGHT, '0'); //let's pad client ID's with leading zeroes.
		clientFields.addField("Name", 20);

		FixedWidthWriterSettings settings = new FixedWidthWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setPadding('_');

		//If a record starts with C#, it's a client record, so we associate "C#" with the client format.
		settings.addFormatForLookahead("C#", clientFields);

		//Rows starting with any character then 'A' should be written using the account format
		settings.addFormatForLookahead("?A", accountFields);

		StringWriter out = new StringWriter();

		//Let's write
		FixedWidthWriter writer = new FixedWidthWriter(out, settings);

		writer.writeRow(new Object[]{"C#",23234, "Miss Foo"});
		writer.writeRow(new Object[]{"#A23234", "HSBC", "123433-000", "HSBCAUS"});
		writer.writeRow(new Object[]{"^A234", "HSBC", "222343-130", "HSBCCAD"});
		writer.writeRow(new Object[]{"C#",322, "Mr Bar"});
		writer.writeRow(new Object[]{"@A1234", "CITI", "213343-130", "CITICAD"});

		writer.close();

		print(out);
		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example003WriteWithLookaheadAndDefault() throws Exception {

		//Here's the format used for client accounts:
		FixedWidthFields accountFields = new FixedWidthFields();
		accountFields.addField("ID", 10); //accounts won't have lookaheads
		accountFields.addField("Bank", 8);
		accountFields.addField("AccountNumber", 15);
		accountFields.addField("Swift", 12);

		//Format for clients' records
		FixedWidthFields clientFields = new FixedWidthFields();
		clientFields.addField("Lookahead", 5); //clients have their lookahead in a separate column
		clientFields.addField("ClientID", 15, FieldAlignment.RIGHT, '0'); //let's pad client ID's with leading zeroes.
		clientFields.addField("Name", 20);

		//##CODE_START
		//As accounts don't have a lookahead value, we use their format as the default.
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(accountFields);
		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setPadding('_');

		//If a record starts with C#, it's a client record, so we associate "C#" with the client format.
		//Any other row will be written using the default format (for accounts)
		settings.addFormatForLookahead("C#", clientFields);

		StringWriter out = new StringWriter();

		//Let's write
		FixedWidthWriter writer = new FixedWidthWriter(out, settings);

		writer.writeRow(new Object[]{"C#",23234, "Miss Foo"});
		writer.writeRow(new Object[]{"23234", "HSBC", "123433-000", "HSBCAUS"});
		writer.writeRow(new Object[]{"234", "HSBC", "222343-130", "HSBCCAD"});
		writer.writeRow(new Object[]{"C#",322, "Mr Bar"});
		writer.writeRow(new Object[]{"1234", "CITI", "213343-130", "CITICAD"});

		writer.close();

		print(out);
		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example004WriteWithLookbehind() throws Exception {

		//Here's the format used for client accounts:
		FixedWidthFields accountFields = new FixedWidthFields();
		accountFields.addField("ID", 10); //accounts won't have lookaheads
		accountFields.addField("Bank", 8);
		accountFields.addField("AccountNumber", 15);
		accountFields.addField("Swift", 12);

		//Format for clients' records
		FixedWidthFields clientFields = new FixedWidthFields();
		clientFields.addField("Lookahead", 5); //clients have their lookahead in a separate column
		clientFields.addField("ClientID", 15, FieldAlignment.RIGHT, '0'); //let's pad client ID's with leading zeroes.
		clientFields.addField("Name", 20);

		FixedWidthWriterSettings settings = new FixedWidthWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setPadding('_');

		//##CODE_START
		//If a record starts with C#, it's a client record, so we associate "C#" with the client format.
		settings.addFormatForLookahead("C#", clientFields);
		//If a record written previously had a C#, but the current doesn't, then we are writing accounts. Let's use the account format.
		settings.addFormatForLookbehind("C#", accountFields);
		//##CODE_END

		StringWriter out = new StringWriter();

		//Let's write
		FixedWidthWriter writer = new FixedWidthWriter(out, settings);

		writer.writeRow(new Object[]{"C#",23234, "Miss Foo"});
		writer.writeRow(new Object[]{"23234", "HSBC", "123433-000", "HSBCAUS"});
		writer.writeRow(new Object[]{"234", "HSBC", "222343-130", "HSBCCAD"});
		writer.writeRow(new Object[]{"C#",322, "Mr Bar"});
		writer.writeRow(new Object[]{"1234", "CITI", "213343-130", "CITICAD"});

		writer.close();

		print(out);

		printAndValidate();
	}
}
