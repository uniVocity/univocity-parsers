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

public class FixedWidthWriterExamples extends Example {

	@Test
	public void example001WriteWithAlignmentAndPadding() throws Exception {

		//##CODE_START
		FixedWidthFieldLengths lengths = new FixedWidthFieldLengths();
		//"id" has length of 5 characters, is aligned to the right and unwritten spaces should be represented as 0
		lengths.addField("id", 5, FieldAlignment.RIGHT, '0');

		//"code" is aligned to the center, and padded with '_'
		lengths.addField("code", 20, FieldAlignment.CENTER, '_');

		//name and quantity use the default padding defined in the settings (further below).
		lengths.addField("name", 15, FieldAlignment.LEFT);
		lengths.addField("quantity", 5, FieldAlignment.CENTER); //"quantity" has more than 5 characters. This header will be truncated.
		lengths.addField("total", 5, FieldAlignment.RIGHT, '0');

		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(lengths);

		//this is the default padding to use to represent unwritten spaces.
		settings.getFormat().setPadding('.');

		//The following settings will override the individual column padding and alignment when writing headers only.
		//we want to write header rows, but use the default padding for them.
		settings.setUseDefaultPaddingForHeaders(true);
		//we also want to align headers to the left.
		settings.setDefaultAlignmentForHeaders(FieldAlignment.LEFT);

		//Let's create the writer
		FixedWidthWriter writer = new FixedWidthWriter(settings);

		//Writing the headers into a formatted String.
		String headers = writer.writeHeadersToString();

		//And a few records
		String line1 = writer.writeRowToString(new String[]{"45", "ABC", "cool thing", "3", "135"});
		String line2 = writer.writeRowToString(new String[]{"8001", "XYZ", "expensive thing", "1", "8001"});

		//Let's see how they look like:
		println(headers);
		println(line1);
		println(line2);

		//##CODE_END
		printAndValidate();
	}



}
