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
import java.sql.*;

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
	 * @param writerSettings the configuration for new writer
	 *
	 * @return a writer implementation configured according to the given settings object.
	 */
	protected abstract AbstractWriter<W> createWriter(Writer output, W writerSettings);

	private final String routineDescription;
	private P parserSettings;
	private W writerSettings;

	/**
	 * Creates a new instance of this routine class.
	 *
	 * @param routineDescription description of the routines for a given format
	 */
	public AbstractRoutines(String routineDescription) {
		this.routineDescription = routineDescription;
	}

	private void validateWriterSettings() {
		if (writerSettings == null) {
			throw new IllegalStateException("Writer settings not defined. Please configure the output for " + routineDescription);
		}
	}

	private void validateParserSettings() {
		if (writerSettings == null) {
			throw new IllegalStateException("Parser settings not defined. Please configure the input for " + routineDescription);
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
	 * @param input the input data to be parsed using the settings provided in {@link #getParserSettings()}
	 * @param output the output into where the input data should be written, using the format provided in {@link #getWriterSettings()}
	 */
	public final void parseAndWrite(Reader input, Writer output) {
		validateWriterSettings();
		validateParserSettings();

		RowWriterProcessor writerProcessor = writerSettings.getRowWriterProcessor();
		if(writerProcessor != null){
			throw new IllegalStateException("Cannot parse and write to the output. Writer is configured to use row writer processor " + writerProcessor + " (" + writerProcessor.getClass().getName() + ")");
		}

		RowProcessor parserProcessor = parserSettings.getRowProcessor();
		if (parserProcessor == NoopRowProcessor.instance) {
			parserSettings.setRowProcessor(createWritingRowProcessor(output));
		} else {
			throw new IllegalStateException("Cannot parse and write to the output. Parser is configured to use row processor " + parserProcessor + " (" + parserProcessor.getClass().getName() + ")");
		}

		AbstractParser<P> parser = createParser(parserSettings);
		parser.parse(input);
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

}
