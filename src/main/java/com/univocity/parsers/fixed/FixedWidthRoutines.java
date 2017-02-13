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

package com.univocity.parsers.fixed;

import com.univocity.parsers.common.routine.*;

import java.io.*;

/**
 * A collection of common routines involving the processing of Fixed-Width data.
 */
public class FixedWidthRoutines  extends AbstractRoutines<FixedWidthParserSettings, FixedWidthWriterSettings> {


	/**
	 * Creates a new instance of the Fixed-width routine class without any predefined parsing/writing configuration.
	 */
	public FixedWidthRoutines() {
		this(null, null);
	}

	/**
	 * Creates a new instance of the Fixed-width routine class.
	 *
	 * @param parserSettings configuration to use for Fixed-width parsing
	 */
	public FixedWidthRoutines(FixedWidthParserSettings parserSettings) {
		this(parserSettings, null);
	}

	/**
	 * Creates a new instance of the Fixed-width routine class.
	 *
	 * @param writerSettings configuration to use for Fixed-width writing
	 */
	public FixedWidthRoutines(FixedWidthWriterSettings writerSettings) {
		this(null, writerSettings);
	}

	/**
	 * Creates a new instance of the Fixed-width routine class.
	 *
	 * @param parserSettings configuration to use for Fixed-width parsing
	 * @param writerSettings configuration to use for Fixed-width writing
	 */
	public FixedWidthRoutines(FixedWidthParserSettings parserSettings, FixedWidthWriterSettings writerSettings) {
		super("Fixed-width parsing/writing routine", parserSettings, writerSettings);
	}

	protected void adjustColumnLengths(String[] headers, int[] lengths){
		if(getWriterSettings().getFieldLengths() == null) {
			getWriterSettings().setFieldLengths(new FixedWidthFields(headers, lengths));
		}
	}

	@Override
	protected FixedWidthParser createParser(FixedWidthParserSettings parserSettings) {
		return new FixedWidthParser(parserSettings);
	}

	@Override
	protected FixedWidthWriter createWriter(Writer output, FixedWidthWriterSettings writerSettings) {
		return new FixedWidthWriter(output, writerSettings);
	}

	@Override
	protected FixedWidthParserSettings createDefaultParserSettings() {
		return new FixedWidthParserSettings();
	}

	@Override
	protected FixedWidthWriterSettings createDefaultWriterSettings() {
		return new FixedWidthWriterSettings();
	}
}
