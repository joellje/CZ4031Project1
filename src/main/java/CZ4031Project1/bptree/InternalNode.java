package CZ4031Project1.bptree;

public class InternalNode extends Node {
  Node[] children;

  InternalNode left = null;
  InternalNode right = null;

  public InternalNode(int maxSize) {
    this.maxSize = maxSize;
    this.size = 0;
    this.keys = new short[maxSize - 1];
    this.children = new Node[maxSize];
  }

  public InternalNode(int maxSize, int size, short[] keys, Node[] children) {
    super();
    this.maxSize = maxSize;
    this.size = size;
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
    if (this.size == 0)
      throw new IllegalStateException("Cannot insert key into empty InternalNode");
    this.keys[size - 1] = key;
    this.children[size] = child;
    size++;
  }

  void appendChild(Node child) {
    if (this.isOverfull())
      throw new IllegalStateException("Cannot insert into overfull InternalNode");
    this.children[size] = child;
    size++;
  }

  void insert(short key, Node child, int insertIndex) throws IllegalStateException {
    if (this.isOverfull())
      throw new IllegalStateException("Cannot insert into overfull InternalNode");
    if (this.size == 0)
      throw new IllegalStateException("Cannot insert key into empty InternalNode");
    // shift
    for (int i = size; i > insertIndex; i--) {
      this.keys[i - 1] = this.keys[i - 2];
      this.children[i] = this.children[i - 1];
    }
    this.keys[insertIndex - 1] = key;
    this.children[insertIndex] = child;
    size++;
  }
}
