/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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

import com.univocity.parsers.common.*;

/**
 * An implementation of {@link CharAppender} that does nothing. Used by {@link ParserOutput} to transparently discard any unwanted input while parsing.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.common.ParserOutput
 * @see com.univocity.parsers.common.input.CharAppender
 */
public class NoopCharAppender implements CharAppender {

	private static final NoopCharAppender instance = new NoopCharAppender();

	/**
	 * Returns the singleton instance of NoopCharAppender
	 *
	 * @return the singleton instance of NoopCharAppender
	 */
	public static CharAppender getInstance() {
		return instance;
	}

	/**
	 * This is a singleton class and cannot be instantiated. Use {@link NoopCharAppender#getInstance()}.
	 */
	private NoopCharAppender() {

	}

	/**
	 * Returns -1 as this appender does nothing.
	 *
	 * @return -1 as this appender does nothing.
	 */
	@Override
	public int length() {
		return -1;
	}

	/**
	 * Returns null as this appender does nothing.
	 *
	 * @return null as this appender does nothing.
	 */
	@Override
	public String getAndReset() {
		return null;
	}

	/**
	 * Does nothing
	 */
	@Override
	public void appendIgnoringWhitespace(char ch) {
	}

	/**
	 * Does nothing
	 */
	@Override
	public void append(char ch) {
	}

	/**
	 * Returns null as this appender does nothing.
	 *
	 * @return null as this appender does nothing.
	 */
	@Override
	public char[] getCharsAndReset() {
		return null;
	}

	/**
	 * Returns 0 as this appender does nothing.
	 *
	 * @return 0 as this appender does nothing.
	 */
	@Override
	public int whitespaceCount() {
		return 0;
	}

	/**
	 * Does nothing
	 */
	@Override
	public void reset() {
	}

	/**
	 * Does nothing
	 */
	@Override
	public void resetWhitespaceCount() {

	}

	/**
	 * Does nothing
	 */
	@Override
	public char[] getChars() {
		return null;
	}

	/**
	 * Does nothing
	 */
	@Override
	public void fill(char ch, int length) {
	}

	/**
	 * Does nothing
	 */
	@Override
	public void appendIgnoringPadding(char ch, char padding) {

	}

	/**
	 * Does nothing
	 */
	@Override
	public void appendIgnoringWhitespaceAndPadding(char ch, char padding) {

	}

	/**
	 * Does nothing
	 */
	@Override
	public void prepend(char ch) {
	}

	/**
	 * Does nothing
	 */
	@Override
	public void updateWhitespace() {

	}

	@Override
	public char appendUntil(char ch, CharInput input, char stop) {
		for (; ch != stop; ch = input.nextChar()) ;
		return ch;
	}

	@Override
	public final char appendUntil(char ch, CharInput input, char stop1, char stop2) {
		for (; ch != stop1 && ch != stop2; ch = input.nextChar()) ;
		return ch;
	}

	@Override
	public final char appendUntil(char ch, CharInput input, char stop1, char stop2, char stop3) {
		for (; ch != stop1 && ch != stop2 && ch != stop3; ch = input.nextChar()) ;
		return ch;
	}

	/**
	 * Does nothing
	 */
	@Override
	public void append(char[] ch, int from, int length) {

	}

	/**
	 * Does nothing
	 */
	@Override
	public void prepend(char ch1, char ch2) {

	}

	/**
	 * Does nothing
	 */
	@Override
	public void prepend(char[] chars) {

	}

	/**
	 * Does nothing
	 */
	@Override
	public void append(char[] ch) {

	}

	/**
	 * Does nothing
	 */
	@Override
	public void append(String string) {

	}

	/**
	 * Does nothing
	 */
	@Override
	public void append(String string, int from, int to) {

	}

	/**
	 * Does nothing
	 */
	@Override
	public char charAt(int i) {
		return 0;
	}

	/**
	 * Does nothing
	 */
	@Override
	public CharSequence subSequence(int i, int i1) {
		return null;
	}

	/**
	 * Does nothing
	 */
	@Override
	public void append(int ch) {

	}

	/**
	 * Does nothing
	 */
	@Override
	public void append(int[] ch) {

	}
}
