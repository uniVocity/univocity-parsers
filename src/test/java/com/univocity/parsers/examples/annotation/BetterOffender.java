/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.annotations.*;

import java.util.*;

public class BetterOffender {

	@Nested
	private Profile profile;

	private String[] words;

	@Parsed(field = "words")
	public void setWords(String words) {
		this.words = words.split(";");
	}

	@Override
	public String toString() {
		return "BetterOffender{" +
				"id=" + profile.getId() +
				", user='" + profile.getUser() +
				"', words=" + Arrays.toString(words) +
				'}';
	}
}
