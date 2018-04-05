/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.annotations.*;

public class ProfileByMultipleFieldNames {

	@Parsed(field = {"profile_id", "id"})
	private Long profileId;

	@Parsed(field = {"username", "user"})
	private String username;

	@Parsed
	private int followers;

	@Override
	public String toString() {
		return "ProfileByMultipleFieldNames{" +
				"profileId=" + profileId +
				", username='" + username + '\'' +
				", followers=" + followers +
				'}';
	}
}
