package com.univocity.parsers.annotations.helpers;

import java.lang.annotation.*;
import java.lang.reflect.*;

public final class AnnotatedParameter implements AnnotatedElement {

	private final int index;
	private Object value;
	private final Class<?> type;
	private final Annotation[] annotations;
	private final Class<?> declaringClass;

	AnnotatedParameter(Class<?> declaringClass, int index, Class<?> type, Annotation[] annotations) {
		this.declaringClass = declaringClass;
		this.index = index;
		this.type = type;
		this.annotations = annotations;
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
		return getAnnotation(annotationType) != null;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		for (int i = 0; i < annotations.length; i++) {
			if (annotationType.isInstance(annotations[i])) {
				return annotationType.cast(annotations[i]);
			}
		}
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return getDeclaredAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return annotations;
	}

	public Class<?> getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}
}