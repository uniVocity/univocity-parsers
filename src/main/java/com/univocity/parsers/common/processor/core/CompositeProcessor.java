/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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

/**
 * A utility {@link Processor} implementation that facilitates using multiple implementations of {@link Processor} at the
 * same time.
 *
 * @param <C> the tye of the contextual object with information and controls over the current state of the parsing process
 */
public class CompositeProcessor<C extends Context> implements Processor<C> {

	private final Processor processors[];

	/**
	 * Creates a new {@code CompositeProcessor} with the list of {@link Processor} implementations to be used.
	 *
	 * @param processors the sequence of {@link Processor} implementations to be used.
	 */
	public CompositeProcessor(Processor... processors) {
		this.processors = processors;
	}

	/**
	 * Initializes each {@link Processor} used by this class. This is invoked by the parser once, when it is ready to start processing the input.
	 *
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	@Override
	public void processStarted(C context) {
		for (int i = 0; i < processors.length; i++) {
			processors[i].processStarted(context);
		}
	}

	/**
	 * Invoked by the parser after all values of a valid record have been processed. All {@link Processor} implementations
	 * will have their corresponding {@link Processor#rowProcessed(String[], Context)} method called with the given row.
	 *
	 * @param row     the data extracted by the parser for an individual record. Note that:
	 *                <ul>
	 *                <li>it will never by null. </li>
	 *                <li>it will never be empty unless explicitly configured using {@link CommonSettings#setSkipEmptyLines(boolean)}</li>
	 *                <li>it won't contain lines identified by the parser as comments. To disable comment processing set {@link Format#setComment(char)} to '\0'</li>
	 *                </ul>
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 */
	@Override
	public void rowProcessed(String[] row, C context) {
		for (int i = 0; i < processors.length; i++) {
			processors[i].rowProcessed(row, context);
		}
	}


	/**
	 * This method will by invoked by the parser once for each {@link Processor} used by this class, after the parsing process stopped and all resources were closed.
	 * <p> It will always be called by the parser: in case of errors, if the end of the input us reached, or if the user stopped the process manually using {@link ParsingContext#stop()}.
	 *
	 * @param context A contextual object with information and controls over the state of the parsing process
	 */
	@Override
	public void processEnded(C context) {
		for (int i = 0; i < processors.length; i++) {
			processors[i].processEnded(context);
		}
	}
}