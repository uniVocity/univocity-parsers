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
public class ParsingContextWrapper extends ContextWrapper implements ParsingContext {

	private final ParsingContext parsingContext;

	/**
	 * Wraps a {@link ParsingContext}.
	 * @param context the parsingContext object to be wrapped.
	 */
	public ParsingContextWrapper(Context context) {
		super(context);
		if(context instanceof ParsingContext) {
			this.parsingContext = (ParsingContext) context;
		} else {
			this.parsingContext = NoopParsingContext.instance;
		}
	}

	@Override
	public void stop() {
		parsingContext.stop();
	}

	@Override
	public boolean isStopped() {
		return parsingContext.isStopped();
	}

	@Override
	public long currentLine() {
		return parsingContext.currentLine();
	}

	@Override
	public long currentChar() {
		return parsingContext.currentChar();
	}

	@Override
	public void skipLines(long lines) {
		parsingContext.skipLines(lines);
	}

	@Override
	public String currentParsedContent() {
		return parsingContext.currentParsedContent();
	}

	@Override
	public Map<Long, String> comments() {
		return parsingContext.comments();
	}

	@Override
	public String lastComment() {
		return parsingContext.lastComment();
	}

	@Override
	public String[] parsedHeaders() {
		return parsingContext.parsedHeaders();
	}

	@Override
	public char[] lineSeparator() {
		return parsingContext.lineSeparator();
	}
}
