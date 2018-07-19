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
import com.univocity.parsers.common.fields.*;

import java.util.*;

/**
 * This is the configuration class used by the CSV writer ({@link CsvWriter})
 *
 * <p>In addition to the configuration options provided by {@link CommonWriterSettings}, the CsvWriterSettings include:
 *
 * <ul>
 * <li><b>emptyValue <i>(defaults to null)</i>:</b> Defines a replacement string to signify an empty value (which is not a null value)
 * <p>If the writer has an empty String to write to the output, the emptyValue is used instead of an empty string</li>
 * <li><b>quoteAllFields <i>(defaults to false)</i>:</b> By default, only values that contain a field separator are enclosed within quotes.
 * <p>If this property is set to true, this indicates that all written values should be enclosed within quotes (as defined in {@link CsvFormat})</li>
 * </ul>
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.csv.CsvWriter
 * @see com.univocity.parsers.csv.CsvFormat
 * @see com.univocity.parsers.common.CommonWriterSettings
 */
public class CsvWriterSettings extends CommonWriterSettings<CsvFormat> {

	private boolean escapeUnquotedValues = false;
	private boolean quoteAllFields = false;
	private boolean isInputEscaped = false;
	private boolean normalizeLineEndingsWithinQuotes = true;
	private char[] quotationTriggers = new char[0];
	private boolean quoteEscapingEnabled = false;
	private FieldSelector quotedFieldSelector = null;

	/**
	 * Indicates that all written values should be enclosed within quotes (as defined in {@link CsvFormat})
	 * <p> (Defaults to false)
	 *
	 * @return true if all written values should be enclosed within quotes, false otherwise
	 */
	public boolean getQuoteAllFields() {
		return quoteAllFields;
	}

	/**
	 * Indicates indicates whether or not all written values should be enclosed within quotes (as defined in {@link CsvFormat})
	 *
	 * <p> (Defaults to false)
	 * <p> By default, only values that contain a field separator are enclosed within quotes.
	 *
	 * @param quoteAllFields a flag indicating whether to enclose all fields within quotes
	 */
	public void setQuoteAllFields(boolean quoteAllFields) {
		this.quoteAllFields = quoteAllFields;
	}

	/**
	 * Indicates whether escape sequences should be written in unquoted values. Defaults to {@code false}.
	 *
	 * <p>By default, this is disabled and if the input is {@code A""B,C}, the resulting value will be
	 * {@code [A""B] and [C]} (i.e. the content is written as-is). However, if the writer is configured
	 * to process escape sequences in unquoted values, the values will be written as {@code [A""""B] and [C]}</p>
	 *
	 * @return true if escape sequences should be processed in unquoted values, otherwise false
	 */
	public boolean isEscapeUnquotedValues() {
		return escapeUnquotedValues;
	}

	/**
	 * Configures the writer to process escape sequences in unquoted values. Defaults to {@code false}.
	 *
	 * <p>By default, this is disabled and if the input is {@code A""B,C}, the result will be written as
	 * {@code [A""B] and [C]} (i.e. the quotes written as-is). However, if the writer is configured
	 * to process escape sequences in unquoted values, the values will written as {@code [A""""B] and [C]}</p>
	 *
	 * @param escapeUnquotedValues a flag indicating whether escape sequences should be processed in unquoted values
	 */
	public void setEscapeUnquotedValues(boolean escapeUnquotedValues) {
		this.escapeUnquotedValues = escapeUnquotedValues;
	}

	/**
	 * Indicates that the user will provide escaped input, and the writer will not try to introduce escape sequences. The input will be written as-is.
	 * <p><strong>Warning!</strong> ensure your data is properly escaped, otherwise the writer will produce invalid CSV.</p>
	 * <p>This is disabled by default</p>
	 *
	 * @return a flag indicating whether the escape sequences should not be introduced by the writer.
	 */
	public final boolean isInputEscaped() {
		return isInputEscaped;
	}

	/**
	 * Configures the writer to prevent it to introduce escape sequences.  The writer will assume the user is providing escaped input, and it will be written as-is.
	 * <p><strong>Warning!</strong> ensure your data is properly escaped, otherwise the writer will produce invalid CSV.</p>
	 * <p>This is disabled by default</p>
	 *
	 * @param isInputEscaped a flag indicating whether the input that will be written is already properly escaped.
	 */
	public final void setInputEscaped(boolean isInputEscaped) {
		this.isInputEscaped = isInputEscaped;
	}

