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
package com.univocity.parsers.common;

/**
 * A simple a wrapper for a {@link ParsingContext}.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class ParsingContextWrapper implements ParsingContext {

	private final ParsingContext context;

	public ParsingContextWrapper(ParsingContext context) {
		this.context = context;
	}

	@Override
	public void stop() {
		context.stop();
	}

	@Override
	public boolean isStopped() {
		return context.isStopped();
	}

	@Override
	public long currentLine() {
		return context.currentLine();
	}

	@Override
	public long currentChar() {
		return context.currentChar();
	}

	@Override
	public int currentColumn() {
		return context.currentColumn();
	}

	@Override
	public long currentRecord() {
		return context.currentRecord();
	}

	@Override
	public void skipLines(int lines) {
		context.skipLines(lines);
	}

	@Override
	public String[] headers() {
		return context.headers();
	}

	@Override
	public int[] extractedFieldIndexes() {
		return context.extractedFieldIndexes();
	}

	@Override
	public boolean columnsReordered() {
		return context.columnsReordered();
	}

	@Override
	public String currentParsedContent() {
		return context.currentParsedContent();
	}

}
