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

/**
 * A {@code DataProcessingException} is an error thrown during the processing of a record successfully parsed.
 * This type of error usually indicates that the input text has been parsed correctly, but the subsequent
 * transformations applied over the input (generally via a {@link RowProcessor}} failed.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class DataProcessingException extends TextParsingException {

	private static final long serialVersionUID = 1410975527141918215L;

	private String columnName;
	private int columnIndex;
	private Object[] row;
	private Object value;
	private boolean fatal = true;

	public DataProcessingException(String message) {
		this(message, -1, null, null);
	}

	public DataProcessingException(String message, Throwable cause) {
		this(message, -1, null, cause);
	}

	public DataProcessingException(String message, Object[] row) {
		this(message, -1, row, null);
	}

	public DataProcessingException(String message, Object[] row, Throwable cause) {
		this(message, -1, row, cause);
	}

	public DataProcessingException(String message, int columnIndex) {
		this(message, columnIndex, null, null);
	}

	public DataProcessingException(String message, int columnIndex, Object[] row, Throwable cause) {
		super(null, message, cause);
		setColumnIndex(columnIndex);
		this.row = row;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorDescription() {
		return "Error processing parsed input";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDetails() {
		String details = super.getDetails();
		details = printIfNotEmpty(details, "row", getRow());
		details = printIfNotEmpty(details, "value", getValue());
		details = printIfNotEmpty(details, "columnName", getColumnName());
		details = printIfNotEmpty(details, "columnIndex", getColumnIndex());
		return details;
	}

	/**
	 * Returns the name of the column from where the error occurred, if available.
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
	 * @return the index of the column from where the error occurred.
	 */
	public final int getColumnIndex() {
		return columnIndex;
	}

	/**
	 * Returns the record being processed when the error occurred, if available.
	 * @return the record being processed when error occurred, if available.
	 */
	public final Object[] getRow() {
		return row;
	}

	/**
	 * Defines the value being processed when the error occurred.
	 * @param value the value being processed when error occurred.
	 */
	public final void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Returns the value being processed when the error occurred, if available.
	 * @return the value being processed when the error occurred, if available.
	 */
	public final Object getValue() {
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
	 * @param columnIndex the column index being processed when error occurred.
	 */
	public final void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	private int getExtractedColumnIndex() {
		if (this.extractedIndexes != null && columnIndex < extractedIndexes.length) {
			return extractedIndexes[columnIndex];
		}
		return columnIndex;
	}

	/**
	 * Defines the name of the column being processed when the error occurred.
	 * @param columnName the name of the column being processed when error occurred.
	 */
	public final void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * Updates the exception with the record being processed when the error occurred.
	 * @param row the record data processed when the error occurred.
	 */
	public final void setRow(Object[] row) {
		this.row = row;
	}

	/**
	 * Returns a flag indicating whether this error is fatal and the process must stop as it is impossible to proceed.
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
}
