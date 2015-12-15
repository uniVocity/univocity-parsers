/*
 * Copyright (c) 2015 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 *
 */

package com.univocity.parsers.common.routine;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;

import java.io.*;
import java.sql.*;

public abstract class AbstractRoutines<P extends CommonParserSettings<?>, W extends CommonWriterSettings<?>> {

	protected abstract AbstractParser<P> createParser(P parserSettings);

	protected abstract AbstractWriter<W> createWriter(Writer output, W writerSettings);

	private final String routineDescription;
	private P parserSettings;
	private W writerSettings;

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

	public final P getParserSettings() {
		return parserSettings;
	}

	public final void setParserSettings(P parserSettings) {
		this.parserSettings = parserSettings;
	}

	public final W getWriterSettings() {
		return writerSettings;
	}

	public final void setWriterSettings(W writerSettings) {
		this.writerSettings = writerSettings;
	}

	protected void adjustColumnLengths(String[] headers, int[] lengths){

	}

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
					if(precision != 0 && scale != 0) {
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

	public final void parseAndWrite(Reader input, Writer output) {
		validateWriterSettings();
		validateParserSettings();

		RowProcessor parserProcessor = parserSettings.getRowProcessor();
		RowWriterProcessor writerProcessor = writerSettings.getRowWriterProcessor();

		if (parserProcessor == NoopRowProcessor.instance) {
			parserSettings.setRowProcessor(createWritingRowProcessor(output));
		} else if (parserProcessor instanceof BeanProcessor) {
			BeanProcessor beanProcessor = (BeanProcessor) parserProcessor;
			Class parserClass = beanProcessor.getBeanClass();
			parserSettings.setRowProcessor(createWritingRowProcessor(beanProcessor, output));

			if (writerProcessor == null) {
				writerSettings.setRowWriterProcessor(new BeanWriterProcessor(parserClass));
			}
		} else if (parserProcessor instanceof ObjectRowProcessor) {
			ObjectRowProcessor objectRowProcessor = (ObjectRowProcessor) parserProcessor;
			parserSettings.setRowProcessor(createWritingRowProcessor(objectRowProcessor, writerProcessor, output));
		} else {
			throw new IllegalStateException("Cannot parse and write to the output. Parser is configured to use unknown row processor " + parserProcessor + " (" + parserProcessor.getClass().getName() + ")");
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

	private RowProcessor createWritingRowProcessor(final BeanProcessor processor, final Writer output) {
		return new BeanProcessor(processor.getBeanClass()) {
			private AbstractWriter<W> writer;

			@Override
			public void processStarted(ParsingContext context) {
				super.processStarted(context);
				writer = createWriter(output, writerSettings);
			}

			@Override
			public void beanProcessed(Object bean, ParsingContext context) {
				writer.processRecord(bean);
			}

			@Override
			public void processEnded(ParsingContext context) {
				super.processEnded(context);
				writer.close();
			}
		};
	}

	private RowProcessor createWritingRowProcessor(final ObjectRowProcessor processor, final RowWriterProcessor writerProcessor, final Writer output) {
		if (writerProcessor != null) {
			return new ObjectRowProcessor() {
				private AbstractWriter<W> writer;

				@Override
				public void processStarted(ParsingContext context) {
					super.processStarted(context);
					writer = createWriter(output, writerSettings);
				}

				@Override
				public void rowProcessed(Object[] row, ParsingContext context) {
					writer.processRecord(row);
				}

				@Override
				public void processEnded(ParsingContext context) {
					super.processEnded(context);
					writer.close();
				}
			};
		} else {
			return new ObjectRowProcessor() {
				private AbstractWriter<W> writer;

				@Override
				public void processStarted(ParsingContext context) {
					super.processStarted(context);
					writer = createWriter(output, writerSettings);
				}

				@Override
				public void rowProcessed(Object[] row, ParsingContext context) {
					writer.writeRow(row);
				}

				@Override
				public void processEnded(ParsingContext context) {
					super.processEnded(context);
					writer.close();
				}
			};
		}
	}
}
