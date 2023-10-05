package CZ4031Project1.bptree;

public class Node {
  InternalNode parent;
  short[] keys;
  int maxSize;
  int size;

  int keyLowerBound(int key) {
    int low = 0, high = this.size - 1;
    while (low < high) {
      int mid = low + (high - low) / 2;

      if (key <= this.keys[mid]) {
        high = mid;
      } else {
        low = mid + 1;
      }
    }
    if (low < this.size && this.keys[low] < key) {
      low++;
    }
    return low;
  }

  int keyUpperBound(int key) {
    int low = 0, high = this.size - 1;
    while (low < high) {
      int mid = low + (high - low) / 2;

      if (key >= this.keys[mid]) {
        low = mid + 1;
      } else {
        high = mid;
      }
    }
    if (low < this.size && this.keys[low] < key) {
      low++;
    }
    return low;
  }

  boolean isFull() {
    // store at most maxSize-1 records to leave 1 extra space at all times
    return this.size == this.maxSize - 1;
  }

  boolean isOverfull() {
    return this.size == this.maxSize;
  }
}
