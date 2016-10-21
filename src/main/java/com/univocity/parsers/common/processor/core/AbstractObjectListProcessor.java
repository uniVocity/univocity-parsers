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
package com.univocity.parsers.common.processor.core;

import com.univocity.parsers.common.*;

import java.util.*;

/**
 *
 * A convenience {@link Processor} implementation for storing all rows parsed and converted to Object arrays into a list.
 * A typical use case of this class will be:
 *
 * <hr><blockquote><pre>{@code
 *
 * ObjectRowListProcessor processor = new ObjectRowListProcessor();
 * processor.convertIndexes(Conversions.toBigDecimal()).set(4, 6);
 * parserSettings.setRowProcessor(new ObjectRowListProcessor());
 * parser.parse(reader); // will invoke the {@link AbstractObjectListProcessor#rowProcessed(Object[], T)} method for each parsed record.
 *
 * String[] headers = rowProcessor.getHeaders();
 * List&lt;Object[]&gt; rows = rowProcessor.getRows();
 * BigDecimal value1 = (BigDecimal) row.get(4);
 * BigDecimal value2 = (BigDecimal) row.get(6);
 * }</pre></blockquote><hr>
 *
 * @see AbstractParser
 * @see Processor
 * @see AbstractProcessor
 * @see AbstractObjectProcessor
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class AbstractObjectListProcessor<T extends Context> extends AbstractObjectProcessor<T> {

	private List<Object[]> rows;
	private String[] headers;

	@Override
	public void processStarted(T context) {
		super.processStarted(context);
		rows = new ArrayList<Object[]>(100);
	}

	/**
	 * Stores the row extracted by the parser and them converted to an Object array into a list.
	 *
	 * @param row the data extracted by the parser for an individual record and converted to an Object array.
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	@Override
	public void rowProcessed(Object[] row, T context) {
		rows.add(row);
	}

	@Override
	public void processEnded(T context) {
		super.processEnded(context);
		this.headers = context.headers();

	}

	/**
	 * Returns the list of parsed and converted records
	 * @return the list of parsed and converted records
	 */
	public List<Object[]> getRows() {
		return rows == null ? Collections.<Object[]>emptyList() : rows;
	}

	/**
	 * Returns the record headers. This can be either the headers defined in {@link CommonSettings#getHeaders()} or the headers parsed in the file when {@link CommonSettings#getHeaders()}  equals true
	 * @return the headers of all records parsed.
	 */
	public String[] getHeaders() {
		return headers;
	}

}
