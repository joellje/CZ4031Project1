package CZ4031Project1.bptree;

import CZ4031Project1.storage.NBARecord;
import java.util.ArrayList;
import java.util.Arrays;

public class BPlusTree {
  private InternalNode root;
  private LeafNode firstLeaf;
  private int maxNodeSize;

  private BPlusTreeProfiler profiler;

  public BPlusTree(int maxNodeSize, boolean profile) {
    this.root = null;
    this.firstLeaf = null;
    this.maxNodeSize = maxNodeSize;

    this.profiler = new BPlusTreeProfiler();
  }

  private LeafNode getLeafNode(short key) {
    if (root == null) return this.firstLeaf;
    Node cursor = this.root;

    while (!(cursor instanceof LeafNode)) {
      this.setNodeAccessed(cursor);
      int childIndex = 0;
      InternalNode cur = (InternalNode) cursor;
      // traverse leaf nodes and get right most
      while (key >= cur.keys[childIndex]) {
        childIndex++;
        if (childIndex == cur.size - 1) {
          if (cur.right == null) {
            break;
          }
          cur = cur.right;
          childIndex = 0;
        }
      }
      cursor = cur.children[childIndex];
    }
    this.setNodeAccessed(cursor);
    return (LeafNode) cursor;
  }

  private void splitInternalNode(InternalNode node) {
    // max internal node size is maxNodeSize-1
    int splitIndex = (int) Math.ceil((node.maxSize - 1) / 2.0) + 1;
    short[] keys = new short[this.maxNodeSize];
    Node[] children = new Node[this.maxNodeSize];

    // split keys
    for (int i = splitIndex + 1; i < node.size; i++) {
      keys[i - splitIndex - 1] = node.keys[i - 1];
    }

    // split children
    for (int i = splitIndex; i < node.size; i++) {
      children[i - splitIndex] = node.children[i];
    }

    InternalNode splitNode =
        new InternalNode(this.maxNodeSize, node.size - splitIndex, keys, children);

    for (int i = 0; i < splitNode.size; i++) {
      children[i].parent = splitNode;
    }

    splitNode.parent = node.parent;
    node.size = splitIndex;

    splitNode.left = node;
    splitNode.right = node.right;
    node.right = splitNode;
    if (splitNode.right != null) splitNode.right.left = splitNode;

    if (node.parent == null) {
      // at top of tree, create new root
      InternalNode newRoot = new InternalNode(this.maxNodeSize);
      newRoot.appendChild(node);
      newRoot.append(splitNode.keys[0], splitNode);

      node.parent = newRoot;
      splitNode.parent = newRoot;
      this.root = newRoot;
    } else {
      node.parent.insert(splitNode.keys[0], splitNode, node.parent.getChildIndex(node) + 1);
      splitNode.parent = node.parent;
    }
  }

  public void insert(short key, NBARecord record) {
    // empty tree
    if (this.firstLeaf == null) {
      this.firstLeaf = new LeafNode(this.maxNodeSize);
      this.firstLeaf.parent = null;
      this.firstLeaf.insert(key, record);
      return;
    }

    LeafNode ln = getLeafNode(key);

    // available capacity
    ln.insert(key, record);
    if (!ln.isOverfull()) {
      return;
    }

    // leaf node full, split leaf node
    short[] splitKeys = new short[this.maxNodeSize];
    NBARecord[] splitRecords = new NBARecord[this.maxNodeSize];

    // max number of nodes in leaf node is maxNodeSize-1
    int splitIndex = (int) Math.ceil(((this.maxNodeSize - 1) + 1) / 2.0);

    // split keys and records
    for (int i = splitIndex; i < this.maxNodeSize; i++) {
      splitKeys[i - splitIndex] = ln.keys[i];
      splitRecords[i - splitIndex] = ln.records[i];
    }

    LeafNode splitNode =
        new LeafNode(this.maxNodeSize, ln.size - splitIndex, splitKeys, splitRecords);

    // no root, create and set root
    if (this.root == null) {
      this.root = new InternalNode(this.maxNodeSize);
      ln.parent = this.root;
      this.root.appendChild(ln);
    }
    ln.parent.insert(splitNode.keys[0], splitNode, ln.parent.getChildIndex(ln) + 1);

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

    // empty tree
    if (this.firstLeaf == null) return results;

    LeafNode ln = getLeafNode(key);

    int rightMost = ln.keyUpperBound(key) - 1;

    // no match
    if (rightMost == -1) return results;

    // traverse keys to find all matches
    while (ln != null && ln.keys[rightMost] == key) {
      results.add(ln.records[rightMost]);
      this.setNodeAccessed(ln);
      this.setBlockIndexAccessed(ln.records[rightMost].getBlockIndex());
      rightMost--;
      if (rightMost == -1) {
        ln = ln.left;
        rightMost = ln.size - 1;
      }
    }
    return results;
  }

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
    if (this.root == null) return 0;
    return this.getNumNodes(this.root);
  }

  private int getLevels(Node root) {
    if (root instanceof LeafNode) return 1;
    return 1 + this.getLevels(((InternalNode) root).children[0]);
  }

  public int getLevels() {
    if (this.root == null) return 0;
    return this.getLevels(this.root);
  }

  public ArrayList<Short> getRootNodeKeys() {
    ArrayList<Short> ret = new ArrayList<Short>();
    if (this.root == null) return ret;
    for (int i = 0; i < this.root.size - 1; i++) ret.add(this.root.keys[i]);
    return ret;
  }

  public int getNumRecords() {
    int total = 0;
    LeafNode cursor = this.firstLeaf;
    while (cursor != null) {
      total += cursor.size;
      cursor = cursor.right;
    }
    return total;
  }

  // profiling
  public void startProfiling() {
    this.profiler.startProfiling();
  }

  public void endProfiling() {
    this.profiler.endProfiling();
  }

  public void setBlockIndexAccessed(int blockIndex) {
    if (!this.profiler.started) return;
    this.profiler.setBlockIndexAccessed(blockIndex);
  }

  public void setNodeAccessed(Node node) {
    if (!this.profiler.started) return;
    this.profiler.setNodeAccessed(node);
  }

  public long getProfiledDurationNano() {
    return this.profiler.endTime - this.profiler.startTime;
  }

  public int getNumBlocksAccessed() {
    return this.profiler.blocksIndexesAccessed.size();
  }

  public int getNumNodesAccessed() {
    return this.profiler.nodesAccessed.size();
  }

  // for debug
  public void printRecords() {
    LeafNode ln = this.firstLeaf;
    int idx = 0;
    while (ln != null) {
      System.out.print(ln.keys[idx] + " ");
      idx++;
      if (idx == ln.size) {
        System.out.println();
        ln = ln.right;
        idx = 0;
      }
    }
    System.out.println();
  }

  public void printKeysOfNodesAccessed() {
    for (Node node : this.profiler.nodesAccessed) {
      if (node instanceof InternalNode) {
        InternalNode n = (InternalNode) node;
        System.out.println(
            "Accessed Internal node with keys: "
                + Arrays.toString(Arrays.copyOfRange(n.keys, 0, n.size - 1)));
      } else if (node instanceof LeafNode) {
        LeafNode n = (LeafNode) node;
        System.out.println(
            "Accessed Leaf node with keys: "
                + Arrays.toString(Arrays.copyOfRange(n.keys, 0, n.size)));
      }
    }
  }
}
