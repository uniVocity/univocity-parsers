/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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
 * A {@link RowProcessor} implementation for associating rows extracted from any implementation of {@link AbstractParser} into {@link MasterDetailRecord} instances.
 *
 * <p> For each row processed, a call to {@link MasterDetailProcessor#isMasterRecord(String[], Context)} will be made to identify whether or not it is a master row.
 * <p> The detail rows are automatically associated with the master record in an instance of {@link MasterDetailRecord}.
 * <p> When the master record is fully processed (i.e. {@link MasterDetailRecord} contains a master row and  all associated detail rows),
 * it is sent to the user for processing in {@link MasterDetailProcessor#masterDetailRecordProcessed(MasterDetailRecord, Context)}.
 *
 * <p> <b>Note</b> this class extends {@link ObjectRowProcessor} and value conversions provided by {@link Conversion} instances are fully supported.
 *
 * @see MasterDetailRecord
 * @see RowPlacement
 * @see AbstractParser
 * @see ObjectRowListProcessor
 * @see RowProcessor
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class MasterDetailProcessor extends AbstractMasterDetailProcessor<ParsingContext> {

	/**
	 * Creates a MasterDetailProcessor
	 *
	 * @param rowPlacement indication whether the master records are placed in relation its detail records in the input.
	 *
	 * <hr><blockquote><pre>
	 *
	 * Master record (Totals)       Master record (Totals)
	 *  above detail records         under detail records
	 *
	 *    Totals | 100                 Item   | 60
	 *    Item   | 60                  Item   | 40
	 *    Item   | 40                  Totals | 100
	 * </pre></blockquote><hr>
	 * @param detailProcessor the {@link ObjectRowListProcessor} that processes detail rows.
	 */
	public MasterDetailProcessor(RowPlacement rowPlacement, ObjectRowListProcessor detailProcessor) {
		super(rowPlacement, detailProcessor);
	}

	public MasterDetailProcessor(ObjectRowListProcessor detailProcessor) {
		super(RowPlacement.TOP, detailProcessor);
	}

}
