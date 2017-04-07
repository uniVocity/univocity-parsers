package com.univocity.parsers.common.iterators;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.record.*;

/**
 * An {@code iterator} over the parser results returning them as {@code Records}
 *
 * This allows the {@code input} to be re-parsed by the {@code parser} so multiple iterations
 * are possible.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public abstract class ParserRecordIteratorRepeatable extends ParserRecordIterator {

	/**
	 * Creates a {@code ParserRecordIteratorRepeatable} using the provided {@code parser}
	 *
	 * @param parser the {@code parser} to iterate over
	 */
	protected ParserRecordIteratorRepeatable(AbstractParser parser) {
		super(parser);
	}

	@Override
	public ResultIterator<Record, ParsingContext> iterator() {
		return new ResultIterator<Record, ParsingContext>() {
			@Override
			public ParsingContext getContext() {
				return ParserRecordIteratorRepeatable.super.getContext();
			}

			@Override
			public boolean hasNext() {
				if (!startedParser) {
					beginParsing();
					startedParser = true;
				}
				if (getContext().isStopped()) {
					startedParser = false;
					return false;
				}
				return true;
			}

			@Override
			public Record next() {
				return parser.parseNextRecord();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Can't remove record");
			}
		};
	}
}
