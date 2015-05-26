/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common;

import java.util.*;

import com.univocity.parsers.common.processor.*;

class RowProcessorSwitcher {

	private final int columnIndex;
	private final String columnValue;
	private final RowProcessor rowProcessor;
	private final String[] fieldNames;
	private ParsingContext rowContext = null;

	RowProcessorSwitcher(int columnIndex, String columnValue, String[] fieldNames, RowProcessor rowProcessor) {
		this.columnIndex = columnIndex;
		this.columnValue = columnValue;
		this.rowProcessor = rowProcessor;
		this.fieldNames = fieldNames;
	}

	boolean executeRowProcessor(String[] parsedRow, ParsingContext context) {
		if (rowContext != null) {
			processStarted(context);
		}

		if (columnIndex < parsedRow.length) {
			String parsedValue = parsedRow[columnIndex];

			if (columnValue.equals(parsedValue)) {
				try {
					rowProcessor.rowProcessed(parsedRow, rowContext);
				} catch (DataProcessingException ex) {
					ex.setContext(rowContext);
					throw ex;
				} catch (Throwable t) {
					throw new DataProcessingException("Unexpected error processing input row " + Arrays.toString(parsedRow) + " using RowProcessor " + rowProcessor.getClass().getName() + ".", parsedRow, t);
				}

				return true;
			}
		}
		return false;
	}

	private void processStarted(ParsingContext context) {
		if (fieldNames != null) {
			rowContext = new ParsingContextWrapper(context) {
				@Override
				public String[] headers() {
					return fieldNames;
				}
			};
		} else {
			rowContext = context;
		}
		rowProcessor.processStarted(rowContext);
	}

	public RowProcessor getRowProcessor() {
		return rowProcessor;
	}

	public void processEnded() {
		try {
			rowProcessor.processEnded(rowContext);
		} catch (Throwable ex) {
			//ignore errors here.
		}
	}
}
