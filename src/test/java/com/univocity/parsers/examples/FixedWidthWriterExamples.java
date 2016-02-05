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
package com.univocity.parsers.examples;


import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

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


}
