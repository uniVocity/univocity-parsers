/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common.routine;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;

import java.io.*;
import java.nio.charset.*;
import java.sql.*;
import java.util.*;

/**
 * Basic implementation of commonly used routines around parsing/writing of data that can be reused and extended
 * by parsers/writers of any supported format.
 *
 * @param <P> parser configuration class
 * @param <W> writer configuration class
 */
public abstract class AbstractRoutines<P extends CommonParserSettings<?>, W extends CommonWriterSettings<?>> {

	/**
	 * Creates a new parser implementation using the given parser configuration
	 *
	 * @param parserSettings the configuration for new parser
	 *
	 * @return a parser implementation configured according to the given settings object.
	 */
	protected abstract AbstractParser<P> createParser(P parserSettings);

	/**
	 * Creates a new writer implementation using the given writer configuration
	 *
	 * @param output         target output of the routine.
	 * @param writerSettings the configuration for new writer
	 *
	 * @return a writer implementation configured according to the given settings object.
	 */
	protected abstract AbstractWriter<W> createWriter(Writer output, W writerSettings);

	/**
	 * Creates a default parser settings configuration
	 *
	 * @return a new instance of a usable parser configuration.
	 */
	protected abstract P createDefaultParserSettings();

	/**
	 * Creates a default writer settings configuration
	 *
	 * @return a new instance of a usable writer configuration.
	 */
	protected abstract W createDefaultWriterSettings();

	private final String routineDescription;
	private P parserSettings;
	private W writerSettings;

	/**
	 * Creates a new instance of this routine class.
	 *
	 * @param routineDescription description of the routines for a given format
	 */
	public AbstractRoutines(String routineDescription) {
		this(routineDescription, null, null);
	}

	/**
	 * Creates a new instance of this routine class.
	 *
	 * @param routineDescription description of the routines for a given format
	 * @param parserSettings     configuration to use for parsing
	 */
	public AbstractRoutines(String routineDescription, P parserSettings) {
		this(routineDescription, parserSettings, null);
	}

	/**
	 * Creates a new instance of this routine class.
	 *
	 * @param routineDescription description of the routines for a given format
	 * @param writerSettings     configuration to use for writing
	 */
	public AbstractRoutines(String routineDescription, W writerSettings) {
		this(routineDescription, null, writerSettings);
	}

	/**
	 * Creates a new instance of this routine class.
	 *
	 * @param routineDescription description of the routines for a given format
	 * @param parserSettings     configuration to use for parsing
	 * @param writerSettings     configuration to use for writing
	 */
	public AbstractRoutines(String routineDescription, P parserSettings, W writerSettings) {
		this.routineDescription = routineDescription;
		this.parserSettings = parserSettings;
		this.writerSettings = writerSettings;
	}

	private void validateWriterSettings() {
		if (writerSettings == null) {
			writerSettings = createDefaultWriterSettings();
		}
	}

	private void validateParserSettings() {
		if (parserSettings == null) {
			parserSettings = createDefaultParserSettings();
		}
	}

	/**
	 * Returns the parser configuration (if any) used by the routines of this utility class.
	 *
	 * @return the parser configuration.
	 */
	public final P getParserSettings() {
		return parserSettings;
	}

	/**
	 * Defines the parser configuration to be used by the routines of this utility class.
	 *
	 * @param parserSettings the parser configuration.
	 */
	public final void setParserSettings(P parserSettings) {
		this.parserSettings = parserSettings;
	}

	/**
	 * Returns the writer configuration (if any) used by the routines of this utility class.
	 *
	 * @return the writer configuration.
	 */
	public final W getWriterSettings() {
		return writerSettings;
	}

	/**
	 * Defines the writer configuration to be used by the routines of this utility class.
	 *
	 * @param writerSettings the parser configuration.
	 */
	public final void setWriterSettings(W writerSettings) {
		this.writerSettings = writerSettings;
	}

