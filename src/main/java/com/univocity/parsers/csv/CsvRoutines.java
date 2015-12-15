/*
 * Copyright (c) 2015 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 *
 */

package com.univocity.parsers.csv;

import com.univocity.parsers.common.routine.*;

import java.io.*;

public class CsvRoutines extends AbstractRoutines<CsvParserSettings, CsvWriterSettings> {

	public CsvRoutines() {
		super("CSV parsing/writing routine");
	}

	@Override
	protected CsvParser createParser(CsvParserSettings parserSettings) {
		return new CsvParser(parserSettings);
	}

	@Override
	protected CsvWriter createWriter(Writer output, CsvWriterSettings writerSettings) {
		return new CsvWriter(output, writerSettings);
	}
}
