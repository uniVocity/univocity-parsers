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
		for (int i = 0; i < conversions.length; i++) {
			out = conversions[i].execute(out);
		}
		return out;
	}

}
