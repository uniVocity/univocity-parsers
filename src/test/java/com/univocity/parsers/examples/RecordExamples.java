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

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.testng.annotations.Test;

import java.util.List;

public class RecordExamples extends Example {

	@Test
	public void example001ParseRecords() throws Exception {

		CsvParserSettings settings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		//##CODE_START

		settings.getFormat().setDelimiter(';');
		settings.getFormat().setQuoteEscape('\\');

		settings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(settings);

		// parses all records in one go
		List<Record> allRecords = parser.parseAllRecords(getReader("/examples/european.csv"));

		double sumOfPrices = 0.0;
		for (Record record : allRecords) {
			//Let's use the convenience methods in the record class to convert the parsed data into a Double.
			//Numbers are stored in the file using the european format.
			//Here we read the "price" column, using the "0,00" format mask, and decimal separator set to comma.
			Double price = record.getDouble("price", "0,00", "decimalSeparator=,");

			if (price != null) {
				sumOfPrices += price;
			}
		}

		printAndValidate("Average car price: $" + (sumOfPrices / allRecords.size()));
	}
}
