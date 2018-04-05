/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

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