	/**
	 * Allows writers of any given format to adjust its settings to take into account column headers and lengths
	 * prior to writing data in any routine.
	 *
	 * @param headers headers to be written
	 * @param lengths the corresponding lengths of each header
	 */
	protected void adjustColumnLengths(String[] headers, int[] lengths) {

	}

	/**
	 * Dumps the content of a {@link java.sql.ResultSet} into a file.
	 *
	 * @param rs     the {@link java.sql.ResultSet} whose contents should be read and written to a given output
	 * @param output the output file that will store the data in the given {@link java.sql.ResultSet}
	 *               in the format specified by concrete implementations of this class.
	 */
	public final void write(ResultSet rs, File output) {
		write(rs, ArgumentUtils.newWriter(output));
	}

	/**
	 * Dumps the content of a {@link java.sql.ResultSet} into a file.
	 *
	 * @param rs       the {@link java.sql.ResultSet} whose contents should be read and written to a given output
	 * @param output   the output file that will store the data in the given {@link java.sql.ResultSet}
	 *                 in the format specified by concrete implementations of this class.
	 * @param encoding the output encoding of the file
	 */
	public final void write(ResultSet rs, File output, String encoding) {
		write(rs, ArgumentUtils.newWriter(output, encoding));
	}

	/**
	 * Dumps the content of a {@link java.sql.ResultSet} into a file.
	 *
	 * @param rs       the {@link java.sql.ResultSet} whose contents should be read and written to a given output
	 * @param output   the output file that will store the data in the given {@link java.sql.ResultSet}
	 *                 in the format specified by concrete implementations of this class.
	 * @param encoding the output encoding of the file
	 */
	public final void write(ResultSet rs, File output, Charset encoding) {
		write(rs, ArgumentUtils.newWriter(output, encoding));
	}

	/**
	 * Dumps the content of a {@link java.sql.ResultSet} into an output stream.
	 *
	 * @param rs     the {@link java.sql.ResultSet} whose contents should be read and written to a given output
	 * @param output the output stream that will store the data in the given {@link java.sql.ResultSet}
	 *               in the format specified by concrete implementations of this class.
	 */
	public final void write(ResultSet rs, OutputStream output) {
		write(rs, ArgumentUtils.newWriter(output));
	}

	/**
	 * Dumps the content of a {@link java.sql.ResultSet} into an output stream.
	 *
	 * @param rs       the {@link java.sql.ResultSet} whose contents should be read and written to a given output
	 * @param output   the output file that will store the data in the given {@link java.sql.ResultSet}
	 *                 in the format specified by concrete implementations of this class.
	 * @param encoding the output encoding of the output stream
	 */
	public final void write(ResultSet rs, OutputStream output, String encoding) {
		write(rs, ArgumentUtils.newWriter(output, encoding));
	}

	/**
	 * Dumps the content of a {@link java.sql.ResultSet} into an output stream.
	 *
	 * @param rs       the {@link java.sql.ResultSet} whose contents should be read and written to a given output
	 * @param output   the output file that will store the data in the given {@link java.sql.ResultSet}
	 *                 in the format specified by concrete implementations of this class.
	 * @param encoding the output encoding of the output stream
	 */
	public final void write(ResultSet rs, OutputStream output, Charset encoding) {
		write(rs, ArgumentUtils.newWriter(output, encoding));
	}

