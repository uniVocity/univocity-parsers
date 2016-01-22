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
	private Map<String, Integer> columnMap;
	private Map<String, Integer> normalizedColumnMap;
	private int[] enumMap;
	private final AbstractParser<?> parser;

	public DefaultParsingContext(AbstractParser<?> parser) {
		this.parser = parser;
		this.input = parser.input;
		this.output = parser.output;
	}

	@Override
	public void stop() {
		stopped = true;
	}

	@Override
	public boolean isStopped() {
		return stopped;
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
	public int currentColumn() {
		return output.getCurrentColumn();
	}

	@Override
	public String[] headers() {
		return output.getHeaders();
	}

	@Override
	public int[] extractedFieldIndexes() {
		if (extractedIndexes == null) {
			extractedIndexes = output.getSelectedIndexes();
		}
		return extractedIndexes;
	}

	@Override
	public boolean columnsReordered() {
		return output.isColumnReorderingEnabled();
	}

	@Override
	public void skipLines(long lines) {
		input.skipLines(lines);
	}

	@Override
	public long currentRecord() {
		return output.getCurrentRecord();
	}

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

	@Override
	public int indexOf(String header) {
		if(columnMap != null && columnMap.isEmpty()){
			return -1;
		}
		if (header == null) {
			if(headers() == null){
				throw new IllegalArgumentException("Header name cannot be null.");
			}
			throw new IllegalArgumentException("Header name cannot be null. Use one of the available column names: " + Arrays.asList(headers()));
		}

		if (columnMap == null) {
			String[] headers = headers();
			if(headers == null){
				columnMap = Collections.emptyMap();
				normalizedColumnMap = Collections.emptyMap();
				return -1;
			}
			columnMap = new HashMap<String, Integer>(headers.length);

			int[] extractedIndexes = extractedFieldIndexes();
			boolean columnsReordered = columnsReordered();

			if (extractedIndexes != null) {
				if (columnsReordered) {
					for (int i = 0; i < extractedIndexes.length; i++) {
						int originalIndex = extractedIndexes[i];
						String h = headers[originalIndex];
						columnMap.put(h, i);
					}
				} else {
					for (int i = 0; i < extractedIndexes.length; i++) {
						columnMap.put(headers[i], i);
					}
				}
			} else {
				for (int i = 0; i < headers.length; i++) {
					columnMap.put(headers[i], i);
				}
			}

			normalizedColumnMap = new HashMap<String, Integer>(headers.length);
			for (Map.Entry<String, Integer> e : columnMap.entrySet()) {
				normalizedColumnMap.put(e.getKey().trim().toLowerCase(), e.getValue());
			}
		}


		Integer index = columnMap.get(header);
		if (index == null) {
			index = normalizedColumnMap.get(header.trim().toLowerCase());
			if (index == null) {
				return -1;
			}
		}
		return index.intValue();
	}

	@Override
	public int indexOf(Enum<?> header) {
		if(enumMap != null && enumMap.length == 0){
			return -1;
		}
		if (header == null) {
			if(headers() == null){
				throw new IllegalArgumentException("Header name cannot be null.");
			}
			throw new IllegalArgumentException("Header name cannot be null. Use one of the available column names: " + Arrays.asList(headers()));
		}

		if (enumMap == null) {
			String[] headers = headers();
			if(headers == null){
				enumMap = new int[0];
				return -1;
			}

			Enum<?>[] constants = header.getClass().getEnumConstants();
			int lastOrdinal = Integer.MIN_VALUE;
			for (int i = 0; i < constants.length; i++) {
				if (lastOrdinal < constants[i].ordinal()) {
					lastOrdinal = constants[i].ordinal();
				}
			}

			enumMap = new int[lastOrdinal + 1];
			for (int i = 0; i < constants.length; i++) {
				Enum<?> constant = constants[i];
				String name = constant.toString();
				int index = ArgumentUtils.indexOf(headers, name);
				enumMap[constant.ordinal()] = index;
			}
		}
		return enumMap[header.ordinal()];
	}

	@Override
	public Map<Long, String> getComments() {
		return parser.getComments();
	}

	@Override
	public String getLastComment() {
		return parser.getLastComment();
	}
}
