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
package com.univocity.parsers.tsv;

import java.util.*;

import com.univocity.parsers.common.*;

/**
 * This is the configuration class used by the TSV parser ({@link TsvParser})
 *
 * <p>It supports the configuration options provided by {@link CommonParserSettings} only
 *
 * @see com.univocity.parsers.tsv.TsvParser
 * @see com.univocity.parsers.tsv.TsvFormat
 * @see com.univocity.parsers.common.CommonParserSettings
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class TsvParserSettings extends CommonParserSettings<TsvFormat> {

	/**
	 * Returns the default TsvFormat configured to handle TSV inputs
	 * @return and instance of TsvFormat configured to handle TSV
	 */
	@Override
	protected TsvFormat createDefaultFormat() {
		return new TsvFormat();
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
	}
}
