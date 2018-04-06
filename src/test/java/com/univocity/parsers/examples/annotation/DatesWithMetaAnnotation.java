/*******************************************************************************
 * Copyright 2018 uniVocity Software Pty Ltd
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

public class DatesWithMetaAnnotation {

	@Parsed
	private Long id;

	@MyCompanyDate(field = "created_at")
	private Date createdAt;

	@MyCompanyDate(field = "updated_at")
	private Date updatedAt;

	@MyCompanyDate(field = "deleted_at")
	private Date deletedAt;

	//##CLASS_END

	@Override
	public String toString() {
		return "DatesWithMetaAnnotation{" +
				"id=" + id +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				", deletedAt=" + deletedAt +
				'}';
	}
}
