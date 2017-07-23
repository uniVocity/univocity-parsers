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
package com.univocity.parsers.annotations.helpers;

import com.univocity.parsers.annotations.*;

import java.lang.reflect.*;

import static com.univocity.parsers.annotations.helpers.AnnotationHelper.*;

/**
 * A pair associating a Field of an annotated class to an optional {@link HeaderTransformer} obtained from
 * {@link Nested#headerTransformer()} when nested classes are used to process beans.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class TransformedHeader {

	private Field field;
	private HeaderTransformer transformer;

	public TransformedHeader(Field field, HeaderTransformer transformer) {
		this.field = field;
		this.transformer = transformer;
	}


	/**
	 * Returns the name to be used as a header based on a given field and its {@link Parsed} annotation.
	 *
	 * @return the header name to be used for the given field.
	 */
	public String getHeaderName() {
		if (field == null) {
			return null;
		}
		String name = null;

		Parsed annotation = findAnnotation(field, Parsed.class);
		if (annotation != null) {
			if (annotation.field().length == 0) {
				name = field.getName();
			} else {
				name = annotation.field()[0];
			}
		}

		if (transformer != null) {
			return transformer.transformName(field, name);
		}

		return name;
	}

	/**
	 * Returns the index that determines which column the current field represents, as specified by {@link Parsed#index()}
	 *
	 * @return the current header index.
	 */
	public int getHeaderIndex() {
		Parsed annotation = findAnnotation(field, Parsed.class);
		if (annotation != null) {
			int index = annotation.index();
			if (index != -1) {
				if (transformer != null) {
					return transformer.transformIndex(field, index);
				}
				return index;
			}
		}
		return -1;
	}

	/**
	 * Returns the original attribute name of the field in its containing class.
	 *
	 * @return the original attribute name of the field
	 */
	public String getAttributeName() {
		if (field == null) {
			return null;
		}
		return field.getName();
	}

	/**
	 * Returns the {@link Field} used to read/write values from/to.
	 *
	 * @return the field being manipulated by the parser/writer when processing java beans
	 */
	public Field getField() {
		return field;
	}

}
