/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
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
package com.univocity.parsers.issues.github;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.input.*;
import com.univocity.parsers.common.input.concurrent.*;
import com.univocity.parsers.common.processor.core.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.examples.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/186
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_186 extends Example {

	@Test
	public void testConcurrentInitialization() throws Exception {
		CsvParserSettings settings = new CsvParserSettings() {
			@Override
			protected CharInputReader newCharInputReader(int whitespaceRangeStart) {
				return new ConcurrentCharInputReader(getFormat().getNormalizedNewline(), this.getInputBufferSize(), 10, whitespaceRangeStart, true){
					protected void setReader(Reader reader) {
						super.setReader(reader);
						try {
							Thread.sleep(100);
						} catch (Exception e) {
						}
					}
				};
			}
		};

		final boolean[] processorRan = new boolean[]{false};
		settings.setProcessor(new AbstractProcessor<ParsingContext>() {
			@Override
			public void processStarted(ParsingContext context) {
				processorRan[0] = true;
				assertEquals(context.headers(), new String[]{"a", "b"});
			}
		});
		settings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(settings);
		parser.beginParsing(new ByteArrayInputStream("a,b".getBytes()));


		assertTrue(processorRan[0]);
	}

}
