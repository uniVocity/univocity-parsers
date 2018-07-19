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
package com.univocity.parsers.examples;

import com.univocity.parsers.conversions.*;

import java.util.*;

public class WordsToSetConversion implements Conversion<String, Set<String>> {

	private final String separator;
	private final boolean toUpperCase;

	public WordsToSetConversion(String... args) {
		String separator = ",";
		boolean toUpperCase = true;

		if (args.length == 1) {
			separator = args[0];
		}

		if (args.length == 2) {
			toUpperCase = Boolean.valueOf(args[1]);
		}

		this.separator = separator;
		this.toUpperCase = toUpperCase;
	}

	public WordsToSetConversion(String separator, boolean toUpperCase) {
		this.separator = separator;
		this.toUpperCase = toUpperCase;
	}

	@Override
	public Set<String> execute(String input) {
		if (input == null) {
			return Collections.emptySet();
		}

		if (toUpperCase) {
			input = input.toUpperCase();
		}

		Set<String> out = new TreeSet<String>();
		for (String token : input.split(separator)) {
			//extracting words separated by white space as well
			for (String word : token.trim().split("\\s")) {
				out.add(word.trim());
			}
		}

		return out;
	}

	//##CLASS_END

	@Override
	public String revert(Set<String> input) {
		if (input == null || input.isEmpty()) {
			return null;
		}
		StringBuilder out = new StringBuilder();

		for (String word : input) {
			if (word == null || word.trim().isEmpty()) {
				continue;
			}
			if (out.length() > 0) {
				out.append(separator);
			}
			if (toUpperCase) {
				word = word.toUpperCase();
			}
			out.append(word.trim());
		}

		if (out.length() == 0) {
			return null;
		}

		return out.toString();
	}
}
