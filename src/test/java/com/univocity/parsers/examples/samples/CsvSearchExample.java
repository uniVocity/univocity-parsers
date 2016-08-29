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
package com.univocity.parsers.examples.samples;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;

import java.io.*;
import java.util.*;

/**
 * Example to demonstrate how to implement a basic search over a CSV file.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class CsvSearchExample {

	// Let's create our own RowProcessor to analyze the rows
	static class CsvSearch extends RowListProcessor {

		private final String stringToMatch;
		private final String columnToMatch;
		private int indexToMatch = -1;

		public CsvSearch(String columnToMatch, String stringToMatch){
			this.columnToMatch = columnToMatch;
			this.stringToMatch = stringToMatch.toLowerCase();
		}

		public CsvSearch(int columnToMatch, String stringToMatch){
			this(stringToMatch, null);
			indexToMatch = columnToMatch;
		}

		@Override
		public void rowProcessed(String[] row, ParsingContext context) {
			if(indexToMatch == -1) {
				indexToMatch = context.indexOf(columnToMatch);
			}

			String value = row[indexToMatch];
			if(value != null && value.toLowerCase().contains(stringToMatch)) {
				super.rowProcessed(row, context);
			}
			// else skip the row.
		}
	}

	public static void main(String... args) {
		// let's measure the time roughly
		long start = System.currentTimeMillis();

		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);

		CsvSearch search = new CsvSearch("City", "Paris"); //searching for cities with "paris" in the name

		//We instruct the parser to send all rows parsed to your custom RowProcessor.
		settings.setProcessor(search);

		//Finally, we create a parser
		CsvParser parser = new CsvParser(settings);

		//And parse! All rows are sent to your custom RowProcessor (CsvSearch)
		//I'm using a 150MB CSV file with 1.3 million rows.
		parser.parse(new File("/Users/jbax/dev/data/worldcitiespop.txt"), "ISO-8859-1");

		List<String[]> results = search.getRows();

		//Nothing else to do. The parser closes the input and does everything for you safely. Let's just get the results:
		System.out.println("Rows matched: " + results.size());
		System.out.println("Time taken: " + (System.currentTimeMillis() - start) + " ms");

		System.out.println("Matched rows");
		for(String[] row : results){
			System.out.println(Arrays.toString(row));
		}
	}
}
