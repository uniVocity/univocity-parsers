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

import com.univocity.parsers.common.*;

import java.util.*;

/**
 * A concrete implementation of {@link RowProcessorSwitch} that allows switching among different implementations of
 * {@link RowProcessor} based on values found on the rows parsed from the input.
 */
public class InputValueSwitch extends RowProcessorSwitch {

	private int columnIndex = -1;
	private String columnName = null;
	private Switch[] switches = new Switch[0];
	private Switch defaultSwitch = new Switch(NoopRowProcessor.instance, null, null, null);
	private String[] headers;

	private static final Comparator<String> caseSensitiveComparator = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			return (o1 == o2 || o1 != null && o1.equals(o2)) ? 0 : 1; //strings are interned, no issues here
		}
	};

	private static final Comparator<String> caseInsensitiveComparator = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			return (o1 == o2 || o1 != null && o1.equalsIgnoreCase(o2)) ? 0 : 1;  //strings are interned, no issues here
		}
	};

	private Comparator<String> comparator = caseInsensitiveComparator;

	/**
	 * Creates a switch that will analyze the first column of rows found in the input to determine which
	 * {@link RowProcessor} to use for each parsed row
	 */
	public InputValueSwitch() {
		this(0);
	}

	/**
	 * Creates a switch that will analyze a column of rows parsed from the input to determine which
	 * {@link RowProcessor} to use.
	 * @param columnIndex the column index whose value will be used to determine which {@link RowProcessor} to use for each parsed row.
	 */
	public InputValueSwitch(int columnIndex) {
		if (columnIndex < 0) {
			throw new IllegalArgumentException("Column index must be positive");
		}
		this.columnIndex = columnIndex;
	}

	/**
	 * Creates a switch that will analyze a column in rows parsed from the input to determine which
	 * {@link RowProcessor} to use.
	 * @param columnName name of the column whose values will be used to determine which {@link RowProcessor} to use for each parsed row.
	 */
	public InputValueSwitch(String columnName) {
		if (columnName == null || columnName.trim().isEmpty()) {
			throw new IllegalArgumentException("Column name cannot be blank");
		}
		this.columnName = columnName;
	}

	/**
	 * Configures the switch to be case sensitive when comparing values provided in {@link #addSwitchForValue(String, RowProcessor, String...)}
	 * with the column given in the constructor of this class.
	 * @param caseSensitive a flag indicating whether the switch should compare values not considering the character case.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.comparator = caseSensitive ? caseSensitiveComparator : caseInsensitiveComparator;
	}

	/**
	 * Configures the switch to use a custom {@link Comparator} to compare values in the column to analyze which is given in the constructor of this class.
	 * @param comparator the comparator to use for matching values in the input column with the values provided in  {@link #addSwitchForValue(String, RowProcessor, String...)}
	 */
	public void setComparator(Comparator<String> comparator) {
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator must not be null");
		}
		this.comparator = comparator;
	}

	/**
	 * Defines a default {@link RowProcessor} implementation to use when no matching value is found in the input row.
	 * @param rowProcessor the default row processor implementation
	 * @param headersToUse the (optional) sequence of headers to assign to the {@link ParsingContext} of the given row processor
	 */
	public void setDefaultSwitch(RowProcessor rowProcessor, String... headersToUse) {
		defaultSwitch = new Switch(rowProcessor, headersToUse, null, null);
	}

	/**
	 * Associates a {@link RowProcessor} implementation with an expected value to be matched in the column provided in the constructor of this class.
	 * @param value the value to match against the column of the current input row and trigger the usage of the given row processor implementation.
	 * @param rowProcessor the row processor implementation when the given value matches with the contents in the column provided in the constructor of this class.
	 * @param headersToUse the (optional) sequence of headers to assign to the {@link ParsingContext} of the given row processor
	 */
	public void addSwitchForValue(String value, RowProcessor rowProcessor, String... headersToUse) {
		switches = Arrays.copyOf(switches, switches.length + 1);
		switches[switches.length - 1] = new Switch(rowProcessor, headersToUse, value, null);
	}

	/**
	 * Associates a {@link RowProcessor} implementation with a custom matching algorithm to be executed in the column provided in the constructor of this class.
	 * @param matcher a user defined matching implementation to execute against the values in the column of the current input row and trigger the usage of the given row processor implementation.
	 * @param rowProcessor the row processor implementation when the given value matches with the contents in the column provided in the constructor of this class.
	 * @param headersToUse the (optional) sequence of headers to assign to the {@link ParsingContext} of the given row processor
	 */
	public void addSwitchForValue(CustomMatcher matcher, RowProcessor rowProcessor, String... headersToUse) {
		switches = Arrays.copyOf(switches, switches.length + 1);
		switches[switches.length - 1] = new Switch(rowProcessor, headersToUse, null, matcher);
	}


	@Override
	public String[] getHeaders() {
		return headers;
	}

	@Override
	protected RowProcessor switchRowProcessor(String[] row, ParsingContext context) {
		if (columnIndex == -1) {
			String[] headers = context.headers();
			if (headers == null) {
				throw new DataProcessingException("Unable to determine position of column named '" + columnName + "' as no headers have been defined nor extracted from the input");
			}
			columnIndex = ArgumentUtils.indexOf(headers, columnName);
			if (columnIndex == -1) {
				throw new DataProcessingException("Unable to determine position of column named '" + columnName + "' as it does not exist in the headers. Available headers are " + Arrays.toString(headers));
			}
		}

		if (columnIndex < row.length) {
			String valueToMatch = row[columnIndex];

			for (int i = 0; i < switches.length; i++) {
				Switch s = switches[i];
				if (s.matcher != null && s.matcher.matches(valueToMatch)) {
					return s.processor;
				} else if (comparator.compare(valueToMatch, s.value) == 0) {
					headers = s.headers;
					return s.processor;
				}
			}
		}
		headers = null;
		return defaultSwitch.processor;
	}

	private static class Switch {
		final RowProcessor processor;
		final String[] headers;
		final String value;
		final CustomMatcher matcher;

		Switch(RowProcessor processor, String[] headers, String value, CustomMatcher matcher) {
			this.processor = processor;
			this.headers = headers == null || headers.length == 0 ? null : headers;
			this.value = value == null ? null : value.intern();
			this.matcher = matcher;
		}
	}
}
