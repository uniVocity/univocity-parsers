/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.annotations.*;

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
	@Format(formats = {"yyyy-MM-dd", "dd/MM/yyyy"}, options = "locale=en;lenient=false")
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
				", createdAt=" + createdAt +
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
