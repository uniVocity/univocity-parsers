/*******************************************************************************
 * Copyright 2019 Univocity Software Pty Ltd
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
package com.univocity.parsers.common;

import java.lang.ref.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A simple cache of values associated with strings. It is built to simply prevent generating the same value over
 * and over again over a short period of time. Once its size limit is reached, the cache will be fully cleared.
 * Do not use this as a general purpose caching solution. This meant for storing values that can be cheaply produced
 * and re-generating them every now and then won't incur in any major performance impact.
 *
 * @param <T> the type of entry to be stored in the cache
 */
public abstract class StringCache<T> {

	private static final int DEFAULT_SIZE_LIMIT = 16384;
	private static final int DEFAULT_MAX_STRING_LENGTH = 1024;
	private final Map<String, SoftReference<T>> stringCache = new ConcurrentHashMap<String, SoftReference<T>>();
	private int sizeLimit = DEFAULT_SIZE_LIMIT;
	private int maxStringLength = DEFAULT_MAX_STRING_LENGTH;

	/**
	 * Converts a given string to a value
	 *
	 * @param input the input to be converted and stored in the cache
	 * @return the value generated from the given string/
	 */
	protected abstract T process(String input);

	/**
	 * Tests whether the cache contains the given key
	 *
	 * @param input a string that might have a value associated to it.
	 * @return {@code true} if the cache contains (or contained) a value associated with the given key.
	 */
	public boolean containsKey(String input) {
		return stringCache.containsKey(input);
	}

	/**
	 * Returns the size limit of this string cache. Defaults to 16,384. For simplicity, when
	 * this limit is reached, the entire cache is cleared.
	 *
	 * @return the maximum number of entries that can be stored in this string cache.
	 */
	public int getSizeLimit() {
		return sizeLimit;
	}

	/**
	 * Defines the size limit of this string cache (16,384 by default). For simplicity, when
	 * this limit is reached, the entire cache is cleared.
	 *
	 * @param sizeLimit the maximum number of entries that can be stored in this string cache.
	 */
	public void setSizeLimit(int sizeLimit) {
		if (sizeLimit <= 0) {
			sizeLimit = DEFAULT_SIZE_LIMIT;
		}
		this.sizeLimit = sizeLimit;
	}

	/**
	 * Associates a value to a string
	 *
	 * @param input the string to be associated with a given value
	 * @param value the value associated with the given string
	 */
	public void put(String input, T value) {
		if (input == null || input.length() > maxStringLength) {
			return;
		}
		if (stringCache.size() >= sizeLimit) {
			stringCache.clear();
		}
		stringCache.put(input, new SoftReference<T>(value));

	}

	/**
	 * Returns the value associated with the given string. If it doesn't exist,
	 * or if it has been evicted, a value will be populated using {@link #process(String)}
	 *
	 * @param input the string whose associated value will be returned
	 * @return the value associated with the given string.
	 */
	public T get(String input) {
		if (input == null || input.length() > maxStringLength) {
			return null;
		}
		SoftReference<T> ref = stringCache.get(input);
		T out;
		if (ref == null || ref.get() == null) {
			out = process(input);
			ref = new SoftReference<T>(out);
			stringCache.put(input, ref);
		} else {
			out = ref.get();
		}
		return out;
	}

	/**
	 * Removes all entries stored in this cache.
	 */
	public void clear() {
		stringCache.clear();
	}

	/**
	 * Returns the maximum length a {@code String} key can have to be used as a key in this cache.
	 * If the {@code String} length exceeds this limit, the value associated with it won't be cached.
	 * Defaults to 1024
	 *
	 * @return the maximum length a {@code String} key can have
	 */
	public int getMaxStringLength() {
		return maxStringLength;
	}

	/**
	 * Returns the maximum length a {@code String} key can have to be used as a key in this cache.
	 * If the {@code String} length exceeds this limit, the value associated with it won't be cached.
	 * Defaults to 1024
	 *
	 * @param maxStringLength the maximum length a {@code String} key can have
	 */
	public void setMaxStringLength(int maxStringLength) {
		this.maxStringLength = maxStringLength;
	}
}
