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
 * Indicates that a parsed value is formatted and must be parsed before being assigned.
 * <p>The {@link Conversion} type assigned to this field will depend on its type.
 * <p>Multiple format masks can be tried for a single value.
 * <p>When reading from this value (for writing to a given output), the first mask declared in {@link #formats()} will be used to produce its String representation.
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
@Target(value = { ElementType.FIELD, ElementType.METHOD })
public @interface Format {
	String[] formats();
}
