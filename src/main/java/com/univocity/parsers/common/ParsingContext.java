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

import com.univocity.parsers.common.processor.*;

/**
 * Context information available to instances of {@link RowProcessor}.
 *
 * <p> The ParsingContext can be used to control and to obtain information about the parsing process.
 *
 * @see RowProcessor
 * @see DefaultParsingContext
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public interface ParsingContext {

	/**
	 * Stops the parsing process. Any open resources in use by the parser are closed automatically.
	 */
	public void stop();

	/**
	 * Identifies whether the parser is running.
	 *
	 * @return true if the parser is stopped, false otherwise.
	 */
	public boolean isStopped();

	/**
	 * Returns the current line of text being processed by the parser
	 * @return current line of text being processed by the parser
	 */
	public long currentLine();

	/**
	 * Returns the index of the last char read from the input so far.
	 * @return the index of the last char read from the input so far.
	 */
	public long currentChar();

	/**
	 * Returns the column index of the record being processed.
	 *
	 * @return the column index of the record being processed.
	 */
	public int currentColumn();

	/**
	 * Returns the index of the last valid record parsed from the input
	 * @return the index of the last valid record parsed from the input
	 */
	public long currentRecord();

	/**
	 * Skips a given number of lines from the current position.
	 * @param lines the number of lines to be skipped.
	 */
	public void skipLines(int lines);

	/**
	 * Returns the file headers that identify each parsed record.
	 *
	 *  <p> If the headers are extracted from the input (i.e. {@link CommonParserSettings#isHeaderExtractionEnabled()} == true), then these values will be returned.
	 *  <p> If no headers are extracted from the input, then the configured headers in {@link CommonSettings#getHeaders()} will be returned.
	 *
	 * @return the headers used to identify each record parsed from the input.
	 *
	 * @see com.univocity.parsers.common.CommonParserSettings
	 * @see com.univocity.parsers.common.CommonSettings
	 *
	 */
	public String[] headers();

	/**
	 * Returns the indexes of each field extracted from the input when fields are selected in the parser settings (i.e. using {@link CommonSettings#selectFields} and friends).
	 *
	 * <p> The indexes are relative to their original position in the input.
	 * <p> For example, if the input has the fields "A, B, C, D", and the selected fields are "A, D", then the extracted field indexes will return [0, 3]
	 *
	 * <p>If no fields were selected, then this method will return null. This means all fields are being parsed.
	 *
	 * @return The indexes of each selected field; null if no fields were selected.
	 *
	 * @see com.univocity.parsers.common.CommonSettings
	 */
	public int[] extractedFieldIndexes();

	/**
	 * Indicates whether selected fields (using {@link CommonSettings#selectFields} and friends) are being reordered.
	 *
	 * <p>If columns are reordered, each parsed record will contain values only for the selected fields, as specified by {@link CommonParserSettings#isColumnReorderingEnabled}
	 *
	 * @return true if the parsed records are being reordered by the parser, false otherwise
	 *
	 * @see com.univocity.parsers.common.CommonParserSettings
	 * @see com.univocity.parsers.common.CommonSettings
	 */
	public boolean columnsReordered();

	/**
	 * Returns a String with the input character sequence parsed to produce the current record.
	 * @return the text content parsed for the current input record.
	 */
	public String currentParsedContent();
}
