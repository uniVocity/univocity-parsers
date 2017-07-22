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

import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;

import java.lang.annotation.*;

/**
 * Indicates the field is parsed. If the annotated field type is not a String, it will be automatically converted using one
 * of the existing {@link Conversion} implementations in package {@link com.univocity.parsers.conversions}.
 *
 *
 * <p>Commonly used for java beans processed using {@link BeanProcessor} and/or {@link BeanWriterProcessor}
 * <p><i>Implementation note:</i> All annotations in @Parsed fields are processed by {@link AnnotationHelper}
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see Conversion
 * @see Conversions
 * @see BeanProcessor
 * @see BeanWriterProcessor
 * @see AnnotationHelper
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Parsed {
	/**
	 * The possible field names of a record. If multiple names are provided, the parser/writer will
	 * attempt to match the given names against the headers provided (i.e. headers found in the input when parsing with
	 * {@link CommonParserSettings#isHeaderExtractionEnabled()}, or manually set using
	 * {@link com.univocity.parsers.common.CommonSettings#setHeaders(String...)} for writing or parsing)
	 *
	 * @return the possible field names (optional if the index is provided)
	 */
	String[] field() default {};

	/**
	 * Field position in a parsed record
	 *
	 * @return the position of this field (optional if the field name is provided)
	 */
	int index() default -1;

	/**
	 * The default value to assign to this field in the parsed value is null
	 * <p>The String literal "null" will be interpreted as a regular null.
	 * <p>Use "'null"' if you want the default value to be the string "null"
	 *
	 * <p>this value will have different effects depending on the field type:
	 * <ul>
	 * <li>on fields of type {@link java.util.Date} or {@link java.util.Calendar}: if the null value is "now", the result of new Date() or Calendar.getInstance() will be used.
	 * <li>on numeric fields (primitives, wrappers and {@link java.math.BigDecimal} and {@link java.math.BigInteger}): if the null value contains a number, e.g. "50.01", it will be parsed and assigned to the field.
	 * <li>on boolean and Boolean fields: if the null value contains a String, the result of Boolean.valueOf(defaultNullRead()) will assigned to the field.
	 * <li>on char and Character fields: if the null value contains a String, the result of defaultNullRead().charAt(0) will assigned to the field.
	 * An exception will be thrown if the length of this String is different than 1
	 * </ul>
	 *
	 * @return the default String to return when the parsed value is null
	 */
	String defaultNullRead() default "null";

	/**
	 * The default value to read from this field if it is null. Used for writing to an output by {@link BeanWriterProcessor}.
	 * <p>The String literal "null" will be interpreted as a regular {@code null}.
	 * <p>Use "'null"' if you want the default value to be the string {@code "null"}
	 *
	 * @return default String to write when the input is null.
	 */
	String defaultNullWrite() default "null";

	/**
	 * Flag to indicate whether the parsed field should be converted automatically based on the field type. For example,
	 * if the annotated field is a {@code BigDecimal}, then {@link BigDecimalConversion} will be used to convert Strings to BigDecimal when reading
	 * and BigDecimal to String when writing. You may want to disable the default field conversion when using custom conversions through
	 * {@link BeanWriterProcessor#convertFields(Conversion...)},{@link BeanWriterProcessor#convertIndexes(Conversion...)} or
	 * {@link BeanWriterProcessor#convertAll(Conversion...)}.
	 *
	 * @return flag indicating whether the default conversion, based on the field type, is to be applied for this field.
	 */
	boolean applyDefaultConversion() default true;

}
