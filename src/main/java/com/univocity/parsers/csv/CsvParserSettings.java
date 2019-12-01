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

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.input.*;
import com.univocity.parsers.common.processor.*;

import java.util.*;

/**
 * This is the configuration class used by the CSV parser ({@link CsvParser})
 *
 * <p>In addition to the configuration options provided by {@link CommonParserSettings}, the CSVParserSettings include:
 *
 * <ul>
 * <li><b>emptyValue <i>(defaults to null)</i>:</b> Defines a replacement string to signify an empty value (which is not a null value)
 * <p>When reading, if the parser does not read any character from the input, and the input is within quotes, the empty is used instead of an empty string</li>
 * </ul>
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.csv.CsvParser
 * @see com.univocity.parsers.csv.CsvFormat
 * @see com.univocity.parsers.common.CommonParserSettings
 */
public class CsvParserSettings extends CommonParserSettings<CsvFormat> {

	private String emptyValue = null;
	private boolean parseUnescapedQuotes = true;
	private boolean parseUnescapedQuotesUntilDelimiter = true;
	private boolean escapeUnquotedValues = false;
	private boolean keepEscapeSequences = false;
	private boolean keepQuotes = false;
	private boolean normalizeLineEndingsWithinQuotes = true;

	private boolean ignoreTrailingWhitespacesInQuotes = false;
	private boolean ignoreLeadingWhitespacesInQuotes = false;

	private boolean delimiterDetectionEnabled = false;
	private boolean quoteDetectionEnabled = false;
	private UnescapedQuoteHandling unescapedQuoteHandling = null;
	private char[] delimitersForDetection = null;

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
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setEmptyValue(String emptyValue) {
		this.emptyValue = emptyValue;
		return this;
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
		int chars = getMaxCharsPerColumn();
		if (chars != -1) {
			return new DefaultCharAppender(chars, emptyValue, getWhitespaceRangeStart());
		} else {
			return new ExpandingCharAppender(emptyValue, getWhitespaceRangeStart());
		}
	}

	/**
	 * Returns the default CsvFormat configured to handle CSV inputs compliant to the <a href="http://tools.ietf.org/html/rfc4180">RFC4180</a> standard.
	 *
	 * @return an instance of CsvFormat configured to handle CSV inputs compliant to the <a href="http://tools.ietf.org/html/rfc4180">RFC4180</a> standard.
	 */
	@Override
	protected CsvFormat createDefaultFormat() {
		return new CsvFormat();
	}

	/**
	 * Indicates whether the CSV parser should accept unescaped quotes inside quoted values and parse them normally. Defaults to {@code true}.
	 *
	 * @return a flag indicating whether or not the CSV parser should accept unescaped quotes inside quoted values.
	 *
	 * @deprecated use {@link #getUnescapedQuoteHandling()} instead. The configuration returned by {@link #getUnescapedQuoteHandling()} will override this setting if not null.
	 */
	@Deprecated
	public boolean isParseUnescapedQuotes() {
		return parseUnescapedQuotes || (unescapedQuoteHandling != null && unescapedQuoteHandling != UnescapedQuoteHandling.RAISE_ERROR);
	}

	/**
	 * Configures how to handle unescaped quotes inside quoted values. If set to {@code true}, the parser will parse the quote normally as part of the value.
	 * If set the {@code false}, a {@link TextParsingException} will be thrown. Defaults to {@code true}.
	 *
	 * @param parseUnescapedQuotes indicates whether or not the CSV parser should accept unescaped quotes inside quoted values.
	 *
	 * @return this {@code CsvParserSettings} instance
	 *
	 * @deprecated use {@link #setUnescapedQuoteHandling(UnescapedQuoteHandling)} instead. The configuration returned by {@link #getUnescapedQuoteHandling()} will override this setting if not null.
	 */
	@Deprecated
	public CsvParserSettings setParseUnescapedQuotes(boolean parseUnescapedQuotes) {
		this.parseUnescapedQuotes = parseUnescapedQuotes;
		return this;
	}

