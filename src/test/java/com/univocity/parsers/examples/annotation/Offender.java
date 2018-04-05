/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.annotations.*;

import java.util.*;

public class Offender {

	@Nested
	private Profile profile;

	@Parsed
	@Convert(conversionClass = Splitter.class, args = ";")
	private String[] words;

	@Override
	public String toString() {
		return "Offender{" +
				"id=" + profile.getId() +
				", user='" + profile.getUser() +
				"', words=" + Arrays.toString(words) +
				'}';
	}
}
