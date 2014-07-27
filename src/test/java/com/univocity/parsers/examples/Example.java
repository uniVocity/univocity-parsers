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

import static org.testng.Assert.*;

import java.io.*;
import java.util.*;

/**
 * Just a parent class for all examples provided, with some utility methods.
 */
abstract class Example {

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
		StringBuilder out = new StringBuilder();
		if (headers != null) {
			println(out, Arrays.toString(headers));
			println(out, "=======================");
		}

		int rowCount = 1;
		for (Object row : rows) {
			println(out, (rowCount++) + " " + Arrays.toString((Object[]) row));
			println(out, "-----------------------");
		}

		printAndValidate(out);
	}

	public void println(StringBuilder out, Object content) {
		out.append(content).append('\n');
	}

	public void printAndValidate(StringBuilder output) {
		printAndValidate(output.toString());
	}

	public void printAndValidate(String output) {
		// TODO: If you are modifying the code in the examples to
		// get to know how things work, just set the validate argument false.
		printAndValidateOutput(true, output);
	}

	/**
	 * Finds out the example being executed and compares the output against
	 * the expected output in /src/test/resources/examples/expectedOutputs
	 * @param validate flag to indicate whether the output should be validated
	 * @param producedOutput the output produced by an example
	 */
	private void printAndValidateOutput(boolean validate, String producedOutput) {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (StackTraceElement element : stack) {
			String className = element.getClassName();

			if (className.endsWith("." + Example.class.getSimpleName())) {
				continue;
			}

			if (className.startsWith("com.univocity.parsers.examples")) {
				String method = element.getMethodName();

				if (method.startsWith("print")) {
					continue;
				}

				className = className.substring(className.lastIndexOf('.') + 1, className.length());

				System.out.println("\n=== Output of example: " + className + "." + method + " ===");
				System.out.println(producedOutput);

				if (validate) {
					validateExampleOutput(className, method, producedOutput);
				}

				return;
			}
		}

		fail("Could not load file with expected output");
	}

	private void validateExampleOutput(String className, String testMethod, String producedOutput) {
		String path = "/examples/expectedOutputs/" + className + "/" + testMethod;

		InputStream input = this.getClass().getResourceAsStream(path);

		if (input == null) {
			throw new IllegalStateException("Could not load expected output from path: " + path);
		}

		String expectedOutput = "";

		Scanner scanner = null;
		try {
			scanner = new Scanner(input, "UTF-8").useDelimiter("\\A");
			expectedOutput = scanner.hasNext() ? scanner.next() : "";
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

		assertFalse(producedOutput.isEmpty());
		assertFalse(expectedOutput.isEmpty());

		producedOutput = producedOutput.replaceAll("\\r", "");
		expectedOutput = expectedOutput.replaceAll("\\r", "");

		// adding newlines around the output so it becomes easier to read
		// the error message in case of failure
		producedOutput = "\n" + producedOutput + "\n";
		expectedOutput = "\n" + expectedOutput + "\n";

		assertEquals(producedOutput, expectedOutput);
	}
}
