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

import com.univocity.parsers.common.record.*;

/**
 * Basic context information used internally by instances of {@link com.univocity.parsers.common.processor.core.Processor} and {@link com.univocity.parsers.common.record.Record}.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 * @see DefaultContext
 * @see ParsingContext
 * @see DefaultParsingContext
 */
public interface Context {

	/**
	 * Returns the file headers that identify each parsed record.
	 *
	 * @return the headers used to identify each record parsed from the input.
	 */
	String[] headers();

	/**
	 * Returns the sequence of headers that have been selected. If no selection has been made, all available headers
	 * will be returned, producing the same output as a call to method {@link #headers()}.
	 *
	 * @return the sequence of selected headers, or all headers if no selection has been made.
	 */
	String[] selectedHeaders();

	/**
	 * Returns the indexes of each field extracted from the input when fields are selected.
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
	int[] extractedFieldIndexes();

	/**
	 * Indicates whether selected fields are being reordered.
	 *
	 * <p>If columns are reordered, each parsed record will contain values only for the selected fields, as specified by {@link #extractedFieldIndexes()}
	 *
	 * @return true if the parsed records are being reordered by the parser, false otherwise
	 *
	 * @see com.univocity.parsers.common.CommonParserSettings
	 * @see com.univocity.parsers.common.CommonSettings
	 */
	boolean columnsReordered();

	/**
	 * Returns the position of a header (0 based).
	 *
	 * @param header the header whose position will be returned
	 *
	 * @return the position of the given header, or -1 if it could not be found.
	 */
	int indexOf(String header);

	/**
	 * Returns the position of a header (0 based).
	 *
	 * @param header the header whose position will be returned
	 *
	 * @return the position of the given header, or -1 if it could not be found.
	 */
	int indexOf(Enum<?> header);


	/**
	 * Returns the column index of the record being processed.
	 *
	 * @return the column index of the record being processed.
	 */
	int currentColumn();

	/**
	 * Returns the index of the last valid record parsed from the input
	 *
	 * @return the index of the last valid record parsed from the input
	 */
	long currentRecord();

	/**
	 * Stops the parsing process. Any open resources in use by the parser are closed
	 * automatically unless {@link CommonParserSettings#isAutoClosingEnabled()} evaluates to {@code false}.
	 */
	void stop();

	/**
	 * Identifies whether the parser is running.
	 *
	 * @return true if the parser is stopped, false otherwise.
	 */
	boolean isStopped();

	/**
	 * Returns the length limit of parsed contents appearing in exception messages when an error occurs
	 *
	 * <p>If {@code 0}, then no exceptions will include the content being manipulated in their attributes,
	 * and the {@code "<omitted>"} string will appear in error messages as the parsed content.</p>
	 *
	 * <p>defaults to {@code -1} (no limit)</p>.
	 *
	 * @return the maximum length of the data content to display in exception messages
	 */
	int errorContentLength();

	/**
	 * Converts the given parsed row to a {@link Record}
	 *
	 * @param row the row to be converted into a {@link Record}
	 *
	 * @return a {@link Record} representing the given row.
	 */
	Record toRecord(String[] row);

	/**
	 * Returns the metadata information associated with records produced by the current parsing process.
	 *
	 * @return the record metadata.
	 */
	RecordMetaData recordMetaData();
}
