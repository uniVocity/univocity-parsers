/**
 * ****************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 * ****************************************************************************
 */
package com.univocity.parsers.common;

/**
 * Exception type used provide information about any issue that might happen while parsing from a given input.
 * <p> It generally provides location information about where in the input a parsing error occurred.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class TextParsingException extends AbstractException {

	private static final long serialVersionUID = 1410975527141918214L;

	private long lineIndex;
	private long charIndex;
	private long recordNumber;
	private int columnIndex;
	private String content;
	private String[] headers;
	protected int[] extractedIndexes;

	/**
	 * Creates a new exception with information about an error that occurred when parsing some input.
	 *
	 * @param context the context of the parser when an error occurred
	 * @param message message with details about the error
	 * @param cause   the cause of the error
	 */
	public TextParsingException(Context context, String message, Throwable cause) {
		super(message, cause);
		setContext(context);
	}

	protected void setContext(Context context) {
		if (context instanceof ParsingContext) {
			setParsingContext((ParsingContext) context);
		} else {
			setParsingContext(null);
		}
		this.columnIndex = context == null ? -1 : context.currentColumn();
		this.recordNumber = context == null ? -1L : context.currentRecord();
		if (this.headers == null) {
			this.headers = context == null ? null : context.headers();
		}
		this.extractedIndexes = context == null ? null : context.extractedFieldIndexes();
	}

	private void setParsingContext(ParsingContext parsingContext) {
		this.lineIndex = parsingContext == null ? -1L : parsingContext.currentLine();
		this.charIndex = parsingContext == null ? '\0' : parsingContext.currentChar();
		this.content = parsingContext == null ? null : parsingContext.fieldContentOnError();
	}

	/**
	 * Creates a new exception with information about an error that occurred when parsing some input.
	 *
	 * @param context the context of the parser when an error occurred
	 * @param message message with details about the error
	 */
	public TextParsingException(ParsingContext context, String message) {
		this(context, message, null);
	}

	/**
	 * Creates a new exception with information about an error that occurred when parsing some input.
	 *
	 * @param context the context of the parser when an error occurred
	 * @param cause   the cause of the error
	 */
	public TextParsingException(ParsingContext context, Throwable cause) {
		this(context, cause != null ? cause.getMessage() : null, cause);
	}

	/**
	 * Creates a new exception with information about an error that occurred when parsing some input.
	 *
	 * @param context the context of the parser when an error occurred
	 */
	public TextParsingException(ParsingContext context) {
		this(context, null, null);
	}

	@Override
	protected String getErrorDescription() {
		return "Error parsing input";
	}

	@Override
	protected String getDetails() {
		String details = "";
		details = printIfNotEmpty(details, "line", lineIndex);
		details = printIfNotEmpty(details, "column", columnIndex);
		details = printIfNotEmpty(details, "record", recordNumber);
		details = charIndex == 0 ? details : printIfNotEmpty(details, "charIndex", charIndex);
		details = printIfNotEmpty(details, "headers", headers);
		details = printIfNotEmpty(details, "content parsed", restrictContent(content));
		return details;
	}


	/**
	 * Returns the record number when the exception occurred.
	 *
	 * @return the record number when the exception occurred.
	 */
	public long getRecordNumber() {
		return lineIndex;
	}


	/**
	 * Returns the column index where the exception occurred.
	 *
	 * @return the column index where the exception occurred.
	 */
	public int getColumnIndex() {
		return columnIndex;
	}


	/**
	 * Returns the line number where the exception occurred.
	 *
	 * @return the line number where the exception occurred.
	 */
	public long getLineIndex() {
		return lineIndex;
	}

	/**
	 * Returns the location of the last character read from before the error occurred.
	 *
	 * @return the location of the last character read from before the error occurred.
	 */
	public long getCharIndex() {
		return charIndex;
	}

	/**
	 * Returns the last chunk of content parsed before the error took place
	 *
	 * @return the last chunk of content parsed before the error took place
	 */
	public final String getParsedContent() {
		if (errorContentLength == 0) {
			return null;
		}
		return content;
	}

	/**
	 * Returns the headers processed from the input, if any.
	 *
	 * @return the headers processed from the input, if any.
	 */
	public final String[] getHeaders() {
		return headers;
	}
}
