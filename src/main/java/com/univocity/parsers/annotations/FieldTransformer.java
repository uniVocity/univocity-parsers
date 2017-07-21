package com.univocity.parsers.annotations;

import java.lang.reflect.*;

/**
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public abstract class FieldTransformer {

	public String transformName(Field field, String name) {
		return name;
	}

	public int transformIndex(Field field, int index) {
		return index;
	}
}
