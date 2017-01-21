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

import com.univocity.test.*;

import java.io.*;
import java.util.*;

/**
 * Just a parent class for all examples provided, with some utility methods.
 */
public abstract class Example extends OutputTester {

	protected Example() {
		super("examples/expectedOutputs", "UTF-8");
	}

	/**
	 * Creates a reader for a resource in the relative path
	 *
	 * @param relativePath relative path of the resource to be read
	 *
	 * @return a reader of the resource
	 */
	public static Reader getReader(String relativePath) {
		try {
			return new InputStreamReader(Example.class.getResourceAsStream(relativePath), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unable to read input", e);
		}
	}

	/**
	 * Prints a collection of rows to the standard output
	 *
	 * @param rows A collection of rows to be printed.
	 */
	public void printAndValidate(Collection<?> rows) {
		printAndValidate(null, rows);
	}

	/**
	 * Prints a collection of rows to the standard output, with headings
	 *
	 * @param headers the description of each
	 * @param rows    the rows to print then validate
	 */
	public void printAndValidate(Object[] headers, Collection<?> rows) {
		print(headers, rows);
		printAndValidate();
	}

	/**
	 * Prints a collection of rows to the standard output
	 *
	 * @param rows A collection of rows to be printed.
	 */
	public void print(Collection<?> rows) {
		print((Object[]) null, rows);
	}

	/**
	 * Prints a collection of rows to the standard output, with headings
	 *
	 * @param headers the description of each
	 * @param rows    the rows to print then validate
	 */
	public void print(Object[] headers, Collection<?> rows) {

		if (headers != null) {
			println(Arrays.toString(headers));
			println("=======================");
		}

		int rowCount = 1;
		for (Object row : rows) {
			println((rowCount++) + " " + Arrays.toString((Object[]) row));
			println("-----------------------");
		}
	}

	/**
	 * Modifies the values of an array of Strings to make line separator characters `visible` by replacing
	 * them with their character escapes
	 *
	 * @param input the input array whose values will have line separators replaced by escape sequences.
	 *
	 * @return the modified, input array
	 */
	public String[] displayLineSeparators(String[] input) {
		for (int i = 0; i < input.length; i++) {
			if (input[i] != null) {
				input[i] = input[i].replaceAll("\\n", "\\\\n");
				input[i] = input[i].replaceAll("\\r", "\\\\r");
			}
		}
		return input;

	}

	/**
	 * Prints and validates rows in a map consisting of entity names and their respective rows.
	 *
	 * @param allRows a map of entity names and their rows.
	 */
	public final void printAndValidate(Map<String, List<String[]>> allRows) {
		for (Map.Entry<String, List<String[]>> e : allRows.entrySet()) {
			println("Rows of '" + e.getKey() + "'\n-----------------------");
			for (String[] row : e.getValue()) {
				println(Arrays.toString(row));
			}
		}
		printAndValidate();
	}
}
