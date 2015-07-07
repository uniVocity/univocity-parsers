/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common.processor;

import java.util.*;

import com.univocity.parsers.common.*;

/**
 *
 * A convenience {@link RowProcessor} implementation for storing all rows parsed into a list.
 * A typical use case of this class will be:
 *
 * <hr><blockquote><pre>
 *
 * parserSettings.setRowProcessor(new RowListProcessor());
 * parser.parse(reader); // will invoke the {@link RowListProcessor#rowProcessed(String[], ParsingContext)} method for each parsed record.
 *
 * String[] headers = rowProcessor.getHeaders();
 * List&lt;String[]&gt; rows = rowProcessor.getRows();
 *
 * </pre></blockquote><hr>
 *
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class RowListProcessor implements RowProcessor {

	private List<String[]> rows;
	private String[] headers;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processStarted(ParsingContext context) {
		rows = new ArrayList<String[]>(100);
	}

	/**
	 * Stores the row extracted by the parser into a list.
	 *
	 * @param row the data extracted by the parser for an individual record. Note that:
	 * <ul>
	 * <li>it will never by null. </li>
	 * <li>it will never be empty unless explicitly configured using {@link CommonSettings#setSkipEmptyLines(boolean)}</li>
	 * <li>it won't contain lines identified by the parser as comments. To disable comment processing set {@link Format#setComment(char)} to '\0'</li>
	 * </ul>
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	@Override
	public void rowProcessed(String[] row, ParsingContext context) {
		rows.add(row);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processEnded(ParsingContext context) {
		headers = context.headers();
	}

	/**
	 * The list of parsed records
	 * @return the list of parsed records
	 */
	public List<String[]> getRows() {
		return rows;
	}

	/**
	 * Returns the record headers. This can be either the headers defined in {@link CommonSettings#getHeaders()} or the headers parsed in the file when {@link CommonSettings#getHeaders()}  equals true
	 * @return the headers of all records parsed.
	 */
	public String[] getHeaders() {
		return headers;
	}
}
