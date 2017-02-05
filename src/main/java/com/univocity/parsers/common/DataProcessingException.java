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

import com.univocity.parsers.common.processor.*;

import java.util.*;

/**
 * A {@code DataProcessingException} is an error thrown during the processing of a record successfully parsed.
 * This type of error usually indicates that the input text has been parsed correctly, but the subsequent
 * transformations applied over the input (generally via a {@link RowProcessor}} failed.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class DataProcessingException extends TextParsingException {

	private static final long serialVersionUID = 1410975527141918215L;

	private String columnName;
	private int columnIndex;
	private Object[] row;
	private Object value;
	private Map<String, Object> values = new HashMap<String, Object>();
	private boolean fatal = true;
	private boolean handled = false;

	/**
	 * Creates a new exception with an error message only.
	 *
	 * @param message the error message
	 */
	public DataProcessingException(String message) {
		this(message, -1, null, null);
	}

	/**
	 * Creates a new exception with an error message and error cause
	 *
	 * @param message the error message
	 * @param cause   the cause of the error
	 */
	public DataProcessingException(String message, Throwable cause) {
		this(message, -1, null, cause);
	}

	/**
	 * Creates a new exception with an error message and the row that could not be processed.
	 *
	 * @param message the error message
	 * @param row     the row that could not be processed.
	 */
	public DataProcessingException(String message, Object[] row) {
		this(message, -1, row, null);
	}

	/**
	 * Creates a new exception with an error message, the row that could not be processed, and the error cause.
	 *
	 * @param message the error message
	 * @param row     the row that could not be processed.
	 * @param cause   the cause of the error
	 */
	public DataProcessingException(String message, Object[] row, Throwable cause) {
		this(message, -1, row, cause);
	}

	/**
	 * Creates a new exception with an error message and the column that could not be processed.
	 *
	 * @param message     the error message
	 * @param columnIndex index of the column that could not be processed.
	 */
	public DataProcessingException(String message, int columnIndex) {
		this(message, columnIndex, null, null);
	}

	/**
	 * Creates a new exception with an error message, the column that could not be processed
	 * the row that could not be processed, and the error cause.
	 *
	 * @param message     the error message
	 * @param columnIndex index of the column that could not be processed.
	 * @param row         the row that could not be processed.
	 * @param cause       the cause of the error
	 */
	public DataProcessingException(String message, int columnIndex, Object[] row, Throwable cause) {
		super(null, message, cause);
		setColumnIndex(columnIndex);
		this.row = row;
	}

	@Override
	protected String getErrorDescription() {
		return "Error processing parsed input";
	}

	@Override
	protected String getDetails() {
		String details = super.getDetails();
		Object[] row = getRow();
		if (row != null) {
			row = row.clone();
			for (int i = 0; i < row.length; i++) {
				row[i] = restrictContent(row[i]);
			}
		}
		details = printIfNotEmpty(details, "row", row);
		details = printIfNotEmpty(details, "value", restrictContent(getValue()));
		details = printIfNotEmpty(details, "columnName", getColumnName());
		details = printIfNotEmpty(details, "columnIndex", getColumnIndex());
		return details;
	}

	/**
	 * Returns the name of the column from where the error occurred, if available.
	 *
	 * @return the name of the column from where the error occurred.
	 */
	public String getColumnName() {
		if (columnName != null) {
			return columnName;
		}
		String[] headers = getHeaders();
		if (headers != null && getExtractedColumnIndex() != -1 && getExtractedColumnIndex() < headers.length) {
			return headers[getExtractedColumnIndex()];
		}
		return null;

	}

	/**
	 * Returns the index of the column from where the error occurred, if available.
	 *
	 * @return the index of the column from where the error occurred.
	 */
	public final int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * Returns the record being processed when the error occurred, if available.
	 *
	 * @return the record being processed when error occurred, if available.
	 */
	public final Object[] getRow() {
		return restrictContent(row);
	}

	/**
	 * Defines the value being processed when the error occurred.
	 *
	 * @param value the value being processed when error occurred.
	 */
	public final void setValue(Object value) {
		if (errorContentLength == 0) {
			value = null;
		}
		this.value = value;
	}

	/**
	 * Associates a label in the exception message (idenfied in curly braces) with a value being processed when the error occurred.
	 * Used for formatting the exception message
	 *
	 * @param label a label in the exception message - any string enclosed by curly braces.
	 * @param value the value being processed when error occurred, that should be replaced by the label in the exception message.
	 */
	public final void setValue(String label, Object value) {
		if (errorContentLength == 0) {
			value = null;
		}
		this.values.put(label, value);
	}

	/**
	 * Returns the value being processed when the error occurred, if available.
	 *
	 * @return the value being processed when the error occurred, if available.
	 */
	public final Object getValue() {
		if (errorContentLength == 0) {
			return null;
		}
		if (value != null) {
			return value;
		}
		if (row != null && columnIndex != -1 && columnIndex < row.length) {
			return row[columnIndex];
		}
		return null;
	}

	/**
	 * Defines the column index being processed when the error occurred.
	 *
	 * @param columnIndex the column index being processed when error occurred.
	 */
	public final void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	private int getExtractedColumnIndex() {
		if (this.extractedIndexes != null && columnIndex < extractedIndexes.length && columnIndex > -1) {
			return extractedIndexes[columnIndex];
		}
		return columnIndex;
	}

	/**
	 * Defines the name of the column being processed when the error occurred.
	 *
	 * @param columnName the name of the column being processed when error occurred.
	 */
	public final void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * Updates the exception with the record being processed when the error occurred.
	 *
	 * @param row the record data processed when the error occurred.
	 */
	public final void setRow(Object[] row) {
		if (errorContentLength == 0) {
			row = null;
		}
		this.row = row;
	}

	/**
	 * Returns a flag indicating whether this error is fatal and the process must stop as it is impossible to proceed.
	 *
	 * @return a flag indicating whether this error is fatal and the process must stop as it is impossible to proceed.
	 */
	final boolean isFatal() {
		return fatal;
	}

	/**
	 * Marks the error as non fatal and the parsing process might proceed.
	 */
	public final void markAsNonFatal() {
		this.fatal = false;
	}

	/**
	 * Marks the error as handled so it doesn't trigger a {@link ProcessorErrorHandler} again.
	 * @param handler the {@link ProcessorErrorHandler} used to handle this exception.
	 */
	public final void markAsHandled(ProcessorErrorHandler handler) {
		this.handled = handler != null && !(handler instanceof NoopProcessorErrorHandler) && !(handler instanceof NoopRowProcessorErrorHandler);
	}

	/**
	 * Returns a flag indicating this exception has been handled by a user-provided {@link ProcessorErrorHandler}
	 *
	 * @return {@code true} if this exception has been handled to a user-provided  {@link ProcessorErrorHandler},
	 * otherwise {@code false}
	 */
	public boolean isHandled() {
		return handled;
	}

	@Override
	protected final String updateMessage(String msg) {
		if (errorContentLength == 0 || msg == null) {
			return msg; //doesn't replace labels enclosed within { and }.
		}

		StringBuilder out = new StringBuilder(msg.length());

		int previous = 0;
		int start = 0;
		while (true) {
			start = msg.indexOf('{', start);
			if (start == -1) {
				break;
			}

			int end = msg.indexOf('}', start);
			if (end == -1) {
				break;
			}

			String label = msg.substring(start + 1, end);
			Object value = null;
			if ("value".equals(label)) {
				value = this.value;
			} else if (values.containsKey(label)) {
				value = values.get(label);
			}
			if (value != null) {
				String content = restrictContent(value);
				out.append(msg, previous, start);
				out.append(content);
				previous = end;
			}
			start = end;
		}
		out.append(msg, previous == 0 ? 0 : previous + 1, msg.length());

		return out.toString();
	}
}
