package CZ4031Project1.bptree;

import CZ4031Project1.storage.NBARecord;

public class LeafNode extends Node {
  NBARecord[] records;

  LeafNode left;
  LeafNode right;

  public LeafNode(int maxSize) {
    this.maxSize = maxSize;
    this.size = 0;
    this.keys = new short[maxSize];
    this.records = new NBARecord[maxSize];
  }

  public LeafNode(int maxSize, int size, short[] keys, NBARecord[] records)
      throws IllegalStateException {
    if (keys.length != records.length || keys.length != maxSize) {
      throw new IllegalStateException(
          String.format(
              "Tried to create illegal leaf node with maxSize: %d, size of keys: %d, size of"
                  + " records: %d. Ensure all sizes of equal",
              maxSize, keys.length, records.length));
    }
    this.maxSize = maxSize;
    this.size = size;
    this.keys = keys;
    this.records = records;
  }

  void insert(short key, NBARecord record) throws IllegalStateException {
    if (this.size == this.maxSize)
      throw new IllegalStateException("Cannot insert into full LeafNode");

    // insert in order
    int insertIndex = this.keyUpperBound(key);

    // shift
    for (int i = this.size; i > insertIndex; i--) {
      this.keys[i] = this.keys[i - 1];
      this.records[i] = this.records[i - 1];
    }

    this.keys[insertIndex] = key;
    this.records[insertIndex] = record;
    size++;
  }

  boolean isFull() {
    // store at most maxSize-1 records to leave 1 extra space at all times
    return this.size == this.maxSize - 1;
  }

  boolean isOverfull() {
    return this.size == this.maxSize;
  }
}
