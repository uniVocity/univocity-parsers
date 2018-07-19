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
package com.univocity.parsers.common.input.concurrent;

import java.io.*;
import java.util.*;

/**
 * A buffer of characters.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
class CharBucket {
	/**
	 * The bucket data
	 */
	final char[] data;

	/**
	 * The number of characters this bucket contain. It is modified every time {@link CharBucket#fill(Reader)} is called.
	 */
	int length = -1;

	/**
	 * Creates a bucket capable of holding a fixed number of characters
	 * @param bucketSize the maximum capacity of the bucket
	 */
	public CharBucket(int bucketSize) {
		if (bucketSize > 0) {
			data = new char[bucketSize];
		} else {
			data = new char[0];
		}
	}

	/**
	 * Creates a bucket capable of holding a fixed number of characters
	 * @param bucketSize the maximum capacity of the bucket
	 * @param fillWith a character used to fill all positions of the bucket.
	 */
	public CharBucket(int bucketSize, char fillWith) {
		this(bucketSize);
		if (bucketSize > 0) {
			Arrays.fill(data, fillWith);
		}
	}

	/**
	 * Fills the bucket with the characters take from a {@link java.io.Reader}
	 * <p> The {@link CharBucket#length} attribute will be updated with the number of characters extracted
	 * @param reader the source of characters used to fill the bucket
	 * @return the number of characters extracted from the reader
	 * @throws IOException if any error occurs while extracting characters from the reader
	 */
	public int fill(Reader reader) throws IOException {
		length = reader.read(data, 0, data.length);
		return length;
	}

	/**
	 * Returns true if the bucket is empty (i.e. length <= 0), false otherwise.
	 * @return true if the bucket is empty (i.e. length <= 0), false otherwise.
	 */
	public boolean isEmpty() {
		return length <= 0;
	}
}