	/**
	 * Configures the parser to process values with unescaped quotes, and stop accumulating characters and consider the value parsed when a delimiter is found.
	 * (defaults to {@code true})
	 *
	 * @param parseUnescapedQuotesUntilDelimiter a flag indicating that the parser should stop accumulating values when a field delimiter character is
	 *                                           found when parsing unquoted and unescaped values.
	 *
	 * @return this {@code CsvParserSettings} instance
	 *
	 * @deprecated use {@link #setUnescapedQuoteHandling(UnescapedQuoteHandling)} instead. The configuration returned by {@link #getUnescapedQuoteHandling()} will override this setting if not null.
	 */
	@Deprecated
	public CsvParserSettings setParseUnescapedQuotesUntilDelimiter(boolean parseUnescapedQuotesUntilDelimiter) {
		if (parseUnescapedQuotesUntilDelimiter) {
			parseUnescapedQuotes = true;
		}
		this.parseUnescapedQuotesUntilDelimiter = parseUnescapedQuotesUntilDelimiter;
		return this;
	}

	/**
	 * When parsing unescaped quotes, indicates the parser should stop accumulating characters and consider the value parsed when a delimiter is found.
	 * (defaults to {@code true})
	 *
	 * @return a flag indicating that the parser should stop accumulating values when a field delimiter character is
	 * found when parsing unquoted and unescaped values.
	 *
	 * @deprecated use {@link #getUnescapedQuoteHandling()} instead. The configuration returned by {@link #getUnescapedQuoteHandling()} will override this setting if not null.
	 */
	@Deprecated
	public boolean isParseUnescapedQuotesUntilDelimiter() {
		return (parseUnescapedQuotesUntilDelimiter && isParseUnescapedQuotes()) || (unescapedQuoteHandling == UnescapedQuoteHandling.STOP_AT_DELIMITER || unescapedQuoteHandling == UnescapedQuoteHandling.SKIP_VALUE);
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
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setEscapeUnquotedValues(boolean escapeUnquotedValues) {
		this.escapeUnquotedValues = escapeUnquotedValues;
		return this;
	}

	/**
	 * Indicates whether the parser should keep any escape sequences if they are present in the input (i.e. a quote escape sequence such as two double quotes {@code ""} won't be replaced by a single double quote {@code "}).
	 * <p>This is disabled by default</p>
	 *
	 * @return a flag indicating whether escape sequences should be kept (and not replaced) by the parser.
	 */
	public final boolean isKeepEscapeSequences() {
		return keepEscapeSequences;
	}

	/**
	 * Configures the parser to keep any escape sequences if they are present in the input (i.e. a quote escape sequence such as 2 double quotes {@code ""} won't be replaced by a single double quote {@code "}).
	 * <p>This is disabled by default</p>
	 *
	 * @param keepEscapeSequences the flag indicating whether escape sequences should be kept (and not replaced) by the parser.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public final CsvParserSettings setKeepEscapeSequences(boolean keepEscapeSequences) {
		this.keepEscapeSequences = keepEscapeSequences;
		return this;
	}

	/**
	 * Returns a flag indicating whether the parser should analyze the input to discover the column delimiter character.
	 * <p>Note that the detection process is not guaranteed to discover the correct column delimiter. In this case the delimiter provided by {@link CsvFormat#getDelimiter()} will be used</p>
	 *
	 * @return a flag indicating whether the parser should analyze the input to discover the column delimiter character.
	 */
	public final boolean isDelimiterDetectionEnabled() {
		return delimiterDetectionEnabled;
	}

	/**
	 * Configures the parser to analyze the input before parsing to discover the column delimiter character.
	 * <p>Note that the detection process is not guaranteed to discover the correct column delimiter.
	 * The first character in the list of delimiters allowed for detection will be used, if available, otherwise
	 * the delimiter returned by {@link CsvFormat#getDelimiter()} will be used.</p>
	 *
	 * @param separatorDetectionEnabled the flag to enable/disable discovery of the column delimiter character.
	 * @param delimitersForDetection possible delimiters for detection when {@link #isDelimiterDetectionEnabled()} evaluates
	 * to {@code true}, in order of priority.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public final CsvParserSettings setDelimiterDetectionEnabled(boolean separatorDetectionEnabled, char... delimitersForDetection) {
		this.delimiterDetectionEnabled = separatorDetectionEnabled;
		this.delimitersForDetection = delimitersForDetection;
		return this;
	}

	/**
	 * Returns a flag indicating whether the parser should analyze the input to discover the quote character. The quote escape will also be detected as part of this process.
	 * <p> Note that the detection process is not guaranteed to discover the correct quote &amp; escape.
	 * In this case the characters provided by {@link CsvFormat#getQuote()} and {@link CsvFormat#getQuoteEscape()} will be used </p>
	 *
	 * @return a flag indicating whether the parser should analyze the input to discover the quote character. The quote escape will also be detected as part of this process.
	 */
	public final boolean isQuoteDetectionEnabled() {
		return quoteDetectionEnabled;
	}

	/**
	 * Configures the parser to analyze the input before parsing to discover the quote character. The quote escape will also be detected as part of this process.
	 * <p> Note that the detection process is not guaranteed to discover the correct quote &amp; escape.
	 * In this case the characters provided by {@link CsvFormat#getQuote()} and {@link CsvFormat#getQuoteEscape()} will be used </p>
	 *
	 * @param quoteDetectionEnabled the flag to enable/disable discovery of the quote character. The quote escape will also be detected as part of this process.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public final CsvParserSettings setQuoteDetectionEnabled(boolean quoteDetectionEnabled) {
		this.quoteDetectionEnabled = quoteDetectionEnabled;
		return this;
	}

	/**
	 * Convenience method to turn on all format detection features in a single method call, namely:
	 * <ul>
	 * <li>{@link #setDelimiterDetectionEnabled(boolean, char[])} </li>
	 * <li>{@link #setQuoteDetectionEnabled(boolean)} </li>
	 * <li>{@link #setLineSeparatorDetectionEnabled(boolean)} </li>
	 * </ul>
	 *
	 * @param delimitersForDetection possible delimiters for detection, in order of priority.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public final CsvParserSettings detectFormatAutomatically(char... delimitersForDetection) {
		this.setDelimiterDetectionEnabled(true, delimitersForDetection);
		this.setQuoteDetectionEnabled(true);
		this.setLineSeparatorDetectionEnabled(true);
		return this;
	}

	/**
	 * Flag indicating whether the parser should replace line separators, specified in {@link Format#getLineSeparator()}
	 * by the normalized line separator character specified in {@link Format#getNormalizedNewline()}, even on quoted values.
	 *
	 * This is enabled by default and is used to ensure data be read on any platform without introducing unwanted blank lines.
	 *
	 * For example, consider the quoted value {@code "Line1 \r\n Line2"}. If this is parsed using {@code "\r\n"} as
	 * the line separator sequence, and the normalized new line is set to {@code '\n'} (the default), the output will be:
	 *
	 * {@code [Line1 \n Line2]}
	 *
	 * However, if the value is meant to be kept untouched, and the original line separator should be maintained, set
	 * the {@link #normalizeLineEndingsWithinQuotes} to {@code false}. This will make the parser read the value as-is, producing:
	 *
	 * {@code [Line1 \r\n Line2]}
	 *
	 * @return {@code true} if line separators in quoted values will be normalized, {@code false} otherwise
	 */
	public boolean isNormalizeLineEndingsWithinQuotes() {
		return normalizeLineEndingsWithinQuotes;
	}

	/**
	 * Configures the parser to replace line separators, specified in {@link Format#getLineSeparator()}
	 * by the normalized line separator character specified in {@link Format#getNormalizedNewline()}, even on quoted values.
	 *
	 * This is enabled by default and is used to ensure data be read on any platform without introducing unwanted blank lines.
	 *
	 * For example, consider the quoted value {@code "Line1 \r\n Line2"}. If this is parsed using {@code "\r\n"} as
	 * the line separator sequence, and the normalized new line is set to {@code '\n'} (the default), the output will be:
	 *
	 * {@code [Line1 \n Line2]}
	 *
	 * However, if the value is meant to be kept untouched, and the original line separator should be maintained, set
	 * the {@link #normalizeLineEndingsWithinQuotes} to {@code false}. This will make the parser read the value as-is, producing:
	 *
	 * {@code [Line1 \r\n Line2]}
	 *
	 * @param normalizeLineEndingsWithinQuotes flag indicating whether line separators in quoted values should be replaced by
	 *                                         the the character specified in {@link Format#getNormalizedNewline()} .
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setNormalizeLineEndingsWithinQuotes(boolean normalizeLineEndingsWithinQuotes) {
		this.normalizeLineEndingsWithinQuotes = normalizeLineEndingsWithinQuotes;
		return this;
	}

	/**
	 * Configures the handling of values with unescaped quotes.
	 * Defaults to {@code null}, for backward compatibility with {@link #isParseUnescapedQuotes()} and {@link #isParseUnescapedQuotesUntilDelimiter()}.
	 * If set to a non-null value, this setting will override the configuration of {@link #isParseUnescapedQuotes()} and {@link #isParseUnescapedQuotesUntilDelimiter()}.
	 *
	 * @param unescapedQuoteHandling the handling method to be used when unescaped quotes are found in the input.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setUnescapedQuoteHandling(UnescapedQuoteHandling unescapedQuoteHandling) {
		this.unescapedQuoteHandling = unescapedQuoteHandling;
		return this;
	}

	/**
	 * Returns the method of handling values with unescaped quotes.
	 * Defaults to {@code null}, for backward compatibility with {@link #isParseUnescapedQuotes()} and {@link #isParseUnescapedQuotesUntilDelimiter()}
	 * If set to a non-null value, this setting will override the configuration of {@link #isParseUnescapedQuotes()} and {@link #isParseUnescapedQuotesUntilDelimiter()}.
	 *
	 * @return the handling method to be used when unescaped quotes are found in the input, or {@code null} if not set.
	 */
	public UnescapedQuoteHandling getUnescapedQuoteHandling() {
		return this.unescapedQuoteHandling;
	}


	/**
	 * Flag indicating whether the parser should keep enclosing quote characters in the values parsed from the input.
	 * <p>Defaults to {@code false}</p>
	 *
	 * @return a flag indicating whether enclosing quotes should be maintained when parsing quoted values.
	 */
	public boolean getKeepQuotes() {
		return keepQuotes;
	}

	/**
	 * Configures the parser to keep enclosing quote characters in the values parsed from the input.
	 * <p>Defaults to {@code false}</p>
	 *
	 * @param keepQuotes flag indicating whether enclosing quotes should be maintained when parsing quoted values.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setKeepQuotes(boolean keepQuotes) {
		this.keepQuotes = keepQuotes;
		return this;
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
		out.put("Empty value", emptyValue);
		out.put("Unescaped quote handling", unescapedQuoteHandling);
		out.put("Escape unquoted values", escapeUnquotedValues);
		out.put("Keep escape sequences", keepEscapeSequences);
		out.put("Keep quotes", keepQuotes);
		out.put("Normalize escaped line separators", normalizeLineEndingsWithinQuotes);
		out.put("Autodetect column delimiter", delimiterDetectionEnabled);
		out.put("Autodetect quotes", quoteDetectionEnabled);
		out.put("Delimiters for detection", Arrays.toString(delimitersForDetection));
		out.put("Ignore leading whitespaces in quotes", ignoreLeadingWhitespacesInQuotes);
		out.put("Ignore trailing whitespaces in quotes", ignoreTrailingWhitespacesInQuotes);
	}

	@Override
	public final CsvParserSettings clone() {
		return (CsvParserSettings) super.clone();
	}

	@Override
	public final CsvParserSettings clone(boolean clearInputSpecificSettings) {
		return (CsvParserSettings) super.clone(clearInputSpecificSettings);
	}

	/**
	 * Returns the sequence of possible delimiters for detection when {@link #isDelimiterDetectionEnabled()} evaluates
	 * to {@code true}, in order of priority.
	 *
	 * @return the possible delimiter characters, in order of priority.
	 */
	public final char[] getDelimitersForDetection() {
		return this.delimitersForDetection;
	}

	/**
	 * Returns whether or not trailing whitespaces from within quoted values should be skipped  (defaults to false)
	 *
	 * Note: if {@link #keepQuotes} evaluates to {@code true}, values won't be trimmed.
	 *
	 * @return true if trailing whitespaces from quoted values should be skipped, false otherwise
	 */
	public boolean getIgnoreTrailingWhitespacesInQuotes() {
		return ignoreTrailingWhitespacesInQuotes;
	}

	/**
	 * Defines whether or not trailing whitespaces from quoted values should be skipped  (defaults to false)
	 *
	 * Note: if {@link #keepQuotes} evaluates to {@code true}, values won't be trimmed.
	 *
	 * @param ignoreTrailingWhitespacesInQuotes whether trailing whitespaces from quoted values should be skipped
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setIgnoreTrailingWhitespacesInQuotes(boolean ignoreTrailingWhitespacesInQuotes) {
		this.ignoreTrailingWhitespacesInQuotes = ignoreTrailingWhitespacesInQuotes;
		return this;
	}

	/**
	 * Returns whether or not leading whitespaces from quoted values should be skipped  (defaults to false)
	 *
	 * Note: if {@link #keepQuotes} evaluates to {@code true}, values won't be trimmed.
	 *
	 * @return true if leading whitespaces from quoted values should be skipped, false otherwise
	 */
	public boolean getIgnoreLeadingWhitespacesInQuotes() {
		return ignoreLeadingWhitespacesInQuotes;
	}

	/**
	 * Defines whether or not leading whitespaces from quoted values should be skipped  (defaults to false)
	 *
	 * Note: if {@link #keepQuotes} evaluates to {@code true}, values won't be trimmed.
	 *
	 * @param ignoreLeadingWhitespacesInQuotes whether leading whitespaces from quoted values should be skipped
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setIgnoreLeadingWhitespacesInQuotes(boolean ignoreLeadingWhitespacesInQuotes) {
		this.ignoreLeadingWhitespacesInQuotes = ignoreLeadingWhitespacesInQuotes;
		return this;
	}

	/**
	 * Configures the parser to trim any whitespaces around values extracted from within quotes. Shorthand for
	 * {@link #setIgnoreLeadingWhitespacesInQuotes(boolean)} and {@link #setIgnoreTrailingWhitespacesInQuotes(boolean)}
	 *
	 * Note: if {@link #keepQuotes} evaluates to {@code true}, values won't be trimmed.
	 *
	 * @param trim a flag indicating whether whitespaces around values extracted from a quoted field should be removed
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public final CsvParserSettings trimQuotedValues(boolean trim) {
		setIgnoreTrailingWhitespacesInQuotes(trim);
		setIgnoreLeadingWhitespacesInQuotes(trim);
		return this;
	}

	/**
	 * Sets the String representation of a null value (defaults to null)
	 * <p>When reading, if the parser does not read any character from the input, the nullValue is used instead of an empty string
	 * <p>When writing, if the writer has a null object to write to the output, the nullValue is used instead of an empty string
	 *
	 * @param emptyValue the String representation of a null value
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setNullValue(String emptyValue) {
		super.setNullValue(emptyValue);
		return this;
	}

	/**
	 * Defines the maximum number of characters allowed for any given value being written/read. Used to avoid OutOfMemoryErrors (defaults to 4096).
	 *
	 * <p>To enable auto-expansion of the internal array, set this property to -1</p>
	 *
	 * @param maxCharsPerColumn The maximum number of characters allowed for any given value being written/read
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setMaxCharsPerColumn(int maxCharsPerColumn) {
		super.setMaxCharsPerColumn(maxCharsPerColumn);
		return this;
	}

	/**
	 * Defines whether or not empty lines should be ignored (defaults to true)
	 * <p>when reading, if the parser reads a line that is empty, it will be skipped.
	 * <p>when writing, if the writer receives an empty or null row to write to the output, it will be ignored
	 *
	 * @param skipEmptyLines true if empty lines should be ignored, false otherwise
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setSkipEmptyLines(boolean skipEmptyLines) {
		super.setSkipEmptyLines(skipEmptyLines);
		return this;
	}

	/**
	 * Defines whether or not trailing whitespaces from values being read/written should be skipped  (defaults to true)
	 *
	 * @param ignoreTrailingWhitespaces true if trailing whitespaces from values being read/written should be skipped, false otherwise
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setIgnoreTrailingWhitespaces(boolean ignoreTrailingWhitespaces) {
		super.setIgnoreTrailingWhitespaces(ignoreTrailingWhitespaces);
		return this;
	}

	/**
	 * Defines whether or not leading whitespaces from values being read/written should be skipped  (defaults to true)
	 *
	 * @param ignoreLeadingWhitespaces true if leading whitespaces from values being read/written should be skipped, false otherwise
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setIgnoreLeadingWhitespaces(boolean ignoreLeadingWhitespaces) {
		super.setIgnoreLeadingWhitespaces(ignoreLeadingWhitespaces);
		return this;
	}

	/**
	 * Defines the field names in the input/output, in the sequence they occur (defaults to null).
	 * <p>when reading, the given header names will be used to refer to each column irrespective of whether or not the input contains a header row
	 * <p>when writing, the given header names will be used to refer to each column and can be used for writing the header row
	 *
	 * @param headers the field name sequence associated with each column in the input/output.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setHeaders(String... headers) {
		super.setHeaders(headers);
		return this;
	}

	/**
	 * Defines the field names in the input/output derived from a given class with {@link Parsed} annotated attributes/methods.
	 * <p>when reading, the given header names will be used to refer to each column irrespective of whether or not the input contains a header row
	 * <p>when writing, the given header names will be used to refer to each column and can be used for writing the header row
	 *
	 * @param headerSourceClass the class from which the headers have been derived.
	 * @param headers           the field name sequence associated with each column in the input/output.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setHeadersDerivedFromClass(Class<?> headerSourceClass, String... headers) {
		super.setHeadersDerivedFromClass(headerSourceClass, headers);
		return this;
	}

	/**
	 * Defines a hard limit of how many columns a record can have (defaults to 512).
	 * You need this to avoid OutOfMemory errors in case of inputs that might be inconsistent with the format you are dealing with.
	 *
	 * @param maxColumns The maximum number of columns a record can have.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setMaxColumns(int maxColumns) {
		super.setMaxColumns(maxColumns);
		return this;
	}

	/**
	 * Defines the format of the file to be parsed/written (returns the format's defaults).
	 *
	 * @param format The format of the file to be parsed/written
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setFormat(CsvFormat format) {
		super.setFormat(format);
		return this;
	}

	/**
	 * Indicates whether this settings object can automatically derive configuration options. This is used, for example, to define the headers when the user
	 * provides a {@link BeanWriterProcessor} where the bean class contains a {@link Headers} annotation, or to enable header extraction when the bean class of a
	 * {@link BeanProcessor} has attributes mapping to header names.
	 *
	 * @param autoConfigurationEnabled a flag to turn the automatic configuration feature on/off.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public final CsvParserSettings setAutoConfigurationEnabled(boolean autoConfigurationEnabled) {
		super.setAutoConfigurationEnabled(autoConfigurationEnabled);
		return this;
	}

	/**
	 * Defines a custom error handler to capture and handle errors that might happen while processing records with a {@link RowProcessor}
	 * or a {@link RowWriterProcessor} (i.e. non-fatal {@link DataProcessingException}s).
	 *
	 * <p>The parsing parsing/writing won't stop (unless the error handler rethrows the {@link DataProcessingException} or manually stops the process).</p>
	 *
	 * @param rowProcessorErrorHandler the callback error handler with custom code to manage occurrences of {@link DataProcessingException}.
	 *
	 * @return this {@code CsvParserSettings} instance
	 *
	 * @deprecated Use the {@link #setProcessorErrorHandler(ProcessorErrorHandler)} method as it allows format-specific error handlers to be built to work with different implementations of {@link Context}.
	 * Implementations based on {@link RowProcessorErrorHandler} allow only parsers who provide a {@link ParsingContext} to be used.
	 */
	@Deprecated
	public CsvParserSettings setRowProcessorErrorHandler(RowProcessorErrorHandler rowProcessorErrorHandler) {
		super.setRowProcessorErrorHandler(rowProcessorErrorHandler);
		return this;
	}

	/**
	 * Defines a custom error handler to capture and handle errors that might happen while processing records with a {@link com.univocity.parsers.common.processor.core.Processor}
	 * or a {@link RowWriterProcessor} (i.e. non-fatal {@link DataProcessingException}s).
	 *
	 * <p>The parsing parsing/writing won't stop (unless the error handler rethrows the {@link DataProcessingException} or manually stops the process).</p>
	 *
	 * @param processorErrorHandler the callback error handler with custom code to manage occurrences of {@link DataProcessingException}.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setProcessorErrorHandler(ProcessorErrorHandler<? extends Context> processorErrorHandler) {
		super.setProcessorErrorHandler(processorErrorHandler);
		return this;
	}

	/**
	 * Configures the parser/writer to limit the length of displayed contents being parsed/written in the exception message when an error occurs.
	 *
	 * <p>If set to {@code 0}, then no exceptions will include the content being manipulated in their attributes,
	 * and the {@code "<omitted>"} string will appear in error messages as the parsed/written content.</p>
	 *
	 * <p>defaults to {@code -1} (no limit)</p>.
	 *
	 * @param errorContentLength maximum length of contents displayed in exception messages in case of errors while parsing/writing.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public CsvParserSettings setErrorContentLength(int errorContentLength) {
		super.setErrorContentLength(errorContentLength);
		return this;
	}

	/**
	 * Configures the parser to skip bit values as whitespace.
	 *
	 * By default the parser/writer removes control characters and considers a whitespace any character where {@code character <= ' '} evaluates to
	 * {@code true}. This includes bit values, i.e. {@code 0} (the \0 character) and {@code 1} which might
	 * be produced by database dumps. Disabling this flag will prevent the parser/writer from discarding these characters
	 * when {@link #getIgnoreLeadingWhitespaces()} or {@link #getIgnoreTrailingWhitespaces()} evaluate to {@code true}.
	 *
	 * <p>defaults to {@code true}</p>
	 *
	 * @param skipBitsAsWhitespace a flag indicating whether bit values (0 or 1) should be considered whitespace.
	 *
	 * @return this {@code CsvParserSettings} instance
	 */
	public final CsvParserSettings setSkipBitsAsWhitespace(boolean skipBitsAsWhitespace) {
		super.setSkipBitsAsWhitespace(skipBitsAsWhitespace);
		return this;
	}
}
