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

import java.io.*;
import java.util.*;

import static com.univocity.parsers.common.ArgumentUtils.*;

/**
 * A {@code NormalizedString} allows representing text in a normalized fashion. Strings
 * with different character case or surrounding whitespace are considered the same.
 *
 * Used to represent groups of fields, where users may refer to their names using
 * different character cases or whitespaces.
 *
 * Where the character case or the surrounding space is relevant, the {@code NormalizedString}
 * will have its {@link #isLiteral()} method return {@code true}, meaning the exact
 * character case and surrounding whitespaces are required for matching it.
 *
 * Invoking {@link #valueOf(String)} with a {@code String} surrounded by single quotes
 * will create a literal {@code NormalizedString}. Use {@link #literalValueOf(String)}
 * to obtain the same {@code NormalizedString} without having to introduce single quotes.
 *
 */
public final class NormalizedString implements Serializable, Comparable<NormalizedString>, CharSequence {

	private static final long serialVersionUID = -3904288692735859811L;

	private static final StringCache<NormalizedString> stringCache = new StringCache<NormalizedString>() {
		@Override
		protected NormalizedString process(String input) {
			if (input == null) {
				return null;
			}

			return new NormalizedString(input);
		}
	};

	private final String original;
	private final String normalized;
	private final boolean literal;
	private final int hashCode;

	private NormalizedString(String string) {
		String trimmed = string.trim();
		if (trimmed.length() > 2 && trimmed.charAt(0) == '\'' && trimmed.charAt(trimmed.length() - 1) == '\'') {
			this.original = string.substring(1, string.length() - 1);
			this.normalized = original;
			this.hashCode = normalize(original).hashCode();
			this.literal = true;
		} else {
			this.original = string;
			this.normalized = normalize(original);
			this.hashCode = normalized.hashCode();
			this.literal = false;
		}
	}

	private String normalize(Object value) {
		String str = String.valueOf(value);
		str = str.trim().toLowerCase();
		return str;
	}

	public boolean isLiteral() {
		return literal;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject == this) {
			return true;
		}
		if (anObject == null) {
			return false;
		}
		if (anObject instanceof NormalizedString) {
			NormalizedString other = (NormalizedString) anObject;

			if (this.literal || other.literal) {
				return original.equals(other.original);
			}

			return this.normalized.equals(other.normalized);
		}

		if (literal) {
			return original.equals(String.valueOf(anObject));
		} else {
			return normalized.equals(normalize(anObject));
		}
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public int length() {
		return original.length();
	}

	@Override
	public char charAt(int index) {
		return original.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return original.subSequence(start, end);
	}

	@Override
	public int compareTo(NormalizedString o) {
		if (o == this) {
			return 0;
		}

		if (this.literal || o.literal) {
			return original.compareTo(o.original);
		}

		return this.normalized.compareTo(o.normalized);
	}

	/**
	 * Compares a {@code NormalizedString} against a {@code String} lexicographically.
	 * @param o a plain {@code String}
	 * @return the result of {@link String#compareTo(String)}. If this {@code NormalizedString}
	 * is a literal, the original argument string will be compared. If this {@code NormalizedString}
	 * is not a literal, the result will be from the comparison of the normalized content of both strings
	 * (i.e. surrounding whitespaces and character case differences will be ignored).
	 */
	public int compareTo(String o) {
		return compareTo(valueOf(o));
	}

	@Override
	public String toString() {
		return original;
	}

	/**
	 * Creates a literal {@code NormalizedString}, meaning it will only match with
	 * other {@code String} or {@code NormalizedString} if they have the exact same content
	 * including character case and surrounding whitespaces.
	 *
	 * @param string the input {@code String}
	 * @return the literal {@code NormalizedString} version of the given string.
	 */
	public static NormalizedString literalValueOf(String string) {
		if (string == null) {
			return null;
		}
		return stringCache.get('\'' + string + "\'");
	}


	/**
	 * Creates a non-literal {@code NormalizedString}, meaning it will match with
	 * other {@code String} or {@code NormalizedString} regardless of different
	 * including character case and surrounding whitespaces.
	 *
	 * If the input value is enclosed with single quotes, a literal {@code NormalizedString}
	 * will be returned, as described in {@link #literalValueOf(String)}
	 *
	 * @param o the input object whose {@code String} representation will be used
	 * @return the {@code NormalizedString} of the given object.
	 */
	public static NormalizedString valueOf(Object o) {
		if (o == null) {
			return null;
		}
		return stringCache.get(o.toString());
	}


