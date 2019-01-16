package com.univocity.parsers.annotations.helpers;

import java.lang.reflect.*;

public final class MethodDescriptor {

	private final String prefixedName;
	private final String name;
	private final String prefix;
	private final Class<?> parameterType;
	private final Class<?> returnType;

	private final String string;

	private MethodDescriptor(String name, Class<?> parameterType, Class<?> returnType) {
		prefixedName = name;
		int lastDot = name.lastIndexOf('.');
		if (lastDot == -1) {
			this.name = name;
			this.prefix = "";
		} else {
			this.name = name.substring(lastDot + 1);
			this.prefix = name.substring(0, lastDot);
		}
		this.parameterType = parameterType;
		this.returnType = returnType;
		this.string = generateString();
	}

	private MethodDescriptor(String prefix, String name, Class<?> parameterType, Class<?> returnType) {
		this.prefixedName = prefix + '.' + name;
		this.name = name;
		this.prefix = prefix;
		this.parameterType = parameterType;
		this.returnType = returnType;
		this.string = generateString();
	}

	private String generateString() {
		StringBuilder out = new StringBuilder("method ");
		if (returnType != null) {
			out.append(returnType.getName());
			out.append(' ');
		}
		if (prefix.isEmpty()) {
			out.append(name);
		} else {
			out.append(prefix);
			out.append('.');
			out.append(name);
		}

		if (parameterType != null) {
			out.append('(');
			out.append(parameterType.getName());
			out.append(')');
		} else {
			out.append("()");
		}
		return out.toString();
	}

	public static MethodDescriptor setter(String name, Class<?> parameterType) {
		return new MethodDescriptor(name, parameterType, null);
	}

	public static MethodDescriptor getter(String name, Class<?> returnType) {
		return new MethodDescriptor(name, null, returnType);
	}

	static MethodDescriptor setter(String prefix, Method method) {
		return new MethodDescriptor(prefix, method.getName(), method.getParameterTypes()[0], null);
	}

	static MethodDescriptor getter(String prefix, Method method) {
		return new MethodDescriptor(prefix, method.getName(), null, method.getReturnType());
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public Class<?> getParameterType() {
		return parameterType;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public String getPrefixedName() {
		return prefixedName;
	}

	public String toString() {
		return string;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != this.getClass()) {
			return false;
		}

		return string.equals(o.toString());
	}

	@Override
	public int hashCode() {
		return string.hashCode();
	}
}
