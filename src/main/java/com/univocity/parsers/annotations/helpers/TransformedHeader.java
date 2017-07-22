package com.univocity.parsers.annotations.helpers;

import com.univocity.parsers.annotations.*;

import java.lang.reflect.*;

import static com.univocity.parsers.annotations.helpers.AnnotationHelper.*;

/**
 * A pair associating a Field of an annotated class to an optional {@likn HeaderTransformer} obtained from
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
			if (annotation.field().isEmpty()) {
				name = field.getName();
			} else {
				name = annotation.field();
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
