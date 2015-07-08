package com.univocity.parsers.containers;
import java.util.EnumMap;
import java.util.Set;

/**
 * @author naveen.kasthuri
 *
 * A container that maps column indices to their names. This avoids having duplicate arrays of
 * key-value pairs where the keys are the same for every row in the csv file.
 *
 * This is used by the Row object and the RowListProcessor. Should not be used otherwise.
 */
public class RecordMetaData {
  /** Contains a mapping between column index and column */
  private EnumMap enumIndexMap = null;

  /**
   * Returns index of provided column or throws error if column is not present in map.
   * @param column Enum column
   * @return integer location of column
   */
  @SuppressWarnings("unchecked")
  public int getIndex(Enum column) {
    return validateAndReturnIndex((Integer) enumIndexMap.get(column), column);
  }

  public <T extends Enum<T>> void buildHeaders(T column) {
    Class<? extends Enum> enumType = column.getClass();
    buildHeaders(enumType);
  }

  public void buildHeaders(Class<? extends Enum> enumType) {
    enumIndexMap = new EnumMap(enumType);
    int i = 0;
    for (Enum constant : enumType.getEnumConstants()) {
      enumIndexMap.put(constant, i++);
    }
  }

  private int validateAndReturnIndex(Integer index, Object column) {
    if (index == null) {
      throw new IllegalArgumentException("Column " + column + "not found in RecordMetaData");
    }
    return index;
  }

  public Set getHeaders() {
    return enumIndexMap.keySet();
  }

}
