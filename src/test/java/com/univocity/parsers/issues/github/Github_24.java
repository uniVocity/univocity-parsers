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
package com.univocity.parsers.issues.github;

import static org.testng.Assert.*;

import java.io.*;

import org.testng.annotations.*;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;

/**
*
* From: https://github.com/uniVocity/univocity-parsers/issues/24
*
* @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
*
*/
public class Github_24 {

	public static class AB {

		@Parsed(field = "AA")
		private String a;

		@Parsed(field = "BB")
		private String b;

		public AB() {

		}
	}

	@Test
	public void ensureExceptionsAreThrown() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.setRowProcessor(beanProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		try{
			parser.parse(new StringReader("AAAA,BB\nA,B\nC,D"));
			fail("Expected exception to be thrown here");
		} catch(TextParsingException e){
			//success!!
		}
	}
}
