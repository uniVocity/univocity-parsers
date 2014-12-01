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
package com.univocity.parsers.examples;

import java.io.*;
import java.util.*;

import com.univocity.test.*;

/**
 * Just a parent class for all examples provided, with some utility methods.
 */
abstract class Example extends OutputTester {

	protected Example() {
		super("examples/expectedOutputs", "UTF-8");
	}

	/**
	 * Creates a reader for a resource in the relative path
	 * @param relativePath relative path of the resource to be read
	 * @return a reader of the resource
	 */
	public Reader getReader(String relativePath) {
		try {
			return new InputStreamReader(this.getClass().getResourceAsStream(relativePath), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unable to read input", e);
		}
	}

	/**
	 * Prints a collection of rows to the standard output
	 * @param rows A collection of rows to be printed.
	 */
	public void printAndValidate(Collection<?> rows) {
		printAndValidate(null, rows);
	}

	/**
	 * Prints a collection of rows to the standard output, with headings
	 * @param headers the description of each
	 * @param rows the rows to print then validate
	 */
	public void printAndValidate(Object[] headers, Collection<?> rows) {

		if (headers != null) {
			println(Arrays.toString(headers));
			println("=======================");
		}

		int rowCount = 1;
		for (Object row : rows) {
			println((rowCount++) + " " + Arrays.toString((Object[]) row));
			println("-----------------------");
		}

		printAndValidate();
	}
}
