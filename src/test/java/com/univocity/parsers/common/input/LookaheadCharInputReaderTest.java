/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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

public class LookaheadCharInputReaderTest {

	@Test
	public void testLookahead() {
		LookaheadCharInputReader reader = new LookaheadCharInputReader(new DefaultCharInputReader("\n\r".toCharArray(), '\n', 2, -1, true), '\n', -1);

		reader.start(new StringReader("abcdefgh"));

		assertEquals(reader.nextChar(), 'a');

		reader.lookahead(1);
		reader.lookahead(1);
		assertTrue(reader.matches(new char[]{'b', 'c'}, '?'));
		assertTrue(reader.matches(new char[]{'b'}, '?'));
		assertFalse(reader.matches(new char[]{'c'}, '?'));
		assertFalse(reader.matches(new char[]{'a', 'b'}, '?'));
		assertFalse(reader.matches(new char[]{'c', 'd'}, '?'));

		assertEquals(reader.nextChar(), 'b');

		assertFalse(reader.matches(new char[]{'b'}, '?'));
		assertTrue(reader.matches(new char[]{'c'}, '?'));
		assertEquals(reader.nextChar(), 'c');
		assertFalse(reader.matches(new char[]{'c'}, '?'));
		assertFalse(reader.matches(new char[]{'d'}, '?'));
		assertEquals(reader.nextChar(), 'd');
		assertEquals(reader.nextChar(), 'e');

		reader.lookahead(5);
		assertTrue(reader.matches(new char[]{'f', 'g', 'h'}, '?'));
		assertTrue(reader.matches(new char[]{'f', 'g'}, '?'));
		assertTrue(reader.matches(new char[]{'f'}, '?'));

		assertEquals(reader.nextChar(), 'f');
		assertEquals(reader.nextChar(), 'g');
		assertTrue(reader.matches(new char[]{'h'}, '?'));
		assertEquals(reader.nextChar(), 'h');
		assertFalse(reader.matches(new char[]{'f'}, '?'));

		try {
			char ch = reader.nextChar();
			fail("Expected EOFException after end of the input. Got char: " + ch);
		} catch (EOFException ex) {
			//pass
		}
	}
}
