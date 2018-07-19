/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_113 {

	@Test
	public void testMaxCharsPerColumnBehaves() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();

		settings.setMaxCharsPerColumn(5);

		settings.getFormat().setDelimiter('\t'); //incorrect delimiter to trigger the exception

		try {
			new CsvParser(settings).parseLine("a,b,c,d,e,f\ne,g");
			fail("Expecting exception here");
		} catch(TextParsingException ex){
			assertEquals(ex.getParsedContent(), "a,b,c");
		}
	}
}
