/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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

import java.io.*;
import java.nio.charset.*;

import static com.univocity.parsers.common.ArgumentUtils.*;

/**
 * A wrapper for an {@link InputStream} that attempts to detect a Byte Order Mark (BOM) in the input
 * and derive the character encoding that should be used to decode the incoming content.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public final class BomInput extends InputStream {


	public static final byte[] UTF_8_BOM = toByteArray(0xEF, 0xBB, 0xBF);
	public static final byte[] UTF_16BE_BOM = toByteArray(0xFE, 0xFF);
	public static final byte[] UTF_16LE_BOM = toByteArray(0xFF, 0xFE);
	public static final byte[] UTF_32BE_BOM = toByteArray(0x00, 0x00, 0xFE, 0xFF);
	public static final byte[] UTF_32LE_BOM = toByteArray(0xFF, 0xFE, 0x00, 0x00);

	private int bytesRead;
	private int b1;
	private int b2;
	private int b3;
	private int b4;
	private String encoding;

	private final InputStream input;
	private IOException exception;

	/**
	 * Wraps an {@link InputStream} and reads the first bytes found on it to attempt to read a BOM.
	 *
	 * @param input the input whose first bytes should be analyzed.
	 */
	public BomInput(InputStream input) {
		this.input = input;

		try { //This looks shitty on purpose (all in the name of speed).
			if ((b1 = next()) == 0xEF) {
				if ((b2 = next()) == 0xBB) {
					if ((b3 = next()) == 0xBF) {
						setEncoding("UTF-8");
					}
				}
			} else if (b1 == 0xFE) {
				if ((b2 = next()) == 0xFF) {
					setEncoding("UTF-16BE");
				}
			} else if (b1 == 0xFF) {
				if ((b2 = next()) == 0xFE) {
					if ((b3 = next()) == 0x00) {
						if ((b4 = next()) == 0x00) {
							setEncoding("UTF-32LE");
						} else {
							setEncoding("UTF-16LE"); //gotcha!
						}
					} else {
						setEncoding("UTF-16LE"); //gotcha!
					}
				}
			} else if (b1 == 0x00) {
				if ((b2 = next()) == 0x00) {
					if ((b3 = next()) == 0xFE) {
						if ((b4 = next()) == 0xFF) {
							setEncoding("UTF-32BE");
						}
					}
				}
			}
		} catch (IOException e) {
			// store the exception for later. We want the wrapper to behave exactly like the original input stream and
			// might need to return any bytes read before this blew up.
			exception = e;
		}
	}

	private void setEncoding(String encoding) {
		this.encoding = encoding;
		if (encoding.equals("UTF-16LE")) { //gotcha!
			if (bytesRead == 3) { //third byte not a 0x00
				bytesRead = 1;
				b1 = b3;
				return;
			} else if (bytesRead == 4) { //fourth byte not a 0x00
				bytesRead = 2;
				b1 = b3;
				b2 = b4;
				return;
			}
		}
		this.bytesRead = 0;
	}

	private int next() throws IOException {
		int out = input.read();
		bytesRead++;
		return out;
	}

	@Override
	public final int read() throws IOException {
		if (bytesRead > 0) {
			int out = b1;
			if (bytesRead == 2) {
				out = b2;
			} else if (bytesRead == 3) {
				out = b3;
			} else if (bytesRead == 4) {
				out = b4;
			}

			// Ensures that if the original input stream returned a byte, it will be consumed.
			// In case of exceptions, bytes produced prior to the exception will still be returned.
			// Once the last byte has been consumed, the original exception will be thrown.
			if (--bytesRead == 0 && exception != null) {
				throw exception;
			}
			return out;
		}
		return input.read();
	}

	/**
	 * Returns a flag indicating whether or not all bytes read from the wrapped input stream have been consumed. This
	 * allows client code to determine if the original input stream can be used directly and safely, or if this
	 * {@code BomInput} wrapper class should be used instead.
	 *
	 * If there are stored bytes that need to be consumed before the wrapped input stream is consumed again,
	 * this method will return {@code true}.
	 *
	 * @return {@code false} if there are no bytes stored and the original input stream can be used directly. If this wrapper
	 * needs to be used to return stored bytes before, then {@code true} will be returned.
	 */
	public final boolean hasBytesStored() {
		return bytesRead > 0;
	}

	/**
	 * Returns the detected {@link Charset} determined by the Byte Order Mark (BOM) available in the
	 * input provided in the constructor of this class.
	 *
	 * If no BOM was detected, this method will return {@code null}.
	 *
	 * @return the detected {@link Charset} or {@code null} if a BOM could not be matched.
	 */
	public final Charset getCharset() {
		if (encoding == null) {
			return null;
		}
		return Charset.forName(encoding);
	}

	/**
	 * Returns the detected encoding name determined by the Byte Order Mark (BOM) available in the
	 * input provided in the constructor of this class.
	 *
	 * If no BOM was detected, this method will return {@code null}.
	 *
	 * @return the detected encoding name or {@code null} if a BOM could not be matched.
	 */
	public final String getEncoding() {
		return encoding;
	}
}
