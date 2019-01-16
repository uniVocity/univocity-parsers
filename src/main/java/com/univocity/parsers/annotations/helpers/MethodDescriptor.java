package com.univocity.parsers.annotations.helpers;

import java.lang.reflect.*;

public final class MethodDescriptor {

	private final String prefixedName;
	private final String name;
	private String prefix;
	private final Class<?> parameterType;
	private final Class<?> returnType;

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
	}

	private MethodDescriptor(String prefix, String name, Class<?> parameterType, Class<?> returnType) {
		this.prefixedName = prefix + '.' + name;
		this.name = name;
		this.prefix = prefix;
		this.parameterType = parameterType;
		this.returnType = returnType;
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

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefixedName() {
		return prefixedName;
	}

	public String toString() {
		StringBuilder out = new StringBuilder("method ");
		if (returnType != null) {
			out.append(returnType.getName());
			out.append(' ');
		}
		if (prefix.isEmpty()) {
			out.append(name);
		} else {
			out.append(prefix + '.' + name);
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MethodDescriptor that = (MethodDescriptor) o;

		if (!name.equals(that.name)) return false;
		if (parameterType != null ? !parameterType.equals(that.parameterType) : that.parameterType != null)
			return false;
		return returnType != null ? returnType.equals(that.returnType) : that.returnType == null;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + (parameterType != null ? parameterType.hashCode() : 0);
		result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
		return result;
	}
}
