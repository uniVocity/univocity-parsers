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
package com.univocity.parsers.common;

import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.common.input.*;

import java.util.*;

/**
 * The ParserOutput is the component that manages records parsed by {@link AbstractParser} and their values.
 *
 * It is solely responsible for deciding when:
 * <ul>
 * <li>parsed records should be reordered according to the fields selected in {@link CommonSettings}</li>
 * <li>characters and values parsed in {@link AbstractParser#parseRecord()} should be retained or discarded</li>
 * <li>input headers should be loaded from the records parsed in {@link AbstractParser#parseRecord()} or from {@link CommonSettings#getHeaders()}</li>
 * </ul>
 *
 * Implementations of this class are made available to concrete parser implementations of {@link AbstractParser}.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see AbstractParser
 * @see CommonSettings
 */
public class ParserOutput {

	/**
	 * Keeps track of the current column being parsed in the input.
	 * Calls to {@link ParserOutput#valueParsed} and  {@link ParserOutput#emptyParsed} will increase the column count.
	 * This value is reset to zero after a row is parsed.
	 */
	protected int column = 0;

	/**
	 * Stores the values parsed for a record.
	 */
	protected final String[] parsedValues;

	/**
	 * <p>Stores (shared) references to {@link CharAppender} for each potential column (as given by {@link CommonSettings#getMaxColumns()}).
	 * <p>Fields that are not selected will receive an instance of {@link NoopCharAppender} so all parser calls in {@link AbstractParser#parseRecord()} to {@link ParserOutput#appender} will do nothing.
	 * <p>Selected fields (given by {@link CommonParserSettings}) will receive a functional {@link CharAppender}.
	 */
	private final CharAppender[] appenders;

	protected final CommonParserSettings<?> settings;
	private final boolean skipEmptyLines;
	private final String nullValue;

	/**
	 * <p>The appender available to parsers for accumulating characters read from the input.
	 * <p>This attribute is assigned to different instances of CharAppender during parsing process, namely,
	 * a (potentially) different CharAppender for each parsed column, taken from
	 * {@link ParserOutput#appenders}[{@link ParserOutput#column}]
	 */
	public CharAppender appender;

	private final CharAppender appenderInstance;
	private boolean columnsToExtractInitialized;
	private boolean columnsReordered;
	private boolean columnReorderingEnabledSetting;

	private String[] headerStrings;
	private NormalizedString[] headers;
	private int[] selectedIndexes;

	private long currentRecord;

	public boolean trim = false;
	public final Deque<String[]> pendingRecords = new LinkedList<String[]>();

	/**
	 * Headers parsed from the input when {@link CommonParserSettings#headerExtractionEnabled} is {@code true},
	 * irrespective of any user-provided headers in {@link CommonParserSettings#getHeaders()}
	 */
	String[] parsedHeaders;

	private final AbstractParser<?> parser;

	/**
	 * Initializes the ParserOutput with the configuration specified in {@link CommonParserSettings}
	 *
	 * @param settings the parser configuration
	 */
	public ParserOutput(CommonParserSettings<?> settings) {
		this(null, settings);
	}

	/**
	 * Initializes the ParserOutput with the configuration specified in {@link CommonParserSettings}
	 *
	 * @param parser   the parser whose output will be managed by this class.
	 * @param settings the parser configuration
	 */
	public ParserOutput(AbstractParser<?> parser, CommonParserSettings<?> settings) {
		this.parser = parser;
		this.appenderInstance = settings.newCharAppender();
		this.appender = appenderInstance;
		this.parsedValues = new String[settings.getMaxColumns()];
		this.appenders = new CharAppender[settings.getMaxColumns() + 1];
		Arrays.fill(appenders, appender);
		this.settings = settings;
		this.skipEmptyLines = settings.getSkipEmptyLines();
		this.nullValue = settings.getNullValue();
		this.columnsToExtractInitialized = false;
		this.currentRecord = 0;
		if (settings.isHeaderExtractionEnabled() && parser != null) {
			parser.ignoreTrailingWhitespace = false;
			parser.ignoreLeadingWhitespace = false;
		}
		if (settings.getHeaders() != null) {
			initializeHeaders();
		}
		this.columnReorderingEnabledSetting = settings.isColumnReorderingEnabled();
	}

