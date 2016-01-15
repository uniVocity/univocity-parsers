/*
 * Copyright (c) 2015 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 *
 */
package com.univocity.parsers.common;

import com.univocity.parsers.common.processor.*;

import java.util.*;

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
	void stop();

	/**
	 * Identifies whether the parser is running.
	 *
	 * @return true if the parser is stopped, false otherwise.
	 */
	boolean isStopped();

	/**
	 * Returns the current line of text being processed by the parser
	 * @return current line of text being processed by the parser
	 */
	long currentLine();

	/**
	 * Returns the index of the last char read from the input so far.
	 * @return the index of the last char read from the input so far.
	 */
	long currentChar();

	/**
	 * Returns the column index of the record being processed.
	 *
	 * @return the column index of the record being processed.
	 */
	int currentColumn();

	/**
	 * Returns the index of the last valid record parsed from the input
	 * @return the index of the last valid record parsed from the input
	 */
	long currentRecord();

	/**
	 * Skips a given number of lines from the current position.
	 * @param lines the number of lines to be skipped.
	 */
	void skipLines(int lines);

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
	 * Returns a String with the input character sequence parsed to produce the current record.
	 * @return the text content parsed for the current input record.
	 */
	String currentParsedContent();

	/**
	 * Returns the position of a header (0 based).
	 * @param header the header whose position will be returned
	 * @return the position of the given header, or -1 if it could not be found.
	 */
	int indexOf(String header);

	/**
	 * Returns the position of a header (0 based).
	 * @param header the header whose position will be returned
	 * @return the position of the given header, or -1 if it could not be found.
	 */
	int indexOf(Enum<?> header);

	/**
	 * Returns all comments collected by the parser so far.
	 * An empty map will be returned if {@link CommonParserSettings#isCommentCollectionEnabled()} evaluates to {@code false}.
	 *
	 * @return a map containing the line numbers and comments found in each.
	 */
	Map<Long, String> getComments();

	/**
	 * Returns the last comment found in the input.
	 * {@code null} will be returned if {@link CommonParserSettings#isCommentCollectionEnabled()} is evaluated to {@code false}.
	 *
	 * @return the last comment found in the input.
	 */
	String getLastComment();
}
