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

import com.univocity.parsers.common.input.*;

/**
 *
 * The default {@link ParsingContext} implementation used internally by {@link AbstractParser} to expose information about a parsing process in execution.
 *
 * @see com.univocity.parsers.common.ParsingContext
 * @see com.univocity.parsers.common.AbstractParser
 * @see com.univocity.parsers.common.processor.RowProcessor
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
class DefaultParsingContext implements ParsingContext {

	private final CharInputReader input;
	private final ParserOutput output;
	protected boolean stopped = false;

	private int[] extractedIndexes = null;

	public DefaultParsingContext(CharInputReader input, ParserOutput output) {
		this.input = input;
		this.output = output;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		stopped = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long currentLine() {
		return input.lineCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long currentChar() {
		return input.charCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int currentColumn() {
		return output.getCurrentColumn();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] headers() {
		return output.getHeaders();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] extractedFieldIndexes() {
		if (extractedIndexes == null) {
			extractedIndexes = output.getSelectedIndexes();
		}
		return extractedIndexes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean columnsReordered() {
		return output.isColumnReorderingEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void skipLines(int lines) {
		input.skipLines(lines);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long currentRecord() {
		return output.getCurrentRecord();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String currentParsedContent() {
		char[] chars = output.appender.getChars();
		if (chars != null) {
			int length = output.appender.length();
			if (length > chars.length) {
				length = chars.length;
			}
			if (length > 0) {
				return new String(chars, 0, length);
			}
		}
		return null;
	}

}
