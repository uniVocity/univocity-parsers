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

import com.univocity.parsers.common.input.*;

import java.util.*;

/**
 * The default {@link ParsingContext} implementation used internally by {@link AbstractParser} to expose information about a parsing process in execution.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.common.ParsingContext
 * @see com.univocity.parsers.common.AbstractParser
 * @see com.univocity.parsers.common.processor.RowProcessor
 */
public class DefaultParsingContext extends DefaultContext implements ParsingContext {

	private final CharInputReader input;

	private final AbstractParser<?> parser;


	public DefaultParsingContext(AbstractParser<?> parser, int errorContentLength) {
		super(parser == null ? null : parser.output, errorContentLength);
		this.parser = parser;
		this.input = parser == null ? null : parser.input;
	}

	@Override
	public long currentLine() {
		return input.lineCount();
	}

	@Override
	public long currentChar() {
		return input.charCount();
	}

	@Override
	public void skipLines(long lines) {
		input.skipLines(lines);
	}

	@Override
	public String fieldContentOnError() {
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

	@Override
	public String currentParsedContent() {
		if (input != null) {
			return input.currentParsedContent();
		}
		return null;
	}

	@Override
	public int currentParsedContentLength() {
		if (input != null) {
			return input.currentParsedContentLength();
		}
		return 0;
	}

	@Override
	public Map<Long, String> comments() {
		return parser.getComments();
	}

	@Override
	public String lastComment() {
		return parser.getLastComment();
	}

	@Override
	public String[] parsedHeaders() {
		return parser.getParsedHeaders();
	}

	@Override
	public char[] lineSeparator() {
		return input.getLineSeparator();
	}
}
