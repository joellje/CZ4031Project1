package CZ4031Project1.bptree;

import CZ4031Project1.storage.NBARecord;

public class LeafNode extends Node {
  NBARecord[] records;

  int maxRecords;
  int recordsSize;
  LeafNode left;
  LeafNode right;

  public LeafNode(int maxKeys) {
    this.maxKeys = maxKeys;
    this.maxRecords = maxKeys;
    this.keysSize = 0;
    this.recordsSize = 0;
    // one buffer space
    this.keys = new short[maxKeys + 1];
    this.records = new NBARecord[maxKeys + 1];
  }

  public LeafNode(int maxKeys, int keysSize, int recordsSize, short[] keys, NBARecord[] records)
      throws IllegalStateException {
    if (keys.length != records.length || keys.length != maxKeys + 1) {
      throw new IllegalStateException(
          String.format(
              "Tried to create illegal leaf node with maxSize+1: %d, size of keys: %d, size of"
                  + " records: %d. Ensure all sizes of equal",
              maxKeys + 1, keys.length, records.length));
    }
    if (keysSize != recordsSize) {
      throw new IllegalStateException(
          String.format(
              "Tried to create illegal leaf node with keysSize %d != recordsSize %d",
              keysSize, recordsSize));
    }
    this.maxKeys = maxKeys;
    this.maxRecords = maxKeys;
    this.keysSize = keysSize;
    this.recordsSize = recordsSize;
    this.keys = keys;
    this.records = records;
  }

  void insert(short key, NBARecord record) throws IllegalStateException {
    if (this.isOverfull()) throw new IllegalStateException("Cannot insert into overfull LeafNode");

    // insert in order
    int insertIndex = this.keyUpperBound(key);

    // shift
    for (int i = this.keysSize; i > insertIndex; i--) {
      this.keys[i] = this.keys[i - 1];
      this.records[i] = this.records[i - 1];
    }

    this.keys[insertIndex] = key;
    this.records[insertIndex] = record;
    this.keysSize++;
    this.recordsSize++;
  }

  int delete(short key, NBARecord record) {
    System.out.println("Deleting " + key);
    this.printKeys();
    int deleteIndex = this.keyLowerBound(key);
    // key not found
    if (deleteIndex == this.keysSize) {
      System.out.println("Key " + key + " not found");
      return 0;
    }

    // shift
    for (int i = deleteIndex; i < this.keysSize - 1; i++) {
      this.keys[i] = this.keys[i + 1];
      this.records[i] = this.records[i + 1];
    }
    this.keysSize--;
    this.recordsSize--;
    return 1;
  }
}
