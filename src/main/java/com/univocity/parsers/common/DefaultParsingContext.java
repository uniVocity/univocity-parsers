/*
 * Copyright (c) 2015 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 *
 */
package com.univocity.parsers.common;

import com.univocity.parsers.common.input.*;

import java.util.*;

/**
 * The default {@link ParsingContext} implementation used internally by {@link AbstractParser} to expose information about a parsing process in execution.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
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
