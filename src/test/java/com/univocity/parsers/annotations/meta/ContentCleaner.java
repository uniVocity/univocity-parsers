/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

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