	/**
	 * Creates a non-literal {@code NormalizedString}, meaning it will match with
	 * other {@code String} or {@code NormalizedString} regardless of different
	 * including character case and surrounding whitespaces.
	 *
	 * If the input string is enclosed with single quotes, a literal {@code NormalizedString}
	 * will be returned, as described in {@link #literalValueOf(String)}
	 *
	 * @param string the input string
	 * @return the {@code NormalizedString} of the given string.
	 */
	public static NormalizedString valueOf(String string) {
		if (string == null) {
			return null;
		}
		return stringCache.get(string);
	}

	/**
	 * Converts a  {@code NormalizedString} back to its original {@code String} representation
	 * @param string the normalized string
	 * @return the original string used to create the given normalized representation.
	 */
	public static String valueOf(NormalizedString string) {
		if (string == null) {
			return null;
		}
		return string.original;
	}

	/**
	 * Converts a collection of plain strings into an array of {@code NormalizedString}
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static NormalizedString[] toArray(Collection<String> args) {
		if (args == null) {
			throw new IllegalArgumentException("String collection cannot be null");
		}
		NormalizedString[] out = new NormalizedString[args.size()];

		Iterator<String> it = args.iterator();
		for (int i = 0; i < out.length; i++) {
			out[i] = valueOf(it.next());
		}
		return out;
	}

	/**
	 * Converts a collection of normalized strings into an array of {@code String}
	 * @param args the normalized strings to convert back to to {@code String}
	 * @return the {@code String} representations of all normalized strings.
	 */
	public static String[] toStringArray(Collection<NormalizedString> args) {
		if (args == null) {
			throw new IllegalArgumentException("String collection cannot be null");
		}
		String[] out = new String[args.size()];

		Iterator<NormalizedString> it = args.iterator();
		for (int i = 0; i < out.length; i++) {
			out[i] = valueOf(it.next());
		}
		return out;
	}

