/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common.input;

import static org.testng.Assert.*;

import java.io.*;

import org.testng.annotations.*;

public class DefaultCharInputReaderTest {

	private char nextChar(DefaultCharInputReader reader){
		return reader.nextChar();
	}
	
	@Test
	public void testInputReading() {
		DefaultCharInputReader reader = new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2);

		reader.start(new StringReader("a"));
		assertEquals('a', nextChar(reader));
		assertEquals('\0', nextChar(reader));

		reader.start(new StringReader("ab"));
		assertEquals('a', nextChar(reader));
		assertEquals('b', nextChar(reader));
		assertEquals('\0', nextChar(reader));

		reader.start(new StringReader("a\n\r"));
		assertEquals('a', nextChar(reader));
		assertEquals('\n', nextChar(reader));
		assertEquals('\0', nextChar(reader));

		reader.start(new StringReader("a\r\n"));
		assertEquals('a', nextChar(reader));
		assertEquals('\r', nextChar(reader));
		assertEquals('\n', nextChar(reader));
		assertEquals('\0', nextChar(reader));

		reader.start(new StringReader("\n\ra"));
		assertEquals('\n', nextChar(reader));
		assertEquals('a', nextChar(reader));
		assertEquals('\0', nextChar(reader));

		reader.start(new StringReader("\n\r"));
		assertEquals('\n', nextChar(reader));
		assertEquals('\0', nextChar(reader));
	}
}
