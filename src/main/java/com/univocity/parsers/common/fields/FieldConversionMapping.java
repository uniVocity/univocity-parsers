/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.common.fields;

import java.util.*;

import com.univocity.parsers.common.*;
import com.univocity.parsers.conversions.*;

/**
 * A class for mapping field selections to sequences of {@link Conversion} objects
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class FieldConversionMapping {

	/**
	 * This list contains the sequence of conversions applied to sets of fields over multiple calls.
	 * <p>It is shared by {@link FieldConversionMapping#fieldNameConversionMapping}, {@link FieldConversionMapping#fieldIndexConversionMapping} and {@link FieldConversionMapping#convertAllMapping}.
	 * <p>Every time the user associates a sequence of conversions to a field, conversionSequence list will receive the FieldSelector.
	 */
	private final List<FieldSelector> conversionSequence = new ArrayList<FieldSelector>();

	private final AbstractConversionMapping<String> fieldNameConversionMapping = new AbstractConversionMapping<String>(conversionSequence) {
		@Override
		protected FieldSelector newFieldSelector() {
			return new FieldNameSelector();
		}
	};

	private final AbstractConversionMapping<Integer> fieldIndexConversionMapping = new AbstractConversionMapping<Integer>(conversionSequence) {
		@Override
		protected FieldSelector newFieldSelector() {
			return new FieldIndexSelector();
		}
	};

	private final AbstractConversionMapping<Integer> convertAllMapping = new AbstractConversionMapping<Integer>(conversionSequence) {
		@Override
		protected FieldSelector newFieldSelector() {
			return new AllIndexesSelector();
		}
	};

	/**
	 * This is the final sequence of conversions applied to each index in a record. It is populated when {@link FieldConversionMapping#prepareExecution(String[])} is invoked.
	 */
	private Map<Integer, List<Conversion<?, ?>>> conversionsByIndex = Collections.emptyMap();

	/**
	 * Prepares the conversions registered in this object to be executed against a given sequence of fields
	 *
	 * @param values The field sequence that identifies how records will be organized.
	 * <p> This is generally the sequence of headers in a record, but it might be just the first parsed row from a given input (as field selection by index is allowed).
	 */
	public void prepareExecution(String[] values) {
		if (fieldNameConversionMapping.isEmpty() && fieldIndexConversionMapping.isEmpty() && convertAllMapping.isEmpty()) {
			return;
		}

		if (!conversionsByIndex.isEmpty()) {
			return;
		}

		//Note this property is shared across all conversion mappings. This is required so
		//the correct conversion sequence is registered for all fields.
		conversionsByIndex = new HashMap<Integer, List<Conversion<?, ?>>>();

		// adds the conversions in the sequence they were created.
		for (FieldSelector next : conversionSequence) {
			fieldNameConversionMapping.prepareExecution(next, conversionsByIndex, values);
			fieldIndexConversionMapping.prepareExecution(next, conversionsByIndex, values);
			convertAllMapping.prepareExecution(next, conversionsByIndex, values);
		}
	}

	public void applyConversionsOnAllFields(Conversion<String, ?>... conversions) {
		convertAllMapping.registerConversions(conversions);
	}

	public FieldSet<Integer> applyConversionsOnFieldIndexes(Conversion<String, ?>... conversions) {
		return fieldIndexConversionMapping.registerConversions(conversions);
	}

	public FieldSet<String> applyConversionsOnFieldNames(Conversion<String, ?>... conversions) {
		return fieldNameConversionMapping.registerConversions(conversions);
	}

	/**
	 * Applies a sequence of conversions associated with an Object value at a given index in a record.
	 * @param executeInReverseOrder flag to indicate whether or not the conversion sequence must be executed in reverse order
	 * @param index The index of parsed value in a record
	 * @param value The value in a record
	 * @return the Object resulting from a sequence of conversions against the original value.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object reverseConversions(boolean executeInReverseOrder, int index, Object value) {
		List<Conversion<?, ?>> conversions = conversionsByIndex.get(index);
		if (conversions != null) {
			Conversion conversion = null;
			try {
				if (executeInReverseOrder) {
					for (int i = conversions.size() - 1; i >= 0; i--) {
						conversion = conversions.get(i);
						value = conversion.revert(value);
					}
				} else {
					for (Conversion<?, ?> c : conversions) {
						conversion = c;
						value = conversion.revert(value);
					}
				}
			} catch (DataProcessingException ex) {
				ex.setValue(value);
				ex.setColumnIndex(index);
				ex.markAsNonFatal();
				throw ex;
			} catch (Throwable ex) {
				if (conversion != null) {
					throw new IllegalStateException("Error converting value '" + value + "' using conversion " + conversion.getClass().getName(), ex);
				} else {
					throw new IllegalStateException("Error converting value '" + value + '\'', ex);
				}
			}
		}
		return value;
	}

	/**
	 * Applies a sequence of conversions associated with a String value parsed from a given index.
	 * @param index The index of parsed value in a record
	 * @param stringValue The parsed value in a record
	 * @return the Object produced by a sequence of conversions against the original String value.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object applyConversions(int index, String stringValue) {
		List<Conversion<?, ?>> conversions = conversionsByIndex.get(index);
		if (conversions != null) {
			Object result = stringValue;
			for (Conversion conversion : conversions) {
				try {
					result = conversion.execute(result);
				} catch (DataProcessingException ex) {
					ex.setColumnIndex(index);
					ex.markAsNonFatal();
					throw ex;
				} catch (Throwable ex) {
					throw new IllegalStateException("Error converting value '" + result + "' using conversion " + conversion.getClass().getName(), ex);
				}
			}
			return result;
		}
		return stringValue;
	}
}

/**
 * Class responsible for managing field selections and any conversion sequence associated with each.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @param <T> the FieldSelector type information used to uniquely identify a field (e.g. references to field indexes would use Integer, while references to field names would use String).
 *
 * @see FieldNameSelector
 * @see FieldIndexSelector
 */
