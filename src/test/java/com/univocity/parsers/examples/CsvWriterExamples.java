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

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

public class CsvWriterExamples extends Example {

	@Test
	public void example001Quoting() throws Exception {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setQuote('\'');
		settings.getFormat().setQuoteEscape('\'');

		writeAndPrint(new String[]{"Value 1", "I'm capable", "Value 2", null}, settings, "By default we only quote fields when there's no choice");

		//But quotes might need to be escaped
		settings.setQuoteEscapingEnabled(true);
		writeAndPrint(new String[]{"Value 1", "I'm capable", "Value 2", null}, settings, "Escaping the quote character");

		//And sometimes values with specific characters should be enclosed in quotes.
		settings.setQuotationTriggers('2', '3', '\t', '\0');
		writeAndPrint(new String[]{"Value 1", "I'm capable", "Value 2", null}, settings, "Forcing quotes around values that contain specific characters");

		//Finally, you might want to put quotes everywhere
		settings.setQuoteAllFields(true);
		writeAndPrint(new String[]{"Value 1", "I'm capable", "Value 2", null}, settings, "Quotes in everything");

		printAndValidate();
	}

	private void writeAndPrint(String[] data, CsvWriterSettings settings, String message) {
		CsvWriter writer = new CsvWriter(settings);
		String result = writer.writeRowToString(data);
		println(message + ":\n\t" + result);
	}
}
