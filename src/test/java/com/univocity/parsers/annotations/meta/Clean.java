/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */

package com.univocity.parsers.annotations.meta;

import com.univocity.parsers.annotations.*;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})


@Convert(conversionClass = ContentCleaner.class, args = "a;")
@Parsed
public @interface Clean {

	@Copy(to = Parsed.class)
	String field() default "";

	@Copy(to = Parsed.class)
	int index() default -1;
}
