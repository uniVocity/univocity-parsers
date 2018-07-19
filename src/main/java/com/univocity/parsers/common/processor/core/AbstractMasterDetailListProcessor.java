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
package com.univocity.parsers.common.processor.core;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;

import java.util.*;

/**
 *
 * A convenience {@link Processor} implementation for storing all {@link MasterDetailRecord} generated form the parsed input into a list.
 * A typical use case of this class will be:
 *
 * <hr><blockquote><pre>{@code
 *
 * ObjectRowListProcessor detailProcessor = new ObjectRowListProcessor();
 * MasterDetailListProcessor masterRowProcessor = new MasterDetailListProcessor(detailProcessor) {
 *      protected boolean isMasterRecord(String[] row, ParsingContext context) {
 *          return "Total".equals(row[0]);
 *      }
 * };
 *
 * parserSettings.setRowProcessor(masterRowProcessor);
 *
 * List&lt;MasterDetailRecord&gt; rows = masterRowProcessor.getRecords();
 * }</pre></blockquote><hr>
 *
 * @see AbstractMasterDetailProcessor
 * @see Processor
 * @see AbstractParser
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class AbstractMasterDetailListProcessor<T extends Context> extends AbstractMasterDetailProcessor<T> {

	private final List<MasterDetailRecord> records = new ArrayList<MasterDetailRecord>();
	private String[] headers;

	/**
	 * Creates a MasterDetailListProcessor
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
	public AbstractMasterDetailListProcessor(RowPlacement rowPlacement, AbstractObjectListProcessor detailProcessor) {
		super(rowPlacement, detailProcessor);
	}

	/**
	 * Creates a MasterDetailListProcessor assuming master records are positioned above its detail records in the input.
	 *
	 * @param detailProcessor the {@link ObjectRowListProcessor} that processes detail rows.
	 */
	public AbstractMasterDetailListProcessor(AbstractObjectListProcessor detailProcessor) {
		super(detailProcessor);
	}

	/**
	 * Stores the generated {@link MasterDetailRecord} with the set of associated parsed records into a list.
	 *
	 * @param record {@link MasterDetailRecord} generated with a set of associated records extracted by the parser
	 * @param context A contextual object with information and controls over the current state of the parsing process
	 *
	 * @see MasterDetailRecord
	 */
	@Override
	protected void masterDetailRecordProcessed(MasterDetailRecord record, T context) {
		records.add(record);
	}

	@Override
	public void processEnded(T context) {
		headers = context.headers();
		super.processEnded(context);
	}

	/**
	 * Returns the list of generated MasterDetailRecords at the end of the parsing process.
	 * @return the list of generated MasterDetailRecords at the end of the parsing process.
	 */
	public List<MasterDetailRecord> getRecords() {
		return this.records;
	}

	/**
	 * Returns the record headers. This can be either the headers defined in {@link CommonSettings#getHeaders()} or the headers parsed in the file when {@link CommonSettings#getHeaders()}  equals true
	 * @return the headers of all records parsed.
	 */
	public String[] getHeaders() {
		return headers;
	}
}
