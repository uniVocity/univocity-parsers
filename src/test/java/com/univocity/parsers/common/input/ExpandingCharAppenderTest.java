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
package com.univocity.parsers.common.input;

import org.testng.annotations.*;

import static org.testng.Assert.*;

public class ExpandingCharAppenderTest {
	@Test
	public void testAppendIgnoringWhitespace() throws Exception {
		ExpandingCharAppender a = new ExpandingCharAppender(2, null, -1);
		assertEquals(a.chars.length, 2);
		a.append('a');
		a.append('b');
		a.appendIgnoringWhitespace(' ');
		assertEquals(a.toString(), "ab");
		assertEquals(a.chars.length, 6);

		a.appendIgnoringWhitespace('c');
		a.appendIgnoringWhitespace(' ');
		assertEquals(a.toString(), "ab c");
		assertEquals(a.chars.length, 6);
	}

	@Test
	public void testAppendIgnoringPadding() throws Exception {
		ExpandingCharAppender a = new ExpandingCharAppender(1, null, -1);

		assertEquals(a.chars.length, 1);
		a.append('_');
		a.append('b');
		a.appendIgnoringPadding('_', '_');
		assertEquals(a.toString(), "_b");
		assertEquals(a.chars.length, 4);

		a.appendIgnoringPadding('a', '_');
		assertEquals(a.toString(), "_b_a");
		assertEquals(a.chars.length, 4);
	}

	@Test
	public void testAppendIgnoringWhitespaceAndPadding() throws Exception {
		ExpandingCharAppender a = new ExpandingCharAppender(1, null, -1);

		assertEquals(a.chars.length, 1);
		a.append('_');
		a.append('b');
		a.appendIgnoringWhitespaceAndPadding(' ', '_');
		assertEquals(a.toString(), "_b");
		assertEquals(a.chars.length, 4);

		a.appendIgnoringWhitespaceAndPadding('_', '_');
		assertEquals(a.toString(), "_b");
		assertEquals(a.chars.length, 4);

		a.appendIgnoringWhitespaceAndPadding('c', '_');
		assertEquals(a.toString(), "_b _c");
		assertEquals(a.chars.length, 10);
	}

	@Test
	public void testAppend() throws Exception {
		ExpandingCharAppender a = new ExpandingCharAppender(1, null, -1);
		a.append('a');
		assertEquals(a.toString(), "a");
		assertEquals(a.chars.length, 1);

		a.append('b');
		assertEquals(a.toString(), "ab");
		assertEquals(a.chars.length, 4);

		a.append('c');
		a.append('d');
		a.append('e');
		assertEquals(a.toString(), "abcde");
		assertEquals(a.chars.length, 10);
	}

	@Test
	public void testFill() throws Exception {
		ExpandingCharAppender a = new ExpandingCharAppender(2, null, -1);
		a.fill('*', 2);
		assertEquals(a.toString(), "**");
		assertEquals(a.chars.length, 2);

		a.fill('*', 4);
		assertEquals(a.toString(), "******");
		assertEquals(a.chars.length, 6);

		a.fill('*', 1);
		assertEquals(a.toString(), "*******");
		assertEquals(a.chars.length, 14);
	}

	@Test
	public void testPrepend() throws Exception {
		ExpandingCharAppender a = new ExpandingCharAppender(2, null, -1);
		a.prepend('a');
		assertEquals(a.toString(), "a");
		assertEquals(a.chars.length, 2);

		a.prepend('b');
		assertEquals(a.toString(), "ba");
		assertEquals(a.chars.length, 2);

		a.prepend('c');
		assertEquals(a.toString(), "cba");
		assertEquals(a.chars.length, 4);

		a.prepend("12345678890".toCharArray());
		assertEquals(a.toString(), "12345678890cba");
	}

	@Test
	public void testAppend1() throws Exception {
		ExpandingCharAppender a = new ExpandingCharAppender(2, null, -1);
		ExpandingCharAppender b = new ExpandingCharAppender(2, null, -1);

		a.append(b);
		assertEquals(a.toString(), null);
		assertEquals(a.chars.length, 2);

		b.append('a');
		b.append('b');

		a.append(b); //whitespaceRangeStart gets reset here
		assertEquals(a.toString(), "ab");
		assertEquals(a.chars.length, 2);

		a.append(b); //should make no difference
		assertEquals(a.toString(), "ab");
		assertEquals(a.chars.length, 2);
		assertEquals(b.toString(), null);
		assertEquals(b.chars.length, 2);

		b.append('c');
		b.append('d');

		a.append(b);
		assertEquals(a.toString(), "abcd");
		assertEquals(a.chars.length, 6);
		assertEquals(b.toString(), null);
		assertEquals(b.chars.length, 2);
	}


	@Test
	public void testAppendArray() {

		ExpandingCharAppender a = new ExpandingCharAppender(2, null, -1);
		a.append("abc".toCharArray(), 0, 3);
		assertEquals(a.toString(), "abc");
		assertEquals(a.chars.length, 5);


		a.append("defghi".toCharArray(), 0, 3);
		assertEquals(a.toString(), "abcdef");

		a.append("defghi".toCharArray(), 4, 2);
		assertEquals(a.toString(), "abcdefhi");

		a.append("012345678901234567890123456789012345678901234567890123456789".toCharArray(), 0, 60);
		assertEquals(a.toString(), "abcdefhi012345678901234567890123456789012345678901234567890123456789");
	}

}