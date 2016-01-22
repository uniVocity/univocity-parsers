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
package com.univocity.parsers.common.processor;

import java.util.Arrays;
import java.util.Comparator;

/**
 * A concrete implementation of {@link RowWriterProcessorSwitch} that allows switching among different implementations of
 * {@link RowWriterProcessor} based on values found on rows to be written to an output
 */
public class OutputValueSwitch extends RowWriterProcessorSwitch<Object[]> {

	private Switch defaultSwitch;
	private Switch[] switches = new Switch[0];
	private Switch selectedSwitch;

	private final int columnIndex;
	@SuppressWarnings("rawtypes")
	private Comparator comparator = new Comparator<Object>() {
		@Override
		public int compare(Object o1, Object o2) {
			return o1 == o2 ? 0 : o1 != null && o1.equals(o2) ? 0 : 1;
		}
	};

	/**
	 * Creates a switch that will analyze the first column of output rows to determine which
	 * {@link RowWriterProcessor} to use for each output row
	 */
	public OutputValueSwitch() {
		this(0);
	}

	/**
	 * Creates a switch that will analyze a column of output rows to determine which
	 * {@link RowWriterProcessor} to use.
	 * @param columnIndex the column index whose value will be used to determine which {@link RowWriterProcessor} to use for each output row.
	 */
	public OutputValueSwitch(int columnIndex) {
		if (columnIndex < 0) {
			throw new IllegalArgumentException("Column index must be positive");
		}
		this.columnIndex = columnIndex;
	}

	/**
	 * Configures the switch to use a custom {@link Comparator} to compare values in the column to analyze which is given in the constructor of this class.
	 * @param comparator the comparator to use for matching values in the output column with the values provided in  {@link #addSwitchForValue(Object, RowWriterProcessor)}
	 */
	public void setComparator(Comparator<?> comparator) {
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator must not be null");
		}
		this.comparator = comparator;
	}

	/**
	 * Defines a default {@link RowWriterProcessor} implementation to use when no matching value is found in the output row.
	 * @param rowProcessor the default row writer processor implementation
	 * @param headersToUse the (optional) sequence of headers to assign to the given row writer processor
	 */
	public void setDefaultSwitch(RowWriterProcessor<Object[]> rowProcessor, String... headersToUse) {
		defaultSwitch = new Switch(rowProcessor, headersToUse, null, null);
	}

	/**
	 * Defines a default {@link RowWriterProcessor} implementation to use when no matching value is found in the output row.
	 * @param rowProcessor the default row writer processor implementation
	 * @param indexesToUse the (optional) sequence of column indexes to assign to the given row writer processor
	 */
	public void setDefaultSwitch(RowWriterProcessor<Object[]> rowProcessor, int... indexesToUse) {
		defaultSwitch = new Switch(rowProcessor, null, indexesToUse, null);
	}

	@Override
	protected String[] getHeaders() {
		return selectedSwitch != null ? selectedSwitch.headers : null;
	}

	@Override
	protected int[] getIndexes() {
		return selectedSwitch != null ? selectedSwitch.indexes : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RowWriterProcessor<Object[]> switchRowProcessor(Object[] row) {
		if (row == null || row.length < columnIndex) {
			return defaultSwitch.processor;
		}

		for (int i = 0; i < switches.length; i++) {
			Switch s = switches[i];
			if (comparator.compare(row[columnIndex], s.value) == 0) {
				selectedSwitch = s;
				return s.processor;
			}
		}

		selectedSwitch = defaultSwitch;
		return defaultSwitch != null ? defaultSwitch.processor : null;
	}

	/**
	 * Associates a {@link RowWriterProcessor} implementation with an expected value to be matched in the column provided in the constructor of this class.
	 * @param value the value to match against the column of incoming output rows and trigger the usage of the given row writer processor implementation.
	 * @param rowProcessor the row writer processor implementation to use when the given value matches with the contents of the column provided in the constructor of this class.
	 * @param headersToUse the (optional) sequence of headers to assign to the given row writer processor
	 */
	public void addSwitchForValue(Object value, RowWriterProcessor<Object[]> rowProcessor, String... headersToUse) {
		addSwitch(new Switch(rowProcessor, headersToUse, null, value));
	}

	/**
	 * Associates a {@link RowWriterProcessor} implementation with an expected value to be matched in the column provided in the constructor of this class.
	 * @param value the value to match against the column of incoming output rows and trigger the usage of the given row writer processor implementation.
	 * @param rowProcessor the row writer processor implementation to use when the given value matches with the contents of the column provided in the constructor of this class.
	 */
	public void addSwitchForValue(Object value, RowWriterProcessor<Object[]> rowProcessor) {
		addSwitch(new Switch(rowProcessor, null, null, value));
	}

	/**
	 * Associates a {@link RowWriterProcessor} implementation with an expected value to be matched in the column provided in the constructor of this class.
	 * @param value the value to match against the column of incoming output rows and trigger the usage of the given row writer processor implementation.
	 * @param rowProcessor the row writer processor implementation to use when the given value matches with the contents of the column provided in the constructor of this class.
	 * @param indexesToUse the (optional) sequence of column indexes to assign to the given row writer processor
	 */
	public void addSwitchForValue(Object value, RowWriterProcessor<Object[]> rowProcessor, int... indexesToUse) {
		addSwitch(new Switch(rowProcessor, null, indexesToUse, value));
	}

	private void addSwitch(Switch newSwitch) {
		switches = Arrays.copyOf(switches, switches.length + 1);
		switches[switches.length - 1] = newSwitch;
	}

	private static class Switch {
		final RowWriterProcessor<Object[]> processor;
		final String[] headers;
		final int[] indexes;
		final Object value;

		Switch(RowWriterProcessor<Object[]> processor, String[] headers, int[] indexes, Object value) {
			this.processor = processor;
			this.headers = headers == null || headers.length == 0 ? null : headers;
			this.indexes = indexes == null || indexes.length == 0 ? null : indexes;
			this.value = value;
		}
	}
}
