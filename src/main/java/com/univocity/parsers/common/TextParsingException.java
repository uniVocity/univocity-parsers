/**
 * ****************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 * ****************************************************************************
 */
package com.univocity.parsers.common;

/**
 * Exception type used provide information about any issue that might happen while parsing from a given input.
 *
 *  <p> It generally provides location information about where in the input a parsing error occurred.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class TextParsingException extends AbstractException {

	private static final long serialVersionUID = 1410975527141918214L;

	private long lineIndex;
	private long charIndex;
	private String content;
	private String[] headers;
	protected int[] extractedIndexes;

	public TextParsingException(ParsingContext context, String message, Throwable cause) {
		super(message, cause);
		setContext(context);
	}

	protected void setContext(ParsingContext context) {
		this.lineIndex = context == null ? -1L : context.currentLine();
		this.charIndex = context == null ? '\0' : context.currentChar();
		this.content = context == null ? null : context.currentParsedContent();
		if (this.headers == null) {
			this.headers = context == null ? null : context.headers();
		}
		this.extractedIndexes = context == null ? null : context.extractedFieldIndexes();
	}

	public TextParsingException(ParsingContext context, String message) {
		this(context, message, null);
	}

	public TextParsingException(ParsingContext context, Throwable cause) {
		this(context, cause != null ? cause.getMessage() : null, cause);
	}

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
		details = printIfNotEmpty(details, "charIndex", charIndex);
		details = printIfNotEmpty(details, "headers", headers);
		details = printIfNotEmpty(details, "content parsed", content);
		return details;
	}

	/**
	 * Returns the line number where the exception occurred.
	 * @return the line number where the exception occurred.
	 */
	public long getLineIndex() {
		return lineIndex;
	}

	/**
	 * Returns the location of the last character read from before the error occurred.
	 * @return the location of the last character read from before the error occurred.
	 */
	public long getCharIndex() {
		return charIndex;
	}

	/**
	 * Returns the last chunk of content parsed before the error took place
	 * @return the last chunk of content parsed before the error took place
	 */
	public final String getParsedContent() {
		return content;
	}

	/**
	 * Returns the headers processed from the input, if any.
	 * @return the headers processed from the input, if any.
	 */
	public final String[] getHeaders() {
		return headers;
	}
}
