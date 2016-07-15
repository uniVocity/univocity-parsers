/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.common;

/**
 * Default implementation of the {@link Context} interface with essential information about the output being produced.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class DefaultContext implements Context {

	protected boolean stopped = false;
	final ParserOutput output;
	final ColumnMap columnMap;

	public DefaultContext(ParserOutput output){
		this.output = output;
		this.columnMap = new ColumnMap(this, output);
	}

	@Override
	public String[] headers() {
		return output.getHeaders();
	}

	@Override
	public int[] extractedFieldIndexes() {
		return output.getSelectedIndexes();
	}

	@Override
	public boolean columnsReordered() {
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
		if(output != null) {
			output.reset();
		}
		columnMap.reset();
	}


	@Override
	public int currentColumn() {
		return output.getCurrentColumn();
	}


	@Override
	public long currentRecord() {
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
}
