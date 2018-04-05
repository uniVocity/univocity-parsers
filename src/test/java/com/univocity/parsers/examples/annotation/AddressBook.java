/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.annotations.*;

public class AddressBook {

	@Nested(headerTransformer = AddressTypeTransformer.class, args = "mail")
	private Address mailingAddress;

	@Nested(headerTransformer  = AddressTypeTransformer.class, args = "main")
	private Address mainAddress;

	@Override
	public String toString() {
		return "AddressBook{" +
				"mailing=" + mailingAddress +
				", main=" + mainAddress +
				'}';
	}
}
