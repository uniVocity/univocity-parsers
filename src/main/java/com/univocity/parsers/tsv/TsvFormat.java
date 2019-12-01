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
package com.univocity.parsers.tsv;

import com.univocity.parsers.common.*;

import java.util.*;

/**
 * The TSV format configuration, for tab-separated inputs. It offers the options in the default configuration in {@link Format}, as well as
 * the {@link #escapeChar} character for escaping \t, \n, \r and \ in TSV values.
 *
 * Delimiters are defined as tab characters '\t'
 *
 * @see com.univocity.parsers.common.Format
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class TsvFormat extends Format {

	private char escapeChar = '\\';
	private char escapedTabChar = 't';

	/**
	 * Defines the character used for escaping special characters in TSV inputs: \t, \n, \r and \ . Defaults to '\\'
	 *
	 * @param escapeChar the escape character
	 *
	 * @return this {@code TsvFormat} instance
	 */
	public TsvFormat setEscapeChar(char escapeChar) {
		this.escapeChar = escapeChar;
		return this;
	}

	/**
	 * Returns the character used for escaping special characters in TSV inputs: \t, \n, \r and \
	 * 
	 * @return the escape character.
	 */
	public char getEscapeChar() {
		return escapeChar;
	}

	/**
	 * Returns the character that should be used to represent an escaped tab, i.e. the character before the defined
	 * {@link #getEscapeChar()}. For example, if {@link #getEscapeChar()} == '\\' and {@link #getEscapedTabChar() == 'X'},
	 * the sequence {@code '\X'} will identify a tab.
	 *
	 * Defaults to {@code 't'}.
	 *
	 * @return the character following the {@link #getEscapeChar()} that represents an escaped tab.
	 */
	public char getEscapedTabChar() {
		return escapedTabChar;
	}

	/**
	 * Defines the character that should be used to represent an escaped tab, i.e. the character before the defined
	 * {@link #getEscapeChar()}. For example, if {@link #getEscapeChar()} == '\\' and {@link #getEscapedTabChar() == 'X'},
	 * the sequence {@code '\X'} will identify a tab.
	 *
	 * Defaults to {@code 't'}.
	 *
	 * @param escapedTabChar the character following the {@link #getEscapeChar()} that represents an escaped tab.
	 *
	 * @return this {@code TsvFormat} instance
	 */
	public TsvFormat setEscapedTabChar(char escapedTabChar) {
		this.escapedTabChar = escapedTabChar;
		return this;
	}

	/**
	 * Identifies whether or not a given character is used for escaping special characters in TSV (\t, \n, \r and \).
	 * @param ch the character to be verified
	 * @return true if the given character is escape character, false otherwise
	 */
	public boolean isEscapeChar(char ch) {
		return this.escapeChar == ch;
	}

	/**
	 * Defines the line separator sequence that should be used for parsing and writing.
	 *
	 * @param lineSeparator a sequence of 1 to 2 characters that identifies the end of a line
	 *
	 * @return this {@code TsvFormat} instance
	 */
	public TsvFormat setLineSeparator(String lineSeparator) {
		super.setLineSeparator(lineSeparator);
		return this;
	}

	/**
	 * Defines the line separator sequence that should be used for parsing and writing.
	 *
	 * @param lineSeparator a sequence of 1 to 2 characters that identifies the end of a line
	 *
	 * @return this {@code TsvFormat} instance
	 */
	public TsvFormat setLineSeparator(char[] lineSeparator) {
		super.setLineSeparator(lineSeparator);
		return this;
	}

	/**
	 * Sets the normalized newline character, which is automatically replaced by {@link Format#getLineSeparator} when reading/writing
	 *
	 * @param normalizedNewline a single character used to represent a line separator.
	 *
	 * @return this {@code TsvFormat} instance
	 */
	public TsvFormat setNormalizedNewline(char normalizedNewline) {
		super.setNormalizedNewline(normalizedNewline);
		return this;
	}

	/**
	 * Defines the character that represents a line comment when found in the beginning of a line of text. Defaults to '#'
	 * <p> Use '\0' to disable comment skipping.
	 *
	 * @param comment the comment character
	 *
	 * @return this {@code TsvFormat} instance
	 */
	public TsvFormat setComment(char comment) {
		super.setComment(comment);
		return this;
	}

	@Override
	protected TreeMap<String, Object> getConfiguration() {
		TreeMap<String, Object> out = new TreeMap<String, Object>();
		out.put("Escape character", escapeChar);
		return out;
	}

	@Override
	public final TsvFormat clone() {
		return (TsvFormat) super.clone();
	}
}