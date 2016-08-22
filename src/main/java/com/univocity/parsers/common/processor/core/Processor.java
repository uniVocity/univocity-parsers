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
package com.univocity.parsers.common.processor.core;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;

/**
 * The essential callback interface to handle records parsed by any parser that extends {@link AbstractParser}.
 *
 * <p>When parsing an input, uniVocity-parsers will obtain the RowProcessor from {@link CommonParserSettings#getRowProcessor()}, and
 * delegate each parsed row to {@link Processor#rowProcessed(String[], Context)}.
 *
 * <p>Before parsing the first row, the parser will invoke the {@link Processor#processStarted(Context)} method.
 *    By this time the input buffer will be already loaded and ready to be consumed.
 *
 * <p>After parsing the last row, all resources are closed and the processing stops. Only after the {@link Processor#processEnded(Context)} is called so you
 *    can perform any additional housekeeping you might need.
 *
 * <p>More control and information over the parsing process are provided by the {@link Context} object.
 *
 * <p>uniVocity-parsers provides many useful default implementations of this interface in the package {@link com.univocity.parsers.common.processor}, namely:
 *
 * <ul>
 * <li>{@link RowListProcessor}: convenience class for storing the processed rows into a list.</li>
 * <li>{@link ObjectRowProcessor}: used for processing rows and executing conversions of parsed values to objects using instances of {@link Conversion}</li>
 * <li>{@link ObjectRowListProcessor}: convenience class for rows of converted objects using {@link ObjectRowProcessor} into a list.</li>
 * <li>{@link AbstractMasterDetailProcessor}: used for reading inputs where records are organized in a master-detail fashion (with a master element that contains a list of associated elements) </li>
 * <li>{@link AbstractMasterDetailListProcessor}: convenience class for storing {@link MasterDetailRecord} created by instances created by {@link AbstractMasterDetailProcessor} into a list </li>
 * <li>{@link AbstractBeanProcessor}: used for automatically create and populate javabeans annotated with the annotations provided in package {@link com.univocity.parsers.annotations}</li>
 * <li>{@link AbstractBeanListProcessor}: convenience class for storing all javabeans created by {@link AbstractBeanProcessor} into a list</li>
 * </ul>
 *
 * @see AbstractParser
 * @see CommonParserSettings
 * @see ParsingContext
 * @see Context
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public interface Processor<T extends Context> {

	/**
	 * This method will by invoked by the parser once, when it is ready to start processing the input.
	 *
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	void processStarted(T context);

	/**
	 * Invoked by the parser after all values of a valid record have been processed.
	 *
	 * @param row the data extracted by the parser for an individual record. Note that:
	 * <ul>
	 * <li>it will never by null. </li>
	 * <li>it will never be empty unless explicitly configured using {@link CommonSettings#setSkipEmptyLines(boolean)}</li>
	 * <li>it won't contain lines identified by the parser as comments. To disable comment processing set {@link Format#setComment(char)} to '\0'</li>
	 * </ul>
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	void rowProcessed(String[] row, T context);

	/**
	 * This method will by invoked by the parser once, after the parsing process stopped and all resources were closed.
	 * <p> It will always be called by the parser: in case of errors, if the end of the input us reached, or if the user stopped the process manually using {@link ParsingContext#stop()}.
	 *
	 * @param context A contextual object with information and controls over the state of the parsing process
	 */
	void processEnded(T context);
}
