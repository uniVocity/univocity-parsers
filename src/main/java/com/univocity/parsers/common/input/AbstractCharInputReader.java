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

import com.univocity.parsers.common.*;

import java.io.*;
import java.util.*;

/**
 * The base class for implementing different flavours of {@link CharInputReader}.
 *
 * <p> It provides the essential conversion of sequences of newline characters defined by {@link Format#getLineSeparator()} into the normalized newline character provided in {@link Format#getNormalizedNewline()}.
 * <p> It also provides a default implementation for most of the methods specified by the {@link CharInputReader} interface.
 * <p> Extending classes must essentially read characters from a given {@link java.io.Reader} and assign it to the public {@link AbstractCharInputReader#buffer} when requested (in the {@link AbstractCharInputReader#reloadBuffer()} method).
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.common.Format
 * @see com.univocity.parsers.common.input.DefaultCharInputReader
 * @see com.univocity.parsers.common.input.concurrent.ConcurrentCharInputReader
 */

public abstract class AbstractCharInputReader implements CharInputReader {

	private final CharAppender NOOP = NoopCharAppender.getInstance();
	private final StringBuilder commentBuilder = new StringBuilder(50);
	private boolean lineSeparatorDetected = false;
	private final boolean detectLineSeparator;
	private List<InputAnalysisProcess> inputAnalysisProcesses = null;
	private char lineSeparator1;
	private char lineSeparator2;
	private final char normalizedLineSeparator;

	private long lineCount;
	private long charCount;

	private char delimiter = '\0';
	private char escape = '\0';
	private char quoteEscape = '\0';
	private char escapeEscape = '\0';

	/**
	 * Current position in the buffer
	 */
	public int i;

	/**
	 * The buffer itself
	 */
	public char[] buffer;

	/**
	 * Number of characters available in the buffer.
	 */
	public int length = -1;
	private boolean incrementLineCount = false;
	private boolean normalizeLineEndings = true;

	/**
	 * Creates a new instance that attempts to detect the newlines used in the input automatically.
	 *
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link Format#getNormalizedNewline()}) that is used to replace any lineSeparator sequence found in the input.
	 */
	public AbstractCharInputReader(char normalizedLineSeparator) {
		detectLineSeparator = true;
		submitLineSeparatorDetector();
		this.lineSeparator1 = '\0';
		this.lineSeparator2 = '\0';
		this.normalizedLineSeparator = normalizedLineSeparator;
	}

	private void submitLineSeparatorDetector() {
		if (detectLineSeparator && !lineSeparatorDetected) {
			addInputAnalysisProcess(new LineSeparatorDetector() {
				@Override
				protected void apply(char separator1, char separator2) {
					if (separator1 != '\0') {
						lineSeparatorDetected = true;
						lineSeparator1 = separator1;
						lineSeparator2 = separator2;
					} else {
						setLineSeparator(Format.getSystemLineSeparator());
					}
				}
			});
		}
	}

	/**
	 * Creates a new instance with the mandatory characters for handling newlines transparently.
	 *
	 * @param lineSeparator           the sequence of characters that represent a newline, as defined in {@link Format#getLineSeparator()}
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link Format#getNormalizedNewline()}) that is used to replace any lineSeparator sequence found in the input.
	 */
	public AbstractCharInputReader(char[] lineSeparator, char normalizedLineSeparator) {
		this.detectLineSeparator = false;
		this.normalizedLineSeparator = normalizedLineSeparator;
		setLineSeparator(lineSeparator);
	}

	private void setLineSeparator(char[] lineSeparator) {
		if (lineSeparator == null || lineSeparator.length == 0) {
			throw new IllegalArgumentException("Invalid line separator. Expected 1 to 2 characters");
		}
		if (lineSeparator.length > 2) {
			throw new IllegalArgumentException("Invalid line separator. Up to 2 characters are expected. Got " + lineSeparator.length + " characters.");
		}
		this.lineSeparator1 = lineSeparator[0];
		this.lineSeparator2 = lineSeparator.length == 2 ? lineSeparator[1] : '\0';
	}

	/**
	 * Passes the {@link java.io.Reader} provided in the {@link AbstractCharInputReader#start(Reader)} method to the extending class so it can begin loading characters from it.
	 *
	 * @param reader the {@link java.io.Reader} provided in {@link AbstractCharInputReader#start(Reader)}
	 */
	protected abstract void setReader(Reader reader);

	/**
	 * Informs the extending class that the buffer has been read entirely and requests for another batch of characters.
	 * Implementors must assign the new character buffer to the public {@link AbstractCharInputReader#buffer} attribute, as well as the number of characters available to the public {@link AbstractCharInputReader#length} attribute.
	 * To notify the input does not have any more characters, {@link AbstractCharInputReader#length} must receive the <b>-1</b> value
	 */
	protected abstract void reloadBuffer();

	@Override
	public final void start(Reader reader) {
		stop();
		setReader(reader);
		lineCount = 0;

		lineSeparatorDetected = false;
		submitLineSeparatorDetector();
		updateBuffer();
		if (length > 0) {
			i++;
		}
	}

