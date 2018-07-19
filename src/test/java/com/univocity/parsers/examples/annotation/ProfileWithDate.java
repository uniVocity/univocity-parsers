/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
