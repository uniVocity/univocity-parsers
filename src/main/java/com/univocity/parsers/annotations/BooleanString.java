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

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;

/**
 * Indicates that parsed values such as "y", "No" or "null" should be interpreted as boolean values. 
 * If a parsed value exists in {@link #trueStrings()}, then the field will receive true.
 * If a parsed value exists in  {@link #falseStrings()} then the field will receive false.
 * <p>A {@link BooleanConversion}  will be assigned to this field
 * <p>Commonly used for java beans processed using {@link BeanProcessor} and/or {@link BeanWriterProcessor}
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
@Target(value = { ElementType.FIELD })
public @interface BooleanString {
	/**
	 * A set of Strings that represent the boolean value true (e.g. "y", "yes", "1")
	 */
	String[] trueStrings();

	/**
	 * A set of Strings that represent the boolean value false (e.g. "n", "no", "0")
	 */
	String[] falseStrings();
}
