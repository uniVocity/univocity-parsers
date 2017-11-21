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

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.core.*;

import java.util.*;

/**
 * A convenience {@link RowProcessor} implementation for storing all rows parsed and converted to Object arrays into a list.
 * A typical use case of this class will be:
 *
 * <hr><blockquote><pre>{@code
 *
 * ObjectRowListProcessor processor = new ObjectRowListProcessor();
 * processor.convertIndexes(Conversions.toBigDecimal()).set(4, 6);
 * parserSettings.setRowProcessor(new ObjectRowListProcessor());
 * parser.parse(reader); // will invoke the {@link AbstractObjectListProcessor#rowProcessed(Object[], Context)} method for each parsed record.
 *
 * String[] headers = rowProcessor.getHeaders();
 * List&lt;Object[]&gt; rows = rowProcessor.getRows();
 * BigDecimal value1 = (BigDecimal) row.get(4);
 * BigDecimal value2 = (BigDecimal) row.get(6);
 * }</pre></blockquote><hr>
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see RowProcessor
 * @see ObjectRowProcessor
 * @see AbstractParser
 */
public class ObjectRowListProcessor extends AbstractObjectListProcessor<ParsingContext> implements RowProcessor {

	/**
	 * Creates a new processor of {@code Object[]} rows with varying types.
	 */
	public ObjectRowListProcessor() {
	}

	/**
	 * Creates a new processor of {@code Object[]} rows with varying types.
	 *
	 * @param expectedRowCount expected number of rows to be parsed from the input.
	 *                         Used to pre-allocate the size of the output {@link List} returned by {@link #getRows()}
	 */
	public ObjectRowListProcessor(int expectedRowCount) {
		super(expectedRowCount);
	}

}
