/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.annotations.*;

public class Address {

	@Parsed
	private String street;
	@Parsed
	private String city;
	@Parsed
	private String state;

	@Override
	public String toString() {
		return "Address{" +
				"street='" + street + '\'' +
				", city='" + city + '\'' +
				", state='" + state + '\'' +
				'}';
	}
}

