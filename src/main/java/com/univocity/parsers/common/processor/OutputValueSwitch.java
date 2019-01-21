/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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

import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A concrete implementation of {@link RowWriterProcessorSwitch} that allows switching among different implementations of
 * {@link RowWriterProcessor} based on values found on rows to be written to an output
 */
public class OutputValueSwitch extends RowWriterProcessorSwitch {

	private Switch defaultSwitch;
	private Switch[] switches = new Switch[0];
	private Switch selectedSwitch;
	private Class[] types = new Class[0];

	private final int columnIndex;
	private final String headerName;

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
	 *
	 * @param columnIndex the column index whose value will be used to determine which {@link RowWriterProcessor} to use for each output row.
	 */
	public OutputValueSwitch(int columnIndex) {
		this.columnIndex = getValidatedIndex(columnIndex);
		this.headerName = null;
	}

	/**
	 * Creates a switch that will analyze a column of output rows to determine which {@link RowWriterProcessor} to use.
	 * When no column index is defined, the switch will use the first column of any input rows sent for writing.
	 *
	 * @param headerName the column name whose value will be used to determine which {@link RowWriterProcessor} to use for each output row.
	 */
	public OutputValueSwitch(String headerName) {
		this.headerName = getValidatedHeaderName(headerName);
		this.columnIndex = 0;
	}

	/**
	 * Creates a switch that will analyze a column of output rows to determine which
	 * {@link RowWriterProcessor} to use.
	 *
	 * @param headerName  the column name whose value will be used to determine which {@link RowWriterProcessor} to use for each output row.
	 * @param columnIndex the position of an input column to use to perform the switch when no headers are available.
	 */
	public OutputValueSwitch(String headerName, int columnIndex) {
		this.columnIndex = getValidatedIndex(columnIndex);
		this.headerName = getValidatedHeaderName(headerName);
	}

	private int getValidatedIndex(int columnIndex) {
		if (columnIndex < 0) {
			throw new IllegalArgumentException("Column index must be positive");
		}
		return columnIndex;
	}

	private String getValidatedHeaderName(String headerName) {
		if (headerName == null || headerName.trim().length() == 0) {
			throw new IllegalArgumentException("Header name cannot be blank");
		}
		return headerName;
	}


	/**
	 * Configures the switch to use a custom {@link Comparator} to compare values in the column to analyze which is given in the constructor of this class.
	 *
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
	 *
	 * @param rowProcessor the default row writer processor implementation
	 * @param headersToUse the (optional) sequence of headers to assign to the given row writer processor
	 */
	public void setDefaultSwitch(RowWriterProcessor<Object[]> rowProcessor, String... headersToUse) {
		defaultSwitch = new Switch(rowProcessor, headersToUse, null, null);
	}

	/**
	 * Defines a default {@link RowWriterProcessor} implementation to use when no matching value is found in the output row.
	 *
	 * @param rowProcessor the default row writer processor implementation
	 * @param indexesToUse the (optional) sequence of column indexes to assign to the given row writer processor
	 */
	public void setDefaultSwitch(RowWriterProcessor<Object[]> rowProcessor, int... indexesToUse) {
		defaultSwitch = new Switch(rowProcessor, null, indexesToUse, null);
	}

	@Override
	protected NormalizedString[] getHeaders() {
		return selectedSwitch != null ? selectedSwitch.headers : null;
	}

	@Override
	protected int[] getIndexes() {
		return selectedSwitch != null ? selectedSwitch.indexes : null;
	}

