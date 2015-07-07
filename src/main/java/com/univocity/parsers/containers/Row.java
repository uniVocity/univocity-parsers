package com.univocity.parsers.containers;

import java.util.Arrays;

/**
 * @author naveen.kasthuri
 * A convenient container of information represented in a csv row. Instead of handling lists of
 * arrays of strings, a csv can be fully represented by a list of rows.
 *
 * The row object acts as a map from header to a cell. For example: row.get("Make") will return the
 * value of this row's cell under column "Make". However, each row object does not duplicate the
 * keys (headers) like a typical Map would: only one copy of the keys is stored in the
 * {@link RecordMetaData} object. As such, this is a memory efficient, computationally efficient,
 * convenient container of row objects.
 *
 * @see com.univocity.parsers.examples's RowObjectExample for an implementation example
 */
public class Row {
  private final String[] values;
  private final long lineNumber;
  private final RecordMetaData recordMetaData;

  public Row(String[] row, long lineNumber, RecordMetaData recordMetaData) {
    this.lineNumber = lineNumber;
    this.values = row;
    this.recordMetaData = recordMetaData;
  }

  public long getLineNumber() {
    return lineNumber;
  }

  /**
   * Returns the value of this row for the given column header.
   * @param column String that matches csv's column header.
   * @return null if no such header is present in csv.
   */
  public String get(String column) {
    return values[recordMetaData.getIndex(column)];
  }

  /**
   * Returns the value of this row for the given {@link Column}.
   * @param column {@link Column}
   * @return null if no such header is present in csv.
   */
  public String get(Column column) {
    return get(column.toString());
  }

  public String[] getHeaders() {
    return recordMetaData.getHeaders();
  }

  public String[] getValues() {
    return values;
  }

  public String toString() {
    return Arrays.toString(values);
  }

}
