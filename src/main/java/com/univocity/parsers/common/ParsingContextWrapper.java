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
	public void skipLines(long lines) {
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

	@Override
	public int indexOf(String header) {
		return context.indexOf(header);
	}

	@Override
	public int indexOf(Enum<?> header) {
		return context.indexOf(header);
	}

	@Override
	public Map<Long, String> getComments() {
		return context.getComments();
	}

	@Override
	public String getLastComment() {
		return context.getLastComment();
	}
}
