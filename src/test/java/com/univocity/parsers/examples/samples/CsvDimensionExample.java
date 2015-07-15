/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.examples.samples;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;

import java.io.*;

/**
 * Simple example to calculate the dimensions of a CSV file in the fastest way possible.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class CsvDimensionExample {

	// Let's create our own RowProcessor to analyze the rows
	static class CsvDimension extends AbstractRowProcessor {

		int lastColumn = -1;
		long rowCount = 0;

		@Override
		public void rowProcessed(String[] row, ParsingContext context) {
			rowCount++;
			if (lastColumn < row.length) {
				lastColumn = row.length;
			}
		}
	}

	public static void main(String... args) throws FileNotFoundException {
		// let's measure the time roughly
		long start = System.currentTimeMillis();

		//Creates an instance of our own custom RowProcessor, defined above.
		CsvDimension myDimensionProcessor = new CsvDimension();

		CsvParserSettings settings = new CsvParserSettings();

		//This tells the parser that no row should have more than 2,000,000 columns
		settings.setMaxColumns(2000000);

		//Here you can select the column indexes you are interested in reading.
		//The parser will return values for the columns you selected, in the order you defined
		//By selecting no indexes here, no String objects will be created
		settings.selectIndexes(/*nothing here*/);

		//When you select indexes, the columns are reordered so they come in the order you defined.
		//By disabling column reordering, you will get the original row, with nulls in the columns you didn't select
		settings.setColumnReorderingEnabled(false);

		//We instruct the parser to send all rows parsed to your custom RowProcessor.
		settings.setRowProcessor(myDimensionProcessor);

		//Finally, we create a parser
		CsvParser parser = new CsvParser(settings);

		//And parse! All rows are sent to your custom RowProcessor (CsvDimension)
		//I'm using a 150MB CSV file with 1.3 million rows.
		parser.parse(new File("c:/tmp/worldcitiespop.txt"));

		//Nothing else to do. The parser closes the input and does everything for you safely. Let's just get the results:
		System.out.println("Columns: " + myDimensionProcessor.lastColumn);
		System.out.println("Rows: " + myDimensionProcessor.rowCount);
		System.out.println("Time taken: " + (System.currentTimeMillis() - start) + " ms");

	}
}
