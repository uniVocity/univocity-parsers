/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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

import com.univocity.parsers.common.record.*;

import java.util.*;

/**
 * A {@link ParsingContext} implementation that does nothing.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
class NoopParsingContext implements ParsingContext {

	static final NoopParsingContext instance = new NoopParsingContext();

	private RecordMetaData recordMetaData;

	private NoopParsingContext() {
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public long currentLine() {
		return 0;
	}

	@Override
	public long currentChar() {
		return 0;
	}

	@Override
	public int currentColumn() {
		return 0;
	}

	@Override
	public long currentRecord() {
		return 0;
	}

	@Override
	public void skipLines(long lines) {

	}

	@Override
	public String[] parsedHeaders() {
		return null;
	}

	@Override
	public String currentParsedContent() {
		return null;
	}

	public int currentParsedContentLength() {
		return 0;
	}

	@Override
	public Map<Long, String> comments() {
		return Collections.emptyMap();
	}

	@Override
	public String lastComment() {
		return null;
	}

	@Override
	public char[] lineSeparator() {
		return Format.getSystemLineSeparator();
	}

	@Override
	public String[] headers() {
		return null;
	}

	@Override
	public String[] selectedHeaders() {
		return null;
	}

	@Override
	public int[] extractedFieldIndexes() {
		return null;
	}

	@Override
	public boolean columnsReordered() {
		return true;
	}

	@Override
	public int indexOf(String header) {
		return -1;
	}

	@Override
	public int indexOf(Enum<?> header) {
		return -1;
	}

	@Override
	public String fieldContentOnError() {
		return null;
	}

	@Override
	public int errorContentLength() {
		return -1;
	}

	@Override
	public Record toRecord(String[] row) {
		return null;
	}

	@Override
	public RecordMetaData recordMetaData() {
		if(recordMetaData == null){
			recordMetaData = new RecordFactory(this).getRecordMetaData();
		}
		return recordMetaData;
	}
}
