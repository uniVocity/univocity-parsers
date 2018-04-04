/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.annotations.meta;

public class CleanBeanTest {

	@Clean
	String a;

	@Clean
	String b;

	@Clean
	int c;

	public CleanBeanTest(String a, String b, int c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
}
