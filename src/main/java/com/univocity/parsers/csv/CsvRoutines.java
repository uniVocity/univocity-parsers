/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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

package com.univocity.parsers.csv;

import com.univocity.parsers.common.routine.*;

import java.io.*;

/**
 * A collection of common routines involving the processing of CSV data.
 */
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
