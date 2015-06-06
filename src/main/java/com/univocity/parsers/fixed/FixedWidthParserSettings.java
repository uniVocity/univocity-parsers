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
package com.univocity.parsers.fixed;

import java.util.*;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.input.*;

/**
 * This is the configuration class used by the Fixed-Width parser ({@link FixedWidthParser})
 *
 * <p>In addition to the configuration options provided by {@link CommonParserSettings}, the FixedWidthParserSettings include:
 *
 * <ul>
 * 	<li><b>skipTrailingCharsUntilNewline <i>(defaults to false)</i>:</b> Indicates whether or not any trailing characters beyond the record's length should be skipped until the newline is reached
 * 		<p>For example, if the record length is 5, but the row contains "12345678\n", then portion containing "678" will be discarded and not considered part of the next record </li>
 *  <li><b>recordEndsOnNewline <i>(defaults to false)</i>:</b> Indicates whether or not a record is considered parsed when a newline is reached.
 *  	<p>For example, if recordEndsOnNewline is set to true, then given a record of length 4, and the input "12\n3456", the parser will identify [12] and [3456]
 *  	<p>If recordEndsOnNewline is set to false, then given a record of length 4, and the input "12\n3456", the parser will identify a multi-line record [12\n3] and [456 ]</li>
 * </ul>
 *
 * <p> The FixedWidthParserSettings need a definition of the field lengths of each record in the input. This must provided using an instance of {@link FixedWidthFieldLengths}.
 *
 * @see com.univocity.parsers.fixed.FixedWidthParser
 * @see com.univocity.parsers.fixed.FixedWidthFormat
 * @see com.univocity.parsers.fixed.FixedWidthFieldLengths
 * @see com.univocity.parsers.common.CommonParserSettings
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class FixedWidthParserSettings extends CommonParserSettings<FixedWidthFormat> {

	protected boolean skipTrailingCharsUntilNewline = false;
	protected boolean recordEndsOnNewline = false;

	private final FixedWidthFieldLengths fieldLengths;
	private final Map<String, FixedWidthFieldLengths> lookaheadFormats = new HashMap<String, FixedWidthFieldLengths>();
	private final Map<String, FixedWidthFieldLengths> lookbehindFormats = new HashMap<String, FixedWidthFieldLengths>();

	/**
	 * You can only create an instance of this class by providing a definition of the field lengths of each record in the input.
	 * <p> This must provided using an instance of {@link FixedWidthFieldLengths}.
	 * @param fieldLengths the instance of {@link FixedWidthFieldLengths} which provides the lengths of each field in the fixed-width records to be parsed
	 * @see com.univocity.parsers.fixed.FixedWidthFieldLengths
	 */
	public FixedWidthParserSettings(FixedWidthFieldLengths fieldLengths) {
		if (fieldLengths == null) {
			throw new IllegalArgumentException("Field lengths cannot be null");
		}
		this.fieldLengths = fieldLengths;
		String[] names = fieldLengths.getFieldNames();
		if (names != null) {
			setHeaders(names);
		}
	}

	public FixedWidthParserSettings() {
		fieldLengths = null;
	}

	/**
	 * Returns the sequence of lengths to be read by the parser to form a record.
	 * @return the sequence of lengths to be read by the parser to form a record.
	 */
	int[] getFieldLengths() {
		if (fieldLengths == null) {
			return null;
		}
		return fieldLengths.getFieldLengths();
	}

	/**
	 * Indicates whether or not any trailing characters beyond the record's length should be skipped until the newline is reached (defaults to false)
	 * <p>For example, if the record length is 5, but the row contains "12345678\n", then the portion containing "678\n" will be discarded and not considered part of the next record
	 * @return returns true if any trailing characters beyond the record's length should be skipped until the newline is reached, false otherwise
	 */
	public boolean getSkipTrailingCharsUntilNewline() {
		return skipTrailingCharsUntilNewline;
	}

	/**
	 * Defines whether or not any trailing characters beyond the record's length should be skipped until the newline is reached (defaults to false)
	 * <p>For example, if the record length is 5, but the row contains "12345678\n", then the portion containing "678\n" will be discarded and not considered part of the next record
	 * @param skipTrailingCharsUntilNewline a flag indicating if any trailing characters beyond the record's length should be skipped until the newline is reached
	 */
	public void setSkipTrailingCharsUntilNewline(boolean skipTrailingCharsUntilNewline) {
		this.skipTrailingCharsUntilNewline = skipTrailingCharsUntilNewline;
	}

	/**
	 * Indicates whether or not a record is considered parsed when a newline is reached. Examples:
	 * <ul>
	 *  <li>Consider two records of length <b>4</b>, and the input <b>12\n3456</b></li>
	 * 	<li>When {@link FixedWidthParserSettings#recordEndsOnNewline} is set to true:  the first value will be read as <b>12</b> and the second <b>3456</b></li>
	 *  <li>When {@link FixedWidthParserSettings#recordEndsOnNewline} is set to false:  the first value will be read as <b>12\n3</b> and the second <b>456</b></li>
	 * </ul>
	 * <p><i>Defaults to false</i>
	 * @return true if a record should be considered parsed when a newline is reached; false otherwise
	 */
	public boolean getRecordEndsOnNewline() {
		return recordEndsOnNewline;
	}

	/**
	 * Defines whether or not a record is considered parsed when a newline is reached. Examples:
	 * <ul>
	 *  <li>Consider two records of length <b>4</b>, and the input <b>12\n3456</b></li>
	 * 	<li>When {@link FixedWidthParserSettings#recordEndsOnNewline} is set to true:  the first value will be read as <b>12</b> and the second <b>3456</b></li>
	 *  <li>When {@link FixedWidthParserSettings#recordEndsOnNewline} is set to false:  the first value will be read as <b>12\n3</b> and the second <b>456</b></li>
	 * </ul>
	 * @param recordEndsOnNewline a flag indicating whether or not a record is considered parsed when a newline is reached
	 */
	public void setRecordEndsOnNewline(boolean recordEndsOnNewline) {
		this.recordEndsOnNewline = recordEndsOnNewline;
	}

	/**
	 * Returns the default FixedWidthFormat configured to handle Fixed-Width inputs
	 * @return and instance of FixedWidthFormat configured to handle Fixed-Width inputs
	 */
	@Override
	protected FixedWidthFormat createDefaultFormat() {
		return new FixedWidthFormat();
	}

	/**
	 * Returns an instance of CharAppender with the configured limit of maximum characters per column and, default value used to represent a null value (when the String parsed from the input is empty), and the padding character to handle unwritten positions
	 *
	 * <p>This overrides the parent implementation to create a CharAppender capable of handling padding characters that represent unwritten positions.
	 *
	 * @return an instance of CharAppender with the configured limit of maximum characters per column and, default value used to represent a null value (when the String parsed from the input is empty), and the padding character to handle unwritten positions
	 */
	@Override
	protected CharAppender newCharAppender() {
		return new DefaultCharAppender(getMaxCharsPerColumn(), getNullValue(), getFormat().getPadding());
	}

	/**
	 * The maximum number of characters allowed for any given value being written/read. Used to avoid OutOfMemoryErrors (defaults to a minimum of 4096 characters).
	 *
	 *  <p> This overrides the parent implementation and calculates the absolute minimum number of characters required to store the values of a record
	 *  <p> If the sum of all field lengths is greater than the configured maximum number of characters per column, the calculated amount will be returned.
	 *
	 * @return The maximum number of characters allowed for any given value being written/read
	 */
	@Override
	public int getMaxCharsPerColumn() {
		int max = super.getMaxCharsPerColumn();

		int minimum = 0;
		for (int length : calculateMaxFieldLengths()) {
			//adding 2 to give room for line breaks in every record (e.g. "\r\n").
			minimum += length + 2;
		}

		return max > minimum ? max : minimum;
	}

	/**
	 *  Returns the hard limit of how many columns a record can have (defaults to a maximum of 512).
	 * 	You need this to avoid OutOfMemory errors in case of inputs that might be inconsistent with the format you are dealing width .
	 *
	 *  <p> This overrides the parent implementation and calculates the absolute minimum number of columns required to store the values of a record
	 *  <p> If the sum of all fields is greater than the configured maximum number columns, the calculated amount will be returned.
	 *
	 * @return The maximum number of columns a record can have.
	 */
	@Override
	public int getMaxColumns() {
		int max = super.getMaxColumns();
		int minimum = calculateMaxFieldLengths().length;
		return max > minimum ? max : minimum;
	}

	private int[] calculateMaxFieldLengths() {
		return Lookup.calculateMaxFieldLengths(fieldLengths, lookaheadFormats, lookbehindFormats);
	}

	Lookup[] getLookaheadFormats() {
		return Lookup.getLookupFormats(lookaheadFormats);
	}

	Lookup[] getLookbehindFormats() {
		return Lookup.getLookupFormats(lookbehindFormats);
	}

	public void addFormatForLookahead(String lookahead, FixedWidthFieldLengths lengths) {
		Lookup.registerLookahead(lookahead, lengths, lookaheadFormats);
	}

	public void addFormatForLookbehind(String lookbehind, FixedWidthFieldLengths lengths) {
		Lookup.registerLookbehind(lookbehind, lengths, lookbehindFormats);
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
		out.put("Skip trailing characters until new line", skipTrailingCharsUntilNewline);
		out.put("Record ends on new line", recordEndsOnNewline);
		out.put("Field lengths", fieldLengths == null ? "<null>" : fieldLengths.toString());
		out.put("Lookahead formats", lookaheadFormats);
		out.put("Lookbehind formats", lookbehindFormats);
	}
}
