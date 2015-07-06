package com.univocity.parsers.containers;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author naveen.kasthuri
 *
 * A container that maps column indices to thier names. This avoids having duplicate arrays of
 * key-value pairs where the keys are the same for every row in the csv file.
 *
 * This is used by the Row object and the RowListProcessor. Should not be used otherwise.
 */
public class Csv {
  // Contains a mapping between row index and column
  private final Map<String, Integer> columnIndexMap;

  private Csv() {
    columnIndexMap = new LinkedHashMap<String, Integer>();
  }

  public Csv(String []headers) {
    this();
    for (int i = 0; i < headers.length; i++) {
      columnIndexMap.put(headers[i], i);
    }
  }

  public Csv(Column []headers) {
    this();
    for (int i = 0; i < headers.length; i++) {
      columnIndexMap.put(headers[i].toString(), i);
    }
  }

  public Integer getIndex(String column) {
      return columnIndexMap.get(column);
  }

  public Integer getIndex(Column column) {
    return getIndex(column.toString());
  }

  public String[] getHeaders() {
    String [] s = new String[columnIndexMap.size()];
    return columnIndexMap.keySet().toArray(s);
  }
}
