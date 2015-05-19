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

/**
 * This is the configuration class used by the CSV writer ({@link CsvWriter})
 *
 * <p>In addition to the configuration options provided by {@link CommonWriterSettings}, the CsvWriterSettings include:
 *
 * <ul>
 * 	<li><b>emptyValue <i>(defaults to null)</i>:</b> Defines a replacement string to signify an empty value (which is not a null value)
 *   <p>If the writer has an empty String to write to the output, the emptyValue is used instead of an empty string</li>
 *  <li><b>quoteAllFields <i>(defaults to false)</i>:</b> By default, only values that contain a field separator are enclosed within quotes.
 *   <p>If this property is set to true, this indicates that all written values should be enclosed within quotes (as defined in {@link CsvFormat})</li>
 * </ul>
 *
 * @see com.univocity.parsers.csv.CsvWriter
 * @see com.univocity.parsers.csv.CsvFormat
 * @see com.univocity.parsers.common.CommonWriterSettings
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class CsvWriterSettings extends CommonWriterSettings<CsvFormat> {

	private boolean escapeUnquotedValues = false;
	private boolean quoteAllFields = false;
	private boolean isInputEscaped = false;

	/**
	 * Indicates that all written values should be enclosed within quotes (as defined in {@link CsvFormat})
	 * <p> (Defaults to false)
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
	 * @return a flag indicating whether the escape sequences should not be introduced by the writer.
	 */
	public final boolean isInputEscaped() {
		return isInputEscaped;
	}

	/**
	 * Configures the writer to prevent it to introduce escape sequences.  The writer will assume the user is providing escaped input, and it will be written as-is.
	 * <p><strong>Warning!</strong> ensure your data is properly escaped, otherwise the writer will produce invalid CSV.</p>
	 * <p>This is disabled by default</p>
	 * @param isInputEscaped a flag indicating whether the input that will be written is already properly escaped.
	 */
	public final void setInputEscaped(boolean isInputEscaped) {
		this.isInputEscaped = isInputEscaped;
	}

	/**
	 * Returns the default CsvFormat configured to produce CSV outputs compliant to the <a href="http://tools.ietf.org/html/rfc4180">RFC4180</a> standard.
	 * @return and instance of CsvFormat configured to produce CSV outputs compliant to the <a href="http://tools.ietf.org/html/rfc4180">RFC4180</a> standard.
	 */
	@Override
	protected CsvFormat createDefaultFormat() {
		return new CsvFormat();
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
		out.put("Quote all fields", quoteAllFields);
		out.put("Escape unquoted values", escapeUnquotedValues);
	}
}
