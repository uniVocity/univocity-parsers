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
import com.univocity.parsers.csv.*;

import java.math.*;
import java.util.*;

public class Profile {

	enum Type {
		USER('U'),
		SYSTEM('S');

		public final char typeCode;

		Type(char typeCode) {
			this.typeCode = typeCode;
		}
	}

	@Parsed
	private Long id;

	@Parsed
	@Trim
	@UpperCase
	private String user;

	@Parsed(field = "created_at")
	@Format(formats = {"yyyy-MM-dd", "dd/MM/yyyy"}, options = {"locale=en", "lenient=false", "timezone=CST"})
	private Date createdAt;

	@Parsed
	@Replace(expression = "\\$", replacement = "")
	@Format(formats = {"#0,00"}, options = "decimalSeparator=,")
	private BigDecimal fees;

	@Parsed
	@BooleanString(trueStrings = {"yes", "y"}, falseStrings = {"no", "n"})
	private boolean admin;

	@Parsed
	@EnumOptions(customElement = "typeCode")
	private Type type;

	@Parsed(field = "stars")
	@NullString(nulls = {"?", "N/A"})
	private Integer stars;

	@Override
	public String toString() {
		return "Profile{" +
				"id=" + id +
				", user='" + user + '\'' +
				", createdAt=" + TestUtils.formatDate(createdAt) +
				", fees=" + fees +
				", admin=" + admin +
				", type=" + type +
				", stars=" + stars +
				'}';
	}

	//##CLASS_END

	public Long getId() {
		return id;
	}

	public String getUser() {
		return user;
	}
}
