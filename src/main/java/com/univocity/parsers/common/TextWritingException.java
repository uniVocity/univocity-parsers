/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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

/**
 * Exception type used provide information about any issue that might happen while writing to a given output.
 *
 * <p> It generally provides location and data information in case of a writing failure.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class TextWritingException extends AbstractException {

	private static final long serialVersionUID = 7198462597717255519L;

	private final long recordCount;
	private final Object[] recordData;
	private final String recordCharacters;

	/**
	 * Creates a new exception with information about an error that occurred when writing data to some output.
	 *
	 * @param message          message with details about the error
	 * @param recordCount      the number of records written until the error occurred
	 * @param row              the input row that was being written when the error occurred
	 * @param recordCharacters the characters already written to the output record.
	 * @param cause            the cause of the error
	 */
	private TextWritingException(String message, long recordCount, Object[] row, String recordCharacters, Throwable cause) {
		super(message, cause);
		this.recordCount = recordCount;
		this.recordData = row;
		this.recordCharacters = recordCharacters;
	}

	/**
	 * Creates a new exception with information about an error that occurred when writing data to some output.
	 *
	 * @param message          message with details about the error
	 * @param recordCount      the number of records written until the error occurred
	 * @param recordCharacters the characters already written to the output record.
	 * @param cause            the cause of the error
	 */
	public TextWritingException(String message, long recordCount, String recordCharacters, Throwable cause) {
		this(message, recordCount, null, recordCharacters, cause);
	}

	/**
	 * Creates a new exception with information about an error that occurred when writing data to some output.
	 *
	 * @param message     message with details about the error
	 * @param recordCount the number of records written until the error occurred
	 * @param row         the input row that was being written when the error occurred
	 * @param cause       the cause of the error
	 */
	public TextWritingException(String message, long recordCount, Object[] row, Throwable cause) {
		this(message, recordCount, row, null, cause);
	}

	/**
	 * Creates a new exception with information about an error that occurred when writing data to some output.
	 *
	 * @param message message with details about the error
	 */
	public TextWritingException(String message) {
		this(message, 0, null, null, null);
	}

	/**
	 * Creates a new exception with information about an error that occurred when writing data to some output.
	 *
	 * @param cause the cause of the error
	 */
	public TextWritingException(Throwable cause) {
		this(cause != null ? cause.getMessage() : null, 0, null, null, cause);
	}

	/**
	 * Creates a new exception with information about an error that occurred when writing data to some output.
	 *
	 * @param message message with details about the error
	 * @param line    index of the line being written to the output when the error occurred
	 * @param row     the input row that was being written when the error occurred
	 */
	public TextWritingException(String message, long line, Object[] row) {
		this(message, line, row, null);
	}

	/**
	 * Creates a new exception with information about an error that occurred when writing data to some output.
	 *
	 * @param message          message with details about the error
	 * @param line             index of the line being written to the output when the error occurred
	 * @param recordCharacters the characters already written to the output record.
	 */
	public TextWritingException(String message, long line, String recordCharacters) {
		this(message, line, null, recordCharacters, null);
	}

	/**
	 * Returns the number of records written before the exception occurred.
	 *
	 * @return the number of records written before the exception occurred.
	 */
	public long getRecordCount() {
		return recordCount;
	}

	/**
	 * Returns the data that failed to be written
	 *
	 * @return the data that failed to be written
	 */
	public Object[] getRecordData() {
		return restrictContent(recordData);
	}

	/**
	 * Returns the character data that failed to be written
	 *
	 * @return the character data that failed to be written
	 */
	public String getRecordCharacters() {
		if(errorContentLength == 0){
			return null;
		}
		return recordCharacters;
	}

	@Override
	protected String getDetails() {
		String details = "";
		details = printIfNotEmpty(details, "recordCount", recordCount);
		details = printIfNotEmpty(details, "recordData", restrictContent(recordData));
		details = printIfNotEmpty(details, "recordCharacters", restrictContent(recordCharacters));
		return details;
	}

	@Override
	protected String getErrorDescription() {
		return "Error writing data";
	}
}
