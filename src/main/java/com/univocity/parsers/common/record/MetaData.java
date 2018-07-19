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
package com.univocity.parsers.common.record;

import com.univocity.parsers.conversions.*;

class MetaData {
	MetaData(int index) {
		this.index = index;
	}

	public final int index;
	public Class<?> type = String.class;
	public Object defaultValue = null;
	@SuppressWarnings("rawtypes")
	private Conversion[] conversions = null;
	
	@SuppressWarnings("rawtypes")
	public Conversion[] getConversions(){
		return conversions;
	}

	@SuppressWarnings("rawtypes")
	public void setDefaultConversions(Conversion[] conversions){
		this.conversions = conversions;
	}

	@SuppressWarnings("unchecked")
	public Object convert(Object out){
		if(conversions == null){
			return out;
		}
		for (int i = 0; i < conversions.length; i++) {
			out = conversions[i].execute(out);
		}
		return out;
	}

}
