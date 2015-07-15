package com.univocity.parsers.common.fields;

import com.univocity.parsers.common.*;

/**
 * A FieldSelector capable of selecting fields represented by values of an enumeration type.
 * The {@code toString()} output of the enumeration value will be used to match name of the fields.
 *
 * @see FieldSelector
 * @see FieldSet
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class FieldEnumSelector extends FieldSet<Enum> implements FieldSelector {

	private final FieldNameSelector names = new FieldNameSelector();

	/**
	 * Returns the position of a given column represented by an enumeration value.
	 *
	 * @param column the column whose position will be returned
	 * @return the position of the given column.
	 */
	public int getFieldIndex(Enum column) {
		return names.getFieldIndex(column.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] getFieldIndexes(String[] headers) {
		names.set(ArgumentUtils.toArray(this.get()));
		return names.getFieldIndexes(headers);
	}

}
