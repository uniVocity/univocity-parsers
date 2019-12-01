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
package com.univocity.parsers.csv;

import com.univocity.parsers.common.*;

import java.util.*;

/**
 * The CSV format configuration. In addition to the default configuration in {@link Format}, the CSV format defines:
 *
 * <ul>
 * <li><b>delimiter <i>(defaults to ',')</i>: </b> the field delimiter character. Used to separate individual fields in a CSV record (where the record is usually a line of text with multiple fields).
 * <p>e.g. the value  a , b  is parsed as [ a ][ b ]</li>
 * <li><b>quote <i>(defaults to '"')</i>:</b> character used for escaping values where the field delimiter is part of the value.
 * <p>e.g. the value " a , b " is parsed as [ a , b ] (instead of [ a ][ b ]</li>
 * <li><b>quoteEscape  <i>(defaults to '"')</i>:</b> character used for escaping the quote character inside an already quoted value
 * <p>e.g. the value " "" a , b "" " is parsed as [ " a , b " ]  (instead of [ " a ][ b " ] or [ "" a , b "" ])</li>
 * <li><b>charToEscapeQuoteEscaping  <i>(defaults to '\0' - undefined)</i>:</b> character used for escaping the escape for the quote character
 * <p>e.g. if the quoteEscape and charToEscapeQuoteEscaping are set to '\', the value " \\\" a , b \\\" " is parsed as [ \" a , b \" ]</li>
 * </ul>
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.common.Format
 */
public class CsvFormat extends Format {
	private char quote = '"';
	private char quoteEscape = '"';
	private String delimiter = ",";
	private Character charToEscapeQuoteEscaping = null;

	/**
	 * Returns the character used for escaping values where the field delimiter is part of the value. Defaults to '"'
	 *
	 * @return the quote character
	 */
	public char getQuote() {
		return quote;
	}

	/**
	 * Defines the character used for escaping values where the field delimiter is part of the value. Defaults to '"'
	 *
	 * @param quote the quote character
	 *
	 * @return this {@code CsvFormat} instance
	 */
	public CsvFormat setQuote(char quote) {
		this.quote = quote;
		return this;
	}

	/**
	 * Identifies whether or not a given character is used for escaping values where the field delimiter is part of the value
	 *
	 * @param ch the character to be verified
	 *
	 * @return true if the given character is the character used for escaping values, false otherwise
	 */
	public boolean isQuote(char ch) {
		return this.quote == ch;
	}

	/**
	 * Returns the character used for escaping quotes inside an already quoted value. Defaults to '"'
	 *
	 * @return the quote escape character
	 */
	public char getQuoteEscape() {
		return quoteEscape;
	}

	/**
	 * Defines the character used for escaping quotes inside an already quoted value. Defaults to '"'
	 *
	 * @param quoteEscape the quote escape character
	 *
	 * @return this {@code CsvFormat} instance
	 */
	public CsvFormat setQuoteEscape(char quoteEscape) {
		this.quoteEscape = quoteEscape;
		return this;
	}

	/**
	 * Identifies whether or not a given character is used for escaping quotes inside an already quoted value.
	 *
	 * @param ch the character to be verified
	 *
	 * @return true if the given character is the quote escape character, false otherwise
	 */
	public boolean isQuoteEscape(char ch) {
		return this.quoteEscape == ch;
	}

	/**
	 * Returns the field delimiter character. Defaults to ','
	 *
	 * @return the field delimiter character
	 */
	public char getDelimiter() {
		if (delimiter.length() > 1) {
			throw new UnsupportedOperationException("Delimiter '" + delimiter + "' has more than one character. Use method getDelimiterString()");
		}
		return delimiter.charAt(0);
	}

	/**
	 * Returns the field delimiter sequence.
	 *
	 * @return the field delimiter as a {@code String}.
	 */
	public String getDelimiterString() {
		return delimiter;
	}

	/**
	 * Defines the field delimiter character. Defaults to ','
	 *
	 * @param delimiter the field delimiter character
	 *
	 * @return this {@code CsvFormat} instance
	 */
	public CsvFormat setDelimiter(char delimiter) {
		this.delimiter = String.valueOf(delimiter);
		return this;
	}

	/**
	 * Defines the field delimiter as a sequence of characters. Defaults to ','
	 *
	 * @param delimiter the field delimiter sequence.
	 *
	 * @return this {@code CsvFormat} instance
	 */
	public CsvFormat setDelimiter(String delimiter) {
		if (delimiter == null) {
			throw new IllegalArgumentException("Delimiter cannot be null");
		}
		if (delimiter.isEmpty()) {
			throw new IllegalArgumentException("Delimiter cannot be empty");
		}
		this.delimiter = delimiter;
		return this;
	}

	/**
	 * Identifies whether or not a given character represents a field delimiter
	 *
	 * @param ch the character to be verified
	 *
	 * @return true if the given character is the field delimiter character, false otherwise
	 */
	public boolean isDelimiter(char ch) {
		if (delimiter.length() > 1) {
			throw new UnsupportedOperationException("Delimiter '" + delimiter + "' has more than one character. Use method isDelimiter(String)");
		}
		return this.delimiter.charAt(0) == ch;
	}

