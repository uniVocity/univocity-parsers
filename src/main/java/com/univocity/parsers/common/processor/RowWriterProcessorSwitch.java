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

import com.univocity.parsers.common.CommonSettings;
import com.univocity.parsers.common.CommonWriterSettings;
import com.univocity.parsers.common.DataProcessingException;

import java.util.Arrays;
import java.util.Map;

/**
 * A special {@link RowWriterProcessor} implementation that combines and allows switching among different
 * RowWriterProcessors. Concrete implementations of this class
 * are expected to implement the {@code switchRowProcessor(T)} method and analyze the input row
 * to determine whether or not the current {@link RowWriterProcessor} implementation must be changed to handle a special
 * circumstance (determined by the concrete implementation) such as a different row format.
 *
 * When the row writer processor is switched, the {@link #rowProcessorSwitched(RowWriterProcessor, RowWriterProcessor)}
 * will be called, and must be overridden, to notify the change to the user.
 */
public abstract class RowWriterProcessorSwitch implements RowWriterProcessor<Object> {

	private RowWriterProcessor selectedRowWriterProcessor = null;
	private int minimumRowLength = Integer.MIN_VALUE;

	/**
	 * Analyzes an output row to determine whether or not the row writer processor implementation must be changed
	 *
	 * @param row a record with data to be written to the output
	 *
	 * @return the row writer processor implementation to use. If it is not the same as the one used by the previous row,
	 * the returned row writer processor will be used, and the {@link #rowProcessorSwitched(RowWriterProcessor, RowWriterProcessor)} method
	 * will be called.
	 */
	protected abstract RowWriterProcessor<?> switchRowProcessor(Object row);

	/**
	 * Returns the headers in use by the current row writer processor implementation, which can vary among row writer processors.
	 * If {@code null}, the headers defined in {@link CommonWriterSettings#getHeaders()} will be returned.
	 *
	 * @return the current sequence of headers to use.
	 */
	protected String[] getHeaders() {
		return null;
	}

	/**
	 * Returns the indexes in use by the current row writer processor implementation, which can vary among row writer processors.
	 * If {@code null}, the indexes of fields that have been selected using {@link CommonSettings#selectFields(String...)}
	 * or {@link CommonSettings#selectIndexes(Integer...)} will be returned.
	 *
	 * @return the current sequence of indexes to use.
	 */
	protected int[] getIndexes() {
		return null;
	}

	/**
	 * Notifies a change of row writer processor implementation. Users are expected to override this method to receive the notification.
	 *
	 * @param from the row writer processor previously in use
	 * @param to   the new row writer processor to use to continue processing the output rows.
	 */
	public void rowProcessorSwitched(RowWriterProcessor<?> from, RowWriterProcessor<?> to) {
	}

	/**
	 * Returns the sequence of headers to use for processing an input record represented by a map
	 *
	 * A map of headers can be optionally provided to assign a name to the keys of the input map. This is useful when
	 * the input map has keys will generate unwanted header names.
	 *
	 * @param headerMapping an optional map associating keys of the rowData map with expected header names
	 * @param mapInput      the record data
	 *
	 * @return the sequence of headers to use when processing the given input map.
	 */
	public abstract String[] getHeaders(Map headerMapping, Map mapInput);

	/**
	 * Returns the sequence of headers to use for processing an input record.
	 *
	 * @param input the record data
	 *
	 * @return the sequence of headers to use when processing the given record.
	 */
	public abstract String[] getHeaders(Object input);

	protected abstract String describeSwitch();

	/**
	 * Returns the minimum row length based on the number of headers and index sizes
	 *
	 * @return the minimum length a row must have in order to be sent to the output
	 */
	public final int getMinimumRowLength() {
		if (minimumRowLength == Integer.MIN_VALUE) {
			minimumRowLength = 0;
			if (getHeaders() != null) {
				minimumRowLength = getHeaders().length;
			}
			if (getIndexes() != null) {
				for (int index : getIndexes()) {
					if (index + 1 > minimumRowLength) {
						minimumRowLength = index + 1;
					}
				}
			}
		}
		return minimumRowLength;
	}

	@Override
	public Object[] write(Object input, String[] headers, int[] indicesToWrite) {
		RowWriterProcessor<?> processor = switchRowProcessor(input);
		if (processor == null) {
			DataProcessingException ex = new DataProcessingException("Cannot find switch for input. Headers: {headers}, indices to write: " + Arrays.toString(indicesToWrite) + ". " + describeSwitch());
			ex.setValue("headers", Arrays.toString(headers));
			ex.setValue(input);
			throw ex;
		}
		if (processor != selectedRowWriterProcessor) {
			rowProcessorSwitched(selectedRowWriterProcessor, processor);
			selectedRowWriterProcessor = processor;
		}

		String[] headersToUse = getHeaders();
		int[] indexesToUse = getIndexes();

		headersToUse = headersToUse == null ? headers : headersToUse;
		indexesToUse = indexesToUse == null ? indicesToWrite : indexesToUse;

		return selectedRowWriterProcessor.write(input, headersToUse, indexesToUse);
	}
}
