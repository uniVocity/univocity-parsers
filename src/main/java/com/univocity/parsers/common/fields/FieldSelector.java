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
package com.univocity.parsers.common.fields;

/**
 *
 * Interface used to identify classes capable of selecting fields and returning their positions in a given sequence.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public interface FieldSelector {

	/**
	 * Returns the indexes of any selected fields that are part of a sequence of headers.
	 * @param headers the sequence of headers that might have some elements selected by this FieldSelector
	 * @return the positions of all selected elements in the given headers sequence.
	 */
	int[] getFieldIndexes(String[] headers);

	/**
	 * Returns a string that represents the current field selection
	 * @return a string that represents the current field selection
	 */
	String describe();
}
