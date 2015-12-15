/*
 * Copyright (c) 2015 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 *
 */

package com.univocity.parsers.fixed;

import com.univocity.parsers.common.routine.*;

import java.io.*;

public class FixedWidthRoutines  extends AbstractRoutines<FixedWidthParserSettings, FixedWidthWriterSettings> {

	public FixedWidthRoutines() {
		super("Fixed-width parsing/writing routine");
	}

	protected void adjustColumnLengths(String[] headers, int[] lengths){
		getWriterSettings().setFieldLengths(new FixedWidthFieldLengths(headers,lengths));
	}

	@Override
	protected FixedWidthParser createParser(FixedWidthParserSettings parserSettings) {
		return new FixedWidthParser(parserSettings);
	}

	@Override
	protected FixedWidthWriter createWriter(Writer output, FixedWidthWriterSettings writerSettings) {
		return new FixedWidthWriter(output, writerSettings);
	}
}
