/*
 * Copyright (c) 2015 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 *
 */

package com.univocity.parsers.tsv;

import com.univocity.parsers.common.routine.*;

import java.io.*;

public class TsvRoutines extends AbstractRoutines<TsvParserSettings, TsvWriterSettings> {

	public TsvRoutines() {
		super("TSV parsing/writing routine");
	}

	@Override
	protected TsvParser createParser(TsvParserSettings parserSettings) {
		return new TsvParser(parserSettings);
	}

	@Override
	protected TsvWriter createWriter(Writer output, TsvWriterSettings writerSettings) {
		return new TsvWriter(output, writerSettings);
	}
}