	protected void initializeHeaders() {
		columnsReordered = false;
		selectedIndexes = null;
		this.appender = appenderInstance;
		Arrays.fill(appenders, appender);

		if (column > 0) { //we only initialize headers from a parsed row if it is not empty
			parsedHeaders = new String[column];
			System.arraycopy(parsedValues, 0, parsedHeaders, 0, column);
		}

		boolean usingParsedHeaders = false;
		this.headers = NormalizedString.toIdentifierGroupArray(settings.getHeaders());
		if (headers != null) {
			headers = headers.clone();
		} else if (column > 0) { //we only initialize headers from a parsed row if it is not empty
			headers = NormalizedString.toIdentifierGroupArray(parsedHeaders.clone());
			usingParsedHeaders = true;
		}

		if (parser != null) {
			parser.ignoreTrailingWhitespace = settings.getIgnoreTrailingWhitespaces();
			parser.ignoreLeadingWhitespace = settings.getIgnoreLeadingWhitespaces();
			if (usingParsedHeaders) {
				parser.initialize();
			}
		}

		if (usingParsedHeaders) {
			for (int i = 0; i < headers.length; i++) {
				NormalizedString header = headers[i];
				if (header != null && !header.isLiteral()) {
					if (settings.getIgnoreLeadingWhitespaces()) {
						if (settings.getIgnoreTrailingWhitespaces()) {
							headers[i] = NormalizedString.valueOf(headers[i].toString().trim());
						} else {
							headers[i] = NormalizedString.valueOf(ArgumentUtils.trim(headers[i].toString(), true, false));
						}
					} else if (settings.getIgnoreTrailingWhitespaces()) {
						headers[i] = NormalizedString.valueOf(ArgumentUtils.trim(headers[i].toString(), false, true));
					}
				}
			}
		}

		columnsToExtractInitialized = true;
		initializeColumnsToExtract(headers);
	}

	/**
	 * Gets all values parsed in the {@link ParserOutput#parsedValues} array
	 *
	 * @return the sequence of parsed values in a record.
	 */
	public String[] rowParsed() {
		if (!pendingRecords.isEmpty()) {
			return pendingRecords.poll();
		}
		// some values were parsed. Let's return them
		if (column > 0) {
			// identifies selected columns and headers (in the first non-empty row)
			if (!columnsToExtractInitialized) {
				initializeHeaders();
				//skips the header row. We want to use the headers defined in the settings.
				if (settings.isHeaderExtractionEnabled()) {
					Arrays.fill(parsedValues, null);
					column = 0;
					this.appender = appenders[0];
					return null;
				} else if (!columnsReordered && selectedIndexes != null) {
					String[] out = new String[column];
					for (int i = 0; i < selectedIndexes.length; i++) {
						int index = selectedIndexes[i];
						if (index < column) {
							out[index] = parsedValues[index];
						}
					}
					column = 0;
					return out;
				}
			}

			currentRecord++;
			if (columnsReordered) {
				if (selectedIndexes.length == 0) {
					column = 0;
					return ArgumentUtils.EMPTY_STRING_ARRAY;
				}
				String[] reorderedValues = new String[selectedIndexes.length];
				for (int i = 0; i < selectedIndexes.length; i++) {
					int index = selectedIndexes[i];
					if (index >= column || index == -1) {
						reorderedValues[i] = nullValue;
					} else {
						reorderedValues[i] = parsedValues[index];
					}
				}
				column = 0;
				this.appender = appenders[0];
				return reorderedValues;
			} else {
				int last = columnReorderingEnabledSetting ? column : column < headers.length ? headers.length : column;

				String[] out = new String[last];
				System.arraycopy(parsedValues, 0, out, 0, column);
				column = 0;
				this.appender = appenders[0];
				return out;
			}
		} else if (!skipEmptyLines) { //no values were parsed, but we are not skipping empty lines
			if (!columnsToExtractInitialized) {
				initializeHeaders();
			}

			currentRecord++;

			if (columnsReordered) {
				if (selectedIndexes.length == 0) {
					return ArgumentUtils.EMPTY_STRING_ARRAY;
				}
				String[] out = new String[selectedIndexes.length];
				Arrays.fill(out, nullValue);
				return out;
			}

			return ArgumentUtils.EMPTY_STRING_ARRAY;
		}
		// no values were parsed and we do not care about empty lines.
		return null;
	}

