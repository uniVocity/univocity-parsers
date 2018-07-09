/*
 * Copyright (c) 2018. uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.record.*;

import java.util.*;

/**
 * A simple a wrapper for a {@link ParsingContext}.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class ParsingContextWrapper extends ContextWrapper<ParsingContext> implements ParsingContext {

	/**
	 * Wraps a {@link ParsingContext}.
	 * @param context the parsingContext object to be wrapped.
	 */
	public ParsingContextWrapper(ParsingContext context) {
		super(context);
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
	public void skipLines(long lines) {
		context.skipLines(lines);
	}

	@Override
	public String currentParsedContent() {
		return context.currentParsedContent();
	}

	@Override
	public int currentParsedContentLength() {
		return context.currentParsedContentLength();
	}

	@Override
	public Map<Long, String> comments() {
		return context.comments();
	}

	@Override
	public String lastComment() {
		return context.lastComment();
	}

	@Override
	public String[] parsedHeaders() {
		return context.parsedHeaders();
	}

	@Override
	public char[] lineSeparator() {
		return context.lineSeparator();
	}

	@Override
	public String fieldContentOnError() {
		return context.fieldContentOnError();
	}

	@Override
	public String[] selectedHeaders() {
		return context.selectedHeaders();
	}

	@Override
	public Record toRecord(String[] row) {
		return context.toRecord(row);
	}
}
