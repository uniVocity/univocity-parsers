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

import com.univocity.parsers.common.record.*;

/**
 * Default implementation of the {@link Context} interface with essential information about the output being produced.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class DefaultContext implements Context {

	protected boolean stopped = false;
	final ParserOutput output;
	final ColumnMap columnMap;
	final int errorContentLength;
	protected RecordFactory recordFactory;

	private String[] headers;

	public DefaultContext(int errorContentLength) {
		this(null, errorContentLength);
	}

	public DefaultContext(ParserOutput output, int errorContentLength) {
		this.output = output;
		this.errorContentLength = errorContentLength;
		this.columnMap = new ColumnMap(this, output);
	}

	@Override
	public String[] headers() {
		if(headers == null) {
			if (output == null) {
				headers = ArgumentUtils.EMPTY_STRING_ARRAY;
			}
			headers = output.getHeaderAsStringArray();
		}
		return headers;
	}

	public String[] selectedHeaders() {
		if(headers == null) {
			headers();
		}
		int[] extractedFieldIndexes = extractedFieldIndexes();
		if (extractedFieldIndexes != null) {
			String[] extractedFields = new String[extractedFieldIndexes.length];
			String[] headers = headers();
			for (int i = 0; i < extractedFieldIndexes.length; i++) {
				extractedFields[i] = headers[extractedFieldIndexes[i]];
			}
			return extractedFields;
		}
		return headers();
	}

	@Override
	public int[] extractedFieldIndexes() {
		if (output == null) {
			return null;
		}
		return output.getSelectedIndexes();
	}

	@Override
	public boolean columnsReordered() {
		if (output == null) {
			return false;
		}
		return output.isColumnReorderingEnabled();
	}

	@Override
	public int indexOf(String header) {
		return columnMap.indexOf(header);
	}

	@Override
	public int indexOf(Enum<?> header) {
		return columnMap.indexOf(header);
	}

	void reset() {
		if (output != null) {
			output.reset();
		}
		recordFactory = null;
		columnMap.reset();
	}


	@Override
	public int currentColumn() {
		if (output == null) {
			return -1;
		}
		return output.getCurrentColumn();
	}


	@Override
	public long currentRecord() {
		if (output == null) {
			return -1;
		}
		return output.getCurrentRecord();
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
	public int errorContentLength() {
		return errorContentLength;
	}

	@Override
	public Record toRecord(String[] row) {
		if (recordFactory == null) {
			recordFactory = new RecordFactory(this);
		}
		return recordFactory.newRecord(row);
	}

	@Override
	public RecordMetaData recordMetaData(){
		if(recordFactory == null){
			recordFactory = new RecordFactory(this);
		}
		return recordFactory.getRecordMetaData();
	}


}
