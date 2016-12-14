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

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;

import java.lang.annotation.*;

/**
 * The {@code EnumSelector} annotation is meant to be used in conjunction with enumeration attributes.
 *
 * <p>Values parsed from the input will be matched against one or more properties of the enumeration type.
 * By default, values read from the input will be matched against:</p>
 * <ul>
 * <li><b>{@link Enum#name()}</b> - the name of the elements in the enumeration type</li>
 * <li><b>{@link Enum#ordinal()}</b> - the ordinal of the elements in the enumeration type</li>
 * <li><b>{@link Enum#toString()}</b> - the {@code String} representation of the elements in the enumeration type </li>
 * </ul>
 *
 * You can also define a {@link #customElement()} of your enumeration type (an attribute or method), as long as it
 * uniquely identifies each value of your enumeration type.
 *
 * <p>Use the {@link #selectors()} option to choose which properties to match the parsed input against, and in what order. You will only need to
 * explicitly add a {@link EnumSelector#CUSTOM_FIELD} or {@link EnumSelector#CUSTOM_METHOD} to the list of {@link #selectors()} if your {@link #customElement()} name
 * could point to both an attribute and a method in your enumeration.</p>
 *
 * <p>This will assign an {@link EnumConversion} to this field.</p>
 *
 * <p>Commonly used for java beans processed using {@link BeanProcessor} and/or {@link BeanWriterProcessor}
 *
 * @see BeanProcessor
 * @see BeanWriterProcessor
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface EnumOptions {

	/**
	 * <p>A list of properties of the enumeration type that will be matched against the parsed input to identify which enum element should be assigned to the annotated field.
	 * By default, values read from the input will be matched against:</p>
	 * <ul>
	 * <li><b>{@link Enum#name()}</b> - the name of the elements in the enumeration type</li>
	 * <li><b>{@link Enum#ordinal()}</b> - the ordinal of the elements in the enumeration type</li>
	 * <li><b>{@link Enum#toString()}</b> - the {@code String} representation of the elements in the enumeration type </li>
	 * </ul>
	 * @return the sequence of properties of the enumeration type to match against the parsed input.
	 */
	EnumSelector[] selectors() default {EnumSelector.NAME, EnumSelector.ORDINAL, EnumSelector.STRING};

	/**
	 *
	 * Defines the name of a custom element (attribute or method) of the annotated enumeration. This will be used to match the parsed input and identify an individual value of the enumeration.
	 * The attribute value, or object returned from the method, should uniquely identify a value of the enumeration;
	 *
	 * <p>You will only need to explicitly add a {@link EnumSelector#CUSTOM_FIELD} or {@link EnumSelector#CUSTOM_METHOD} to the list of {@link #selectors()}
	 * if your {@link #customElement()} name could point to both an attribute and a method in your enumeration.</p>
	 *
	 * @return the name of a custom element (attribute or method) of the enumeration which will match the parsed input and identify an enumeration's value.
	 */
	String customElement() default "";
}