	/**
	 * Requests the next batch of characters from the implementing class and updates
	 * the character count.
	 *
	 * <p> If there are no more characters in the input, the reading will stop by invoking the {@link AbstractCharInputReader#stop()} method.
	 */
	private void updateBuffer() {
		reloadBuffer();

		charCount += i;
		i = 0;

		if (length == -1) {
			stop();
			incrementLineCount = true;
		}

		if (inputAnalysisProcesses != null) {
			try {
				for (InputAnalysisProcess process : inputAnalysisProcesses) {
					process.execute(buffer, length);
				}
			} finally {
				inputAnalysisProcesses = null;
			}
		}
	}

	/**
	 * Submits a custom {@link InputAnalysisProcess} to analyze the input buffer and potentially discover configuration options such as
	 * column separators is CSV, data formats, etc. The process will be execute only once.
	 *
	 * @param inputAnalysisProcess a custom process to analyze the contents of the input buffer.
	 */
	public final void addInputAnalysisProcess(InputAnalysisProcess inputAnalysisProcess) {
		if (inputAnalysisProcess == null) {
			return;
		}
		if (this.inputAnalysisProcesses == null) {
			inputAnalysisProcesses = new ArrayList<InputAnalysisProcess>();
		}
		inputAnalysisProcesses.add(inputAnalysisProcess);
	}

	private void throwEOFException() {
		if (incrementLineCount) {
			lineCount++;
		}
		throw new EOFException();
	}

	@Override
	public final char nextChar() {
		if (length == -1) {
			throwEOFException();
		}

		char ch = buffer[i - 1];

		if (i >= length) {
			updateBuffer();
		}

		i++;

		if (lineSeparator1 == ch && (lineSeparator2 == '\0' || length != -1 && lineSeparator2 == buffer[i - 1])) {
			lineCount++;
			if (normalizeLineEndings) {
				if (lineSeparator2 == '\0') {
					return normalizedLineSeparator;
				}
				ch = normalizedLineSeparator;

				if (i >= length) {
					if (length != -1) {
						updateBuffer();
					} else {
						throwEOFException();
					}
				}
				i++;

			}
		}

		return ch;
	}

	@Override
	public final long lineCount() {
		return lineCount;
	}


	@Override
	public final void skipLines(long lines) {
		if (lines < 1) {
			return;
		}
		long expectedLineCount = this.lineCount + lines;

		try {
			do {
				nextChar();
			} while (lineCount < expectedLineCount);
			if (lineCount < lines) {
				throw new IllegalArgumentException("Unable to skip " + lines + " lines from line " + (expectedLineCount - lines) + ". End of input reached");
			}
		} catch (EOFException ex) {
			throw new IllegalArgumentException("Unable to skip " + lines + " lines from line " + (expectedLineCount - lines) + ". End of input reached");
		}
	}

	@Override
	public String readComment() {
		commentBuilder.setLength(0);
		long expectedLineCount = lineCount + 1;
		try {
			do {
				char ch = nextChar();
				if (lineCount < expectedLineCount) {
					commentBuilder.append(ch);
				} else {
					return commentBuilder.toString();
				}
			} while (true);
		} catch (EOFException ex) {
			return commentBuilder.toString();
		}
	}

	@Override
	public final long charCount() {
		return charCount + i;
	}

	@Override
	public final void enableNormalizeLineEndings(boolean normalizeLineEndings) {
		this.normalizeLineEndings = normalizeLineEndings;
	}

	@Override
	public char[] getLineSeparator() {
		if (lineSeparator2 != '\0') {
			return new char[]{lineSeparator1, lineSeparator2};
		} else {
			return new char[]{lineSeparator1};
		}
	}

	@Override
	public final char appendUntilDelimiter(char ch, CharAppender appender) {
		if (appender != NOOP) {
			for (DefaultCharAppender a = (DefaultCharAppender) appender; ch != delimiter && ch != normalizedLineSeparator; ch = nextChar()){
				a.chars[a.index++] = ch;
			}
		} else {
			for (; ch != delimiter && ch != normalizedLineSeparator; ch = nextChar()) ;
		}
		return ch;
	}

	@Override
	public final char appendUntilDelimiterOrEscape(char ch, CharAppender appender) {
		if (appender != NOOP) {
			for (DefaultCharAppender a = (DefaultCharAppender) appender; ch != delimiter && ch != normalizedLineSeparator && ch != escape; ch = nextChar()){
				a.chars[a.index++] = ch;
			}
		} else {
			for (; ch != delimiter && ch != normalizedLineSeparator && ch != escape; ch = nextChar()) ;
		}
		return ch;
	}

	@Override
	public final char appendUtilAnyEscape(char ch, CharAppender appender) {
		if (appender != NOOP) {
			for (DefaultCharAppender a = (DefaultCharAppender) appender; ch != escape && ch != quoteEscape && ch != escapeEscape; ch = nextChar()){
				a.chars[a.index++] = ch;
			}
		} else {
			for (; ch != escape && ch != quoteEscape && ch != escapeEscape; ch = nextChar()) ;
		}
		return ch;
	}

	@Override
	public final void setDelimiter(char ch) {
		delimiter = ch;
	}

	@Override
	public final void setEscape(char ch) {
		escape = ch;
	}

	@Override
	public final void setQuoteEscape(char ch) {
		quoteEscape = ch;
	}

	@Override
	public final void setEscapeEscape(char ch) {
		escapeEscape = ch;
	}

	@Override
	public final char skipWhitespace(char ch) {
		while (ch <= ' ' && ch != delimiter && ch != normalizedLineSeparator) {
			ch = nextChar();
		}
		return ch;
	}
}
