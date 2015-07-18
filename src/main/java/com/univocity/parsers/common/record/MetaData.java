package com.univocity.parsers.common.record;

import com.univocity.parsers.conversions.*;

class MetaData {
	MetaData(int index) {
		this.index = index;
	}

	public final int index;
	public Class<?> type = String.class;
	public Object defaultValue = null;
	private Conversion[] conversions = null;
	private Conversion[] adhocConversions = null;
	private String[] formats = null;
	private String[] adhocFormats = null;

	public Conversion[] getConversions(){
		return conversions;
	}

	public void setDefaultConversions(Conversion[] conversions){
		this.conversions = conversions;
	}

	public Object convert(Object out){
		for (int i = 0; i < conversions.length; i++) {
			out = conversions[i].execute(out);
		}
		return out;
	}

}
