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

public class ObjectColumnProcessor extends ObjectRowProcessor implements ColumnReaderProcessor<Object> {

	private final ColumnSplitter<Object> splitter;

	public ObjectColumnProcessor() {
		this(1000);
	}

	public ObjectColumnProcessor(int expectedRowCount) {
		splitter = new ColumnSplitter<Object>(expectedRowCount);
	}

	@Override
	public final String[] getHeaders() {
		return splitter.getHeaders();
	}

	@Override
	public final List<List<Object>> getColumnValuesAsList() {
		return splitter.getColumnValues();
	}

	@Override
	public final void putColumnValuesInMapOfNames(Map<String, List<Object>> map) {
		splitter.putColumnValuesInMapOfNames(map);
	}

	@Override
	public final void putColumnValuesInMapOfIndexes(Map<Integer, List<Object>> map) {
		splitter.putColumnValuesInMapOfIndexes(map);
	}

	@Override
	public final Map<String, List<Object>> getColumnValuesAsMapOfNames() {
		return splitter.getColumnValuesAsMapOfNames();
	}

	@Override
	public final Map<Integer, List<Object>> getColumnValuesAsMapOfIndexes() {
		return splitter.getColumnValuesAsMapOfIndexes();
	}

	@Override
	public final void rowProcessed(Object[] row, ParsingContext context) {
		splitter.addValuesToColumns(row, context);
	}

	@Override
	public final void processStarted(ParsingContext context) {
		super.processStarted(context);
		splitter.clearValuesAndHeaders();
	}
}
