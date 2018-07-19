/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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

import java.util.*;

/**
 * A snapshot of a {@link ParsingContext} which retains copies of variable attributes of a given {@link ParsingContext} to
 * store the state of the parsing process at a given point in time. All runtime operations such as {@link #stop()}
 * will still work and affect the current parsing process.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class ParsingContextSnapshot extends ParsingContextWrapper {

	private final long currentLine;
	private final long currentChar;
	private final Map<Long, String> comments;
	private final String lastComment;
	private final int currentColumn;
	private final String currentParsedContent;
	private final long currentRecord;

	/**
	 * Creates a snapshot of a given {@link Context}
	 *
	 * @param context the context object whose variable attributes will be copied over.
	 */
	public ParsingContextSnapshot(ParsingContext context) {
		super(context);
		currentLine = context.currentLine();
		currentChar = context.currentChar();
		comments = context.comments() == Collections.EMPTY_MAP ? Collections.<Long, String>emptyMap() : Collections.unmodifiableMap(context.comments());
		lastComment = context.lastComment();
		currentColumn = context.currentColumn();
		currentParsedContent = context.currentParsedContent();
		currentRecord = context.currentRecord();
	}


	@Override
	public long currentLine() {
		return currentLine;
	}

	@Override
	public long currentChar() {
		return currentChar;
	}

	@Override
	public Map<Long, String> comments() {
		return comments;
	}

	@Override
	public String lastComment() {
		return lastComment;
	}

	@Override
	public int currentColumn() {
		return currentColumn;
	}

	@Override
	public String currentParsedContent() {
		return currentParsedContent;
	}

	@Override
	public long currentRecord() {
		return currentRecord;
	}
}
