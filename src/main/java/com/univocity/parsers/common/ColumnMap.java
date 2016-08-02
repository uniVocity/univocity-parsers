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
package com.univocity.parsers.common;

import com.univocity.parsers.common.fields.*;

import java.util.*;

/**
 * Class responsible for calculating and storing the position of fields parsed from the input.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class ColumnMap {

	private Map<String, Integer> columnMap;
	private Map<String, Integer> normalizedColumnMap;
	private int[] enumMap;
	private int[] extractedIndexes = null;
	private final Context context;
	private final ParserOutput output;

	public ColumnMap(Context context, ParserOutput output){
		this.context = context;
		this.output = output;
	}

	/**
	 * Returns the position of a header (0 based).
	 *
	 * @param header the header whose position will be returned
	 *
	 * @return the position of the given header, or -1 if it could not be found.
	 */
	public int indexOf(String header) {
		if (columnMap != null && columnMap.isEmpty()) {
			return -1;
		}
		validateHeader(header);

		if (columnMap == null) {
			String[] headers = context.headers();
			if (headers == null) {
				columnMap = Collections.emptyMap();
				normalizedColumnMap = Collections.emptyMap();
				return -1;
			}
			columnMap = new HashMap<String, Integer>(headers.length);

			extractedIndexes = context.extractedFieldIndexes();

			if (extractedIndexes != null) {
				if (context.columnsReordered()) {
					for (int i = 0; i < extractedIndexes.length; i++) {
						int originalIndex = extractedIndexes[i];
						String h = headers[originalIndex];
						columnMap.put(h, i);
					}
				} else {
					for (int i = 0; i < extractedIndexes.length; i++) {
						columnMap.put(headers[i], i);
					}
				}
			} else {
				for (int i = 0; i < headers.length; i++) {
					columnMap.put(headers[i], i);
				}
			}

			normalizedColumnMap = new HashMap<String, Integer>(headers.length);
			for (Map.Entry<String, Integer> e : columnMap.entrySet()) {
				if (e.getKey() != null) {
					normalizedColumnMap.put(e.getKey().trim().toLowerCase(), e.getValue());
				}
			}
		}


		Integer index = columnMap.get(header);
		if (index == null) {
			index = normalizedColumnMap.get(header.trim().toLowerCase());
			if (index == null) {
				return -1;
			}
		}
		return index.intValue();
	}

	private void validateHeader(Object header){
		if (header == null) {
			if (context.headers() == null) {
				throw new IllegalArgumentException("Header name cannot be null.");
			}
			throw new IllegalArgumentException("Header name cannot be null. Use one of the available column names: " + Arrays.asList(context.headers()));
		}
	}

	/**
	 * Returns the position of a header (0 based).
	 *
	 * @param header the header whose position will be returned
	 *
	 * @return the position of the given header, or -1 if it could not be found.
	 */
	public int indexOf(Enum<?> header) {
		if (enumMap != null && enumMap.length == 0) {
			return -1;
		}
		validateHeader(header);

		if (enumMap == null) {
			String[] headers = context.headers();
			if (headers == null) {
				enumMap = new int[0];
				return -1;
			}

			Enum<?>[] constants = header.getClass().getEnumConstants();
			int lastOrdinal = Integer.MIN_VALUE;
			for (int i = 0; i < constants.length; i++) {
				if (lastOrdinal < constants[i].ordinal()) {
					lastOrdinal = constants[i].ordinal();
				}
			}

			enumMap = new int[lastOrdinal + 1];


			FieldSelector selector = output == null ? null : output.getFieldSelector();
			if (!context.columnsReordered()) {
				selector = null;
			}

			for (int i = 0; i < constants.length; i++) {
				Enum<?> constant = constants[i];
				String name = constant.toString();
				int index = ArgumentUtils.indexOf(headers, name, selector);
				enumMap[constant.ordinal()] = index;
			}
		}
		return enumMap[header.ordinal()];
	}

	void reset() {
		columnMap = null;
		normalizedColumnMap = null;
		enumMap = null;
		extractedIndexes = null;
	}

}
