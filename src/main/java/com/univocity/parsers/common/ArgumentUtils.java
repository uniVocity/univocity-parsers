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
package com.univocity.parsers.common;

import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.common.input.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

/**
 * An utility class for validating inputs.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class ArgumentUtils {

	/**
	 * An empty String array.
	 */
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * Throws an IllegalArgumentException if the given array is null or empty.
	 *
	 * @param argDescription the description of the elements
	 * @param args           the elements to be validated.
	 * @param <T>            Type of arguments to be validated
	 */
	public static <T> void notEmpty(String argDescription, T... args) {
		if (args == null) {
			throw new IllegalArgumentException(argDescription + " must not be null");
		}
		if (args.length == 0) {
			throw new IllegalArgumentException(argDescription + " must not be empty");
		}
	}

	/**
	 * Throws an IllegalArgumentException if the given array is null,empty, or contains null values
	 *
	 * @param argDescription the description of the elements
	 * @param args           the elements to be validated.
	 * @param <T>            Type of arguments to be validated
	 */
	public static <T> void noNulls(String argDescription, T... args) {
		notEmpty(argDescription, args);
		for (T arg : args) {
			if (arg == null) {
				if (args.length > 0) {
					throw new IllegalArgumentException(argDescription + " must not contain nulls");
				} else {
					throw new IllegalArgumentException(argDescription + " must not be null");
				}
			}
		}
	}

	/**
	 * Returns the index of a header, when headers are selected using a {@link FieldSelector}.
	 *
	 * @param array         the element array
	 * @param element       the element to be looked for in the array.
	 * @param fieldSelector a field selector that indicates which elements of the given array are selected.
	 *
	 * @return the index of the given element in the array, or -1 if the element could not be found.
	 */
	public static int indexOf(String[] array, String element, FieldSelector fieldSelector) {
		int index = indexOf(array, element);
		if (fieldSelector == null || index == -1) {
			return index;
		}

		int[] indexes = fieldSelector.getFieldIndexes(array);
		for (int i = 0; i < indexes.length; i++) {
			if (indexes[i] == index) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Returns the indexes of an element in a given array.
	 *
	 * @param array   the element array
	 * @param element the element to be looked for in the array.
	 *
	 * @return the indexes of the given element in the array, or an empty array if no element could be found
	 */
	public static int[] indexesOf(Object[] array, Object element) {
		int[] tmp = new int[0];

		int i = 0;
		int o = 0;
		while (i < array.length) {
			i = indexOf(array, element, i);
			if (i == -1) {
				break;
			}

			tmp = Arrays.copyOf(tmp, tmp.length + 1);
			tmp[o++] = i;
			i++;
		}

		return  tmp;
	}

	/**
	 * Returns the index of an element in a given array.
	 *
	 * @param array   the element array
	 * @param element the element to be looked for in the array.
	 *
	 * @return the index of the given element in the array, or -1 if the element could not be found.
	 */
	public static int indexOf(Object[] array, Object element) {
		return indexOf(array, element, 0);
	}

	/**
	 * Returns the index of an element in a given array.
	 *
	 * @param array   the element array
	 * @param element the element to be looked for in the array.
	 * @param from the starting position of the array from where to start the search
	 *
	 * @return the index of the given element in the array, or -1 if the element could not be found.
	 */
	private static int indexOf(Object[] array, Object element, int from) {
		if (array == null) {
			throw new NullPointerException("Null array");
		}
		if (element == null) {
			for (int i = from; i < array.length; i++) {
				if (array[i] == null) {
					return i;
				}
			}
		} else {
			if (element instanceof String && array instanceof String[]) {
				for (int i = from; i < array.length; i++) {
					String e = String.valueOf(array[i]);
					if (element.toString().equalsIgnoreCase(e)) {
						return i;
					}
				}
			} else {
				for (int i = from; i < array.length; i++) {
					if (element.equals(array[i])) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Searches for elements in a given array and returns the elements not found.
	 *
	 * @param array    An array with elements
	 * @param elements the elements to be found
	 *
	 * @return the elements not found in the array.
	 */
	public static Object[] findMissingElements(Object[] array, Collection<?> elements) {
		return findMissingElements(array, elements.toArray());
	}

	/**
	 * Searches for elements in a given array and returns the elements not found.
	 *
	 * @param array    An array with elements
	 * @param elements the elements to be found
	 *
	 * @return the elements not found in the array.
	 */
	public static Object[] findMissingElements(Object[] array, Object[] elements) {
		List<Object> out = new ArrayList<Object>();

		for (Object element : elements) {
			if (indexOf(array, element) == -1) {
				out.add(element);
			}
		}

		return out.toArray();
	}

	/**
	 * Normalizes the Strings in a given array by trimming all elements and converting them to lower case.
	 *
	 * @param strings a String array with elements to be normalized.
	 *
	 * @return the normalized version of the original string array.
	 */
	public static String[] normalize(String[] strings) {
		String[] out = new String[strings.length];

		for (int i = 0; i < strings.length; i++) {
			out[i] = normalize(strings[i]);
		}

		return out;
	}

	/**
	 * Normalizes a given String by trimming whitespaces and converting it to lower case.
	 *
	 * @param string a String to be normalized.
	 *
	 * @return the normalized version of the original String.
	 */
	public static String normalize(String string) {
		if (string == null) {
			return null;
		}
		return string.trim().toLowerCase();
	}

	/**
	 * Normalizes the Strings in a given array by trimming all elements and converting them to lower case.
	 *
	 * @param strings a String collection with elements to be normalized. The original contents of the collection will be modified.
	 */
	public static void normalize(Collection<String> strings) {
		LinkedHashSet<String> normalized = new LinkedHashSet<String>(strings.size());
		for (String string : strings) {
			if (string == null) {
				normalized.add(null);
			} else {
				normalized.add(string.trim().toLowerCase());
			}
		}

		strings.clear();
		strings.addAll(normalized);
	}

	/**
	 * Creates a {@link java.io.Writer} from an output stream
	 *
	 * @param output the output stream
	 *
	 * @return {@link java.io.Writer} wrapping the given output stream
	 */
	public static Writer newWriter(OutputStream output) {
		return newWriter(output, (Charset) null);
	}

	/**
	 * Creates a {@link java.io.Writer} from an output stream
	 *
	 * @param output   the output stream
	 * @param encoding the encoding to use when writing to the output stream
	 *
	 * @return {@link java.io.Writer} wrapping the given output stream
	 */
	public static Writer newWriter(OutputStream output, String encoding) {
		return newWriter(output, Charset.forName(encoding));
	}

	/**
	 * Creates a {@link java.io.Writer} from an output stream
	 *
	 * @param output   the output stream
	 * @param encoding the encoding to use when writing to the output stream
	 *
	 * @return {@link java.io.Writer} wrapping the given output stream
	 */
	public static Writer newWriter(OutputStream output, Charset encoding) {
		if (encoding != null) {
			return new OutputStreamWriter(output, encoding);
		} else {
			return new OutputStreamWriter(output);
		}
	}

	/**
	 * Creates a {@link java.io.Writer} from a file
	 *
	 * @param file the file to be written
	 *
	 * @return {@link java.io.Writer} for the given file
	 */
	public static Writer newWriter(File file) {
		return newWriter(file, (Charset) null);
	}

	/**
	 * Creates a {@link java.io.Writer} from a file
	 *
	 * @param file     the file to be written
	 * @param encoding the encoding to use when writing to the file
	 *
	 * @return {@link java.io.Writer} for the given file
	 */
	public static Writer newWriter(File file, String encoding) {
		return newWriter(file, Charset.forName(encoding));
	}

	/**
	 * Creates a {@link java.io.Writer} from a file
	 *
	 * @param file     the file to be written
	 * @param encoding the encoding to use when writing to the file
	 *
	 * @return {@link java.io.Writer} for the given file
	 */
	public static Writer newWriter(File file, Charset encoding) {
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new IllegalArgumentException("Unable to create file '" + file.getAbsolutePath() + "', please ensure your application has permission to create files in that path", e);
			}
		}

		FileOutputStream os;
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}

		return newWriter(os, encoding);
	}

	/**
	 * Creates a {@link java.io.Reader} from an input stream
	 *
	 * @param input the input stream
	 *
	 * @return a {@link java.io.Reader} wrapping the given input stream
	 */
	public static Reader newReader(InputStream input) {
		return newReader(input, (Charset) null);
	}

	/**
	 * Creates a {@link java.io.Reader} from an input stream
	 *
	 * @param input    the input stream
	 * @param encoding the encoding to use when reading from the input stream
	 *
	 * @return a {@link java.io.Reader} wrapping the given input stream
	 */
	public static Reader newReader(InputStream input, String encoding) {
		return newReader(input, encoding == null ? (Charset) null : Charset.forName(encoding));
	}

	/**
	 * Creates a {@link java.io.Reader} from an input stream
	 *
	 * @param input    the input stream
	 * @param encoding the encoding to use when reading from the input stream
	 *
	 * @return a {@link java.io.Reader} wrapping the given input stream
	 */
	public static Reader newReader(InputStream input, Charset encoding) {
		if (encoding == null) {
			BomInput bomInput = new BomInput(input);
			if (bomInput.getEncoding() != null) { //charset detected. Just set the encoding and keep using the original input stream.
				encoding = bomInput.getCharset();
			}

			if (bomInput.hasBytesStored()) { //there are bytes to be processed. We should use the BomInput wrapper to read the first bytes already consumed when trying to match the BOM.
				input = bomInput;
			} //else the original input can be used and the wrapper is not necessary, as a BOM has been matched and the bytes discarded.
		}


		if (encoding != null) {
			return new InputStreamReader(input, encoding);
		} else {
			return new InputStreamReader(input);
		}
	}

	/**
	 * Creates a {@link java.io.Reader} for a given a file
	 *
	 * @param file the file to be read
	 *
	 * @return a {@link java.io.Reader} for reading the given file
	 */
	public static Reader newReader(File file) {
		return newReader(file, (Charset) null);
	}

	/**
	 * Creates a {@link java.io.Reader} for a given a file
	 *
	 * @param file     the file to be read
	 * @param encoding the encoding to be used when reading from the file
	 *
	 * @return a {@link java.io.Reader} for reading the given file
	 */
	public static Reader newReader(File file, String encoding) {
		return newReader(file, Charset.forName(encoding));
	}

	/**
	 * Creates a {@link java.io.Reader} for a given a file
	 *
	 * @param file     the file to be read
	 * @param encoding the encoding to be used when reading from the file
	 *
	 * @return a {@link java.io.Reader} for reading the given file
	 */
	public static Reader newReader(File file, Charset encoding) {
		FileInputStream input;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}

		return newReader(input, encoding);
	}

	/**
	 * Converts a list of enumerations to an array of their {@link Enum#toString()} representation
	 *
	 * @param enums a list of enumerations to convert
	 *
	 * @return an array of {@code String} with the values produced by each element's {@link Enum#toString()} method.
	 */
	@SuppressWarnings("rawtypes")
	public static String[] toArray(List<Enum> enums) {
		String[] out = new String[enums.size()];

		for (int i = 0; i < out.length; i++) {
			out[i] = enums.get(i).toString();
		}

		return out;

	}

	/**
	 * Converts any collection of {@code Integer} into an {@code int} array.
	 *
	 * @param ints a collection of (boxed) integers.
	 *
	 * @return a primitive {@code int} array with the unboxed integer values.
	 */
	public static int[] toIntArray(Collection<Integer> ints) {
		int[] out = new int[ints.size()];

		int i = 0;
		for (Integer boxed : ints) {
			out[i++] = boxed.intValue();
		}

		return out;

	}

	/**
	 * Converts any collection of {@code Character} into a char array.
	 *
	 * @param characters a collection of (boxed) characters.
	 *
	 * @return a primitive {@code char} array with the unboxed character values.
	 */
	public static char[] toCharArray(Collection<Character> characters) {
		char[] out = new char[characters.size()];

		int i = 0;
		for (Character boxed : characters) {
			out[i++] = boxed.charValue();
		}

		return out;
	}

	public static String restrictContent(int length, CharSequence content) {
		if (content == null) {
			return null;
		}
		if (length == 0) {
			return "<omitted>";
		}
		if (length == -1) {
			return content.toString();
		}

		int errorMessageStart = content.length() - length;
		if (length > 0 && errorMessageStart > 0) {
			return "..." + content.subSequence(errorMessageStart, content.length()).toString();
		}
		return content.toString();
	}

	public static String restrictContent(int length, Object content) {
		if (content == null) {
			return null;
		}
		if (content instanceof Object[]) {
			return restrictContent(length, Arrays.toString((Object[]) content));
		}
		return restrictContent(length, String.valueOf(content));
	}

	/**
	 * Allows rethrowing a checked exception instead of wrapping it into a runtime exception. For internal use only
	 * as this generally causes more trouble than it solves (your exception-specific catch statement may not catch this
	 * error - make sure you are catching a Throwable)
	 *
	 * @param error the (potentially checked) exception to the thrown.
	 */
	public static void throwUnchecked(Throwable error) {
		ArgumentUtils.<RuntimeException>throwsUnchecked(error);
	}

	private static <T extends Exception> void throwsUnchecked(Throwable toThrow) throws T {
		throw (T) toThrow;
	}

	/**
	 * Converts a sequence of int numbers into a byte array.
	 *
	 * @param ints the integers to be cast to by
	 *
	 * @return the resulting byte array.
	 */
	public static byte[] toByteArray(int... ints) {
		byte[] out = new byte[ints.length];
		for (int i = 0; i < ints.length; i++) {
			out[i] = (byte) ints[i];
		}
		return out;
	}
}
