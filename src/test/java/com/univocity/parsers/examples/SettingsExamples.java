/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.util.*;

public class SettingsExamples extends Example {

	@Test
	public void example001ColumnSelection() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		parserSettings.getFormat().setLineSeparator("\n");

		//##CODE_START
		// Here we select only the columns "Price", "Year" and "Make".
		// The parser just skips the other fields
		parserSettings.selectFields("Price", "Year", "Make");

		// let's parse with these settings and print the parsed rows.
		List<String[]> parsedRows = parseWithSettings(parserSettings);
		//##CODE_END
		printAndValidate(parsedRows);
	}

	@Test
	public void example002ColumnSelectionWithNoReordering() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		parserSettings.getFormat().setLineSeparator("\n");

		//##CODE_START
		// Here we select only the columns "Price", "Year" and "Make".
		// The parser just skips the other fields
		parserSettings.selectFields("Price", "Year", "Make");

		// Column reordering is enabled by default. When you disable it,
		// all columns will be produced in the order they are defined in the file.
		// Fields that were not selected will be null, as they are not processed by the parser
		parserSettings.setColumnReorderingEnabled(false);

		// Let's parse with these settings and print the parsed rows.
		List<String[]> parsedRows = parseWithSettings(parserSettings);
		//##CODE_END
		printAndValidate(parsedRows);
	}

	@Test
	public void example003ColumnSelectionByIndex() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		parserSettings.getFormat().setLineSeparator("\n");

		//##CODE_START
		// Here we select only the columns by their indexes.
		// The parser just skips the values in other columns
		parserSettings.selectIndexes(4, 0, 1);

		// let's parse with these settings and print the parsed rows.
		List<String[]> parsedRows = parseWithSettings(parserSettings);
		//##CODE_END
		printAndValidate(parsedRows);
	}

	@Test
	public void example004LotsOfDifferentSettings() {
		CsvParserSettings parserSettings = new CsvParserSettings();

		//##CODE_START
		//You can configure the parser to automatically detect what line separator sequence is in the input
		parserSettings.setLineSeparatorDetectionEnabled(true);

		// sets what is the default value to use when the parsed value is null
		parserSettings.setNullValue("<NULL>");

		// sets what is the default value to use when the parsed value is empty
		parserSettings.setEmptyValue("<EMPTY>"); // for CSV only

		// sets the headers of the parsed file. If the headers are set then 'setHeaderExtractionEnabled(true)'
		// will make the parser simply ignore the first input row.
		parserSettings.setHeaders("a", "b", "c", "d", "e");

		// prints the columns in reverse order.
		// NOTE: when fields are selected, all rows produced will have the exact same number of columns
		parserSettings.selectFields("e", "d", "c", "b", "a");

		// does not skip leading whitespaces
		parserSettings.setIgnoreLeadingWhitespaces(false);

		// does not skip trailing whitespaces
		parserSettings.setIgnoreTrailingWhitespaces(false);

		// reads a fixed number of records then stop and close any resources
		parserSettings.setNumberOfRecordsToRead(9);

		// does not skip empty lines
		parserSettings.setSkipEmptyLines(false);

		// sets the maximum number of characters to read in each column.
		// The default is 4096 characters. You need this to avoid OutOfMemoryErrors in case a file
		// does not have a valid format. In such cases the parser might just keep reading from the input
		// until its end or the memory is exhausted. This sets a limit which avoids unwanted JVM crashes.
		parserSettings.setMaxCharsPerColumn(100);

		// for the same reasons as above, this sets a hard limit on how many columns an input row can have.
		// The default is 512.
		parserSettings.setMaxColumns(10);

		// Sets the number of characters held by the parser's buffer at any given time.
		parserSettings.setInputBufferSize(1000);

		// Disables the separate thread that loads the input buffer. By default, the input is going to be loaded incrementally
		// on a separate thread if the available processor number is greater than 1. Leave this enabled to get better performance
		// when parsing big files (> 100 Mb).
		parserSettings.setReadInputOnSeparateThread(false);

		// let's parse with these settings and print the parsed rows.
		List<String[]> parsedRows = parseWithSettings(parserSettings);
		//##CODE_END
		printAndValidate(parsedRows);
	}

	@Test
	public void example005FixedWidthSettings() {
		//##CODE_START
		// For the sake of the example, we will not read the last 8 characters (for the Year column).
		// We will also NOT set the padding character to '_' so the output makes more sense for reading
		// and you can see what characters are being processed
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(new FixedWidthFields(4, 5, 40, 40 /*, 8*/));

		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		parserSettings.getFormat().setLineSeparator("\n");

		// The fixed width parser settings has most of the settings for CSV.
		// These are the only extra settings you need:

		// If a row has more characters than what is defined, skip them until the end of the line.
		parserSettings.setSkipTrailingCharsUntilNewline(true);

		// If a record has less characters than what is expected and a new line is found,
		// this record is considered parsed. Data in the next row will be parsed as a new record.
		parserSettings.setRecordEndsOnNewline(true);

		RowListProcessor rowProcessor = new RowListProcessor();

		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		FixedWidthParser parser = new FixedWidthParser(parserSettings);
		parser.parse(getReader("/examples/example.txt"));

		List<String[]> rows = rowProcessor.getRows();
		//##CODE_END
		printAndValidate(rows);
	}

	/**
	 * Parses the example input file (/examples/example.csv) with a given setting.
	 * @param parserSettings settings used to parse the example.csv file
	 * @return a list with all parsed rows.
	 */
	private List<String[]> parseWithSettings(CsvParserSettings parserSettings) {
		RowListProcessor rowProcessor = new RowListProcessor();

		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(getReader("/examples/example.csv"));

		List<String[]> rows = rowProcessor.getRows();
		return rows;
	}
}
