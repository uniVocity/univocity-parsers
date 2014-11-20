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
package com.univocity.parsers.common.processor;

import java.util.*;

interface ColumnReaderProcessor<T> {

	public String[] getHeaders();

	public List<List<T>> getColumnValuesAsList();

	public void putColumnValuesInMapOfNames(Map<String, List<T>> map);

	public void putColumnValuesInMapOfIndexes(Map<Integer, List<T>> map);

	public Map<String, List<T>> getColumnValuesAsMapOfNames();

	public Map<Integer, List<T>> getColumnValuesAsMapOfIndexes();

}
