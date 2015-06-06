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

import java.util.*;
import java.util.Map.Entry;

class Lookup {

	final char[] value;
	final int[] lengths;
	final FieldAlignment[] alignments;
	final String[] fieldNames;

	Lookup(String value, FixedWidthFieldLengths config) {
		this.value = value.toCharArray();
		this.lengths = config.getFieldLengths();
		this.alignments = config.getFieldAlignments();
		this.fieldNames = config.getFieldNames();
	}

	boolean matches(char[] lookup) {
		if (value.length > lookup.length) {
			return false;
		}
		for (int i = 0; i < value.length; i++) {
			if (value[i] != lookup[i]) {
				return false;
			}
		}
		return true;
	}

	static void registerLookahead(String lookup, FixedWidthFieldLengths lengths, Map<String, FixedWidthFieldLengths> map) {
		registerLookup("ahead", lookup, lengths, map);
	}

	static void registerLookbehind(String lookup, FixedWidthFieldLengths lengths, Map<String, FixedWidthFieldLengths> map) {
		registerLookup("behind", lookup, lengths, map);
	}

	private static void registerLookup(String direction, String lookup, FixedWidthFieldLengths lengths, Map<String, FixedWidthFieldLengths> map) {
		if (lookup == null || lookup.trim().isEmpty()) {
			throw new IllegalArgumentException("Look" + direction + " value cannot be null");
		}

		if (lengths == null) {
			throw new IllegalArgumentException("Lengths of fields associated to look" + direction + " value '" + lookup + "' cannot be null");
		}

		map.put(lookup, lengths);
	}

	static Lookup[] getLookupFormats(Map<String, FixedWidthFieldLengths> map) {
		if (map.isEmpty()) {
			return null;
		}
		Lookup[] out = new Lookup[map.size()];
		int i = 0;
		for (Entry<String, FixedWidthFieldLengths> e : map.entrySet()) {
			out[i++] = new Lookup(e.getKey(), e.getValue());
		}

		Arrays.sort(out, new Comparator<Lookup>() {
			@Override
			public int compare(Lookup o1, Lookup o2) {
				//longer values go first.
				return o1.value.length < o2.value.length ? 1 : o1.value.length == o2.value.length ? 0 : -1;
			}
		});

		return out;
	}

	static int calculateMaxLookupLength(Lookup[]... lookupArrays) {
		int max = 0;

		for (Lookup[] lookups : lookupArrays) {
			if (lookups != null) {
				for (Lookup lookup : lookups) {
					if (max < lookup.value.length) {
						max = lookup.value.length;
					}
				}
			}
		}

		return max;
	}

	static int[] calculateMaxFieldLengths(FixedWidthFieldLengths fieldLengths, Map<String, FixedWidthFieldLengths> lookaheadFormats, Map<String, FixedWidthFieldLengths> lookbehindFormats) {
		List<int[]> allLengths = new ArrayList<int[]>();

		if (fieldLengths != null) {
			allLengths.add(fieldLengths.getFieldLengths());
		}
		for (FixedWidthFieldLengths lengths : lookaheadFormats.values()) {
			allLengths.add(lengths.getFieldLengths());
		}

		for (FixedWidthFieldLengths lengths : lookbehindFormats.values()) {
			allLengths.add(lengths.getFieldLengths());
		}

		if (allLengths.isEmpty()) {
			throw new IllegalStateException("Cannot determine field lengths to use.");
		}

		int lastColumn = -1;
		for (int[] lengths : allLengths) {
			if (lastColumn < lengths.length) {
				lastColumn = lengths.length;
			}
		}

		int[] out = new int[lastColumn];
		Arrays.fill(out, 0);
		for (int[] lengths : allLengths) {
			for (int i = 0; i < lastColumn; i++) {
				if (i < lengths.length) {
					int length = lengths[i];
					if (out[i] < length) {
						out[i] = length;
					}
				}
			}
		}

		return out;
	}

}
