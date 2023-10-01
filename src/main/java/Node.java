public class Node {
  private Node parent;
  private short[] keys = new short[39];
  private Node[] children = new Node[40];

  // initialise
  public Node(short[] keys, Node[] children) {
    this.keys = keys;
    this.children = children;
    this.parent = null;
  }

  public Node(short[] keys, Node[] children, Node parent) {
    this.keys = keys;
    this.children = children;
    this.parent = parent;
  }

  // getters
  public Node getParent() {
    return this.parent;
  }

  public Node[] getChildren() {
    return this.children;
  }

  public boolean getIsLeafNode() {
    return false;
  }

  public short[] getKeys() {
    return this.keys;
  }

  // setters
  public void setParent(Node parent) {
    this.parent = parent;
  }

  public void setChildren(Node[] children) {
    this.children = children;
  }

  public void setKeys(short[] keys) {
    this.keys = keys;
  }

  // functions to get stuff
  public Node getChild(short key) {
    short[] keys = getKeys();
    int index = -1;
    for (int i = 0; i < keys.length; i++) {
      if (keys[i] == key) {
        index = i;
        break;
      }
    }
    if (index == -1) {
      System.out.println("Key does not exist!");
      return null;
    }
    return getChildren()[index];
  }
}
