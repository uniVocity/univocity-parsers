package com.univocity.parsers.common.iterators;

import com.univocity.parsers.common.*;

/**
 * An {@code Iterable} over an {@link AbstractParser}
 *
 * @param <T> the type of the results that are returned from the {@code Iterator}
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public abstract class ParserIterable<T> implements IterableResult<T, ParsingContext> {

	protected AbstractParser parser;

	/**
	 * Creates a {@code ParserIterable} using the provided {@code parser}
	 *
	 * @param parser the {@code parser} to iterate over
	 */
	ParserIterable(AbstractParser parser) {
		this.parser = parser;
	}

	@Override
	public ParsingContext getContext() {
		return parser.getContext();
	}

	@Override
	public abstract ResultIterator<T, ParsingContext> iterator();
}