	private Switch getSwitch(Object value) {
		if (value instanceof Object[]) {
			Object[] row = (Object[]) value;
			if (row.length < columnIndex) {
				return defaultSwitch;
			}
			value = row[columnIndex];
		}
		for (int i = 0; i < switches.length; i++) {
			Switch s = switches[i];
			Class type = types[i];
			if (type != null) {
				if (type.isAssignableFrom(value.getClass())) {
					return s;
				}
			} else if (comparator.compare(value, s.value) == 0) {
				return s;
			}
		}
		return defaultSwitch;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RowWriterProcessor<?> switchRowProcessor(Object row) {
		selectedSwitch = getSwitch(row);
		return selectedSwitch != null ? selectedSwitch.processor : null;
	}

	/**
	 * Associates a {@link RowWriterProcessor} implementation with an expected value to be matched in the column provided in the constructor of this class.
	 *
	 * @param value        the value to match against the column of incoming output rows and trigger the usage of the given row writer processor implementation.
	 * @param rowProcessor the row writer processor implementation to use when the given value matches with the contents of the column provided in the constructor of this class.
	 * @param headersToUse the (optional) sequence of headers to assign to the given row writer processor
	 */
	public void addSwitchForValue(Object value, RowWriterProcessor<Object[]> rowProcessor, String... headersToUse) {
		addSwitch(new Switch(rowProcessor, headersToUse, null, value));
	}

	/**
	 * Associates a {@link RowWriterProcessor} implementation with an expected value to be matched in the column provided in the constructor of this class.
	 *
	 * @param value        the value to match against the column of incoming output rows and trigger the usage of the given row writer processor implementation.
	 * @param rowProcessor the row writer processor implementation to use when the given value matches with the contents of the column provided in the constructor of this class.
	 */
	public void addSwitchForValue(Object value, RowWriterProcessor<Object[]> rowProcessor) {
		addSwitch(new Switch(rowProcessor, null, null, value));
	}

	/**
	 * Associates a {@link RowWriterProcessor} implementation with an expected value to be matched in the column provided in the constructor of this class.
	 *
	 * @param value        the value to match against the column of incoming output rows and trigger the usage of the given row writer processor implementation.
	 * @param rowProcessor the row writer processor implementation to use when the given value matches with the contents of the column provided in the constructor of this class.
	 * @param indexesToUse the (optional) sequence of column indexes to assign to the given row writer processor
	 */
	public void addSwitchForValue(Object value, RowWriterProcessor<Object[]> rowProcessor, int... indexesToUse) {
		addSwitch(new Switch(rowProcessor, null, indexesToUse, value));
	}

	/**
	 * Associates a {@link RowWriterProcessor} implementation with an expected value to be matched in the column provided in the constructor of this class.
	 *
	 * @param beanType     the type of annotated java beans whose instances will be used to trigger the usage of a different format.
	 * @param headersToUse the (optional) sequence of headers to assign to the given row writer processor
	 * @param <T>          the type of annotated java beans whose instances will be used to trigger the usage of a different format.
	 */
	public <T> void addSwitchForType(Class<T> beanType, String... headersToUse) {
		addSwitch(new Switch(headersToUse, null, beanType));
	}

	/**
	 * Associates a {@link RowWriterProcessor} implementation with an expected value to be matched in the column provided in the constructor of this class.
	 *
	 * @param beanType     the type of annotated java beans whose instances will be used to trigger the usage of a different format.
	 * @param indexesToUse the (optional) sequence of column indexes to assign to the given row writer processor
	 * @param <T>          the type of annotated java beans whose instances will be used to trigger the usage of a different format.
	 */
	public <T> void addSwitchForType(Class<T> beanType, int... indexesToUse) {
		addSwitch(new Switch(null, indexesToUse, beanType));
	}


	/**
	 * Associates a {@link RowWriterProcessor} implementation with an expected value to be matched in the column provided in the constructor of this class.
	 *
	 * @param beanType the type of annotated java beans whose instances will be used to trigger the usage of a different format.
	 * @param <T>      the type of annotated java beans whose instances will be used to trigger the usage of a different format.
	 */
	public <T> void addSwitchForType(Class<T> beanType) {
		addSwitch(new Switch(null, null, beanType));
	}

	private void addSwitch(Switch newSwitch) {
		switches = Arrays.copyOf(switches, switches.length + 1);
		switches[switches.length - 1] = newSwitch;

		types = Arrays.copyOf(types, types.length + 1);
		if (newSwitch.value != null && newSwitch.value.getClass() == Class.class) {
			types[types.length - 1] = (Class) newSwitch.value;
		}
	}

	private <V> V getValue(Map<?, V> map, int index) {
		int i = 0;
		for (Map.Entry<?, V> e : map.entrySet()) {
			if (i == index) {
				return e.getValue();
			}
		}
		return null;
	}

	private NormalizedString[] getHeadersFromSwitch(Object value) {
		for (int i = 0; i < switches.length; i++) {
			Switch s = getSwitch(value);
			if (s != null) {
				return s.headers;
			}
		}
		return null;
	}

	@Override
	public NormalizedString[] getHeaders(Object input) {
		if (input instanceof Object[]) {
			Object[] row = (Object[]) input;
			if (columnIndex < row.length) {
				return getHeadersFromSwitch(row[columnIndex]);
			}
			return null;
		} else {
			return getHeadersFromSwitch(input);
		}
	}

	@Override
	public NormalizedString[] getHeaders(Map headerMapping, Map mapInput) {
		Object mapValue = null;
		if (mapInput != null && !mapInput.isEmpty()) {
			String headerToUse = headerName;

			if (headerMapping != null) {
				if (headerName != null) {
					Object value = headerMapping.get(headerName);
					headerToUse = value == null ? null : value.toString();
				} else if (columnIndex != -1) {
					Object value = getValue(headerMapping, columnIndex);
					headerToUse = value == null ? null : value.toString();
				}
			}
			if (headerToUse != null) {
				mapValue = mapInput.get(headerToUse);
			} else {
				mapValue = getValue(mapInput, columnIndex);
			}
		}
		return getHeadersFromSwitch(mapValue);
	}

	/**
	 * Returns the column index whose values will be used to switching from a row processor to another.
	 *
	 * @return the column index targeted by this switch.
	 */
	public int getColumnIndex() {
		return columnIndex;
	}

	private List<Object> getSwitchValues() {
		List<Object> values = new ArrayList<Object>(switches.length);

		for (Switch s : switches) {
			values.add(s.value);
		}
		return values;
	}

	@Override
	protected String describeSwitch() {
		return "Expecting one of values: " + getSwitchValues() + " at column index " + getColumnIndex();
	}

	private static class Switch {
		final RowWriterProcessor<Object[]> processor;
		final NormalizedString[] headers;
		final int[] indexes;
		final Object value;

		Switch(RowWriterProcessor<Object[]> processor, String[] headers, int[] indexes, Object value) {
			this.processor = processor;
			this.headers = headers == null || headers.length == 0 ? null : NormalizedString.toIdentifierGroupArray(headers);
			this.indexes = indexes == null || indexes.length == 0 ? null : indexes;
			this.value = value;
		}

		Switch(String[] headers, int[] indexes, Class<?> type) {
			processor = new BeanWriterProcessor(type);

			if (headers == null && indexes == null) {
				headers = AnnotationHelper.deriveHeaderNamesFromFields(type, MethodFilter.ONLY_GETTERS);
				indexes = ArgumentUtils.toIntArray(Arrays.asList(AnnotationHelper.getSelectedIndexes(type, MethodFilter.ONLY_GETTERS)));
			}

			this.headers = headers == null || headers.length == 0 ? null : NormalizedString.toIdentifierGroupArray(headers);
			this.indexes = indexes == null || indexes.length == 0 ? null : indexes;
			this.value = type;
		}
	}
}

