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

import java.io.*;
import java.util.concurrent.*;

/**
 * A concurrent character loader for loading a pool of {@link CharBucket} instances using a {@link java.io.Reader} in a separate thread
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see ConcurrentCharInputReader
 * @see CharBucket
 * @see Entry
 */
class ConcurrentCharLoader implements Runnable {
	private final ArrayBlockingQueue<Object> buckets;
	private final CharBucket end;
	private final FixedInstancePool<CharBucket> instances;

	private Entry<CharBucket> currentBucket;

	private boolean finished = false;
	private boolean active;
	private final Reader reader;
	private final Thread activeExecution;
	private Exception error;

	/**
	 * Creates a {@link FixedInstancePool} with a given amount of {@link CharBucket} instances and starts a thread to fill each one.
	 *
	 * @param reader         The source of characters to extract and fill {@link CharBucket} instances
	 * @param bucketSize     The size of each individual {@link CharBucket}
	 * @param bucketQuantity The number of {@link CharBucket} instances used to extract characters from the given reader.
	 */
	public ConcurrentCharLoader(Reader reader, final int bucketSize, int bucketQuantity) {
		this.end = new CharBucket(-1);
		this.buckets = new ArrayBlockingQueue<Object>(bucketQuantity);

		this.reader = reader;

		this.instances = new FixedInstancePool<CharBucket>(bucketQuantity) {
			@Override
			protected CharBucket newInstance() {
				return new CharBucket(bucketSize);
			}
		};

		finished = false;
		active = true;
		activeExecution = new Thread(this, "unVocity-parsers input reading thread");
		activeExecution.start();
	}

	/**
	 * The {@link CharBucket} loading process that executes in parallel until the input is completely read.
	 * Once the end of the input is reached, the {@link java.io.Reader} instance provided in the constructor is closed.
	 */
	@Override
	public void run() {
		Thread.currentThread().setName("Character reading thread");
		try {
			try {
				int length;
				do {
					Entry<CharBucket> bucket = instances.allocate();
					length = bucket.get().fill(reader);
					if (length != -1) {
						buckets.put(bucket);
					} else {
						instances.release(bucket);
					}
				} while (active && length != -1);
			} finally {
				buckets.put(end);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			finished = true;
			error = e;
		} finally {
			stopReading();
		}
	}

	/**
	 * Returns the next available bucket. Blocks until a bucket is made available or the reading process stops.
	 *
	 * @return the next available bucket.
	 */
	@SuppressWarnings("unchecked")
	public synchronized CharBucket nextBucket() {
		try {
			if (finished) {
				return end;
			}
			if (currentBucket != null) {
				instances.release(currentBucket);
			}

			Object element = buckets.take();
			if (element == end) {
				finished = true;
				return end;
			} else {
				currentBucket = (Entry<CharBucket>) element;
			}
			return currentBucket.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			finished = true;
			return end;
		}
	}

	/**
	 * Stops the {@link CharBucket} loading process and closes the reader provided in the constructor of this class
	 */
	public void stopReading() {
		active = false;
		try {
			reader.close();
		} catch (IOException e) {
			throw new IllegalStateException("Error closing input", e);
		} finally {
			try {
				activeExecution.interrupt();
			} catch (Throwable ex) {
				throw new IllegalStateException("Error stopping input reader thread", ex);
			}
		}
	}

	void reportError() {
		if (error != null) {
			ArgumentUtils.throwUnchecked(error);
		}
	}
}
