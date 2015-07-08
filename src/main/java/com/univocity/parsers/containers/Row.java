package com.univocity.parsers.containers;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import com.sun.tools.javac.util.Assert;
import com.univocity.parsers.conversions.DateConversion;
import com.univocity.parsers.conversions.DoubleConversion;

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
  private static final DoubleConversion doubleConversion = new DoubleConversion();

  public Row(String[] row, long lineNumber, RecordMetaData recordMetaData) {
    this.lineNumber = lineNumber;
    this.values = row;
    this.recordMetaData = recordMetaData;
  }

  public long getLineNumber() {
    return lineNumber;
  }

  public Set getHeaders() {
    return recordMetaData.getHeaders();
  }

  public String[] getValues() {
    return values;
  }


  /* Row.get() Methods that are private */

  /**
   * Returns the value of this row for the given column enum
   * @return String value
   */
  private String get(Enum column) {
    Assert.checkNonNull(column, "Cannot call get() with a null column");
    return values[recordMetaData.getIndex(column)];
  }

  /** Returns a parsed double value or null */
  private Double getDouble(Enum column) {
    return doubleConversion.execute(get(column));
  }

  private Double getDoubleNullIfZero(Enum column) {
    Double result = doubleConversion.execute(get(column));
    if (result == 0d) {
      return null;
    }
    return result;
  }

  /* Row.get() Methods accessible to the public */

  /** Returns the first non-empty non-null string from the list of columns, if one exists */
  public String get(Enum... columns) {
    for (Enum column : columns) {
      String value = get(column);
      if (value != null && !"".equals(value)) {
        return value;
      }
    }
    return null;
  }

  /** Returns the provided default value if {@link #get(Enum...)}  returns null */
  public String getDefaultIfNull(String defaultValue, Enum... columns) {
    String result = get(columns);
    return result == null ? defaultValue : result;
  }

  /** Return an empty string if a normal call to {@link #get(Enum...)} returns null */
  public String getEmptyIfNull(Enum... columns) {
    String result = get(columns);
    return result == null ? "" : result;
  }
  /** Returns a double value for the first numeric non-empty column or returns null */
  public Double getDouble(Enum... columns) {
    for (Enum column : columns) {
      Double value = getDouble(column);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  /**
   * @param defaultValue Returns the value of the column if it is non-null and can be found without
   *        errors, otherwise, returns defaultValue
   * @return defaultValue if getDouble(column) is null or raises an exception
   */
  public Double getDoubleWithDefault(Double defaultValue, Enum... columns) {
    for (Enum column : columns) {
      Double returnValue = getDouble(column);
      if (returnValue != null) {
        return returnValue;
      }
    }
    return defaultValue;
  }

  /** Returns null if the value is zero **/
  public Double getDoubleNullIfZero(Enum... columns) {
    Double value;
    for (Enum column : columns) {
      if ((value = getDoubleNullIfZero(column)) != null) {
        return value;
      }
    }
    return null;
  }

  /** Returns 0 if no double value is found or if exception is thrown */
  public Double getDoubleOrZero(Enum... columnNames) {
    return getDoubleWithDefault(0d, columnNames);
  }

  /** Returns a date after matching using the given pattern. Returns null If pattern doesn't match.*/
  public Date getDateWithPattern(String pattern, Enum... columns) {
    DateConversion dateConversion = new DateConversion(pattern);
    return dateConversion.execute(get(columns));
  }

  /** Uses {@link #getDateWithPattern(String, Enum...)} and returns the default value if null*/
  public Date getDateWithPatternDefault(String pattern, Date defaultValue,
      Enum... columns) {
    Date result = getDateWithPattern(pattern, columns);
    return result == null ? defaultValue : result;
  }

  public String toString() {
    return Arrays.toString(values);
  }

}
