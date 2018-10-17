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
package com.univocity.parsers.fixed;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.input.*;

import java.util.*;

/**
 * This is the configuration class used by the Fixed-Width parser ({@link FixedWidthParser})
 *
 * <p>In addition to the configuration options provided by {@link CommonParserSettings}, the FixedWidthParserSettings include:
 *
 * <ul>
 * <li><b>skipTrailingCharsUntilNewline <i>(defaults to {@code false})</i>:</b> Indicates whether or not any trailing characters beyond the record's length should be skipped until the newline is reached
 * <p>For example, if the record length is 5, but the row contains "12345678\n", then portion containing "678" will be discarded and not considered part of the next record </li>
 * <li><b>recordEndsOnNewline <i>(defaults to {@code false})</i>:</b> Indicates whether or not a record is considered parsed when a newline is reached.
 * <p>For example, if recordEndsOnNewline is set to true, then given a record of length 4, and the input "12\n3456", the parser will identify [12] and [3456]
 * <p>If recordEndsOnNewline is set to false, then given a record of length 4, and the input "12\n3456", the parser will identify a multi-line record [12\n3] and [456 ]</li>
 * </ul>
 *
 * <p> The FixedWidthParserSettings need a definition of the field lengths of each record in the input. This must provided using an instance of {@link FixedWidthFields}.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.fixed.FixedWidthParser
 * @see com.univocity.parsers.fixed.FixedWidthFormat
 * @see FixedWidthFields
 * @see com.univocity.parsers.common.CommonParserSettings
 */
public class FixedWidthParserSettings extends CommonParserSettings<FixedWidthFormat> {

	protected boolean skipTrailingCharsUntilNewline = false;
	protected boolean recordEndsOnNewline = false;
	private boolean useDefaultPaddingForHeaders = true;
	private boolean keepPadding = false;

	private FixedWidthFields fieldLengths;
	private Map<String, FixedWidthFields> lookaheadFormats = new HashMap<String, FixedWidthFields>();
	private Map<String, FixedWidthFields> lookbehindFormats = new HashMap<String, FixedWidthFields>();

	/**
	 * You can only create an instance of this class by providing a definition of the field lengths of each record in the input.
	 * <p> This must provided using an instance of {@link FixedWidthFields}.
	 *
	 * @param fieldLengths the instance of {@link FixedWidthFields} which provides the lengths of each field in the fixed-width records to be parsed
	 *
	 * @see FixedWidthFields
	 */
	public FixedWidthParserSettings(FixedWidthFields fieldLengths) {
		if (fieldLengths == null) {
			throw new IllegalArgumentException("Field lengths cannot be null");
		}
		this.fieldLengths = fieldLengths;
		String[] names = fieldLengths.getFieldNames();
		if (names != null) {
			setHeaders(names);
		}
	}

	/**
	 * Creates a basic configuration object for the Fixed-Width parser with no field length configuration.
	 * This constructor is intended to be used when the record length varies depending of the input row.
	 * Refer to {@link #addFormatForLookahead(String, FixedWidthFields)}, {@link #addFormatForLookbehind(String, FixedWidthFields)}
	 */
	public FixedWidthParserSettings() {
		fieldLengths = null;
	}

	/**
	 * Returns the sequence of lengths to be read by the parser to form a record.
	 *
	 * @return the sequence of lengths to be read by the parser to form a record.
	 */
	int[] getFieldLengths() {
		if (fieldLengths == null) {
			return null;
		}
		return fieldLengths.getFieldLengths();
	}

	int[] getAllLengths() {
		if (fieldLengths == null) {
			return null;
		}
		return fieldLengths.getAllLengths();
	}

	/**
	 * Returns the sequence of paddings used by each field of each record.
	 *
	 * @return the sequence of paddings used by each field of each record.
	 */
	char[] getFieldPaddings() {
		if (fieldLengths == null) {
			return null;
		}
		return fieldLengths.getFieldPaddings(this.getFormat());
	}

	/**
	 * Returns the sequence of fields to ignore.
	 *
	 * @return the sequence of fields to ignore.
	 */
	boolean[] getFieldsToIgnore() {
		if (fieldLengths == null) {
			return null;
		}
		return fieldLengths.getFieldsToIgnore();
	}


	/**
	 * Returns the sequence of alignments to consider for each field of each record.
	 *
	 * @return the sequence of alignments to consider for each field of each record.
	 */
	FieldAlignment[] getFieldAlignments() {
		if (fieldLengths == null) {
			return null;
		}
		return fieldLengths.getFieldAlignments();
	}

	/**
	 * Indicates whether or not any trailing characters beyond the record's length should be skipped until the newline is reached (defaults to {@code false})
	 * <p>For example, if the record length is 5, but the row contains "12345678\n", then the portion containing "678\n" will be discarded and not considered part of the next record
	 *
	 * @return returns true if any trailing characters beyond the record's length should be skipped until the newline is reached, false otherwise
	 */
	public boolean getSkipTrailingCharsUntilNewline() {
		return skipTrailingCharsUntilNewline;
	}

