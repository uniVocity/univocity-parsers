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

	/**
	 * Creates a new instance of the CSV routine class without any predefined parsing/writing configuration.
	 */
	public CsvRoutines() {
		this(null, null);
	}

	/**
	 * Creates a new instance of the CSV routine class.
	 *
	 * @param parserSettings configuration to use for CSV parsing
	 */
	public CsvRoutines(CsvParserSettings parserSettings) {
		this(parserSettings, null);
	}

	/**
	 * Creates a new instance of the CSV routine class.
	 *
	 * @param writerSettings configuration to use for CSV writing
	 */
	public CsvRoutines(CsvWriterSettings writerSettings) {
		this(null, writerSettings);
	}

	/**
	 * Creates a new instance of the CSV routine class.
	 *
	 * @param parserSettings configuration to use for CSV parsing
	 * @param writerSettings configuration to use for CSV writing
	 */
	public CsvRoutines(CsvParserSettings parserSettings, CsvWriterSettings writerSettings) {
		super("CSV parsing/writing routine", parserSettings, writerSettings);
	}

	@Override
	protected CsvParser createParser(CsvParserSettings parserSettings) {
		return new CsvParser(parserSettings);
	}

	@Override
	protected CsvWriter createWriter(Writer output, CsvWriterSettings writerSettings) {
		return new CsvWriter(output, writerSettings);
	}

	@Override
	protected CsvParserSettings createDefaultParserSettings() {
		return new CsvParserSettings();
	}

	@Override
	protected CsvWriterSettings createDefaultWriterSettings() {
		return new CsvWriterSettings();
	}
}
