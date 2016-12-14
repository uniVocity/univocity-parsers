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

import com.univocity.parsers.common.processor.BeanProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.conversions.Conversion;
import com.univocity.parsers.conversions.Conversions;

import java.lang.annotation.*;

/**
 * Indicates that a parsed value is formatted and must be parsed before being assigned.
 * <p>The {@link Conversion} type assigned to this field will depend on its type.</p>
 * <p>Multiple format masks can be tried for a single value.</p>
 * <p>When reading from this value (for writing to a given output), the first mask declared in {@link Format#formats()} will be used to produce its String representation.</p>
 * <p>The {@link #options()} is an optional configuration, with properties and values separated by =.. Each property will be used configure the underlying formatter. For example,
 * if the parsed value is a BigDecimal, and the format is '#0,00', the decimal separator must be set to ','. To specify this using the {@link #options()} annotation, use:
 *
 * <ul>
 * <li>formats="#0,00", options="decimalSeparator=,".</li>
 * <li>The "decimalSeparator" property will be used to identify which method in DecimalFormat to invoke. In this case, the method "setDecimalSeparator", with the value on the right hand side of the = operator</li>
 * </ul>
 * <p>Commonly used for java beans processed using {@link BeanProcessor} and/or {@link BeanWriterProcessor}</p>
 *
 * @see Conversion
 * @see Conversions
 * @see BeanProcessor
 * @see BeanWriterProcessor
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Format {
	/**
	 * Formats that define how a value can be formatted. When reading, the values parsed from the input will be parsed according to the specified format. If multiple formats
	 * are defined, the first successful parsed value will be used. When writing, the first format defined in the sequence of formats will be used to produce the correct
	 * String representation.
	 * @return the sequence of formats to use.
	 */
	String[] formats();

	/**
	 * Defines a sequence of properties and their values, used to configure the underlying formatter. Each element must be specified as {@code property_name=property_value},
	 * e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 * @return a sequence of properties available in the underlying formatter and their respective values
	 */
	String[] options() default {};
}