	/**
	 * Flag indicating whether the writer should replace the the normalized line separator character specified in {@link Format#getNormalizedNewline()}
	 * by the sequence specified in {@link Format#getLineSeparator()}, when the value is enclosed within quotes.
	 *
	 * This is enabled by default and is used to ensure data be read on any platform without introducing unwanted blank lines.
	 *
	 * For example, consider the quoted value {@code "Line1 \n Line2"}. If this is written using {@code "\r\n"} as
	 * the line separator sequence, and the normalized new line is set to {@code '\n'} (the default), the output will be:
	 *
	 * {@code [Line1 \r\n Line2]}
	 *
	 * However, if the value is meant to be kept untouched, and the original line separator should be maintained, set
	 * the {@link #normalizeLineEndingsWithinQuotes} to {@code false}. This will make the writer output the value as-is, producing:
	 *
	 * {@code [Line1 \n Line2]}
	 *
	 * @return {@code true} if line separator characters in quoted values should be considered 'normalized' and replaced by the
	 * sequence specified in {@link Format#getLineSeparator()}, {@code false} otherwise
	 */
	public boolean isNormalizeLineEndingsWithinQuotes() {
		return normalizeLineEndingsWithinQuotes;
	}

	/**
	 * Flag indicating whether the writer should replace the the normalized line separator character specified in {@link Format#getNormalizedNewline()}
	 * by the sequence specified in {@link Format#getLineSeparator()}, when the value is enclosed within quotes.
	 *
	 * This is enabled by default and is used to ensure data can be used on any platform without producing unrecognized line endings.
	 *
	 * For example, consider the quoted value {@code "Line1 \n Line2"}. If this is written using {@code "\r\n"} as
	 * the line separator sequence, and the normalized new line is set to {@code '\n'} (the default), the output will be:
	 *
	 * {@code [Line1 \r\n Line2]}
	 *
	 * However, if the value is meant to be kept untouched, and the original line separator should be maintained, set
	 * the {@link #normalizeLineEndingsWithinQuotes} to {@code false}. This will make the writer output the value as-is, producing:
	 *
	 * {@code [Line1 \n Line2]}
	 *
	 * @param normalizeLineEndingsWithinQuotes flag indicating that line separator characters in quoted values should be
	 *                                         considered 'normalized' and occurrences of {@link Format#getNormalizedNewline()}
	 *                                         should be replaced by the sequence specified in {@link Format#getLineSeparator()}
	 */
	public void setNormalizeLineEndingsWithinQuotes(boolean normalizeLineEndingsWithinQuotes) {
		this.normalizeLineEndingsWithinQuotes = normalizeLineEndingsWithinQuotes;
	}

	/**
	 * Returns the default CsvFormat configured to produce CSV outputs compliant to the <a href="http://tools.ietf.org/html/rfc4180">RFC4180</a> standard.
	 *
	 * @return and instance of CsvFormat configured to produce CSV outputs compliant to the <a href="http://tools.ietf.org/html/rfc4180">RFC4180</a> standard.
	 */
	@Override
	protected CsvFormat createDefaultFormat() {
		return new CsvFormat();
	}


	/**
	 * Returns the list of characters that when present in a value to be written, will
	 * force the output value to be enclosed in quotes.
	 *
	 * @return the characters that will trigger values to be quoted when present in a value to be written.
	 */
	public char[] getQuotationTriggers() {
		return quotationTriggers;
	}

	/**
	 * Defines one or more "triggers" for enclosing a value within quotes. If one of the characters in the quotation trigger
	 * list is found in a value to be written, the entire value will be enclosed in quotes.
	 *
	 * @param quotationTriggers a list of characters that when present in a value to be written, will
	 *                          force the output value to be enclosed in quotes.
	 */
	public void setQuotationTriggers(char... quotationTriggers) {
		this.quotationTriggers = quotationTriggers == null ? new char[0] : quotationTriggers;
	}

