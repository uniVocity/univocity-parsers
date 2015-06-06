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

/**
 * This is the configuration class used by the Fixed-Width writer ({@link FixedWidthWriter})
 *
 * <p>The FixedWidthWriterSettings provides all configuration options in {@link CommonWriterSettings} and currently does not require any additional setting.
 *
 * <p> The FixedWidthParserSettings requires a definition of the field lengths of each record in the input. This must provided using an instance of {@link FixedWidthFieldLengths}.
 *
 * @see com.univocity.parsers.fixed.FixedWidthWriter
 * @see com.univocity.parsers.fixed.FixedWidthFormat
 * @see com.univocity.parsers.fixed.FixedWidthFieldLengths
 * @see com.univocity.parsers.common.CommonWriterSettings
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class FixedWidthWriterSettings extends CommonWriterSettings<FixedWidthFormat> {

	private final FixedWidthFieldLengths fieldLengths;
	private final Map<String, FixedWidthFieldLengths> lookaheadFormats = new HashMap<String, FixedWidthFieldLengths>();
	private final Map<String, FixedWidthFieldLengths> lookbehindFormats = new HashMap<String, FixedWidthFieldLengths>();

	/**
	 * You can only create an instance of this class by providing a definition of the field lengths of each record in the input.
	 * <p> This must provided using an instance of {@link FixedWidthFieldLengths}.
	 * @param fieldLengths the instance of {@link FixedWidthFieldLengths} which provides the lengths of each field in the fixed-width records to be parsed
	 * @see com.univocity.parsers.fixed.FixedWidthFieldLengths
	 */
	public FixedWidthWriterSettings(FixedWidthFieldLengths fieldLengths) {
		if (fieldLengths == null) {
			throw new IllegalArgumentException("Field lengths cannot be null");
		}
		this.fieldLengths = fieldLengths;
		String[] names = fieldLengths.getFieldNames();
		if (names != null) {
			setHeaders(names);
		}
	}

	public FixedWidthWriterSettings() {
		this.fieldLengths = null;
	}

	/**
	 * Returns the sequence of field lengths to be written to form a record.
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
	 * @return the sequence of field alignments to apply to each field in the record.
	 */
	FieldAlignment[] getFieldAlignments() {
		if (fieldLengths == null) {
			return null;
		}
		return fieldLengths.getFieldAlignments();
	}

	/**
	 * Returns the default FixedWidthFormat configured to handle Fixed-Width outputs
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

	public void addFormatForLookahead(String lookahead, FixedWidthFieldLengths lengths) {
		Lookup.registerLookahead(lookahead, lengths, lookaheadFormats);
	}

	public void addFormatForLookbehind(String lookbehind, FixedWidthFieldLengths lengths) {
		Lookup.registerLookbehind(lookbehind, lengths, lookbehindFormats);
	}

	Lookup[] getLookaheadFormats() {
		return Lookup.getLookupFormats(lookaheadFormats);
	}

	Lookup[] getLookbehindFormats() {
		return Lookup.getLookupFormats(lookbehindFormats);
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
		out.put("Field lengths", fieldLengths);
		out.put("Lookahead formats", lookaheadFormats);
		out.put("Lookbehind formats", lookbehindFormats);
	}
}
