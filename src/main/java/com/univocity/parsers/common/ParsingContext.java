/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.univocity.parsers.common;

import com.univocity.parsers.common.processor.*;

import java.util.*;

/**
 * Parsing context information available to instances of {@link RowProcessor}.
 *
 * <p> The ParsingContext can be used to control and to obtain information about the parsing process.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see RowProcessor
 * @see DefaultParsingContext
 */
public interface ParsingContext extends Context {

	/**
	 * Returns the file headers that identify each parsed record.
	 *
	 * <p> If the headers are extracted from the input (i.e. {@link CommonParserSettings#isHeaderExtractionEnabled()} == true), then these values will be returned.
	 * <p> If no headers are extracted from the input, then the configured headers in {@link CommonSettings#getHeaders()} will be returned.
	 * Note that the user-provided headers will override the header list parsed from the input, if any. To obtain the
	 * original list of headers found in the input use {@link ParsingContext#parsedHeaders()}
	 *
	 * @return the headers used to identify each record parsed from the input.
	 *
	 * @see com.univocity.parsers.common.CommonParserSettings
	 * @see com.univocity.parsers.common.CommonSettings
	 */
	String[] headers();


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
	int[] extractedFieldIndexes();

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
	boolean columnsReordered();

	/**
	 * Returns the current line of text being processed by the parser
	 *
	 * @return current line of text being processed by the parser
	 */
	long currentLine();

	/**
	 * Returns the index of the last char read from the input so far.
	 *
	 * @return the index of the last char read from the input so far.
	 */
	long currentChar();

	/**
	 * Skips a given number of lines from the current position.
	 *
	 * @param lines the number of lines to be skipped.
	 */
	void skipLines(long lines);

	/**
	 * Returns the headers <b>parsed</b> from the input, if and only if {@link CommonParserSettings#headerExtractionEnabled} is {@code true}.
	 * The result of this method won't return the list of headers manually set by the user in {@link CommonParserSettings#getHeaders()}.
	 * Use the {@link #headers()} method instead to obtain the headers actually used by the parser.
	 *
	 * @return the headers parsed from the input, when {@link CommonParserSettings#headerExtractionEnabled} is {@code true}.
	 */
	String[] parsedHeaders();

	/**
	 * Returns a String with the input character sequence parsed to produce the current record.
	 *
	 * @return the text content parsed for the current input record.
	 */
	String currentParsedContent();

	/**
	 * Returns the length of the character sequence parsed to produce the current record.
	 * @return the length of the text content parsed for the current input record
	 */
	int currentParsedContentLength();


	/**
	 * Returns a String with the input character sequence accumulated on a field before {@link TextParsingException} occurred.
	 *
	 * @return the text content parsed for the current field of the current input record at the time of the error.
	 */
	String fieldContentOnError();

	/**
	 * Returns all comments collected by the parser so far.
	 * An empty map will be returned if {@link CommonParserSettings#isCommentCollectionEnabled()} evaluates to {@code false}.
	 *
	 * @return a map containing the line numbers and comments found in each.
	 */
	Map<Long, String> comments();

	/**
	 * Returns the last comment found in the input.
	 * {@code null} will be returned if {@link CommonParserSettings#isCommentCollectionEnabled()} is evaluated to {@code false}.
	 *
	 * @return the last comment found in the input.
	 */
	String lastComment();

	/**
	 * Returns the line separator characters used to separate individual records when parsing. This could be the line
	 * separator defined in the {@link Format#getLineSeparator()} configuration, or the line separator sequence
	 * identified automatically when {@link CommonParserSettings#isLineSeparatorDetectionEnabled()} evaluates to {@code true}.
	 *
	 * @return the line separator sequence. Might contain one or two characters.
	 */
	char[] lineSeparator();
}
