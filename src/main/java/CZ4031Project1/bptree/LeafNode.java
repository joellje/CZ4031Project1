package CZ4031Project1.bptree;
import java.util.Arrays;
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
    if (this.size == this.maxSize){
      // throw new IllegalStateException("Cannot insert into full LeafNode");
      return;
    }

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

  int delete(short key, NBARecord record) {
    // System.out.println("key to delete: " + key);
    // System.out.println(Arrays.toString(this.keys));

    for (int i = 0; i < size; i++) {
      short[] newKeys = new short[maxSize];
      NBARecord[] newRecords = new NBARecord[maxSize];
      if (keys[i] > key) {
        break;
      }
      if (keys[i] == key) {
        if (record == records[i]) {
          for (int j = 0; j < i; j++) {
            newKeys[j] = keys[j];
            newRecords[j] = records[j];
          }
          for (int j = i + 1; j < size; j++) {
            newKeys[j - 1] = keys[j];
            newRecords[j - 1] = records[j];
          }
          this.records = newRecords;
          this.keys = newKeys;
          size--;
          return 1;
        }
      }
    }
    // System.out.println("key not in this node");
    return 0;
  }

  boolean isFull() {
    // store at most maxSize-1 records to leave 1 extra space at all times
    return this.size == this.maxSize - 1;
  }

  boolean isOverfull() {
    return this.size == this.maxSize;
  }

  int getMaxSize() {
    return this.maxSize;
  }
}
