/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface Nested {

	String identityValue() default "";

	int identityIndex() default 0;

	String identityField() default "";

	Class<?> instanceOf() default Object.class;

	/**
	 * Used to determine the class of the elements stored in a collection annotated with the {@code @Parsed} annotation.
	 * @return the class of elements inside a collection.
	 */
	Class<?> componentType() default Object.class;

	int keyIndex() default -1;

	String keyField() default "";
}
