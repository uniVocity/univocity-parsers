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
package com.univocity.parsers.csv;

import java.util.*;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.input.*;

/**
 * This is the configuration class used by the CSV parser ({@link CsvParser})
 *
 * <p>In addition to the configuration options provided by {@link CommonParserSettings}, the CSVParserSettings include:
 *
 * <ul>
 * 	<li><b>emptyValue <i>(defaults to null)</i>:</b> Defines a replacement string to signify an empty value (which is not a null value)
 *   <p>When reading, if the parser does not read any character from the input, and the input is within quotes, the empty is used instead of an empty string</li>
 * </ul>
 *
 * @see com.univocity.parsers.csv.CsvParser
 * @see com.univocity.parsers.csv.CsvFormat
 * @see com.univocity.parsers.common.CommonParserSettings
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class CsvParserSettings extends CommonParserSettings<CsvFormat> {

	private String emptyValue = null;
	private boolean parseUnescapedQuotes = true;
	private boolean escapeUnquotedValues = false;

	/**
	 * Returns the String representation of an empty value (defaults to null)
	 *
	 * <p>When reading, if the parser does not read any character from the input, and the input is within quotes, the empty is used instead of an empty string
	 *
	 * @return the String representation of an empty value
	 */
	public String getEmptyValue() {
		return emptyValue;
	}

	/**
	 * Sets the String representation of an empty value (defaults to null)
	 *
	 * <p>When reading, if the parser does not read any character from the input, and the input is within quotes, the empty is used instead of an empty string
	 *
	 * @param emptyValue the String representation of an empty value
	 */
	public void setEmptyValue(String emptyValue) {
		this.emptyValue = emptyValue;
	}

	/**
	 * Returns an instance of CharAppender with the configured limit of maximum characters per column and the default value used to represent an empty value (when the String parsed from the input, within quotes, is empty)
	 *
	 * <p>This overrides the parent's version because the CSV parser does not rely on the appender to identify null values, but on the other hand, the appender is required to identify empty values.
	 *
	 * @return an instance of CharAppender with the configured limit of maximum characters per column and the default value used to represent an empty value (when the String parsed from the input, within quotes, is empty)
	 */
	@Override
	protected CharAppender newCharAppender() {
		return new DefaultCharAppender(getMaxCharsPerColumn(), emptyValue);
	}

	/**
	 * Returns the default CsvFormat configured to handle CSV inputs compliant to the <a href="http://tools.ietf.org/html/rfc4180">RFC4180</a> standard.
	 * @return and instance of CsvFormat configured to handle CSV inputs compliant to the <a href="http://tools.ietf.org/html/rfc4180">RFC4180</a> standard.
	 */
	@Override
	protected CsvFormat createDefaultFormat() {
		return new CsvFormat();
	}

	/**
	 * Indicates whether the CSV parser should accept unescaped quotes inside quoted values and parse them normally. Defaults to {@code true}.
	 * @return a flag indicating whether or not the CSV parser should accept unescaped quotes inside quoted values.
	 */
	public boolean isParseUnescapedQuotes() {
		return parseUnescapedQuotes;
	}

	/**
	 * Configures how to handle unescaped quotes inside quoted values. If set to {@code true}, the parser will parse the quote normally as part of the value.
	 * If set the {@code false}, a {@link TextParsingException} will be thrown. Defaults to {@code true}.
	 * @param parseUnescapedQuotes indicates whether or not the CSV parser should accept unescaped quotes inside quoted values.
	 */
	public void setParseUnescapedQuotes(boolean parseUnescapedQuotes) {
		this.parseUnescapedQuotes = parseUnescapedQuotes;
	}

	/**
	 * Indicates whether escape sequences should be processed in unquoted values. Defaults to {@code false}.
	 *
	 * <p>By default, this is disabled and if the input is {@code A""B,C}, the resulting value will be
	 * {@code [A""B] and [C]} (i.e. the content is read as-is). However, if the parser is configured
	 * to process escape sequences in unquoted values, the result will be {@code [A"B] and [C]}</p>
	 *
	 * @return true if escape sequences should be processed in unquoted values, otherwise false
	 */
	public boolean isEscapeUnquotedValues() {
		return escapeUnquotedValues;
	}

	/**
	 * Configures the parser to process escape sequences in unquoted values. Defaults to {@code false}.
	 *
	 * <p>By default, this is disabled and if the input is {@code A""B,C}, the resulting value will be
	 * {@code [A""B] and [C]} (i.e. the content is read as-is). However, if the parser is configured
	 * to process escape sequences in unquoted values, the result will be {@code [A"B] and [C]}</p>
	 *
	 * @param escapeUnquotedValues a flag indicating whether escape sequences should be processed in unquoted values
	 */
	public void setEscapeUnquotedValues(boolean escapeUnquotedValues) {
		this.escapeUnquotedValues = escapeUnquotedValues;
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
		out.put("Empty value", emptyValue);
		out.put("Parse unescaped quotes", parseUnescapedQuotes);
		out.put("Escape unquoted values", escapeUnquotedValues);
	}
}