	FieldSelector getFieldSelector() {
		return settings.getFieldSelector();
	}

	/**
	 * Initializes the sequence of selected fields, if any.
	 *
	 * @param values a sequence of values that represent the headers of the input. This can be either a parsed record or the headers as defined in {@link CommonSettings#getHeaders()}
	 */
	private void initializeColumnsToExtract(NormalizedString[] values) {
		FieldSelector selector = settings.getFieldSelector();
		if (selector != null) {
			selectedIndexes = selector.getFieldIndexes(values);

			if (selectedIndexes != null) {
				Arrays.fill(appenders, NoopCharAppender.getInstance());

				for (int i = 0; i < selectedIndexes.length; i++) {
					int index = selectedIndexes[i];
					if (index != -1) {
						appenders[index] = appender;
					}
				}

				columnsReordered = settings.isColumnReorderingEnabled();

				int length = values == null ? selectedIndexes.length : values.length;

				if (!columnsReordered && length < appenders.length && !(selector instanceof FieldIndexSelector)) {
					Arrays.fill(appenders, length, appenders.length, appender);
				}
				appender = appenders[0];
			}
		}
	}

	public String[] getHeaderAsStringArray() {
		if (headerStrings == null) {
			headerStrings = NormalizedString.toArray(getHeaders());
		}
		return headerStrings;
	}

	/**
	 * Returns the sequence of values that represent the headers each field in the input. This can be either a parsed record or the headers as defined in {@link CommonSettings#getHeaders()}
	 *
	 * @return the headers each field in the input
	 */
	public NormalizedString[] getHeaders() {
		if (parser != null) {
			parser.extractHeadersIfRequired();
		}
		if (this.headers == null) {
			this.headers = NormalizedString.toIdentifierGroupArray(settings.getHeaders());
		}
		return this.headers;
	}

	/**
	 * Returns the selected indexes of all fields as defined in {@link CommonSettings}. Null if no fields were selected.
	 *
	 * @return the selected indexes of all fields as defined in {@link CommonSettings}. Null if no fields were selected.
	 */
	public int[] getSelectedIndexes() {
		return this.selectedIndexes;
	}

	/**
	 * Indicates whether fields selected using the field selection methods (in {@link CommonSettings}) are being reordered.
	 *
	 * @return <p> false if no fields were selected or column reordering has been disabled in {@link CommonParserSettings#isColumnReorderingEnabled()}
	 * <p> true if fields were selected and column reordering has been enabled in {@link CommonParserSettings#isColumnReorderingEnabled()}
	 */
	public boolean isColumnReorderingEnabled() {
		return columnsReordered;
	}

	/**
	 * Returns the position of the current parsed value
	 *
	 * @return the position of the current parsed value
	 */
	public int getCurrentColumn() {
		return column;
	}

	/**
	 * Adds a nullValue (as specified in {@link CommonSettings#getNullValue()}) to the output and prepares the next position in the record to receive more values.
	 */
	public void emptyParsed() {
		this.parsedValues[column++] = nullValue;
		this.appender = appenders[column];
	}

	/**
	 * Adds the accumulated value in the appender object to the output and prepares the next position in the record to receive more values.
	 */
	public void valueParsed() {
		if (trim) {
			appender.updateWhitespace();
		}
		this.parsedValues[column++] = appender.getAndReset();
		this.appender = appenders[column];
	}

	/**
	 * Adds a value processed externally to the output and prepares the next position in the record to receive more values
	 *
	 * @param value the value to be added to the current record position.
	 */
	public void valueParsed(String value) {
		this.parsedValues[column++] = value;
		this.appender = appenders[column];
	}

	/**
	 * Returns the current record index. The number returned here reflects the number of actually parsed and valid records sent to the output of {@link ParserOutput#rowParsed}.
	 *
	 * @return the current record index.
	 */
	public long getCurrentRecord() {
		return currentRecord;
	}

	/**
	 * Discards the values parsed so far
	 */
	public final void discardValues() {
		column = 0;
		this.appender = appenders[0];
	}

	/**
	 * Resets the parser output and prepares for a new parsing process.
	 */
	final void reset() {
		this.columnsToExtractInitialized = false;
		this.currentRecord = 0;
		this.column = 0;
		this.headers = null;
		this.headerStrings = null;
	}
}
