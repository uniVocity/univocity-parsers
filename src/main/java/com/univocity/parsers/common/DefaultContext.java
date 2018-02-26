/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.common;

import com.univocity.parsers.common.record.*;

/**
 * Default implementation of the {@link Context} interface with essential information about the output being produced.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class DefaultContext implements Context {

	protected boolean stopped = false;
	final ParserOutput output;
	final ColumnMap columnMap;
	final int errorContentLength;
	private RecordFactory recordFactory;

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
		if (output == null) {
			return ArgumentUtils.EMPTY_STRING_ARRAY;
		}
		return output.getHeaders();
	}

	public String[] selectedHeaders() {
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
