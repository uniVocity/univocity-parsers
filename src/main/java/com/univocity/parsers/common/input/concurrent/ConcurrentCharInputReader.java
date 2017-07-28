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
package com.univocity.parsers.common.input.concurrent;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.input.*;

import java.io.*;

/**
 * A concurrent CharInputReader that loads batches of characters in a separate thread and assigns them to buffer in {@link AbstractCharInputReader} when requested.
 *
 * <p> This class loads "buckets" of characters in the background and provides them sequentially to the {@link ConcurrentCharInputReader#buffer}
 * attribute in {@link AbstractCharInputReader}.
 * <p> The bucket loading process will block and wait while all buckets are full.
 * <p> Similarly, the reader will block while all buckets are empty.
 *
 * This CharInputReader implementation provides a better throughput than {@link DefaultCharInputReader} when reading large inputs ({@code > 100 mb}).
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see CharInputReader
 * @see ConcurrentCharLoader
 * @see CharBucket
 */
public class ConcurrentCharInputReader extends AbstractCharInputReader {

	private ConcurrentCharLoader bucketLoader;
	private final int bucketSize;
	private final int bucketQuantity;

	/**
	 * Creates a new instance with the mandatory characters for handling newlines transparently. Line separators will be detected automatically.
	 *
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link Format#getNormalizedNewline()})
	 *                                that is used to replace any lineSeparator sequence found in the input.
	 * @param bucketSize              the size of an each individual "bucket" used to store characters read from the input.
	 * @param bucketQuantity          the number of "buckets" to load in memory. Note the reader will stop if all buckets are full.
	 * @param whitespaceRangeStart    starting range of characters considered to be whitespace.
	 */
	public ConcurrentCharInputReader(char normalizedLineSeparator, int bucketSize, int bucketQuantity, int whitespaceRangeStart) {
		super(normalizedLineSeparator, whitespaceRangeStart);
		this.bucketSize = bucketSize;
		this.bucketQuantity = bucketQuantity;
	}

	/**
	 * Creates a new instance with the mandatory characters for handling newlines transparently.
	 *
	 * @param lineSeparator           the sequence of characters that represent a newline, as defined in {@link Format#getLineSeparator()}
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link Format#getNormalizedNewline()})
	 *                                that is used to replace any lineSeparator sequence found in the input.
	 * @param bucketSize              the size of an each individual "bucket" used to store characters read from the input.
	 * @param bucketQuantity          the number of "buckets" to load in memory. Note the reader will stop if all buckets are full.
	 * @param whitespaceRangeStart    starting range of characters considered to be whitespace.
	 */
	public ConcurrentCharInputReader(char[] lineSeparator, char normalizedLineSeparator, int bucketSize, int bucketQuantity, int whitespaceRangeStart) {
		super(lineSeparator, normalizedLineSeparator, whitespaceRangeStart);
		this.bucketSize = bucketSize;
		this.bucketQuantity = bucketQuantity;
	}

	/**
	 * Stops the CharInputReader from reading characters from the {@link java.io.Reader} provided in {@link ConcurrentCharInputReader#start(Reader)} and closes it.
	 * Also stops the input reading thread.
	 */
	@Override
	public void stop() {
		if (bucketLoader != null) {
			bucketLoader.stopReading();
			bucketLoader.reportError();

			if(bucketLoader.notification != null){
				BomInput.BytesProcessedNotification notification = bucketLoader.notification;
				bucketLoader = null;
				unwrapInputStream(notification);
			}
		}
	}

	/**
	 * Starts an input reading thread to load characters from the given reader into "buckets" of characters
	 */
	@Override
	protected void setReader(Reader reader) {
		stop();
		bucketLoader = new ConcurrentCharLoader(reader, bucketSize, bucketQuantity);
		bucketLoader.reportError();
	}

	/**
	 * Assigns the next "bucket" of characters to the {@link ConcurrentCharInputReader#buffer} attribute, and updates the {@link ConcurrentCharInputReader#length} to the number of characters read.
	 */
	@Override
	protected void reloadBuffer() {
		CharBucket currentBucket = bucketLoader.nextBucket();
		bucketLoader.reportError();
		super.buffer = currentBucket.data;
		super.length = currentBucket.length;
	}
}
