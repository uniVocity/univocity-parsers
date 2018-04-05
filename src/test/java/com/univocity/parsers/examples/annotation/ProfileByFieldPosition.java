/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.annotations.*;

public class ProfileByFieldPosition {

	@Parsed(index = 0)
	private Long profileId;

	@Parsed(index = 1)
	private String username;

	@Parsed(index = 2)
	private int followers;

	@Override
	public String toString() {
		return "ProfileByFieldPosition{" +
				"profileId=" + profileId +
				", username='" + username + '\'' +
				", followers=" + followers +
				'}';
	}
}
