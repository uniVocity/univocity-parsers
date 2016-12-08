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

import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

public class DefaultCharInputReaderTest {

	private void assertEOF(DefaultCharInputReader reader) {
		try {
			reader.nextChar();
			fail("Expected EOFException");
		} catch (EOFException ex) {
			//pass
		}
	}

	@Test
	public void testInputReading() {
		DefaultCharInputReader reader = new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1);

		reader.start(new StringReader("a"));
		assertEquals('a', reader.nextChar());
		assertEOF(reader);

		reader.start(new StringReader("ab"));
		assertEquals('a', reader.nextChar());
		assertEquals('b', reader.nextChar());
		assertEOF(reader);

		reader.start(new StringReader("a\n\r"));
		assertEquals('a', reader.nextChar());
		assertEquals('\n', reader.nextChar());
		assertEOF(reader);

		reader.start(new StringReader("a\r\n"));
		assertEquals('a', reader.nextChar());
		assertEquals('\r', reader.nextChar());
		assertEquals('\n', reader.nextChar());
		assertEOF(reader);

		reader.start(new StringReader("\n\ra"));
		assertEquals('\n', reader.nextChar());
		assertEquals('a', reader.nextChar());
		assertEOF(reader);

		reader.start(new StringReader("\n\r"));
		assertEquals('\n', reader.nextChar());
		assertEOF(reader);
	}
}
