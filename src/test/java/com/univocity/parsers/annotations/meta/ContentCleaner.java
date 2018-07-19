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


package com.univocity.parsers.annotations.meta;

import com.univocity.parsers.common.input.*;
import com.univocity.parsers.conversions.*;

import java.util.*;

public class ContentCleaner implements Conversion<Object, String> {

	private final char[] charsToRemove;
	private final char min;
	private final char max;

	public ContentCleaner(String[] args) {
		charsToRemove = args[0].toCharArray();
		Arrays.sort(charsToRemove);

		min = charsToRemove[0];
		max = charsToRemove[charsToRemove.length - 1];
	}

	@Override
	public String execute(Object input) {
		return clean(input);
	}

	@Override
	public Object revert(String input) {
		return clean(input);
	}

	private String clean(Object input){
		String result = String.valueOf(input);

		StringBuilder out = null;

		for(int i = 0; i < result.length(); i++){
			char ch = result.charAt(i);

			if(ch >= min && ch <= max){
				if(Arrays.binarySearch(charsToRemove, ch) >= 0){
					if(out == null){
						out = new StringBuilder(result.length());
						out.append(result, 0, i);
					}
				}
			} else if (out != null){
				out.append(ch);
			}
		}

		if(out != null){
			result = out.toString();
		}

		return result;
	}
}
