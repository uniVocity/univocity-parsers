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

	private final ExpandingCharAppender tmp;
	private boolean lineSeparatorDetected;
	private final boolean detectLineSeparator;
	private List<InputAnalysisProcess> inputAnalysisProcesses;
	private char lineSeparator1;
	private char lineSeparator2;
	private final char normalizedLineSeparator;

	private long lineCount;
	private long charCount;
	private int recordStart;
	final int whitespaceRangeStart;

	/**
	 * Current position in the buffer
	 */
	public int i;

	private char ch;

	/**
	 * The buffer itself
	 */
	public char[] buffer;

	/**
	 * Number of characters available in the buffer.
	 */
	public int length = -1;
	private boolean incrementLineCount;
	private boolean normalizeLineEndings = true;

	/**
	 * Creates a new instance that attempts to detect the newlines used in the input automatically.
	 *
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link Format#getNormalizedNewline()}) that is used to replace any lineSeparator sequence found in the input.
	 * @param whitespaceRangeStart    starting range of characters considered to be whitespace.
	 */
	public AbstractCharInputReader(char normalizedLineSeparator, int whitespaceRangeStart) {
		this(null, normalizedLineSeparator, whitespaceRangeStart);
	}

	/**
	 * Creates a new instance with the mandatory characters for handling newlines transparently.
	 *
	 * @param lineSeparator           the sequence of characters that represent a newline, as defined in {@link Format#getLineSeparator()}
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link Format#getNormalizedNewline()}) that is used to replace any lineSeparator sequence found in the input.
	 * @param whitespaceRangeStart    starting range of characters considered to be whitespace.
	 */
	public AbstractCharInputReader(char[] lineSeparator, char normalizedLineSeparator, int whitespaceRangeStart) {
		this.whitespaceRangeStart = whitespaceRangeStart;
		this.tmp = new ExpandingCharAppender(4096, null, whitespaceRangeStart);
		if (lineSeparator == null) {
			detectLineSeparator = true;
			submitLineSeparatorDetector();
			this.lineSeparator1 = '\0';
			this.lineSeparator2 = '\0';
		} else {
			setLineSeparator(lineSeparator);
			this.detectLineSeparator = false;
		}

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

	protected final void unwrapInputStream(BomInput.BytesProcessedNotification notification){
		InputStream inputStream = notification.input;
		String encoding = notification.encoding;

		if (encoding != null) {
			try {
				start(new InputStreamReader(inputStream, encoding));
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		} else {
			length = -1;
			start(new InputStreamReader(inputStream));
		}
	}

	@Override
	public final void start(Reader reader) {
		stop();
		setReader(reader);
		lineCount = 0;

		lineSeparatorDetected = false;
		submitLineSeparatorDetector();

		updateBuffer();

		//if the input has been properly decoded with the correct UTF* character set, but has a BOM marker, we can safely discard it.
		if (length > 0 && buffer[0] == '\uFEFF') { //regardless of the UTF* encoding used, the BOM bytes always produce the '\uFEFF' character when decoded.
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
		if (length - recordStart > 0 && buffer != null) {
			tmp.append(buffer, recordStart, length - recordStart);
		}
		recordStart = 0;
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
				if(length > 4) {
					inputAnalysisProcesses = null;
				}
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
		ch = '\0';
		throw new EOFException();
	}

	@Override
	public final char nextChar() {
		if (length == -1) {
			throwEOFException();
		}

		ch = buffer[i++];

		if (i >= length) {
			updateBuffer();
		}

		if (lineSeparator1 == ch && (lineSeparator2 == '\0' || length != -1 && lineSeparator2 == buffer[i])) {
			lineCount++;
			if (normalizeLineEndings) {
				ch = normalizedLineSeparator;
				if (lineSeparator2 == '\0') {
					return ch;
				}
				if (++i >= length) {
					if (length != -1) {
						updateBuffer();
					} else {
						throwEOFException();
					}
				}
			}
		}

		return ch;
	}

	@Override
	public final char getChar() {
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
		long expectedLineCount = lineCount + 1;
		try {
			do {
				char ch = nextChar();
				if (ch <= ' ' && whitespaceRangeStart < ch) {
					ch = skipWhitespace(ch, normalizedLineSeparator, normalizedLineSeparator);
				}
				tmp.appendUntil(ch, this, normalizedLineSeparator, normalizedLineSeparator);

				if (lineCount < expectedLineCount) {
					tmp.appendIgnoringWhitespace(nextChar());
				} else {
					tmp.updateWhitespace();
					return tmp.getAndReset();
				}
			} while (true);
		} catch (EOFException ex) {
			tmp.updateWhitespace();
			return tmp.getAndReset();
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
	public final char skipWhitespace(char ch, char stopChar1, char stopChar2) {
		while (ch <= ' ' && ch != stopChar1 && ch != normalizedLineSeparator && ch != stopChar2 && whitespaceRangeStart < ch) {
			ch = nextChar();
		}
		return ch;
	}

	@Override
	public final String currentParsedContent() {
		if (tmp.length() == 0) {
			if (i > recordStart) {
				return new String(buffer, recordStart, i - recordStart);
			}
			return null;
		}
		if (i > recordStart) {
			tmp.append(buffer, recordStart, i - recordStart);
		}
		return tmp.getAndReset();

	}

	@Override
	public final void markRecordStart() {
		tmp.reset();
		recordStart = i % length;
	}

	@Override
	public String getString(char ch, char stop, boolean trim, String nullValue, int maxLength) {
		if (i == 0) {
			return null;
		}
		int i = this.i;
		for (; ch != stop; ch = buffer[i++]) {
			if (i >= length) {
				return null;
			}
			if (lineSeparator1 == ch && (lineSeparator2 == '\0' || lineSeparator2 == buffer[i])) {
				break;
			}
		}

		int pos = this.i - 1;
		int len = i - this.i;
		if (len > maxLength) { //validating before trailing whitespace handling so this behaves as an appender.
			return null;
		}

		this.i = i - 1;

		if (trim) {
			i = i - 2;
			while (buffer[i] <= ' ' && whitespaceRangeStart < buffer[i]) {
				len--;
				i--;
			}
		}

		String out;
		if (len <= 0) {
			out = nullValue;
		} else {
			out = new String(buffer, pos, len);
		}

		nextChar();

		return out;
	}
}
