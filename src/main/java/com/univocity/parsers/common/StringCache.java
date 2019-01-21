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
 * A simple cache of values associated with strings.
 * @param <T> the type of entry to be stored in the cache
 */
public abstract class StringCache<T> {

	private final Map<String, SoftReference<T>> stringCache = new ConcurrentHashMap<String, SoftReference<T>>();

	/**
	 * Converts a given string to a value
	 * @param input the input to be converted and stored in the cache
	 * @return the value generated from the given string/
	 */
	protected abstract T process(String input);

	/**
	 * Tests whether the cache contains the given key
	 * @param input a string that might have a value associated to it.
	 * @return {@code true} if the cache contains (or contained) a value associated with the given key.
	 */
	public boolean containsKey(String input){
		return stringCache.containsKey(input);
	}

	/**
	 * Associates a value to a string
	 * @param input the string to be associated with a given value
	 * @param value the value associated with the given string
	 */
	public void put(String input, T value) {
		stringCache.put(input, new SoftReference<T>(value));
	}

	/**
	 * Returns the value associated with the given string. If it doesn't exist,
	 * or if it has been evicted, a value will be populated using {@link #process(String)}
	 * @param input the string whose associated value will be returned
	 * @return the value associated with the given string.
	 */
	public T get(String input) {
		if(input == null){
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
}
