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
import com.univocity.parsers.common.input.*;

/**
 * A fast and flexible fixed-with parser implementation.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see FixedWidthFormat
 * @see FixedWidthFields
 * @see FixedWidthParserSettings
 * @see FixedWidthWriter
 * @see AbstractParser
 */
public class FixedWidthParser extends AbstractParser<FixedWidthParserSettings> {

	private int[] lengths;
	private int[] rootLengths;

	private boolean[] ignore;
	private boolean[] rootIgnore;

	private FieldAlignment[] alignments;
	private FieldAlignment[] rootAlignments;

	private char[] paddings;
	private char[] rootPaddings;

	private final Lookup[] lookaheadFormats;
	private final Lookup[] lookbehindFormats;
	private Lookup lookupFormat;
	private Lookup lookbehindFormat;
	private int maxLookupLength;

	private final boolean ignoreLeadingWhitespace;
	private final boolean ignoreTrailingWhitespace;
	private final boolean skipToNewLine;
	private final boolean recordEndsOnNewLine;
	private final boolean skipEmptyLines;

	private boolean useDefaultPadding;
	private final char defaultPadding;
	private char padding;
	private FieldAlignment alignment;
	private final char newLine;

	private int length;
	private boolean initializeLookaheadInput = false;
	private LookaheadCharInputReader lookaheadInput;
	private final char wildcard;

	/**
	 * The FixedWidthParser supports all settings provided by {@link FixedWidthParserSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param settings the parser configuration
	 */
	public FixedWidthParser(FixedWidthParserSettings settings) {
		super(settings);
		ignoreLeadingWhitespace = settings.getIgnoreLeadingWhitespaces();
		ignoreTrailingWhitespace = settings.getIgnoreTrailingWhitespaces();
		skipToNewLine = settings.getSkipTrailingCharsUntilNewline();
		recordEndsOnNewLine = settings.getRecordEndsOnNewline();
		skipEmptyLines = settings.getSkipEmptyLines();
		lengths = settings.getAllLengths();
		alignments = settings.getFieldAlignments();
		paddings = settings.getFieldPaddings();
		ignore = settings.getFieldsToIgnore();

		lookaheadFormats = settings.getLookaheadFormats();
		lookbehindFormats = settings.getLookbehindFormats();
		wildcard = settings.getFormat().getLookupWildcard();

		if (lookaheadFormats != null || lookbehindFormats != null) {
			initializeLookaheadInput = true;
			rootLengths = lengths;
			rootAlignments = alignments;
			rootPaddings = paddings;
			rootIgnore = ignore;
			maxLookupLength = Lookup.calculateMaxLookupLength(lookaheadFormats, lookbehindFormats);

			this.context = new ParsingContextWrapper(context) {
				@Override
				public String[] headers() {
					return lookupFormat != null ? lookupFormat.fieldNames : super.headers();
				}
			};
		}

		FixedWidthFormat format = settings.getFormat();
		padding = format.getPadding();
		defaultPadding = padding;
		newLine = format.getNormalizedNewline();
		useDefaultPadding = settings.getUseDefaultPaddingForHeaders() && settings.isHeaderExtractionEnabled();
	}

