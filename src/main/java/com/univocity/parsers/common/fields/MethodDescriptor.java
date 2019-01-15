package com.univocity.parsers.common.fields;

public class MethodDescriptor {

	private final String name;
	private final String prefix;
	private final Class<?> parameterType;
	private final Class<?> returnType;

	private MethodDescriptor(String name, Class<?> parameterType, Class<?> returnType) {
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

	public static MethodDescriptor setter(String name, Class<?> parameterType) {
		return new MethodDescriptor(name, parameterType, null);
	}

	public static MethodDescriptor getter(String name, Class<?> returnType) {
		return new MethodDescriptor(name, null, returnType);
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
}
