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
package com.univocity.parsers.fixed;

import com.univocity.parsers.common.*;

import java.util.*;

/**
 * The Fixed-Width format configuration. In addition to the default configuration in {@link Format}, the fixed-width format defines:
 *
 * <ul>
 * <li><b>padding <i>(defaults to ' ')</i>: </b> the character used for filling unwritten spaces in a fixed-width record.
 * <p>e.g. if a field has a length of 5 characters, but the value is 'ZZ', the field should contain <b>[ZZ   ]</b> (i.e. ZZ followed by 3 unwritten spaces).
 * <br>If the padding is set to '_', then the field will be written as <b>[ZZ___]</b></li>
 * </ul>
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.common.Format
 */
public class FixedWidthFormat extends Format {

	private char padding = ' ';
	private char lookupWildcard = '?';

	/**
	 * Returns the padding character used to represent unwritten spaces. Defaults to ' '
	 *
	 * @return the padding character
	 */
	public char getPadding() {
		return padding;
	}

	/**
	 * Defines the padding character used to represent unwritten spaces. Defaults to ' '
	 *
	 * @param padding the padding character
	 */
	public void setPadding(char padding) {
		this.padding = padding;
	}

	/**
	 * Identifies whether or not a given character represents a padding character
	 *
	 * @param padding the character to be verified
	 *
	 * @return true if the given character is the padding character, false otherwise
	 */
	public boolean isPadding(char padding) {
		return this.padding == padding;
	}

	@Override
	protected TreeMap<String, Object> getConfiguration() {
		TreeMap<String, Object> out = new TreeMap<String, Object>();
		out.put("Padding", padding);
		return out;
	}

	@Override
	public final FixedWidthFormat clone() {
		return (FixedWidthFormat) super.clone();
	}


	/**
	 * Returns the lookup wildcard character to accept any character in look-ahead or look-behind patterns defined
	 * using {@link FixedWidthParserSettings#addFormatForLookahead(String, FixedWidthFields)} or
	 * {@link FixedWidthParserSettings#addFormatForLookbehind(String, FixedWidthFields)}.
	 *
	 * Defaults to {@code '?'}
	 *
	 * @return the wildcard character to be used in lookahead/behind patterns.
	 */
	public char getLookupWildcard() {
		return lookupWildcard;
	}

	/**
	 * Defines the lookup wildcard character to accept any character in look-ahead or look-behind patterns defined
	 * using {@link FixedWidthParserSettings#addFormatForLookahead(String, FixedWidthFields)} or
	 * {@link FixedWidthParserSettings#addFormatForLookbehind(String, FixedWidthFields)}.
	 *
	 * Defaults to {@code '?'}
	 *
	 * @param lookupWildcard the wildcard character to be used in lookahead/behind patterns.
	 */
	public void setLookupWildcard(char lookupWildcard) {
		this.lookupWildcard = lookupWildcard;
	}
}
