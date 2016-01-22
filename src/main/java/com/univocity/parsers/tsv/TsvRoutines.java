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

package com.univocity.parsers.tsv;

import com.univocity.parsers.common.routine.*;

import java.io.*;

/**
 * A collection of common routines involving the processing of TSV data.
 */
public class TsvRoutines extends AbstractRoutines<TsvParserSettings, TsvWriterSettings> {

	/**
	 * Creates a new instance of the TSV routine class without any predefined parsing/writing configuration.
	 */
	public TsvRoutines() {
		this(null, null);
	}

	/**
	 * Creates a new instance of the TSV routine class.
	 *
	 * @param parserSettings configuration to use for TSV parsing
	 */
	public TsvRoutines(TsvParserSettings parserSettings) {
		this(parserSettings, null);
	}

	/**
	 * Creates a new instance of the TSV routine class.
	 *
	 * @param writerSettings configuration to use for TSV writing
	 */
	public TsvRoutines(TsvWriterSettings writerSettings) {
		this(null, writerSettings);
	}

	/**
	 * Creates a new instance of the TSV routine class.
	 *
	 * @param parserSettings configuration to use for TSV parsing
	 * @param writerSettings configuration to use for TSV writing
	 */
	public TsvRoutines(TsvParserSettings parserSettings, TsvWriterSettings writerSettings) {
		super("TSV parsing/writing routine", parserSettings, writerSettings);
	}

	@Override
	protected TsvParser createParser(TsvParserSettings parserSettings) {
		return new TsvParser(parserSettings);
	}

	@Override
	protected TsvWriter createWriter(Writer output, TsvWriterSettings writerSettings) {
		return new TsvWriter(output, writerSettings);
	}

	@Override
	protected TsvParserSettings createDefaultParserSettings() {
		return new TsvParserSettings();
	}

	@Override
	protected TsvWriterSettings createDefaultWriterSettings() {
		return new TsvWriterSettings();
	}
}
