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

import java.util.Map.*;
import java.util.*;

/**
 * This is the parent class for all configuration classes that define a text format.
 *
 * <p>By default, all parsers and writers have to handle, at least, the following format definitions:
 *
 * <ul>
 *  <li><b>lineSeparator:</b> the 1-2 character sequence that indicates the end of a line. Newline sequences are different across operating systems. Typically:
 *		<ul>
 *			<li>Windows uses carriage return and line feed: <i>\r\n</i></li>
 *			<li>Linux/Unix uses line feed only: <i>\n</i></li>
 *			<li>MacOS uses carriage return only: <i>\r</i></li>
 *		</ul>
 *   	<i>{@link Format#lineSeparator} defaults to the system line separator</i>
 *  </li>
 *  <li><b>normalizedNewline:</b> a single character used to represent the end of a line uniformly in any parsed content. It has the following implications:
 *  	<ul>
 *			<li>When <i>reading</i> a text-based input, the sequence of characters defined in {@link Format#lineSeparator} will be replaced by this character.</li>
 *			<li>When <i>writing</i> to a text-based output, this character will be replaced by the sequence of characters defined in {@link Format#lineSeparator}.</li>
 *		</ul>
 *  	<p><i>{@link Format#normalizedNewline} defaults to '\n'.</i>
 *  </li>
 *  <li><b>comment:</b>a character that, if found in the beginning of a line of text, represents comment in any text-based input supported by uniVocity-parsers.
 *  	<p><i>{@link Format#comment} defaults to '#'.</i></li>
 * </ul>
 *
 * @see com.univocity.parsers.csv.CsvFormat
 * @see com.univocity.parsers.fixed.FixedWidthFormat
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */

public abstract class Format implements Cloneable{

	private static final String systemLineSeparatorString;
	private static final char[] systemLineSeparator;

	static {
		String lineSeparator = System.getProperty("line.separator");
		if (lineSeparator == null) {
			systemLineSeparatorString = "\n";
		} else {
			systemLineSeparatorString = lineSeparator;
		}
		systemLineSeparator = systemLineSeparatorString.toCharArray();
	}

	private String lineSeparatorString;
	private char[] lineSeparator;
	private char normalizedNewline = '\n';
	private char comment = '#';

	protected Format() {
		this.lineSeparator = systemLineSeparator.clone();
		this.lineSeparatorString = systemLineSeparatorString;
	}

	/**
	 * Returns the current line separator character sequence, which can contain 1 to 2 characters. Defaults to the system's line separator sequence (usually '\r\n' in Windows, '\r' in MacOS, and '\n' in Linux/Unix).
	 * @return the sequence of 1 to 2 characters that identifies the end of a line
	 */
	public char[] getLineSeparator() {
		return lineSeparator.clone();
	}

	/**
	 * Returns the system's line separator sequence, which can contain 1 to 2 characters.
	 * @return a sequence of 1 to 2 characters used as the system's line ending.
	 */
	public static char[] getSystemLineSeparator(){
		return systemLineSeparator.clone();
	}

	/**
	 * Returns the current line separator sequence as a String of 1 to 2 characters. Defaults to the system's line separator sequence (usually "\r\n" in Windows, "\r" in MacOS, and "\n" in Linux/Unix).
	 * @return the sequence of 1 to 2 characters that identifies the end of a line
	 */
	public String getLineSeparatorString() {
		return lineSeparatorString;
	}

	/**
	 * Defines the line separator sequence that should be used for parsing and writing.
	 * @param lineSeparator a sequence of 1 to 2 characters that identifies the end of a line
	 */
	public void setLineSeparator(String lineSeparator) {
		if (lineSeparator == null || lineSeparator.isEmpty()) {
			throw new IllegalArgumentException("Line separator cannot be empty");
		}
		setLineSeparator(lineSeparator.toCharArray());
	}

