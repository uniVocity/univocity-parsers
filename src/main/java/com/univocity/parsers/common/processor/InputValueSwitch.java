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

import java.util.*;

import com.univocity.parsers.common.*;

public class InputValueSwitch extends RowProcessorSwitch {

	private int columnIndex = -1;
	private String columnName = null;
	private Switch[] switches = new Switch[0];
	private Switch defaultSwitch = new Switch(NoopRowProcessor.instance, null, null);
	private String[] headers;

	private static final Comparator<String> caseSensitiveComparator = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			return (o1 == o2 || o1 != null && o1.equals(o2)) ? 0 : 1;
		}
	};

	private static final Comparator<String> caseInsensitiveComparator = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			return (o1 == o2 || o1 != null && o1.equalsIgnoreCase(o2)) ? 0 : 1;
		}
	};

	private Comparator<String> comparator = caseInsensitiveComparator;

	public InputValueSwitch() {
		this(0);
	}

	public InputValueSwitch(int columnIndex) {
		if (columnIndex < 0) {
			throw new IllegalArgumentException("Column index must be positive");
		}
		this.columnIndex = columnIndex;
	}

	public InputValueSwitch(String columnName) {
		if (columnName == null || columnName.trim().isEmpty()) {
			throw new IllegalArgumentException("Column name cannot be blank");
		}
		this.columnName = columnName;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.comparator = caseSensitive ? caseSensitiveComparator : caseInsensitiveComparator;
	}

	public void setComparator(Comparator<String> comparator) {
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator must not be null");
		}
		this.comparator = comparator;
	}

	public void setDefaultSwitch(RowProcessor rowProcessor, String... headersToUse) {
		defaultSwitch = new Switch(rowProcessor, headersToUse, null);
	}

	public void addSwitchForValue(String value, RowProcessor rowProcessor, String... headersToUse) {
		switches = Arrays.copyOf(switches, switches.length + 1);
		switches[switches.length - 1] = new Switch(rowProcessor, headersToUse, value);
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
			for (int i = 0; i < switches.length; i++) {
				Switch s = switches[i];
				if (comparator.compare(row[columnIndex], s.value) == 0) {
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

		Switch(RowProcessor processor, String[] headers, String value) {
			this.processor = processor;
			this.headers = headers == null || headers.length == 0 ? null : headers;
			this.value = value == null ? null : value.intern();
		}
	}
}
