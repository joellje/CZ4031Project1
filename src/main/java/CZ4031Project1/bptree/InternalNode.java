package CZ4031Project1.bptree;

public class InternalNode extends Node {
  Node[] children;
  int maxChildren;
  int childrenSize;

  InternalNode left = null;
  InternalNode right = null;

  public InternalNode(int maxSize) {
    this.maxKeys = maxSize;
    this.maxChildren = maxSize - 1;
    this.keysSize = 0;
    this.childrenSize = 0;
    // one buffer space
    this.keys = new short[maxSize + 1];
    this.children = new Node[maxSize + 1];
  }

  public InternalNode(int maxKeys, int keysSize, int childrenSize, short[] keys, Node[] children) {
    if (keys.length != children.length || keys.length != maxKeys + 1) {
      throw new IllegalStateException(
          String.format(
              "Tried to create illegal internal node with maxSize+1: %d, size of keys: %d, size of"
                  + " children: %d. Ensure all sizes of equal",
              maxKeys + 1, keys.length, children.length));
    }
    if (keysSize != childrenSize) {
      throw new IllegalStateException(
          String.format(
              "Tried to create illegal internal node with keysSize %d != recordsSize %d",
              keysSize, childrenSize));
    }
    this.maxKeys = maxKeys;
    this.keysSize = keysSize;
    this.keys = keys;
    this.children = children;
  }

  int getChildIndex(Node child) {
    for (int i = 0; i < this.childrenSize; i++) {
      if (this.children[i] == child) return i;
    }
    return -1;
  }

  void append(short key, Node child) throws IllegalStateException {
    if (this.isOverfull())
      throw new IllegalStateException("Cannot insert into overfull InternalNode");
    if (this.keysSize == 0) this.keys[0] = key;
    else this.keys[keysSize - 1] = key;
    this.children[childrenSize] = child;
    keysSize++;
    childrenSize++;
  }

  void appendChild(Node child) {
    if (this.isOverfull())
      throw new IllegalStateException("Cannot insert into overfull InternalNode");
    if (this.keysSize == 0)
      throw new IllegalStateException("Cannot insert child into empty InternalNode");
    this.children[childrenSize] = child;
    childrenSize++;
  }

  void insert(short key, Node child, int childInsertIndex) throws IllegalStateException {
    if (this.isOverfull())
      throw new IllegalStateException("Cannot insert into overfull InternalNode");
    // shift
    for (int i = childrenSize; i > childInsertIndex; i--) {
      this.keys[i - 1] = this.keys[i - 2];
      this.children[i] = this.children[i - 1];
    }
    this.keys[childInsertIndex - 1] = key;
    this.children[childInsertIndex] = child;
    this.keysSize++;
    this.childrenSize++;
  }
}
