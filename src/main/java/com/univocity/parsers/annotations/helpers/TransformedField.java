package com.univocity.parsers.annotations.helpers;

import com.univocity.parsers.annotations.*;

import java.lang.reflect.*;

/**
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class TransformedField {

	private Field field;
	private FieldTransformer transformer;

	public TransformedField(Field field, FieldTransformer transformer) {
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

		Parsed annotation = AnnotationHelper.findAnnotation(field, Parsed.class);
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

	public String attributeName(){
		if(field == null){
			return null;
		}
		return field.getName();
	}

	public Field getField(){
		return field;
	}

}
