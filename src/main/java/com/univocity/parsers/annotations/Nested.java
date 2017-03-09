/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.annotations;

import java.lang.annotation.*;

/**
 * Marks a field as a nested object to be constructed with the values of the current row. It is expected that
 * the annotated attribute is of a type, or provides an explicit type via the {@link #type()} option,
 * that contains one or more {@link Parsed} annotations. The given type and its {@link Parsed} annotations will
 * determine which fields from each row should be used to populate the {@code Nested} instance.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Nested {

	/**
	 * Defines the concrete type of nested object to be instantiated, if it has to be a subclass of the declared attribute type.
	 *
	 * @return the type of nested object to be instantiated.
	 */
	Class type() default Object.class;
}
