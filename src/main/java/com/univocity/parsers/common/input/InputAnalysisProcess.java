/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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
package com.univocity.parsers.common.input;

/**
 * A process to be executed over a sample of data being parsed. When {@link AbstractCharInputReader#reloadBuffer()} loads a batch of characters from the input,
 * the {@code InputAnalysisProcess} will be executed and then discarded.
 *
 * <p>Parsers can implement their custom analysis processes to identify patterns and attempt to automatically derive configuration options to process the input
 * by calling {@link AbstractCharInputReader#addInputAnalysisProcess(InputAnalysisProcess)} at any time.</p>
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public interface InputAnalysisProcess {

	/**
	 * A sequence of characters of the input buffer to be analyzed.
	 * @param characters the input buffer
	 * @param length the last character position loaded into the buffer.
	 */
	void execute(char[] characters, int length);

}
