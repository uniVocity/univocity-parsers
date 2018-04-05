/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.annotations.*;

import java.lang.reflect.*;

public class AddressTypeTransformer extends HeaderTransformer {

	private String prefix;

	public AddressTypeTransformer(String... args) {
		prefix = args[0];
	}

	@Override
	public String transformName(Field field, String name) {
		return prefix + "_" + name;
	}
}
