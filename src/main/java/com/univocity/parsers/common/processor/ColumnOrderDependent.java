package com.univocity.parsers.common.processor;

/**
 * A marker interface used by special implementations of {@link RowProcessor} to indicate columns should not
 * be reorderd by the parser. Conflicting settings provided in {@link com.univocity.parsers.common.CommonParserSettings#setColumnReorderingEnabled(boolean)} will be prevented.
 *
 * This marker is used to configure the parser automatically based on the specific {@link RowProcessor} implementation used.
 */
public interface ColumnOrderDependent {

	/**
	 * Returns a flag indicating whether or not columns should be reordered by the parser
	 * @return a flag indicating whether or not columns should be reordered by the parser
	 */
	public boolean preventColumnReordering();
}