	/**
	 * Identifies whether or not a given character represents a field delimiter
	 *
	 * @param sequence the character sequence to be verified
	 *
	 * @return true if the given sequence is the field delimiter character sequence, false otherwise
	 */
	public boolean isDelimiter(String sequence) {
		return this.delimiter.equals(sequence);
	}

	/**
	 * Returns the character used to escape the character used for escaping quotes defined by {@link #getQuoteEscape()}.
	 * For example, if the quote escape is set to '\', and the quoted value ends with: \", as in the following example:
	 *
	 * <p>
	 * [ " a\\", b ]
	 * </p>
	 *
	 * Then:
	 * <ul>
	 * <li>If the character to escape the '\' is undefined, the record won't be parsed. The parser will read characters: [a],[\],["],[,],[ ],[b] and throw an error because it cannot find a closing quote</li>
	 * <li>If the character to escape the '\' is defined as '\', the record will be read with 2 values: [a\] and [b]</li>
	 * </ul>
	 * Defaults to '\0' (undefined)
	 *
	 * @return the character to escape the character used for escaping quotes defined
	 */
	public final char getCharToEscapeQuoteEscaping() {
		if (charToEscapeQuoteEscaping == null) { //not provided by the user
			if (quote == quoteEscape) {
				return '\0'; //not required
			} else {
				return quoteEscape;
			}
		}
		return charToEscapeQuoteEscaping;
	}

	/**
	 * Defines the character used to escape the character used for escaping quotes defined by {@link #getQuoteEscape()}.
	 * For example, if the quote escape is set to '\', and the quoted value ends with: \", as in the following example:
	 *
	 * <p>
	 * [ " a\\", b ]
	 * </p>
	 *
	 * Then:
	 * <ul>
	 * <li>If the character to escape the '\' is undefined, the record won't be parsed. The parser will read characters: [a],[\],["],[,],[ ],[b] and throw an error because it cannot find a closing quote</li>
	 * <li>If the character to escape the '\' is defined as '\', the record will be read with 2 values: [a\] and [b]</li>
	 * </ul>
	 * Defaults to '\0' (undefined)
	 *
	 * @param charToEscapeQuoteEscaping the character to escape the character used for escaping quotes defined
	 *
	 * @return this {@code CsvFormat} instance
	 */
	public final CsvFormat setCharToEscapeQuoteEscaping(char charToEscapeQuoteEscaping) {
		this.charToEscapeQuoteEscaping = charToEscapeQuoteEscaping;
		return this;
	}

	/**
	 * Identifies whether or not a given character is used to escape the character used for escaping quotes defined by {@link #getQuoteEscape()}.
	 *
	 * @param ch the character to be verified
	 *
	 * @return true if the given character is used to escape the quote escape character, false otherwise
	 */
	public final boolean isCharToEscapeQuoteEscaping(char ch) {
		char current = getCharToEscapeQuoteEscaping();
		return current != '\0' && current == ch;
	}

	/**
	 * Defines the line separator sequence that should be used for parsing and writing.
	 *
	 * @param lineSeparator a sequence of 1 to 2 characters that identifies the end of a line
	 *
	 * @return this {@code CsvFormat} instance
	 */
	public CsvFormat setLineSeparator(String lineSeparator) {
		super.setLineSeparator(lineSeparator);
		return this;
	}

	/**
	 * Defines the line separator sequence that should be used for parsing and writing.
	 *
	 * @param lineSeparator a sequence of 1 to 2 characters that identifies the end of a line
	 *
	 * @return this {@code CsvFormat} instance
	 */
	public CsvFormat setLineSeparator(char[] lineSeparator) {
		super.setLineSeparator(lineSeparator);
		return this;
	}

	/**
	 * Sets the normalized newline character, which is automatically replaced by {@link Format#getLineSeparator} when reading/writing
	 *
	 * @param normalizedNewline a single character used to represent a line separator.
	 *
	 * @return this {@code CsvFormat} instance
	 */
	public CsvFormat setNormalizedNewline(char normalizedNewline) {
		super.setNormalizedNewline(normalizedNewline);
		return this;
	}

	/**
	 * Defines the character that represents a line comment when found in the beginning of a line of text. Defaults to '#'
	 * <p> Use '\0' to disable comment skipping.
	 *
	 * @param comment the comment character
	 *
	 * @return this {@code CsvFormat} instance
	 */
	public CsvFormat setComment(char comment) {
		super.setComment(comment);
		return this;
	}

	@Override
	protected TreeMap<String, Object> getConfiguration() {
		TreeMap<String, Object> out = new TreeMap<String, Object>();
		out.put("Quote character", quote);
		out.put("Quote escape character", quoteEscape);
		out.put("Quote escape escape character", charToEscapeQuoteEscaping);
		out.put("Field delimiter", delimiter);
		return out;
	}

	@Override
	public final CsvFormat clone() {
		return (CsvFormat) super.clone();
	}
}
