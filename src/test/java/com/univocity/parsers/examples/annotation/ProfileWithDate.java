/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.annotations.*;

import java.util.*;

public class ProfileWithDate {

	@Parsed
	private Long id;

	@Parsed
	private String user;

	@Parsed(field = "created_at")
	@Format(formats = {"yyyy-MM-dd", "dd/MM/yyyy"}, options = "locale=en;")
	private Date createdAt;

	@Override
	public String toString() {
		return "ProfileWithDate{" +
				"id=" + id +
				", user='" + user + '\'' +
				", createdAt=" + createdAt +
				'}';
	}
}
