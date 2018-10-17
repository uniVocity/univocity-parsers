package com.univocity.parsers.annotations.helpers;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.*;

import java.lang.annotation.*;
import java.lang.reflect.*;

public class ConstructorMapping<T> {

	private final Class<T> beanClass;
	private Constructor<?> defaultConstructor = null;
	private Constructor<?> annotatedConstructor = null;
	private AnnotatedParameter[] annotatedConstructorParameters = null;
	private Object[] args;


	public ConstructorMapping(Class<T> beanClass) {
		this.beanClass = beanClass;

		Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
		for (int i = 0; i < constructors.length; i++) {
			Constructor<?> constructor = constructors[i];

			Class<?>[] parameterTypes = constructor.getParameterTypes();
			if (parameterTypes.length == 0) {
				defaultConstructor = constructor;
				continue;
			}
			AnnotatedParameter[] mappedParameters = buildAnnotatedParameters(parameterTypes, constructor);
			if (mappedParameters != null) {
				if (annotatedConstructor != null) {
					throw new IllegalStateException("Multiple constructors defined in '" + beanClass + "' with annotated parameters");
				}
				annotatedConstructor = constructor;
				annotatedConstructorParameters = mappedParameters;
				args = new Object[annotatedConstructorParameters.length];
			}
		}
	}

	private AnnotatedParameter[] buildAnnotatedParameters(Class<?>[] parameterTypes, Constructor<?> constructor) {
		Annotation[][] annotations = constructor.getParameterAnnotations();
		if (annotations.length == 0) {
			return null;
		}

		AnnotatedParameter[] out = new AnnotatedParameter[annotations.length];
		for (int i = 0; i < annotations.length; i++) {
			AnnotatedParameter param = new AnnotatedParameter(beanClass, i, parameterTypes[i], annotations[i]);
			Parsed parsed = AnnotationHelper.findAnnotation(param, Parsed.class);
			Nested nested = AnnotationHelper.findAnnotation(param, Nested.class);
			if (parsed != null || nested != null) {
				out[i] = param;
			} else {
				return null;
			}
		}
		return out;
	}

	public T newInstance(Object[] row) {
		T instance;
		try {
			if (annotatedConstructor != null) {
				for (int i = 0; i < args.length; i++) {
					args[i] = annotatedConstructorParameters[i].getValue();
				}
				instance = (T) annotatedConstructor.newInstance(args);
			} else if (defaultConstructor != null) {
				instance = (T) defaultConstructor.newInstance();
			} else {
				//should throw a meaningful exception
				instance = beanClass.newInstance();
			}
		} catch (Throwable e) {
			throw new DataProcessingException("Unable to instantiate class '" + beanClass.getName() + '\'', row, e);
		}

		return instance;
	}

	public AnnotatedParameter[] getAnnotatedParameters() {
		return annotatedConstructorParameters;
	}
}