	/**
	 * Converts multiple plain strings into an array of {@code NormalizedString}, ensuring
	 * no duplicate {@code NormalizedString} elements exist, even if their original {@code String}s
	 * are different.
	 *
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static NormalizedString[] toUniqueArray(String... args) {
		notEmpty("Element array", args);
		NormalizedString[] out = toArray(args);

		NormalizedString[] duplicates = findDuplicates(out);
		if (duplicates.length > 0) {
			throw new IllegalArgumentException("Duplicate elements found: " + Arrays.toString(duplicates));
		}
		return out;
	}

	/**
	 * Converts multiple plain strings into an array of {@code NormalizedString}.
	 *
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static NormalizedString[] toArray(String... args) {
		if (args == null) {
			return null;
		} else if (args.length == 0) {
			return EMPTY_NORMALIZED_STRING_ARRAY;
		}

		NormalizedString[] out = new NormalizedString[args.length];
		for (int i = 0; i < args.length; i++) {
			out[i] = valueOf(args[i]);
		}
		return out;
	}

	/**
	 * Converts multiple normalized strings into an array of {@code String}.
	 *
	 * @param args the normalized strings to convert to {@code String}
	 * @return the {@code String} representations of all input strings.
	 */
	public static String[] toArray(NormalizedString... args) {
		if (args == null) {
			return null;
		} else if (args.length == 0) {
			return EMPTY_STRING_ARRAY;
		}
		String[] out = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			out[i] = valueOf(args[i]);
		}
		return out;
	}

	private static <T extends Collection<NormalizedString>> T getCollection(T out, String... args) {
		Collections.addAll(out, toArray(args));
		return out;
	}

	private static <T extends Collection<NormalizedString>> T getCollection(T out, Collection<String> args) {
		Collections.addAll(out, toArray(args));
		return out;
	}

	private static <T extends Collection<String>> T getCollection(T out, NormalizedString... args) {
		Collections.addAll(out, toArray(args));
		return out;
	}

	private static <T extends Collection<String>> T getStringCollection(T out, Collection<NormalizedString> args) {
		Collections.addAll(out, toStringArray(args));
		return out;
	}

	/**
	 * Converts multiple plain strings into an {@code ArrayList} of {@code NormalizedString}.
	 *
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static ArrayList<NormalizedString> toArrayList(String... args) {
		return getCollection(new ArrayList<NormalizedString>(), args);
	}

	/**
	 * Converts multiple plain strings into an {@code ArrayList} of {@code NormalizedString}.
	 *
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static ArrayList<NormalizedString> toArrayList(Collection<String> args) {
		return getCollection(new ArrayList<NormalizedString>(), args);
	}

	/**
	 * Converts multiple normalized strings into a {@code HashSet} of {@code String}.
	 *
	 * @param args the normalized strings to convert to {@code String}
	 * @return the original {@code String}s of all input normalized strings.
	 */
	public static ArrayList<String> toArrayListOfStrings(NormalizedString... args) {
		return getCollection(new ArrayList<String>(), args);
	}

	/**
	 * Converts multiple normalized strings into a {@code HashSet} of {@code String}.
	 *
	 * @param args the normalized strings to convert to {@code String}
	 * @return the original {@code String}s of all input normalized strings.
	 */
	public static ArrayList<String> toArrayListOfStrings(Collection<NormalizedString> args) {
		return getStringCollection(new ArrayList<String>(), args);
	}


	/**
	 * Converts multiple plain strings into a {@code TreeSet} of {@code NormalizedString}.
	 *
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static TreeSet<NormalizedString> toTreeSet(String... args) {
		return getCollection(new TreeSet<NormalizedString>(), args);
	}

	/**
	 * Converts multiple plain strings into a {@code TreeSet} of {@code NormalizedString}.
	 *
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static TreeSet<NormalizedString> toTreeSet(Collection<String> args) {
		return getCollection(new TreeSet<NormalizedString>(), args);
	}

	/**
	 * Converts multiple normalized strings into a {@code HashSet} of {@code String}.
	 *
	 * @param args the normalized strings to convert to {@code String}
	 * @return the original {@code String}s of all input normalized strings.
	 */
	public static TreeSet<String> toTreeSetOfStrings(NormalizedString... args) {
		return getCollection(new TreeSet<String>(), args);
	}

	/**
	 * Converts multiple normalized strings into a {@code HashSet} of {@code String}.
	 *
	 * @param args the normalized strings to convert to {@code String}
	 * @return the original {@code String}s of all input normalized strings.
	 */
	public static TreeSet<String> toTreeSetOfStrings(Collection<NormalizedString> args) {
		return getStringCollection(new TreeSet<String>(), args);
	}

	/**
	 * Converts multiple plain strings into a {@code HashSet} of {@code NormalizedString}.
	 *
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static HashSet<NormalizedString> toHashSet(String... args) {
		return getCollection(new HashSet<NormalizedString>(), args);
	}

	/**
	 * Converts multiple plain strings into a {@code HashSet} of {@code NormalizedString}.
	 *
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static HashSet<NormalizedString> toHashSet(Collection<String> args) {
		return getCollection(new HashSet<NormalizedString>(), args);
	}


	/**
	 * Converts multiple normalized strings into a {@code HashSet} of {@code String}.
	 *
	 * @param args the normalized strings to convert to {@code String}
	 * @return the original {@code String}s of all input normalized strings.
	 */
	public static HashSet<String> toHashSetOfStrings(NormalizedString... args) {
		return getCollection(new HashSet<String>(), args);
	}

	/**
	 * Converts multiple normalized strings into a {@code HashSet} of {@code String}.
	 *
	 * @param args the normalized strings to convert to {@code String}
	 * @return the original {@code String}s of all input normalized strings.
	 */
	public static HashSet<String> toHashSetOfStrings(Collection<NormalizedString> args) {
		return getStringCollection(new HashSet<String>(), args);
	}

	/**
	 * Converts multiple plain strings into a {@code LinkedHashSet} of {@code NormalizedString}.
	 *
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static LinkedHashSet<NormalizedString> toLinkedHashSet(String... args) {
		return getCollection(new LinkedHashSet<NormalizedString>(), args);
	}

	/**
	 * Converts multiple plain strings into a {@code LinkedHashSet} of {@code NormalizedString}.
	 *
	 * @param args the strings to convert to {@code NormalizedString}
	 * @return the {@code NormalizedString} representations of all input strings.
	 */
	public static LinkedHashSet<NormalizedString> toLinkedHashSet(Collection<String> args) {
		return getCollection(new LinkedHashSet<NormalizedString>(), args);
	}

	/**
	 * Converts multiple normalized strings into a {@code LinkedHashSet} of {@code String}.
	 *
	 * @param args the normalized strings to convert to {@code String}
	 * @return the original {@code String}s of all input normalized strings.
	 */
	public static LinkedHashSet<String> toLinkedHashSetOfStrings(NormalizedString... args) {
		return getCollection(new LinkedHashSet<String>(), args);
	}

	/**
	 * Converts multiple normalized strings into a {@code LinkedHashSet} of {@code String}.
	 *
	 * @param args the normalized strings to convert to {@code String}
	 * @return the original {@code String}s of all input normalized strings.
	 */
	public static LinkedHashSet<String> toLinkedHashSetOfStrings(Collection<NormalizedString> args) {
		return getStringCollection(new LinkedHashSet<String>(), args);
	}


	/**
	 * Returns the literal representation of this {@code NormalizedString}, meaning it will only match with
	 * other {@code String} or {@code NormalizedString} if they have the exact same content
	 * including character case and surrounding whitespaces.
	 *
	 * @return the literal representation of the current {@code NormalizedString}
	 */
	public NormalizedString toLiteral() {
		if (literal) {
			return this;
		}
		return literalValueOf(this.original);
	}

	/**
	 * Analyzes a group of NormalizedString to identify any instances whose normalized content will generate
	 * clashes. Any clashing entries will be converted to their literal counterparts (using {@link #toLiteral()}),
	 * making it possible to identify one from the other.
	 *
	 * @param strings a group of identifiers that may contain ambiguous entries if their character case or surrounding whitespaces is not considered.
	 *                This array will be modified.
	 *
	 * @return the input string array, with {@code NormalizedString} literals in the positions where clashes would originally occur.
	 */
	public static NormalizedString[] toIdentifierGroupArray(NormalizedString[] strings) {
		identifyLiterals(strings);
		return strings;
	}

	/**
	 * Analyzes a group of String to identify any instances whose normalized content will generate
	 * clashes. Any clashing entries will be converted to their literal counterparts (using {@link #toLiteral()}),
	 * making it possible to identify one from the other.
	 *
	 * @param strings a group of identifiers that may contain ambiguous entries if their character case or surrounding whitespaces is not considered.
	 *
	 *
	 * @return a {@code NormalizedString} array with literals in the positions where clashes would originally occur.
	 */
	public static NormalizedString[] toIdentifierGroupArray(String[] strings) {
		NormalizedString[] out = toArray(strings);
		identifyLiterals(out, false, false);
		return out;
	}

	/**
	 * Analyzes a group of NormalizedString to identify any instances whose normalized content will generate
	 * clashes. Any clashing entries will be converted to their literal counterparts (using {@link #toLiteral()}),
	 * making it possible to identify one from the other.
	 *
	 * @param strings a group of identifiers that may contain ambiguous entries if their character case or surrounding whitespaces is not considered.
	 *                This array will be modified.
	 *
	 * @return {@code true} if any entry has been modified to be a literal, otherwise {@code false}
	 *
	 */
	public static boolean identifyLiterals(NormalizedString[] strings) {
		return identifyLiterals(strings, false, false);
	}

	/**
	 * Analyzes a group of NormalizedString to identify any instances whose normalized content will generate
	 * clashes. Any clashing entries will be converted to their literal counterparts (using {@link #toLiteral()}),
	 * making it possible to identify one from the other.
	 *
	 * @param strings a group of identifiers that may contain ambiguous entries if their character case or surrounding whitespaces is not considered.
	 *                This array will be modified.
	 *
	 * @param lowercaseIdentifiers flag indicating that identifiers are stored in lower case (for compatibility with databases).
	 *                             If a string has a uppercase character, it means it must become a literal.
	 * @param uppercaseIdentifiers flag indicating that identifiers are stored in upper case (for compatibility with databases).
	 *                             If a string has a lowercase character, it means it must become a literal.
	 *
	 * @return {@code true} if any entry has been modified to be a literal, otherwise {@code false}
	 *
	 */
	public static boolean identifyLiterals(NormalizedString[] strings, boolean lowercaseIdentifiers, boolean uppercaseIdentifiers) {
		if (strings == null) {
			return false;
		}
		TreeMap<NormalizedString, Object[]> normalizedMap = new TreeMap<NormalizedString, Object[]>();

		boolean modified = false;

		for (int i = 0; i < strings.length; i++) {
			NormalizedString string = strings[i];
			if (string == null || string.isLiteral()) {
				continue;
			}

			if (shouldBeLiteral(string.original, lowercaseIdentifiers, uppercaseIdentifiers)) {
				strings[i] = NormalizedString.literalValueOf(string.original);
				continue;
			}

			Object[] clashing = normalizedMap.get(string);
			if (clashing != null && !string.original.equals(((NormalizedString) clashing[0]).original)) {
				strings[i] = NormalizedString.literalValueOf(string.original);
				strings[(Integer) clashing[1]] = ((NormalizedString) clashing[0]).toLiteral();
				modified = true;
			} else {
				normalizedMap.put(string, new Object[]{string, i});
			}
		}

		return modified;
	}

	private static boolean shouldBeLiteral(String string, boolean lowercaseIdentifiers, boolean uppercaseIdentifiers) {
		if (lowercaseIdentifiers || uppercaseIdentifiers) {
			for (int i = 0; i < string.length(); i++) {
				char ch = string.charAt(i);
				if ((uppercaseIdentifiers && !Character.isUpperCase(ch)) || (lowercaseIdentifiers && !Character.isLowerCase(ch))) {
					return true;
				}
			}
		}
		return false;
	}
}
