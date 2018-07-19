/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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
 * Allows copying values of meta-annotation properties into the properties of an annotation that composes it. For example,
 * consider the {@code @MyReplacement} meta-annotation defined below as:
 *
 * <pre>
 * <code>
 *
 * {@literal @}Replace(expression = "`", replacement = "")
 * {@literal @}Parsed
 * public {@literal @}interface MyReplacement {
 *
 *     {@literal @}Copy(to = Parsed.class)
 *     String field() default "";
 *
 *     {@literal @}Copy(to = Parsed.class, property = "index")
 *     int myIndex() default -1;
 * }
 * </code>
 * </pre>
 *
 * Values set on attributes {@code field} or {@code myIndex} in {@code @MyReplacement} will be assigned to the
 * attributes {@code field} and {@code index} of the {@code @Parsed} annotation. This allows you to apply the
 * {@code @MyReplacement} annotation to any given field of your class while configuring the field name and index
 * to be set for the {@code @Parsed} annotation. This eliminates the need for adding explicit, additional annotations and
 * their specific property values to each and every field.
 *
 * The following class can now make use of the {@code @MyReplacement} annotation to apply the the annotations
 * {@code @Replace} and {@code @Parsed}, configuring the properties of the "inherited" {@code @Parsed}:
 *
 * <pre>
 * <code>
 * public class MyBean {
 *
 *     {@literal @}MyReplacement
 *     public String id;
 *
 *     {@literal @}MyReplacementUpperCase(field = "client_name")
 *     public String name;
 *
 *     {@literal @}MyReplacementUpperCase(myIndex = 4)
 *     public String address;
 * }
 * </code>
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface Copy {

	/**
	 * Target annotation class that is part of a meta-annotation.
	 *
	 * @return the class whose properties will be set from a given attribute of a meta-annotation
	 */
	Class to();

	/**
	 * Target property of the given annotation class that is part of a meta-annotation.
	 *
	 * @return the name of the property in the given annotation class that should receive the value of the
	 * meta-annotation property.
	 */
	String property() default "";
}
