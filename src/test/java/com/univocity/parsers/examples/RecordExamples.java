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
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.testng.annotations.Test;

import java.util.*;

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

		print("Average car price: $" + (sumOfPrices / allRecords.size()));
		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example002RecordMetadata() throws Exception {

		CsvParserSettings settings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		settings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(settings);

		// parses all records in one go
		List<Record> allRecords = parser.parseAllRecords(getReader("/examples/example.csv"));

		//##CODE_START
		parser.getRecordMetadata().setTypeOfColumns(Long.class, "year", "price");
		parser.getRecordMetadata().setDefaultValueOfColumns("0", "Year");
		parser.getRecordMetadata().convertFields(Conversions.replace("\\.00", "")).set("Price");

		for (Record record : allRecords) {
			Long year = record.getLong("year");
			String model = record.getString("MODEL");
			Long price = record.getLong("Price");
			println(year + " " + model + ": $" + price);
		}

		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example003RecordToMap() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		settings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(settings);

		//##CODE_START
		// this time, lest's parse on demand.
		parser.beginParsing(getReader("/examples/example.csv"));

		//null year should become 0000
		parser.getRecordMetadata().setDefaultValueOfColumns("0000", "Year");

		//decimal separator in prices will be replaced by comma
		parser.getRecordMetadata().convertFields(Conversions.replace("\\.00", ",00")).set("Price");

		//make will be uppercase
		parser.getRecordMetadata().convertFields(Conversions.toUpperCase()).set("make");

		//let's fill a map with values.
		LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();

		//create instances of Record on demand
		Record record;
		while ((record = parser.parseNextRecord()) != null) {
			//we can get the original values of selected columns (by name or index) in a map.
			//Map<String, String> originalValues = record.toFieldMap();

			//to get the converted values as specified in the record metadata use the method ending with "ObjectMap"
			//Map<String, Object> convertedValues = record.toFieldObjectMap();

			//all map methods allow you to choose which columns to get data from. Here we select just the (originally parsed) year:
			Map<String, String> originalYearValues = record.toFieldMap("YEAR"); //the character case of the selection is kept regardless of what the headers contain.
			println(originalYearValues); //original values, no conversions applied.

			//instead of creating new maps every time, we can also reuse maps and invoke the "fill" methods
			record.fillFieldObjectMap(values);
			println(values);

		}

		//##CODE_END
		printAndValidate();
	}
}