	/**
	 * Queries if a given character is a quotation trigger, i.e. a character that if present in a value to be written,
	 * will make the CSV writer enclose the entire value within quotes.
	 *
	 * @param ch the character to be tested
	 *
	 * @return {@code true} if the given character is a quotation trigger, {@code false} otherwise.
	 */
	public boolean isQuotationTrigger(char ch) {
		for (int i = 0; i < quotationTriggers.length; i++) {
			if (quotationTriggers[i] == ch) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Indicates whether the CSV writer should escape values that contain the quote character, by enclosing the entire
	 * value in quotes.
	 *
	 * For example, consider a value such as {@code [My "precious" value]}.
	 * When quote escaping is enabled, the output will be:
	 *
	 * {@code ["My ""precious"" value"]}
	 *
	 * If disabled (the default), the value will be written as-is. Note that the CSV output will not conform to the RFC 4180 standard,
	 * but it will still be valid as the value does not contain line separators nor the delimiter character.
	 *
	 * @return a flag indicating whether values containing quotes should be enclosed in quotes.
	 */
	public boolean isQuoteEscapingEnabled() {
		return quoteEscapingEnabled;
	}

	/**
	 * Configures the CSV writer to escape values that contain the quote character, by enclosing the entire
	 * value in quotes.
	 *
	 * For example, consider a value such as {@code [My "precious" value]}.
	 * When quote escaping is enabled, the output will be:
	 *
	 * {@code ["My ""precious"" value"]}
	 *
	 * If disabled (the default), the value will be written as-is. Note that the CSV output will not conform to the RFC 4180 standard,
	 * but it will still be valid as the value does not contain line separators nor the delimiter character.
	 *
	 * @param quoteEscapingEnabled a flag indicating whether values containing quotes should be enclosed in quotes.
	 */
	public void setQuoteEscapingEnabled(boolean quoteEscapingEnabled) {
		this.quoteEscapingEnabled = quoteEscapingEnabled;
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
		out.put("Quote all fields", quoteAllFields);
		out.put("Escape unquoted values", escapeUnquotedValues);
		out.put("Normalize escaped line separators", normalizeLineEndingsWithinQuotes);
		out.put("Input escaped", isInputEscaped);
		out.put("Quote escaping enabled", quoteEscapingEnabled);
		out.put("Quotation triggers", Arrays.toString(quotationTriggers));
	}

	@Override
	public final CsvWriterSettings clone() {
		return (CsvWriterSettings) super.clone();
	}

	@Override
	public final CsvWriterSettings clone(boolean clearInputSpecificSettings) {
		return (CsvWriterSettings) super.clone(clearInputSpecificSettings);
	}

	/**
	 * Returns the current selection of quoted fields (if any)
	 * @return the current selection of quoted fields
	 */
	final FieldSelector getQuotedFieldSelector(){
		return quotedFieldSelector;
	}

	/**
	 * Replaces the current quoted field selection
	 *
	 * @param fieldSet the new set of selected fields
	 * @param values   the values to include to the selection
	 *
	 * @return the set of selected fields given in as a parameter.
	 */
	private <T> FieldSet<T> setFieldSet(FieldSet<T> fieldSet, T... values) {
		this.quotedFieldSelector = (FieldSelector) fieldSet;
		fieldSet.add(values);
		return fieldSet;
	}

	/**
	 * Selects fields whose values should always be written within quotes
	 *
	 * @param columns a selection of columns that will always be quoted
	 *
	 * @return the (modifiable) set of selected fields to be quoted.
	 */
	public final FieldSet<Enum> quoteFields(Enum... columns) {
		return setFieldSet(new FieldEnumSelector(), columns);
	}

	/**
	 * Selects fields whose values should always be written within quotes
	 *
	 * @param columns a selection of columns that will always be quoted
	 *
	 * @return the (modifiable) set of selected fields to be quoted.
	 */
	public final FieldSet<String> quoteFields(String... columns) {
		return setFieldSet(new FieldNameSelector(), columns);
	}

	/**
	 * Selects field positions whose values should always be written within quotes
	 *
	 * @param columns a selection of column indexes that will always be quoted
	 *
	 * @return the (modifiable) set of column positions to be quoted.
	 */
	public final FieldSet<Integer> quoteIndexes(Integer... columns) {
		return setFieldSet(new FieldIndexSelector(), columns);
	}

}
