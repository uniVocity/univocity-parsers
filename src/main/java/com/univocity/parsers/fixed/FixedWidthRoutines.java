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
