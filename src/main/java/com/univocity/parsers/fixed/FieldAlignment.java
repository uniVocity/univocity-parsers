/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
package com.univocity.parsers.fixed;

/**
 * Alignment of text in a fixed-width field.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public enum FieldAlignment {
	/**
	 * Aligns values to the left
	 */
	LEFT,

	/**
	 * Centralizes values
	 */
	CENTER,

	/**
	 * Aligns values to the right
	 */
	RIGHT;

	/**
	 * Calculates how many whites paces to introduce before a value so it is printed as specified by the alignment type.
	 * @param totalLength the total length available to write for a given field
	 * @param lengthToWrite the length of a value that will be written to the field
	 * @return the number of whites paces to introduce in before the value to make it align as specified.
	 */
	public int calculatePadding(int totalLength, int lengthToWrite) {
		if (this == LEFT || totalLength <= lengthToWrite) {
			return 0;
		}

		if (this == RIGHT) {
			return totalLength - lengthToWrite;
		}

		int padding = (totalLength / 2) - (lengthToWrite / 2);
		if (lengthToWrite + padding > totalLength) {
			padding--;
		}
		return padding;
	}
}