abstract class AbstractConversionMapping<T> {

	private Map<FieldSelector, Conversion<String, ?>[]> conversionsMap;
	private final List<FieldSelector> conversionSequence;

	AbstractConversionMapping(List<FieldSelector> conversionSequence) {
		this.conversionSequence = conversionSequence;
	}

	/**
	 * Registers a sequence of conversions to a set of fields.
	 * <p>The selector instance that is used to store which fields should be converted is added to the {@link AbstractConversionMapping#conversionSequence} list in order to keep track of the correct conversion order.
	 * <p>This is required further conversion sequences might be added to the same fields in separate calls.
	 *
	 * @param conversions the conversion sequence to be applied to a set of fields.
	 * @return a FieldSet which provides methods to select the fields that must be converted or null if the FieldSelector returned by #newFieldSelector is not an instance of FieldSet (which is the case of {@link AllIndexesSelector}).
	 *
	 */
	@SuppressWarnings("unchecked")
	public FieldSet<T> registerConversions(Conversion<String, ?>... conversions) {
		ArgumentUtils.noNulls("Conversions", conversions);

		FieldSelector selector = newFieldSelector();

		if (conversionsMap == null) {
			conversionsMap = new LinkedHashMap<FieldSelector, Conversion<String, ?>[]>();
		}
		conversionsMap.put(selector, conversions);
		conversionSequence.add(selector);
		if (selector instanceof FieldSet) {
			return (FieldSet<T>) selector;
		}
		return null;
	}

	/**
	 * Creates a FieldSelector instance of the desired type. Used in @link FieldConversionMapping}.
	 * @return a new FieldSelector instance.
	 */
	protected abstract FieldSelector newFieldSelector();

	/**
	 * Get all indexes in the given selector and adds the conversions defined at that index to the map of conversionsByIndex.
	 * <p>This method is called in the same sequence each selector was created (in {@link FieldConversionMapping#prepareExecution(String[])})
	 * <p>At the end of the process, the map of conversionsByIndex will have each index with its list of conversions in the order they were declared.
	 *
	 * @param selector the selected fields for a given conversion sequence.
	 * @param conversionsByIndex map of all conversions registered to every field index, in the order they were declared
	 * @param values The field sequence that identifies how records will be organized.
	 * <p> This is generally the sequence of headers in a record, but it might be just the first parsed row from a given input (as field selection by index is allowed).
	 */
	@SuppressWarnings("unchecked")
	public void prepareExecution(FieldSelector selector, Map<Integer, List<Conversion<?, ?>>> conversionsByIndex, String[] values) {
		if (conversionsMap == null) {
			return;
		}

		//conversionsMap contains maps the conversions applied to a field selection
		//we will match the indexes where these conversions where applied and add them to the corresponding list in conversionsByIndex
		Conversion<String, ?>[] conversions = conversionsMap.get(selector);
		if (conversions == null) {
			return;
		}

		int[] fieldIndexes = selector.getFieldIndexes(values);

		for (int fieldIndex : fieldIndexes) {
			List<Conversion<?, ?>> conversionsAtIndex = conversionsByIndex.get(fieldIndex);
			if (conversionsAtIndex == null) {
				conversionsAtIndex = new ArrayList<Conversion<?, ?>>();
				conversionsByIndex.put(fieldIndex, conversionsAtIndex);
			}

			validateDuplicates(selector, conversionsAtIndex, conversions);
			conversionsAtIndex.addAll(Arrays.asList(conversions));
		}
	}

	/**
	 * Ensures an individual field does not have the same conversion object applied to it more than once.
	 * @param selector the selection of fields
	 * @param conversionsAtIndex the sequence of conversions applied to a given index
	 * @param conversionsToAdd the sequence of conversions to add to conversionsAtIndex
	 */
	private static void validateDuplicates(FieldSelector selector, List<Conversion<?, ?>> conversionsAtIndex, Conversion<?, ?>[] conversionsToAdd) {
		for (Conversion<?, ?> toAdd : conversionsToAdd) {
			for (Conversion<?, ?> existing : conversionsAtIndex) {
				if (toAdd == existing) {
					throw new DataProcessingException("Duplicate conversion " + toAdd.getClass().getName() + " being applied to " + selector.describe());
				}
			}
		}
	}

	/**
	 * Queries if any conversions were associated with any field
	 * @return true if no conversions were associated with any field; false otherwise
	 */
	public boolean isEmpty() {
		return conversionsMap == null || conversionsMap.isEmpty();
	}
}
