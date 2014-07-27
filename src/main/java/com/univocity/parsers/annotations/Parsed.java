/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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

import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;

/**
 * Indicates the field is parsed. If the annotated field type is not a String, it will be automatically converted using one
 * of the existing {@link Conversion} implementations in package {@link com.univocity.parsers.conversions}.
 *
 *
 * <p>Commonly used for java beans processed using {@link BeanProcessor} and/or {@link BeanWriterProcessor}
 * <p><i>Implementation note:</i> All annotations in @Parsed fields are processed by {@link AnnotationHelper}
 *
 * @see Conversion
 * @see Conversions
 * @see BeanProcessor
 * @see BeanWriterProcessor
 * @see AnnotationHelper
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface Parsed {
	/**
	 * The field name in a parsed record
	 */
	String field() default "";

	/**
	 * Field position in a parsed record
	 */
	int index() default -1;

	/**
	 * The default value to assign to this field in the parsed value is null
	 * <p>The String literal "null" will be interpreted as a regular null.
	 * <p>Use "'null"' if you want the default value to be the string "null"
	 *
	 * <p>this value will have different effects depending on the field type:
	 * <ul>
	 * 	<li>on fields of type {@link java.util.Date} or {@link java.util.Calendar}: if the null value is "now", the result of new Date() or Calendar.getInstance() will be used.
	 *  <li>on numeric fields (primitives, wrappers and {@link java.math.BigDecimal} and {@link java.math.BigInteger}): if the null value contains a number, e.g. "50.01", it will be parsed and assigned to the field.
	 *  <li>on boolean and Boolean fields: if the null value contains a String, the result of Boolean.valueOf(defaultNullRead()) will assigned to the field.
	 *  <li>on char and Character fields: if the null value contains a String, the result of defaultNullRead().charAt(0) will assigned to the field.
	 *      An exception will be thrown if the length of this String is different than 1
	 * </ul>
	 */
	String defaultNullRead() default "null";

	/**
	 * The default value to read from this field if it is null. Used for writing to an output by {@link BeanWriterProcessor}.
	 * <p>The String literal "null" will be interpreted as a regular null.
	 * <p>Use "'null"' if you want the default value to be the string "null"
	 */
	String defaultNullWrite() default "null";
}
