/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
 * Exception type used provide information about any issue that might happen while parsing from a given input.
 *
 *  <p> It generally provides location information about where in the input a parsing error occurred.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class TextParsingException extends RuntimeException {

	private static final long serialVersionUID = 1410975527141918214L;

	private final int lineIndex;
	private final int charIndex;
	private final String content;

	public TextParsingException(ParsingContext context, String message, Throwable cause) {
		super(message, cause);
		this.lineIndex = context.currentLine();
		this.charIndex = context.currentChar();
		this.content = context.currentParsedContent();
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
	public String getMessage() {
		String msg = super.getMessage();
		msg = msg == null ? "" : msg;

		return "Error processing input: " + msg + ", line=" + lineIndex + ", char=" + charIndex + ". Content parsed " + content;
	}

	/**
	 * Returns the line number where the exception occurred.
	 * @return the line number where the exception occurred.
	 */
	public int getLineIndex() {
		return lineIndex;
	}

	/**
	 * Returns the location of the last character read from before the error occurred.
	 * @return the location of the last character read from before the error occurred.
	 */
	public long getCharIndex() {
		return charIndex;
	}

}
