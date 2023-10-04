package CZ4031Project1.bptree;

public class InternalNode extends Node {
  short[] keys;
  Node[] children;
  int size;
  int maxSize;

  InternalNode left = null;
  InternalNode right = null;

  public InternalNode(int maxSize) {
    super();
    this.maxSize = maxSize;
    this.size = 0;
    this.keys = new short[maxSize];
    this.children = new Node[maxSize];
  }

  public InternalNode(int maxSize, short[] keys, Node[] children) {
    super();
    this.maxSize = maxSize;
    this.size = 0;
    this.keys = keys;
    this.children = children;
  }

  int getChildIndex(Node child) {
    for (int i = 0; i < this.size; i++) {
      if (this.children[i] == child) return i;
    }
    return -1;
  }

  void append(short key, Node child) {
    if (this.isOverfull())
      throw new IllegalStateException("Cannot insert into overfull InternalNode");
    this.keys[size] = key;
    this.children[size] = child;
    size++;
  }

  void insert(short key, Node child, int insertIndex) throws IllegalStateException {
    if (this.isOverfull())
      throw new IllegalStateException("Cannot insert into overfull InternalNode");
    // shift
    for (int i = size; i > insertIndex; i--) {
      this.keys[i] = this.keys[i - 1];
      this.children[i] = this.children[i - 1];
    }
    this.keys[insertIndex] = key;
    this.children[insertIndex] = child;
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
