/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
import com.univocity.parsers.common.Format;

import java.util.*;

/**
 * This is the configuration class used by the Fixed-Width writer ({@link FixedWidthWriter})
 *
 * <p>The FixedWidthWriterSettings provides all configuration options in {@link CommonWriterSettings} and currently does not require any additional setting.
 *
 * <p> The FixedWidthParserSettings requires a definition of the field lengths of each record in the input. This must provided using an instance of {@link FixedWidthFields}.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.fixed.FixedWidthWriter
 * @see com.univocity.parsers.fixed.FixedWidthFormat
 * @see FixedWidthFields
 * @see com.univocity.parsers.common.CommonWriterSettings
 */
public class FixedWidthWriterSettings extends CommonWriterSettings<FixedWidthFormat> {

	private FixedWidthFields fieldLengths;
	private Map<String, FixedWidthFields> lookaheadFormats = new HashMap<String, FixedWidthFields>();
	private Map<String, FixedWidthFields> lookbehindFormats = new HashMap<String, FixedWidthFields>();
	private boolean useDefaultPaddingForHeaders = true;
	private FieldAlignment defaultAlignmentForHeaders = null;
	private boolean writeLineSeparatorAfterRecord = true;

	/**
	 * You can only create an instance of this class by providing a definition of the field lengths of each record in the input.
	 * <p> This must provided using an instance of {@link FixedWidthFields}.
	 *
	 * @param fieldLengths the instance of {@link FixedWidthFields} which provides the lengths of each field in the fixed-width records to be parsed
	 *
	 * @see FixedWidthFields
	 */
	public FixedWidthWriterSettings(FixedWidthFields fieldLengths) {
		setFieldLengths(fieldLengths);
		String[] names = fieldLengths.getFieldNames();
		if (names != null) {
			setHeaders(names);
		}
	}

	/**
	 * Creates a basic configuration object for the Fixed-Width writer with no field length configuration.
	 * This constructor is intended to be used when the record length varies depending of the input row.
	 * Refer to {@link #addFormatForLookahead(String, FixedWidthFields)}, {@link #addFormatForLookbehind(String, FixedWidthFields)}
	 */
	public FixedWidthWriterSettings() {
		this.fieldLengths = null;
	}

	final void setFieldLengths(FixedWidthFields fieldLengths) {
		if (fieldLengths == null) {
			throw new IllegalArgumentException("Field lengths cannot be null");
		}
		this.fieldLengths = fieldLengths;
	}

	/**
	 * Returns the sequence of field lengths to be written to form a record.
	 *
	 * @return the sequence of field lengths to be written to form a record.
	 */
	int[] getFieldLengths() {
		if (fieldLengths == null) {
			return null;
		}
		return fieldLengths.getFieldLengths();
	}

	/**
	 * Returns the sequence of field alignments to apply to each field in the record.
	 *
	 * @return the sequence of field alignments to apply to each field in the record.
	 */
	FieldAlignment[] getFieldAlignments() {
		if (fieldLengths == null) {
			return null;
		}
		return fieldLengths.getFieldAlignments();
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
		return fieldLengths.getFieldPaddings(getFormat());
	}

	/**
	 * Returns the default FixedWidthFormat configured to handle Fixed-Width outputs
	 *
	 * @return and instance of FixedWidthFormat configured to handle Fixed-Width outputs
	 */
	@Override
	protected FixedWidthFormat createDefaultFormat() {
		return new FixedWidthFormat();
	}

	@Override
	public int getMaxColumns() {
		int max = super.getMaxColumns();
		int minimum = Lookup.calculateMaxFieldLengths(fieldLengths, lookaheadFormats, lookbehindFormats).length;
		return max > minimum ? max : minimum;
	}

	/**
	 * Defines the format of records identified by a lookahead symbol.
	 *
	 * @param lookahead the lookahead value that when found in the output row,
	 *                  will notify the writer to switch to a new record format, with different field lengths
	 * @param lengths   the field lengths of the record format identified by the given lookahead symbol.
	 */
	public void addFormatForLookahead(String lookahead, FixedWidthFields lengths) {
		Lookup.registerLookahead(lookahead, lengths, lookaheadFormats);
	}

	/**
	 * Defines the format of records identified by a lookbehind symbol.
	 *
	 * @param lookbehind the lookbehind value that when present in the previous output row,
	 *                   will notify the writer to switch to a new record format, with different field lengths
	 * @param lengths    the field lengths of the record format identified by the given lookbehind symbol.
	 */
	public void addFormatForLookbehind(String lookbehind, FixedWidthFields lengths) {
		Lookup.registerLookbehind(lookbehind, lengths, lookbehindFormats);
	}

	Lookup[] getLookaheadFormats() {
		return Lookup.getLookupFormats(lookaheadFormats, getFormat());
	}

	Lookup[] getLookbehindFormats() {
		return Lookup.getLookupFormats(lookbehindFormats, getFormat());
	}

	/**
	 * Indicates whether headers should be written using the default padding specified in {@link FixedWidthFormat#getPadding()}
	 * instead of any custom padding associated with a given field (in {@link FixedWidthFields#setPadding(char, int...)})
	 * Defaults to {@code true}
	 *
	 * @return {@code true} if the default padding is to be used when writing headers, otherwise {@code false}
	 */
	public boolean getUseDefaultPaddingForHeaders() {
		return useDefaultPaddingForHeaders;
	}

