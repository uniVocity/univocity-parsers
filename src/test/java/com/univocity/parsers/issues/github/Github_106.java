/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_106 {

	@Test
	public void ensureCurrentParsedContentIsValid(){

		String input = "abc,def,gh\nij,klm,no";

		CsvParserSettings settings = new CsvParserSettings();
		settings.setProcessor(new RowProcessor() {
			@Override
			public void processStarted(ParsingContext context) {
				assertNull(context.currentParsedContent());
			}

			@Override
			public void rowProcessed(String[] row, ParsingContext context) {
				if(context.currentLine() == 1){
					assertEquals(context.currentParsedContent(), "abc,def,gh");
				} else {
					assertEquals(context.currentParsedContent(), "ij,klm,no");
				}
			}

			@Override
			public void processEnded(ParsingContext context) {
				assertNull(context.currentParsedContent());
			}
		});

		new CsvParser(settings).parse(new StringReader(input));
	}
}