	/**
	 * Dumps the content of a {@link java.sql.ResultSet}.
	 *
	 * @param rs     the {@link java.sql.ResultSet} whose contents should be read and written to a given output
	 * @param output the output that will store the data in the given {@link java.sql.ResultSet}
	 *               in the format specified by concrete implementations of this class.
	 */
	public final void write(ResultSet rs, Writer output) {
		validateWriterSettings();
		boolean hasWriterProcessor = writerSettings.getRowWriterProcessor() != null;

		AbstractWriter<W> writer = null;
		long rowCount = 0L;

		Object[] row = null;
		try {
			try {
				ResultSetMetaData md = rs.getMetaData();
				int columns = md.getColumnCount();

				String[] headers = new String[columns];
				int[] lengths = new int[columns];
				for (int i = 1; i <= columns; i++) {
					headers[i - 1] = md.getColumnLabel(i);

					int precision = md.getPrecision(i);
					int scale = md.getScale(i);
					int length;
					if (precision != 0 && scale != 0) {
						length = precision + scale + 2; //+2 to account for decimal point (non-integer numbers) and minus characters (negative numbers).
					} else {
						length = precision + scale;
					}
					lengths[i - 1] = length;
				}

				writerSettings.setHeaders(headers);
				adjustColumnLengths(headers, lengths);

				writer = createWriter(output, writerSettings);

				if (writerSettings.isHeaderWritingEnabled()) {
					writer.writeHeaders();
				}

				row = new Object[columns];
				while (rs.next()) {
					for (int i = 1; i <= columns; i++) {
						row[i - 1] = rs.getObject(i);
					}
					if (hasWriterProcessor) {
						writer.processRecord(row);
					} else {
						writer.writeRow(row);
					}
					rowCount++;
				}
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			throw new TextWritingException("Error writing data from result set", rowCount, row, e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Reads all data from a given input and writes it to an output.
	 *
	 * @param input  the input data to be parsed using the settings provided in {@link #getParserSettings()}
	 * @param output the output into where the input data should be written, using the format provided in {@link #getWriterSettings()}
	 */
	public final void parseAndWrite(Reader input, Writer output) {

		setRowWriterProcessor(null);
		setRowProcessor(createWritingRowProcessor(output));
		try {
			AbstractParser<P> parser = createParser(parserSettings);
			parser.parse(input);
		} finally {
			parserSettings.setRowProcessor(null);
		}
	}

	private void setRowWriterProcessor(RowWriterProcessor rowWriterProcessor) {
		validateWriterSettings();
		writerSettings.setRowWriterProcessor(rowWriterProcessor);
	}

	private void setRowProcessor(RowProcessor rowProcessor) {
		validateParserSettings();
		parserSettings.setRowProcessor(rowProcessor);
	}

	private RowProcessor createWritingRowProcessor(final Writer output) {
		return new RowProcessor() {
			private AbstractWriter<W> writer;

			@Override
			public void processStarted(ParsingContext context) {
				writer = createWriter(output, writerSettings);
			}

			@Override
			public void rowProcessed(String[] row, ParsingContext context) {
				writer.writeRow(row);
			}

			@Override
			public void processEnded(ParsingContext context) {
				writer.close();
			}
		};
	}

	/**
	 * Writes a collection of annotated java beans to a given output.
	 *
	 * @param elements the elements to write to the output
	 * @param beanType the type of element in the given collection
	 * @param output   the output into which the given elements will be written
	 * @param headers  headers to use in the first row of the written result.
	 * @param <T>      the type of element in the given collection
	 */
	public <T> void writeAll(Iterable<T> elements, Class<T> beanType, File output, String... headers) {
		writeAll(elements, beanType, ArgumentUtils.newWriter(output), headers);
	}

	/**
	 * Writes a collection of annotated java beans to a given output.
	 *
	 * @param elements the elements to write to the output
	 * @param beanType the type of element in the given collection
	 * @param output   the output into which the given elements will be written
	 * @param encoding the output encoding to use for writing
	 * @param headers  headers to use in the first row of the written result.
	 * @param <T>      the type of element in the given collection
	 */
	public <T> void writeAll(Iterable<T> elements, Class<T> beanType, File output, String encoding, String[] headers) {
		writeAll(elements, beanType, ArgumentUtils.newWriter(output, encoding), headers);
	}

	/**
	 * Writes a collection of annotated java beans to a given output.
	 *
	 * @param elements the elements to write to the output
	 * @param beanType the type of element in the given collection
	 * @param output   the output into which the given elements will be written
	 * @param encoding the output encoding to use for writing
	 * @param headers  headers to use in the first row of the written result.
	 * @param <T>      the type of element in the given collection
	 */
	public <T> void writeAll(Iterable<T> elements, Class<T> beanType, File output, Charset encoding, String... headers) {
		writeAll(elements, beanType, ArgumentUtils.newWriter(output, encoding), headers);
	}


	/**
	 * Writes a collection of annotated java beans to a given output.
	 *
	 * @param elements the elements to write to the output
	 * @param beanType the type of element in the given collection
	 * @param output   the output into which the given elements will be written
	 * @param headers  headers to use in the first row of the written result.
	 * @param <T>      the type of element in the given collection
	 */
	public <T> void writeAll(Iterable<T> elements, Class<T> beanType, OutputStream output, String... headers) {
		writeAll(elements, beanType, ArgumentUtils.newWriter(output), headers);
	}

	/**
	 * Writes a collection of annotated java beans to a given output.
	 *
	 * @param elements the elements to write to the output
	 * @param beanType the type of element in the given collection
	 * @param output   the output into which the given elements will be written
	 * @param encoding the output encoding to use for writing
	 * @param headers  headers to use in the first row of the written result.
	 * @param <T>      the type of element in the given collection
	 */
	public <T> void writeAll(Iterable<T> elements, Class<T> beanType, OutputStream output, String encoding, String[] headers) {
		writeAll(elements, beanType, ArgumentUtils.newWriter(output, encoding), headers);
	}

	/**
	 * Writes a collection of annotated java beans to a given output.
	 *
	 * @param elements the elements to write to the output
	 * @param beanType the type of element in the given collection
	 * @param output   the output into which the given elements will be written
	 * @param encoding the output encoding to use for writing
	 * @param headers  headers to use in the first row of the written result.
	 * @param <T>      the type of element in the given collection
	 */
	public <T> void writeAll(Iterable<T> elements, Class<T> beanType, OutputStream output, Charset encoding, String... headers) {
		writeAll(elements, beanType, ArgumentUtils.newWriter(output, encoding), headers);
	}

	/**
	 * Writes a collection of annotated java beans to a given output.
	 *
	 * @param elements the elements to write to the output
	 * @param beanType the type of element in the given collection
	 * @param output   the output into which the given elements will be written
	 * @param headers  headers to use in the first row of the written result.
	 * @param <T>      the type of element in the given collection
	 */
	public <T> void writeAll(Iterable<T> elements, Class<T> beanType, Writer output, String... headers) {
		setRowWriterProcessor(new BeanWriterProcessor<T>(beanType));
		try {
			if (headers.length > 0) {
				writerSettings.setHeaders(headers);
				writerSettings.setHeaderWritingEnabled(true);
			}
			createWriter(output, writerSettings).processRecordsAndClose(elements);
		} finally {
			writerSettings.setRowWriterProcessor(null);
		}
	}

	/**
	 * Parses a file into a list of annotated java beans
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the file to be parsed
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return an {@link Iterable} that allows iterating over the input and producing instances of java beans on demand.
	 */
	public <T> List<T> parseAll(final Class<T> beanType, final File input) {
		return parseAll(beanType, ArgumentUtils.newReader(input));
	}

	/**
	 * Parses a file into a list of annotated java beans
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the file to be parsed
	 * @param encoding encoding of the given file
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return an {@link Iterable} that allows iterating over the input and producing instances of java beans on demand.
	 */
	public <T> List<T> parseAll(final Class<T> beanType, final File input, String encoding) {
		return parseAll(beanType, ArgumentUtils.newReader(input, encoding));
	}

	/**
	 * Parses a file into a list of annotated java beans
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the file to be parsed
	 * @param encoding encoding of the given file
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return a list containing all java beans read from the input.
	 */
	public <T> List<T> parseAll(final Class<T> beanType, final File input, Charset encoding) {
		return parseAll(beanType, ArgumentUtils.newReader(input, encoding));
	}


	/**
	 * Parses an input stream into a list of annotated java beans
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the input stream to be parsed
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return a list containing all java beans read from the input.
	 */
	public <T> List<T> parseAll(final Class<T> beanType, final InputStream input) {
		return parseAll(beanType, ArgumentUtils.newReader(input));
	}

	/**
	 * Parses an input stream into a list of annotated java beans
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the input stream to be parsed
	 * @param encoding encoding of the given input stream
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return a list containing all java beans read from the input.
	 */
	public <T> List<T> parseAll(final Class<T> beanType, final InputStream input, String encoding) {
		return parseAll(beanType, ArgumentUtils.newReader(input, encoding));
	}

	/**
	 * Parses an input stream into a list of annotated java beans
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the input stream to be parsed
	 * @param encoding encoding of the given input stream
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return a list containing all java beans read from the input.
	 */
	public <T> List<T> parseAll(final Class<T> beanType, final InputStream input, Charset encoding) {
		return parseAll(beanType, ArgumentUtils.newReader(input, encoding));
	}

	/**
	 * Parses the input into a list of annotated java beans
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the input to be parsed
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return a list containing all java beans read from the input.
	 */
	public <T> List<T> parseAll(Class<T> beanType, Reader input) {
		BeanListProcessor processor = new BeanListProcessor<T>(beanType);
		setRowProcessor(processor);
		try {
			createParser(parserSettings).parse(input);
			return processor.getBeans();
		} finally {
			parserSettings.setRowProcessor(null);
		}
	}

	/**
	 * Iterates over a file to produce instances of annotated java beans on demand.
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the file to be parsed
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return an {@link Iterable} that allows iterating over the input and producing instances of java beans on demand.
	 */
	public <T> IterableResult<T, ParsingContext> iterate(final Class<T> beanType, final File input) {
		return iterate(beanType, ArgumentUtils.newReader(input));
	}

	/**
	 * Iterates over a file to produce instances of annotated java beans on demand.
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the file to be parsed
	 * @param encoding encoding of the given file
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return an {@link Iterable} that allows iterating over the input and producing instances of java beans on demand.
	 */
	public <T> IterableResult<T, ParsingContext> iterate(final Class<T> beanType, final File input, String encoding) {
		return iterate(beanType, ArgumentUtils.newReader(input, encoding));
	}

	/**
	 * Iterates over a file to produce instances of annotated java beans on demand.
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the file to be parsed
	 * @param encoding encoding of the given file
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return an {@link Iterable} that allows iterating over the input and producing instances of java beans on demand.
	 */
	public <T> IterableResult<T, ParsingContext> iterate(final Class<T> beanType, final File input, Charset encoding) {
		return iterate(beanType, ArgumentUtils.newReader(input, encoding));
	}


	/**
	 * Iterates over an input stream to produce instances of annotated java beans on demand.
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the input stream to be parsed
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return an {@link Iterable} that allows iterating over the input and producing instances of java beans on demand.
	 */
	public <T> IterableResult<T, ParsingContext> iterate(final Class<T> beanType, final InputStream input) {
		return iterate(beanType, ArgumentUtils.newReader(input));
	}

	/**
	 * Iterates over an input stream to produce instances of annotated java beans on demand.
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the input stream to be parsed
	 * @param encoding encoding of the given input stream
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return an {@link Iterable} that allows iterating over the input and producing instances of java beans on demand.
	 */
	public <T> IterableResult<T, ParsingContext> iterate(final Class<T> beanType, final InputStream input, String encoding) {
		return iterate(beanType, ArgumentUtils.newReader(input, encoding));
	}

	/**
	 * Iterates over an input stream to produce instances of annotated java beans on demand.
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the input stream to be parsed
	 * @param encoding encoding of the given input stream
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return an {@link Iterable} that allows iterating over the input and producing instances of java beans on demand.
	 */
	public <T> IterableResult<T, ParsingContext> iterate(final Class<T> beanType, final InputStream input, Charset encoding) {
		return iterate(beanType, ArgumentUtils.newReader(input, encoding));
	}

	/**
	 * Iterates over an input to produce instances of annotated java beans on demand.
	 *
	 * @param beanType the type of java beans to be instantiated.
	 * @param input    the input to be parsed
	 * @param <T>      the type of java beans to be instantiated.
	 *
	 * @return an {@link Iterable} that allows iterating over the input and producing instances of java beans on demand.
	 */
	public <T> IterableResult<T, ParsingContext> iterate(final Class<T> beanType, final Reader input) {
		final Object[] beanHolder = new Object[1];

		setRowProcessor(new BeanProcessor<T>(beanType) {
			@Override
			public void beanProcessed(T bean, ParsingContext context) {
				beanHolder[0] = bean;
			}

			@Override
			public void processEnded(ParsingContext context) {
				super.processEnded(context);
				parserSettings.setRowProcessor(null);
			}
		});

		return new IterableResult<T, ParsingContext>() {

			private ParsingContext context;

			@Override
			public ParsingContext getContext() {
				return context;
			}

			@Override
			public ResultIterator<T, ParsingContext> iterator() {
				final AbstractParser<P> parser = createParser(parserSettings);
				parser.beginParsing(input);
				context = parser.getContext();
				parser.parseNext();

				return new ResultIterator<T, ParsingContext>() {

					@Override
					public boolean hasNext() {
						return !parser.getContext().isStopped() || beanHolder[0] != null;
					}

					@Override
					public T next() {
						T out = (T) beanHolder[0];
						if (parser.parseNext() == null) {
							beanHolder[0] = null;
						}
						return out;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException("Can't remove beans");
					}

					@Override
					public ParsingContext getContext() {
						return context;
					}
				};
			}
		};
	}

	@Override
	public String toString() {
		return routineDescription;
	}

	/**
	 * Calculates the dimensions of a file (row and column count).
	 *
	 * @param input the file to be parsed
	 *
	 * @return a {@link InputDimension} with information about the dimensions of the given input.
	 */
	public InputDimension getInputDimension(final File input) {
		return getInputDimension(ArgumentUtils.newReader(input));
	}

	/**
	 * Calculates the dimensions of a file (row and column count).
	 *
	 * @param input    the file to be parsed
	 * @param encoding encoding of the given file
	 *
	 * @return a {@link InputDimension} with information about the dimensions of the given input.
	 */
	public InputDimension getInputDimension(final File input, String encoding) {
		return getInputDimension(ArgumentUtils.newReader(input, encoding));
	}

	/**
	 * Calculates the dimensions of a given input (row and column count).
	 *
	 * @param input the input to be parsed
	 *
	 * @return a {@link InputDimension} with information about the dimensions of the given input.
	 */
	public InputDimension getInputDimension(final InputStream input) {
		return getInputDimension(ArgumentUtils.newReader(input));
	}

	/**
	 * Calculates the dimensions of a given input (row and column count).
	 *
	 * @param input    the input to be parsed
	 * @param encoding encoding of the given input
	 *
	 * @return a {@link InputDimension} with information about the dimensions of the given input.
	 */
	public InputDimension getInputDimension(final InputStream input, String encoding) {
		return getInputDimension(ArgumentUtils.newReader(input, encoding));
	}

	/**
	 * Calculates the dimensions of a given input (row and column count).
	 *
	 * @param input the input to be parsed
	 *
	 * @return a {@link InputDimension} with information about the dimensions of the given input.
	 */
	public InputDimension getInputDimension(Reader input) {

		final InputDimension out = new InputDimension();

		setRowProcessor(new AbstractRowProcessor() {
			int lastColumn;

			@Override
			public void rowProcessed(String[] row, ParsingContext context) {
				if (lastColumn < row.length) {
					lastColumn = row.length;
				}
			}

			@Override
			public void processEnded(ParsingContext context) {
				out.rows = context.currentRecord();
				out.columns = lastColumn;
			}
		});

		P settings = getParserSettings();
		settings.setMaxCharsPerColumn(-1);

		if (settings.getMaxColumns() < 1000000) { //one million columns should be more than enough.
			settings.setMaxColumns(1000000);
		}

		//The parser will return values for the columns selected.
		//By selecting no indexes here, no String objects will be created
		settings.selectIndexes(/*nothing here*/);

		//By disabling column reordering, we get the original row, with nulls in the columns as nothing was selected.
		settings.setColumnReorderingEnabled(false);

		createParser(settings).parse(input);

		return out;
	}
}
