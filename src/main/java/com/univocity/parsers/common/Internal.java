/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.univocity.parsers.common;

import com.univocity.parsers.common.processor.core.*;

import java.util.*;

/**
 * Internal class to keep common internal functions that are used in multiple places.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
class Internal {
	public static final <C extends Context> void process(String[] row, Processor<C> processor, C context, ProcessorErrorHandler<C> errorHandler) {
		try {
			processor.rowProcessed(row, context);
		} catch (DataProcessingException ex) {
			ex.setContext(context);

			if (!ex.isFatal() && !ex.isHandled() && ex.getColumnIndex() > -1 && errorHandler instanceof RetryableErrorHandler) {
				RetryableErrorHandler retry = ((RetryableErrorHandler) errorHandler);
				ex.markAsHandled(errorHandler);
				retry.handleError(ex, row, context);
				if (!retry.isRecordSkipped()) {
					try {
						processor.rowProcessed(row, context);
						return;
					} catch (DataProcessingException e) {
						ex = e;
					} catch (Throwable t) {
						throwDataProcessingException(processor, t, row, context.errorContentLength());
					}
				}
			}

			ex.setErrorContentLength(context.errorContentLength());
			if (ex.isFatal()) {
				throw ex;
			}
			ex.markAsHandled(errorHandler);
			errorHandler.handleError(ex, row, context);
		} catch (Throwable t) {
			throwDataProcessingException(processor, t, row, context.errorContentLength());
		}
	}

	private static final void throwDataProcessingException(Processor processor, Throwable t, String[] row, int errorContentLength) throws DataProcessingException {
		DataProcessingException ex = new DataProcessingException("Unexpected error processing input row "
				+ AbstractException.restrictContent(errorContentLength, Arrays.toString(row))
				+ " using Processor " + processor.getClass().getName() + '.'
				, AbstractException.restrictContent(errorContentLength, row)
				, t);
		ex.restrictContent(errorContentLength);
		throw ex;
	}
}
