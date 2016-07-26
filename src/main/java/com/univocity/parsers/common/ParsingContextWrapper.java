/*
 * Copyright (c) 2015 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 *
 */
package com.univocity.parsers.common;

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
}
