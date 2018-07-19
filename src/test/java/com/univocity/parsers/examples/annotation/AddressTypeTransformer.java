/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
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

package com.univocity.parsers.examples.annotation;

import com.univocity.parsers.annotations.*;

import java.lang.reflect.*;

public class AddressTypeTransformer extends HeaderTransformer {

	private String prefix;

	public AddressTypeTransformer(String... args) {
		prefix = args[0];
	}

	@Override
	public String transformName(Field field, String name) {
		return prefix + "_" + name;
	}
}
