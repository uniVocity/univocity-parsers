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
package com.univocity.parsers.common;

/**
 * An {@link Iterable} result that provides the current parsing context
 * through the {@link #getContext()} method
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public interface IterableResult<T, C extends Context> extends Iterable<T> {

	/**
	 * Returns the current parsing {@link Context}, if available
	 * @return the contextual object with information about an ongoing parsing process
	 */
	C getContext();

	@Override
	ResultIterator<T, C> iterator();
}
