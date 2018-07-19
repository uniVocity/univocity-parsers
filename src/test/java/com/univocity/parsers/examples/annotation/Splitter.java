/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
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

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.conversions.*;

public class Splitter implements Conversion<String, String[]> {

	private String separator;

	public Splitter(String... args) {
		if(args.length == 0){
			separator = ",";
		} else {
			separator = args[0];
		}
	}

	@Override
	public String[] execute(String input) {
		if(input == null){
			return new String[0];
		}
		return input.split(separator);
	}

	@Override
	public String revert(String[] input) {
		StringBuilder out = new StringBuilder();
		for (String value : input) {
			if (out.length() > 0) {
				out.append(separator);
			}
			out.append(value);
		}
		return out.toString();
	}
}
