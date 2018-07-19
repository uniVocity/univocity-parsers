/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
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

package com.univocity.parsers.annotations.meta;

import com.univocity.parsers.annotations.*;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})


@Convert(conversionClass = ContentCleaner.class)
@Parsed
@IntArray(ints = {})
public @interface Clean {

	@Copy(to = Parsed.class)
	String field() default "";

	@Copy(to = Parsed.class)
	int index() default -1;

	@Copy(to = Convert.class, property = "args")
	String remove();

	//not used for anything else than testing whether the AnnotationHelper will bomb
	@Copy(to = IntArray.class, property = "ints")
	int[] theInts();

}
