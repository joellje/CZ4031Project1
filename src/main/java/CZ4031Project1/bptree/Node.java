package CZ4031Project1.bptree;

import java.util.Arrays;

public class Node {
  InternalNode parent;
  short[] keys;
  int maxKeys;
  int keysSize;

  int keyLowerBound(int key) {
    int low = 0, high = this.keysSize;
    while (low < high) {
      int mid = low + (high - low) / 2;

      if (key <= this.keys[mid]) {
        high = mid;
      } else {
        low = mid + 1;
      }
    }
    if (low < this.keysSize && this.keys[low] < key) {
      low++;
    }
    return low;
  }

  int keyUpperBound(int key) {
    int low = 0, high = this.keysSize;
    while (low < high) {
      int mid = low + (high - low) / 2;

      if (key >= this.keys[mid]) {
        low = mid + 1;
      } else {
        high = mid;
      }
    }
    return low;
  }

  boolean isFull() {
    return this.keysSize == this.maxKeys;
  }

  boolean isOverfull() {
    return this.keysSize == this.maxKeys + 1;
  }

  public void printKeys() {
    System.out.println(Arrays.toString(Arrays.copyOfRange(this.keys, 0, this.keysSize)));
  }
}
