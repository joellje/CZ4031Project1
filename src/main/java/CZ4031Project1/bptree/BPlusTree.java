package CZ4031Project1.bptree;

import CZ4031Project1.storage.NBARecord;
import java.util.ArrayList;

public class BPlusTree {
  private InternalNode root;
  private int maxNodeSize;

  public BPlusTree(int maxNodeSize) {
    this.root = new InternalNode(maxNodeSize);
    this.maxNodeSize = maxNodeSize;
  }

  private LeafNode getLeafNode(short key) {
    Node cursor = this.root;

    while (!(cursor instanceof LeafNode)) {
      int childIndex = 0;
      InternalNode cur = (InternalNode) cursor;
      for (int i = 0; i < cur.size; i++) {
        if (cur.keys[i] > key) break;
        childIndex = i;
      }
      cursor = cur.children[childIndex];
    }
    return (LeafNode) cursor;
  }

  private void splitInternalNode(InternalNode node) {
    int splitIndex = (int) Math.ceil(node.maxSize / 2.0);
    short[] keys = new short[this.maxNodeSize];
    Node[] children = new Node[this.maxNodeSize];

    for (int i = splitIndex; i < node.size; i++) {
      keys[splitIndex - i] = node.keys[i];
      children[splitIndex - i] = node.children[i];

      if (children[splitIndex - i] != null) children[splitIndex - i].parent = node;
    }

    InternalNode splitNode = new InternalNode(this.maxNodeSize, keys, children);
    splitNode.parent = node.parent;
    node.size = splitIndex;

    splitNode.left = node;
    splitNode.right = node.right;
    node.right = splitNode;
    if (splitNode.right != null) splitNode.right.left = splitNode;

    if (node.parent == null) {
      // at top of tree, create new root
      short[] rootKeys = new short[this.maxNodeSize];
      Node[] rootChildren = new Node[this.maxNodeSize];

      InternalNode newRoot = new InternalNode(this.maxNodeSize, rootKeys, rootChildren);
      newRoot.append(node.keys[0], node);
      newRoot.append(splitNode.keys[0], splitNode);

      node.parent = newRoot;
      splitNode.parent = newRoot;
      this.root = newRoot;
    } else {
      node.parent.insert(splitNode.keys[0], splitNode, node.parent.getChildIndex(node));
      splitNode.parent = node.parent;
    }
  }

  public void insert(short key, NBARecord record) {
    if (this.root.size == 0) {
      LeafNode ln = new LeafNode(this.maxNodeSize);
      ln.parent = this.root;
      ln.keys[0] = key;
      ln.records[0] = record;

      this.root.append(key, ln);
      return;
    }

    LeafNode ln = getLeafNode(key);

    // available capacity
    if (!ln.isFull()) {
      ln.insert(key, record);
      return;
    }

    // leaf node full, split leaf node
    short[] splitKeys = new short[this.maxNodeSize];
    NBARecord[] splitRecords = new NBARecord[this.maxNodeSize];

    int splitIndex = (int) Math.ceil((this.maxNodeSize + 1) / 2.0) - 1;

    for (int i = splitIndex; i < ln.size; i++) {
      splitKeys[i - splitIndex] = ln.keys[i];
      splitRecords[i - splitIndex] = ln.records[i];
    }

    LeafNode splitNode = new LeafNode(this.maxNodeSize, splitKeys, splitRecords);
    ln.parent.insert(key, splitNode, ln.parent.getChildIndex(ln) + 1);
    splitNode.parent = ln.parent;
    ln.size = splitIndex;

    splitNode.left = ln;
    splitNode.right = ln.right;
    ln.right = splitNode;
    if (splitNode.right != null) splitNode.right.left = splitNode;

    // fix tree
    InternalNode cursor = ln.parent;
    while (cursor != null && cursor.isOverfull()) {
      this.splitInternalNode(cursor);
      cursor = cursor.parent;
    }
  }

  public ArrayList<NBARecord> queryKey(short key) {
    ArrayList<NBARecord> results = new ArrayList<NBARecord>();
    LeafNode ln = getLeafNode(key);

    int firstMatch = ln.lowerBound(key);

    // no match
    if (firstMatch == ln.size) return results;

    // traverse keys to find all matches
    while (ln.keys[firstMatch] == key) {
      results.add(ln.records[firstMatch]);
      firstMatch++;
      if (firstMatch == ln.size) {
        firstMatch = 0;
        ln = ln.right;
      }
    }
    return results;
  }

  // for experiments
  public int getMaxNodeSize() {
    return this.maxNodeSize;
  }

  private int getNumNodes(Node root) {
    if (root instanceof LeafNode) return 1;
    InternalNode r = (InternalNode) root;
    int total = 1;
    for (int i = 0; i < r.size; i++) total += this.getNumNodes(r.children[i]);
    return total;
  }

  public int getNumNodes() {
    if (root.size == 0) return 0;
    return this.getNumNodes(this.root);
  }

  private int getLevels(Node root) {
    if (root instanceof LeafNode) return 1;
    return 1 + this.getLevels(((InternalNode) root).children[0]);
  }

  public int getLevels() {
    if (root.size == 0) return 0;
    return this.getLevels(this.root);
  }

  public ArrayList<Short> getRootNodeKeys() {
    ArrayList<Short> ret = new ArrayList<Short>();
    for (int i = 0; i < this.root.size; i++) ret.add(this.root.keys[i]);
    return ret;
  }
}