	/**
	 * Defines whether or not any trailing characters beyond the record's length should be skipped until the newline is reached (defaults to {@code false})
	 * <p>For example, if the record length is 5, but the row contains "12345678\n", then the portion containing "678\n" will be discarded and not considered part of the next record
	 *
	 * @param skipTrailingCharsUntilNewline a flag indicating if any trailing characters beyond the record's length should be skipped until the newline is reached
	 */
	public void setSkipTrailingCharsUntilNewline(boolean skipTrailingCharsUntilNewline) {
		this.skipTrailingCharsUntilNewline = skipTrailingCharsUntilNewline;
	}

	/**
	 * Indicates whether or not a record is considered parsed when a newline is reached. Examples:
	 * <ul>
	 * <li>Consider two records of length <b>4</b>, and the input <b>12\n3456</b></li>
	 * <li>When {@link FixedWidthParserSettings#recordEndsOnNewline} is set to true:  the first value will be read as <b>12</b> and the second <b>3456</b></li>
	 * <li>When {@link FixedWidthParserSettings#recordEndsOnNewline} is set to false:  the first value will be read as <b>12\n3</b> and the second <b>456</b></li>
	 * </ul>
	 * <p><i>defaults to {@code false}</i>
	 *
	 * @return true if a record should be considered parsed when a newline is reached; false otherwise
	 */
	public boolean getRecordEndsOnNewline() {
		return recordEndsOnNewline;
	}

	/**
	 * Defines whether or not a record is considered parsed when a newline is reached. Examples:
	 * <ul>
	 * <li>Consider two records of length <b>4</b>, and the input <b>12\n3456</b></li>
	 * <li>When {@link FixedWidthParserSettings#recordEndsOnNewline} is set to true:  the first value will be read as <b>12</b> and the second <b>3456</b></li>
	 * <li>When {@link FixedWidthParserSettings#recordEndsOnNewline} is set to false:  the first value will be read as <b>12\n3</b> and the second <b>456</b></li>
	 * </ul>
	 *
	 * @param recordEndsOnNewline a flag indicating whether or not a record is considered parsed when a newline is reached
	 */
	public void setRecordEndsOnNewline(boolean recordEndsOnNewline) {
		this.recordEndsOnNewline = recordEndsOnNewline;
	}

	/**
	 * Returns the default FixedWidthFormat configured to handle Fixed-Width inputs
	 *
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
		return new DefaultCharAppender(getMaxCharsPerColumn(), getNullValue(), getWhitespaceRangeStart());
	}

	/**
	 * The maximum number of characters allowed for any given value being written/read. Used to avoid OutOfMemoryErrors (defaults to a minimum of 4096 characters).
	 *
	 * <p> This overrides the parent implementation and calculates the absolute minimum number of characters required to store the values of a record
	 * <p> If the sum of all field lengths is greater than the configured maximum number of characters per column, the calculated amount will be returned.
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
	 * Returns the hard limit of how many columns a record can have (defaults to a maximum of 512).
	 * You need this to avoid OutOfMemory errors in case of inputs that might be inconsistent with the format you are dealing with.
	 *
	 * <p> This overrides the parent implementation and calculates the absolute minimum number of columns required to store the values of a record
	 * <p> If the sum of all fields is greater than the configured maximum number columns, the calculated amount will be returned.
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
		return Lookup.getLookupFormats(lookaheadFormats, getFormat());
	}

	Lookup[] getLookbehindFormats() {
		return Lookup.getLookupFormats(lookbehindFormats, getFormat());
	}

	/**
	 * Defines the format of records identified by a lookahead symbol.
	 *
	 * @param lookahead the lookahead value that when found in the input,
	 *                  will notify the parser to switch to a new record format, with different field lengths
	 * @param lengths   the field lengths of the record format identified by the given lookahead symbol.
	 */
	public void addFormatForLookahead(String lookahead, FixedWidthFields lengths) {
		Lookup.registerLookahead(lookahead, lengths, lookaheadFormats);
	}

	/**
	 * Defines the format of records identified by a lookbehind symbol.
	 *
	 * @param lookbehind the lookbehind value that when found in the previous input row,
	 *                   will notify the parser to switch to a new record format, with different field lengths
	 * @param lengths    the field lengths of the record format identified by the given lookbehind symbol.
	 */
	public void addFormatForLookbehind(String lookbehind, FixedWidthFields lengths) {
		Lookup.registerLookbehind(lookbehind, lengths, lookbehindFormats);
	}

	/**
	 * Indicates whether headers should be parsed using the default padding specified in {@link FixedWidthFormat#getPadding()}
	 * instead of any custom padding associated with a given field (in {@link FixedWidthFields#setPadding(char, int...)})
	 * Defaults to {@code true}
	 *
	 * @return {@code true} if the default padding is to be used when reading headers, otherwise {@code false}
	 */
	public boolean getUseDefaultPaddingForHeaders() {
		return useDefaultPaddingForHeaders;
	}

