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
package com.univocity.parsers.common.processor;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.core.*;

/**
 * A special {@link RowProcessor} implementation that combines and allows switching among different
 * RowProcessors. Each RowProcessor will have its own {@link ParsingContext}. Concrete implementations of this class
 * are expected to implement the {@link #switchRowProcessor(String[], Context)} method and analyze the input row
 * to determine whether or not the current {@link RowProcessor} implementation must be changed to handle a special
 * circumstance (determined by the concrete implementation) such as a different row format.
 *
 * When the row processor is switched, the {@link #rowProcessorSwitched(RowProcessor, RowProcessor)} will be called, and
 * must be overridden, to notify the change to the user.
 */
public abstract class RowProcessorSwitch extends AbstractProcessorSwitch<ParsingContext> implements RowProcessor {

}