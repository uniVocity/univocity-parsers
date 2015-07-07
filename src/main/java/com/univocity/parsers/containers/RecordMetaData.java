package com.univocity.parsers.containers;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author naveen.kasthuri
 *
 * A container that maps column indices to their names. This avoids having duplicate arrays of
 * key-value pairs where the keys are the same for every row in the csv file.
 *
 * This is used by the Row object and the RowListProcessor. Should not be used otherwise.
 */
public class RecordMetaData {
  // Contains a mapping between row index and column
  private final Map<String, Integer> columnIndexMap;

  public RecordMetaData() {
    columnIndexMap = new LinkedHashMap<String, Integer>();
  }

  public RecordMetaData(String []headers) {
    this();
    setHeaders(headers);
  }

  public RecordMetaData(Column []headers) {
    this();
    setHeaders(headers);
  }

  public void setHeaders(String[] headers) {
    for (int i = 0; i < headers.length; i++) {
      columnIndexMap.put(headers[i], i);
    }
  }

  public void setHeaders(Column[] headers) {
    for (int i = 0; i < headers.length; i++) {
      columnIndexMap.put(headers[i].toString(), i);
    }
  }


  public int getIndex(String column) {
    if  (columnIndexMap.containsKey(column)) {
      return columnIndexMap.get(column);
    } else {
      throw new IllegalArgumentException("Column " + column + "not found in RecordMetaData");
    }
  }

  public int getIndex(Column column) {
    return getIndex(column.toString());
  }

  public String[] getHeaders() {
    if (!columnIndexMap.isEmpty()) {
      String[] s = new String[columnIndexMap.size()];
      return columnIndexMap.keySet().toArray(s);
    }
    return null;
  }
}
