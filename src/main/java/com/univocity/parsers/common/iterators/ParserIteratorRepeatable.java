package com.univocity.parsers.common.iterators;

import com.univocity.parsers.common.*;

/**
 * An {@code iterator} over the parser results returning them as {@code String[]s}
 *
 * This allows the {@code input} to be re-parsed by the {@code parser} so multiple iterations
 * are possible.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public abstract class ParserIteratorRepeatable extends ParserIterator {

	/**
	 *Creates a {@code ParserIteratorRepeatable} using the provided {@code parser}
	 *
	 * @param parser the {@code parser} to iterate over
	 */
	protected ParserIteratorRepeatable(AbstractParser parser) {
		super(parser);
	}

	@Override
	public ResultIterator<String[], ParsingContext> iterator() {
		return new ResultIterator<String[], ParsingContext>() {
			@Override
			public ParsingContext getContext() {
				return ParserIteratorRepeatable.super.getContext();
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
			public String[] next() {
				return parser.parseNext();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Can't remove row");
			}
		};
	}
}