	@Override
	protected void parseRecord() {
		if (ch == newLine && skipEmptyLines) {
			return;
		}

		boolean matched = false;
		if (lookaheadFormats != null || lookbehindFormats != null) {
			if (initializeLookaheadInput) {
				initializeLookaheadInput = false;
				this.lookaheadInput = new LookaheadCharInputReader(input, newLine, whitespaceRangeStart);
				this.input = lookaheadInput;
			}

			lookaheadInput.lookahead(maxLookupLength);

			if (lookaheadFormats != null) {
				for (int i = 0; i < lookaheadFormats.length; i++) {
					if (lookaheadInput.matches(ch, lookaheadFormats[i].value, wildcard)) {
						lengths = lookaheadFormats[i].lengths;
						ignore = lookaheadFormats[i].ignore;
						lookupFormat = lookaheadFormats[i];
						matched = true;
						break;
					}
				}
				if (lookbehindFormats != null && matched) {
					lookbehindFormat = null;
					for (int i = 0; i < lookbehindFormats.length; i++) {
						if (lookaheadInput.matches(ch, lookbehindFormats[i].value, wildcard)) {
							lookbehindFormat = lookbehindFormats[i];
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < lookbehindFormats.length; i++) {
					if (lookaheadInput.matches(ch, lookbehindFormats[i].value, wildcard)) {
						lookbehindFormat = lookbehindFormats[i];
						matched = true;
						lengths = rootLengths;
						ignore = rootIgnore;
						break;
					}
				}
			}

			if (!matched) {
				if (lookbehindFormat == null) {
					if (rootLengths == null) {
						throw new TextParsingException(context, "Cannot process input with the given configuration. No default field lengths defined and no lookahead/lookbehind value match '" + lookaheadInput.getLookahead(ch) + '\'');
					}
					lengths = rootLengths;
					alignments = rootAlignments;
					paddings = rootPaddings;
					ignore = rootIgnore;
					lookupFormat = null;
				} else {
					lengths = lookbehindFormat.lengths;
					alignments = lookbehindFormat.alignments;
					paddings = lookbehindFormat.paddings;
					ignore = lookbehindFormat.ignore;
					lookupFormat = lookbehindFormat;
				}
			}
		}

		int i;
		for (i = 0; i < lengths.length; i++) {
			length = lengths[i];
			if (paddings != null) {
				padding = useDefaultPadding ? defaultPadding : paddings[i];
			}
			if (alignments != null) {
				alignment = alignments[i];
			}
			skipPadding();

			if (ignoreLeadingWhitespace) {
				skipWhitespace();
			}

			if (recordEndsOnNewLine) {
				readValueUntilNewLine();
				if (ch == newLine) {
					output.valueParsed();
					useDefaultPadding = false;
					return;
				}
			} else if (length > 0) {
				readValue();
				if (i + 1 < lengths.length) {
					ch = input.nextChar();
				}
			}
			if (ignore[i]) {
				output.appender.reset();
			} else {
				output.valueParsed();
			}
		}

		if (skipToNewLine) {
			skipToNewLine();
		}
		useDefaultPadding = false;

	}

	private void skipToNewLine() {
		try {
			while (ch != newLine) {
				ch = input.nextChar();
			}
		} catch (EOFException e) {
			//ignore and let the EOF blow up again after the record is generated.
		}
	}

	private void skipPadding() {
		while (ch == padding && length-- > 0) {
			ch = input.nextChar();
		}
	}

	private void skipWhitespace() {
		while ((ch <= ' ' && whitespaceRangeStart < ch || ch == padding) && length-- > 0) {
			ch = input.nextChar();
		}
	}

	private void readValueUntilNewLine() {
		if (ignoreTrailingWhitespace) {
			if (alignment == FieldAlignment.RIGHT) {
				while (length-- > 0 && ch != newLine) {
					output.appender.appendIgnoringWhitespace(ch);
					ch = input.nextChar();
				}
			} else {
				while (length-- > 0 && ch != newLine) {
					output.appender.appendIgnoringWhitespaceAndPadding(ch, padding);
					ch = input.nextChar();
				}
			}

		} else {
			if (alignment == FieldAlignment.RIGHT) {
				while (length-- > 0 && ch != newLine) {
					output.appender.append(ch);
					ch = input.nextChar();
				}
			} else {
				while (length-- > 0 && ch != newLine) {
					output.appender.appendIgnoringPadding(ch, padding);
					ch = input.nextChar();
				}
			}
		}
	}

	private void readValue() {
		length--;
		if (ignoreTrailingWhitespace) {
			if (alignment == FieldAlignment.RIGHT) {
				output.appender.appendIgnoringWhitespace(ch);
				while (length-- > 0) {
					output.appender.appendIgnoringWhitespace(ch = input.nextChar());
				}
			} else {
				output.appender.appendIgnoringWhitespaceAndPadding(ch, padding);
				while (length-- > 0) {
					output.appender.appendIgnoringWhitespaceAndPadding(ch = input.nextChar(), padding);
				}
			}
		} else {
			if (alignment == FieldAlignment.RIGHT) {
				output.appender.append(ch);
				while (length-- > 0) {
					output.appender.append(ch = input.nextChar());
				}
			} else {
				output.appender.appendIgnoringPadding(ch, padding);
				while (length-- > 0) {
					output.appender.appendIgnoringPadding(ch = input.nextChar(), padding);
				}
			}
		}
	}
}