	/**
	 * Defines the line separator sequence that should be used for parsing and writing.
	 * @param lineSeparator a sequence of 1 to 2 characters that identifies the end of a line
	 */
	public void setLineSeparator(char[] lineSeparator) {
		if (lineSeparator == null || lineSeparator.length == 0) {
			throw new IllegalArgumentException("Invalid line separator. Expected 1 to 2 characters");
		}
		if (lineSeparator.length > 2) {
			throw new IllegalArgumentException("Invalid line separator. Up to 2 characters are expected. Got " + lineSeparator.length + " characters.");
		}
		this.lineSeparator = lineSeparator;
		this.lineSeparatorString = new String(lineSeparator);
		if(lineSeparator.length == 1){
			setNormalizedNewline(lineSeparator[0]);
		}
	}

	/**
	 * Returns the normalized newline character, which is automatically replaced by {@link Format#lineSeparator} when reading/writing. Defaults to '\n'.
	 * @return the normalized newline character
	 */
	public char getNormalizedNewline() {
		return normalizedNewline;
	}

	/**
	 * Sets the normalized newline character, which is automatically replaced by {@link Format#lineSeparator} when reading/writing
	 * @param normalizedNewline a single character used to represent a line separator.
	 */
	public void setNormalizedNewline(char normalizedNewline) {
		this.normalizedNewline = normalizedNewline;
	}

	/**
	 * Compares the given character against the {@link Format#normalizedNewline} character.
	 * @param  ch the character to be verified
	 * @return true if the given character is the normalized newline character, false otherwise
	 */
	public boolean isNewLine(char ch) {
		return this.normalizedNewline == ch;
	}

	/**
	 * Returns the character that represents a line comment. Defaults to '#'
	 * <p> Set it to '\0' to disable comment skipping.
	 * @return the comment character
	 */
	public char getComment() {
		return comment;
	}

	/**
	 * Defines the character that represents a line comment when found in the beginning of a line of text. Defaults to '#'
	 * <p> Use '\0' to disable comment skipping.
	 * @param comment the comment character
	 */
	public void setComment(char comment) {
		this.comment = comment;
	}

	/**
	 * Identifies whether or not a given character represents a comment
	 * @param ch the character to be verified
	 * @return true if the given character is the comment character, false otherwise
	 */
	public boolean isComment(char ch) {
		return this.comment == ch;
	}

	private static String getFormattedValue(Object value) {
		if (value instanceof Character) {
			char ch = (Character) value;
			switch (ch) {
				case '\n':
					return "\\n";
				case '\r':
					return "\\r";
				case '\t':
					return "\\t";
				case '\0':
					return "\\0";
				default:
					return value.toString();
			}
		}
		if (value instanceof String) {
			String s = (String) value;
			StringBuilder tmp = new StringBuilder();
			for (int i = 0; i < s.length(); i++) {
				tmp.append(getFormattedValue(s.charAt(i)));
			}
			value = tmp.toString();
		}
		if (String.valueOf(value).trim().isEmpty()) {
			return "'" + value + '\'';
		}
		return String.valueOf(value);
	}

	@Override
	public final String toString() {
		StringBuilder out = new StringBuilder();

		out.append(getClass().getSimpleName()).append(':');

		TreeMap<String, Object> config = getConfiguration();
		config.put("Comment character", comment);
		config.put("Line separator sequence", lineSeparatorString);
		config.put("Line separator (normalized)", normalizedNewline);

		for (Entry<String, Object> e : config.entrySet()) {
			out.append("\n\t\t");
			out.append(e.getKey()).append('=').append(getFormattedValue(e.getValue()));
		}

		return out.toString();
	}

	protected abstract TreeMap<String, Object> getConfiguration();

	@Override
	protected Format clone() {
		try {
			return (Format) super.clone();
		} catch(CloneNotSupportedException e){
			throw new IllegalStateException("Error cloning format object", e);
		}
	}
}
