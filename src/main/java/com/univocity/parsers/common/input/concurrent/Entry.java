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

/**
 * An entry used by the {@link FixedInstancePool}

 * @param <T> the type of this entry.
 *
 * @see FixedInstancePool
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
class Entry<T> {
	final T entry;
	final int index;

	/**
	 * Creates a new entry with an object and its position in the {@link FixedInstancePool}
	 * @param entry the value in this entry
	 * @param index the position of this entry in the {@link FixedInstancePool}
	 */
	Entry(T entry, int index) {
		this.entry = entry;
		this.index = index;
	}

	/**
	 * Returns the object stored in this  {@link FixedInstancePool} entry.
	 * @return the object stored in this  {@link FixedInstancePool} entry.
	 */
	public T get() {
		return this.entry;
	}
}
