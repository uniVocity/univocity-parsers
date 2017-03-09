/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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
