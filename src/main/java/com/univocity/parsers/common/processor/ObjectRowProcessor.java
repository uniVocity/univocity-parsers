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
package com.univocity.parsers.common.processor;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.core.*;
import com.univocity.parsers.conversions.*;

/**
 *
 * A {@link RowProcessor} implementation for converting rows extracted from any implementation of {@link AbstractParser} into arrays of objects.
 * <p>This uses the value conversions provided by {@link Conversion} instances.
 *
 * <p> For each row processed, a sequence of conversions will be executed and stored in an object array, at its original position.
 * <p> The row with the result of these conversions will then be sent to the {@link ObjectRowProcessor#rowProcessed(Object[], Context)} method, where the user can access it.
 *
 * @see AbstractParser
 * @see RowProcessor
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class ObjectRowProcessor extends AbstractObjectProcessor<ParsingContext> implements RowProcessor {

}