	/**
	 * Defines whether headers should be parsed using the default padding specified in {@link FixedWidthFormat#getPadding()}
	 * instead of any custom padding associated with a given field (in {@link FixedWidthFields#setPadding(char, int...)})
	 *
	 * @param useDefaultPaddingForHeaders flag indicating whether the default padding is to be used when parsing headers
	 */
	public void setUseDefaultPaddingForHeaders(boolean useDefaultPaddingForHeaders) {
		this.useDefaultPaddingForHeaders = useDefaultPaddingForHeaders;
	}

	@Override
	protected void configureFromAnnotations(Class<?> beanClass) {
		if (fieldLengths == null) {
			try {
				fieldLengths = FixedWidthFields.forParsing(beanClass);
				Headers headerAnnotation = AnnotationHelper.findHeadersAnnotation(beanClass);

				if (headerExtractionEnabled == null && headerAnnotation != null) {
					setHeaderExtractionEnabled(headerAnnotation.extract());
				}
			} catch (IllegalArgumentException e) {
				throw e;
			} catch (Exception ex) {
				//ignore.
			}
		}

		if (headerExtractionEnabled == null) {
			setHeaderExtractionEnabled(false);
		}

		super.configureFromAnnotations(beanClass);

		if (!isHeaderExtractionEnabled()) {
			FixedWidthFields.setHeadersIfPossible(fieldLengths, this);
		}
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

	/**
	 * Clones this configuration object to reuse all user-provided settings, including the fixed-width field configuration.
	 *
	 * @return a copy of all configurations applied to the current instance.
	 */
	@Override
	public final FixedWidthParserSettings clone() {
		return (FixedWidthParserSettings) super.clone();
	}

	/**
	 * Clones this configuration object to reuse most user-provided settings. This includes the fixed-width field configuration,
	 * but doesn't include other input-specific settings. This method is meant to be used internally only.
	 *
	 * @return a copy of all configurations applied to the current instance.
	 *
	 * @deprecated doesn't really make sense for fixed-width. Use alternative method {@link #clone(FixedWidthFields)}.
	 */
	@Deprecated
	protected final FixedWidthParserSettings clone(boolean clearInputSpecificSettings) {
		return clone(clearInputSpecificSettings, fieldLengths == null ? null : fieldLengths.clone());
	}

	/**
	 * Clones this configuration object to reuse most user-provided settings. Properties that are specific to a given
	 * input (such as header names and selection of fields) will be reset to their defaults.
	 *
	 * To obtain a full copy, use {@link #clone()}.
	 *
	 * @param fields the fixed-width field configuration to be used by the cloned settings object.
	 *
	 * @return a copy of the <em>general</em> configurations applied to the current instance.
	 */
	public final FixedWidthParserSettings clone(FixedWidthFields fields) {
		return clone(true, fields);
	}

	private FixedWidthParserSettings clone(boolean clearInputSpecificSettings, FixedWidthFields fields) {
		FixedWidthParserSettings out = (FixedWidthParserSettings) super.clone(clearInputSpecificSettings);
		out.fieldLengths = fields;

		if (clearInputSpecificSettings) {
			out.lookaheadFormats = new HashMap<String, FixedWidthFields>();
			out.lookbehindFormats = new HashMap<String, FixedWidthFields>();
		} else {
			out.lookaheadFormats = new HashMap<String, FixedWidthFields>(this.lookaheadFormats);
			out.lookbehindFormats = new HashMap<String, FixedWidthFields>(this.lookbehindFormats);
		}
		return out;
	}

	/**
	 * Indicate the padding character should be kept in the parsed value
	 *
	 * <i>(defaults to {@code false})</i>
	 *
	 * This setting can be overridden for individual fields through
	 * {@link FixedWidthFields#stripPaddingFrom(String, String...)} and
	 * {@link FixedWidthFields#keepPaddingOn(String, String...)}
	 *
	 * @return flag indicating the padding character should be kept in the parsed value
	 */
	public final boolean getKeepPadding() {
		return keepPadding;
	}

	/**
	 * Configures the fixed-width parser to retain the padding character in any parsed values
	 *
	 * <i>(defaults to {@code false})</i>
	 *
	 * This setting can be overridden for individual fields through
	 * {@link FixedWidthFields#stripPaddingFrom(String, String...)} and
	 * {@link FixedWidthFields#keepPaddingOn(String, String...)}
	 *
	 * @param keepPadding flag indicating the padding character should be kept in the parsed value
	 */
	public final void setKeepPadding(boolean keepPadding) {
		this.keepPadding = keepPadding;
	}

	/**
	 * Returns the sequence of fields whose padding character must/must not be retained in the parsed value
	 * @return the sequence that have an explicit 'keepPadding' flag.
	 */
	Boolean[] getKeepPaddingFlags() {
		if (fieldLengths == null) {
			return null;
		}
		Boolean[] keepFlags = fieldLengths.getKeepPaddingFlags();
		Boolean[] out = new Boolean[keepFlags.length];
		Arrays.fill(out, getKeepPadding());
		for (int i = 0; i < keepFlags.length; i++) {
			Boolean flag = keepFlags[i];
			if (flag != null) {
				out[i] = flag;
			}
		}
		return out;
	}

}