	/**
	 * Defines whether headers should be written using the default padding specified in {@link FixedWidthFormat#getPadding()}
	 * instead of any custom padding associated with a given field (in {@link FixedWidthFields#setPadding(char, int...)})
	 *
	 * @param useDefaultPaddingForHeaders flag indicating whether the default padding is to be used when writing headers
	 */
	public void setUseDefaultPaddingForHeaders(boolean useDefaultPaddingForHeaders) {
		this.useDefaultPaddingForHeaders = useDefaultPaddingForHeaders;
	}

	/**
	 * Returns the default alignment to use when writing headers. If none is specified (i.e. {@code null}), the headers will be aligned
	 * according to the corresponding field alignment in {@link FixedWidthFields#getAlignment(String)}.
	 *
	 * Defaults to {@code null}.
	 *
	 * @return the default alignment for headers, or {@code null} if the headers should be aligned according to the column alignment.
	 */
	public FieldAlignment getDefaultAlignmentForHeaders() {
		return defaultAlignmentForHeaders;
	}

	/**
	 * Defines the default alignment to use when writing headers. If none is specified (i.e. {@code null}), the headers will be aligned
	 * according to the corresponding field alignment in {@link FixedWidthFields#getAlignment(String)}.
	 *
	 * Defaults to {@code null}.
	 *
	 * @param defaultAlignmentForHeaders the alignment to use when writing headers. {@code null} indicates that headers
	 *                                   should be aligned according to the column alignment.
	 */
	public void setDefaultAlignmentForHeaders(FieldAlignment defaultAlignmentForHeaders) {
		this.defaultAlignmentForHeaders = defaultAlignmentForHeaders;
	}

	/**
	 * Returns a flag indicating whether each record, when written, should be followed by a line separator (as specified in {@link Format#getLineSeparator()}.
	 *
	 * Consider the records {@code [a,b]} and {@code [c,d]}, with field lengths {@code [2, 2]}, and line separator = {@code \n}:
	 * <ul>
	 * <li>When {@link #getWriteLineSeparatorAfterRecord()} is enabled, the output will be written as: {@code a b \nc d \n}</li>
	 * <li>When {@link #getWriteLineSeparatorAfterRecord()} is disabled, the output will be written as: {@code a b c d }</li>
	 * </ul>
	 *
	 * Defaults to {@code true}.
	 *
	 * @return whether the writer should add a line separator after a record is written to the output.
	 */
	public boolean getWriteLineSeparatorAfterRecord() {
		return writeLineSeparatorAfterRecord;
	}

	/**
	 * Defines whether each record, when written, should be followed by a line separator (as specified in {@link Format#getLineSeparator()}.
	 *
	 * Consider the records {@code [a,b]} and {@code [c,d]}, with field lengths {@code [2, 2]}, and line separator = {@code \n}:
	 * <ul>
	 * <li>When {@link #getWriteLineSeparatorAfterRecord()} is enabled, the output will be written as: {@code a b \nc d \n}</li>
	 * <li>When {@link #getWriteLineSeparatorAfterRecord()} is disabled, the output will be written as: {@code a b c d }</li>
	 * </ul>
	 *
	 * Defaults to {@code true}.
	 *
	 * @param writeLineSeparatorAfterRecord flag indicating whether the writer should add a line separator after a record is written to the output.
	 */
	public void setWriteLineSeparatorAfterRecord(boolean writeLineSeparatorAfterRecord) {
		this.writeLineSeparatorAfterRecord = writeLineSeparatorAfterRecord;
	}

	@Override
	protected void configureFromAnnotations(Class<?> beanClass) {
		if (fieldLengths != null) {
			return;
		}

		try {
			fieldLengths = new FixedWidthFields(beanClass);
			Headers headerAnnotation = AnnotationHelper.findHeadersAnnotation(beanClass);
			setHeaderWritingEnabled(headerAnnotation != null && headerAnnotation.write());
		} catch (Exception ex) {
			//ignore.
		}
		super.configureFromAnnotations(beanClass);

		FixedWidthFields.setHeadersIfPossible(fieldLengths, this);
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
		out.put("Write line separator after record", writeLineSeparatorAfterRecord);
		out.put("Field lengths", fieldLengths);
		out.put("Lookahead formats", lookaheadFormats);
		out.put("Lookbehind formats", lookbehindFormats);
		out.put("Use default padding for headers", useDefaultPaddingForHeaders);
		out.put("Default alignment for headers", defaultAlignmentForHeaders);
	}

	/**
	 * Clones this configuration object to reuse all user-provided settings, including the fixed-width field configuration.
	 *
	 * @return a copy of all configurations applied to the current instance.
	 */
	@Override
	public final FixedWidthWriterSettings clone() {
		return (FixedWidthWriterSettings) super.clone(false);
	}

	/**
	 * Clones this configuration object to reuse most user-provided settings. This includes the fixed-width field configuration,
	 * but doesn't include other input-specific settings. This method is meant to be used internally only.
	 *
	 * @return a copy of all configurations applied to the current instance.
	 *
	 * @deprecated doesn't really make sense for fixed-width. . Use alternative method {@link #clone(FixedWidthFields)}.
	 */
	@Deprecated
	protected final FixedWidthWriterSettings clone(boolean clearInputSpecificSettings) {
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
	public final FixedWidthWriterSettings clone(FixedWidthFields fields) {
		return clone(true, fields);
	}

	private FixedWidthWriterSettings clone(boolean clearInputSpecificSettings, FixedWidthFields fields) {
		FixedWidthWriterSettings out = (FixedWidthWriterSettings) super.clone(clearInputSpecificSettings);
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
}
