package CZ4031Project1.bptree;

public class Node {
  InternalNode parent;

  public Node() {
    this.parent = null;
  }

  public Node(InternalNode parent) {
    this.parent = parent;
  }
}
