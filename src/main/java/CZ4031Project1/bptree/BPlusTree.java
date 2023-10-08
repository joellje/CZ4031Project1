package CZ4031Project1.bptree;

import CZ4031Project1.PctCompressor;
import CZ4031Project1.storage.NBARecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

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
      InternalNode cur = (InternalNode) cursor;

      // traverse right while there are duplicates
      while (cur.right != null && cur.right.keys[0] == key) cur = cur.right;

      // get the right most key
      int childIndex = cur.keyUpperBound(key);

      // if (cur.left != null)
      // System.out.println(
      // Arrays.toString(Arrays.copyOfRange(cur.left.keys, 0, cur.left.keysSize)));
      // System.out.println(
      // Arrays.toString(Arrays.copyOfRange(cursor.keys, 0, cursor.keysSize))
      // + " "
      // + childIndex
      // + "");
      // if (cur.right != null)
      // System.out.println(
      // Arrays.toString(Arrays.copyOfRange(cur.right.keys, 0, cur.right.keysSize)));
      // System.out.println();

      cursor = cur.children[childIndex];
    }
    // LeafNode c = (LeafNode) cursor;
    // System.out.println();
    // if (c.left != null)
    // System.out.println(Arrays.toString(Arrays.copyOfRange(c.left.keys, 0,
    // c.left.keysSize)));
    // System.out.println(Arrays.toString(Arrays.copyOfRange(cursor.keys, 0,
    // cursor.keysSize)) +
    // "<<<<<<");
    // if (c.right != null)
    // System.out.println(Arrays.toString(Arrays.copyOfRange(c.right.keys, 0,
    // c.right.keysSize)));

    this.setNodeAccessed(cursor);
    return (LeafNode) cursor;
  }

  private void splitInternalNode(InternalNode node) {
    // System.out.println("splitting");
    int keySplitIndex = (int) Math.ceil((node.maxKeys) / 2.0);
    short[] keys = new short[this.maxNodeSize + 1];
    Node[] children = new Node[this.maxNodeSize + 1];
    short parentKey = node.keys[keySplitIndex];

    // split keys, ignore middle key
    for (int i = keySplitIndex + 1; i < node.keysSize; i++) {
      keys[i - keySplitIndex - 1] = node.keys[i];
    }

    for (int i = keySplitIndex + 1; i < node.childrenSize; i++) {
      children[i - keySplitIndex - 1] = node.children[i];
    }
    // 1 2 3 4 5
    // 0 1 2 3 4 5
    //
    // 2 2 5
    // 1 2 4 5
    // 0 1 2 3 4 5

    // 4 keys, 5 children
    // maxkeys = 3
    // maxchildren = 4
    // index = 2
    // leftnode = 2 key 3 children
    // rightnode = 1 key 2 children

    InternalNode splitNode =
        new InternalNode(
            this.maxNodeSize,
            node.maxKeys - keySplitIndex,
            node.maxChildren - keySplitIndex,
            keys,
            children);

    for (int i = 0; i < splitNode.childrenSize; i++) {
      splitNode.children[i].parent = splitNode;
    }

    splitNode.parent = node.parent;
    node.keysSize = keySplitIndex;
    node.childrenSize = keySplitIndex + 1;

    splitNode.left = node;
    splitNode.right = node.right;
    node.right = splitNode;
    if (splitNode.right != null) splitNode.right.left = splitNode;

    if (node.parent == null) {
      // at top of tree, create new root
      InternalNode newRoot = new InternalNode(this.maxNodeSize);
      newRoot.children[0] = node;
      newRoot.childrenSize++;
      newRoot.append(parentKey, splitNode);

      node.parent = newRoot;
      splitNode.parent = newRoot;
      this.root = newRoot;
    } else {
      node.parent.insert(parentKey, splitNode, node.parent.getChildIndex(node) + 1);
      splitNode.parent = node.parent;
    }
    // System.out.println("split internal");
    // System.out.println(
    // Arrays.toString(Arrays.copyOfRange(node.parent.keys, 0,
    // node.parent.keysSize)));
    // System.out.println(Arrays.toString(Arrays.copyOfRange(node.keys, 0,
    // node.keysSize)));
    // System.out.println(Arrays.toString(Arrays.copyOfRange(splitNode.keys, 0,
    // splitNode.keysSize)));
    // this.printInternalNodes();
    // System.out.println();
  }

  public void insert(short key, NBARecord record) {
    // System.out.println("Inserting " + key);
    // empty tree
    if (this.firstLeaf == null) {
      this.firstLeaf = new LeafNode(this.maxNodeSize);
      this.firstLeaf.parent = null;
      this.firstLeaf.insert(key, record);
      return;
    }

    // insert record
    LeafNode ln = this.getLeafNode(key);
    ln.insert(key, record);

    // available capacity
    if (!ln.isOverfull()) {
      return;
    }

    // leaf node full, split leaf node
    short[] splitKeys = new short[this.maxNodeSize + 1];
    NBARecord[] splitRecords = new NBARecord[this.maxNodeSize + 1];

    int splitIndex = (int) Math.ceil((ln.maxKeys + 1) / 2.0);

    // split keys and records
    for (int i = splitIndex; i < ln.keysSize; i++) {
      splitKeys[i - splitIndex] = ln.keys[i];
      splitRecords[i - splitIndex] = ln.records[i];
    }

    LeafNode splitNode =
        new LeafNode(
            this.maxNodeSize,
            ln.keysSize - splitIndex,
            ln.keysSize - splitIndex,
            splitKeys,
            splitRecords);

    // no root, create and set root
    if (this.root == null) {
      this.root = new InternalNode(this.maxNodeSize);
      ln.parent = this.root;
      this.root.children[0] = ln;
      this.root.childrenSize++;
    }
    ln.parent.insert(splitNode.keys[0], splitNode, ln.parent.getChildIndex(ln) + 1);

    splitNode.parent = ln.parent;
    ln.keysSize = splitIndex;
    ln.recordsSize = splitIndex;

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
    // System.out.println(Arrays.toString(Arrays.copyOfRange(ln.keys, 0,
    // ln.keysSize)));
    // System.out.println(Arrays.toString(Arrays.copyOfRange(splitNode.keys, 0,
    // splitNode.keysSize)));
    assertTreeStructure();
  }

  public void deleteRange(short start, short end) {
    ArrayList<NBARecord> toDelete = queryKeyRange(start, end);
    for (NBARecord record : toDelete) {
      double fgpct = record.getFgPctHome();
      short key = PctCompressor.compress(fgpct);
      // System.out.println(record.getTeamIdHome());
      deleteKey(key, record);
    }
    return;
  }

  public void deleteKey(short key, NBARecord record) {
    this.assertTreeStructure();

    // empty tree
    if (this.firstLeaf == null) {
      return;
    }
    // first find the Record
    LeafNode ln = getLeafNode(key);

    // delete the record
    // System.out.println("delete " + key);

    boolean deleted = ln.delete(key, record);

    while (ln.keys[0] >= key && !deleted) {
      ln = ln.left;
      deleted = ln.delete(key, record);
    }

    if (!deleted) {
      // System.out.println(
      //     "Record with key " + key + " does not exist!"); // if cannot find the record
      return;
    }

    // we have the leaf node with the key, and that key has been deleted. what do?
    // we wanna check if the leafnode size fits the minimum requirement
    int minLeafSize = (ln.maxKeys + 1) / 2;

    // if(ln.parent != null){
    // System.out.println(Arrays.toString(ln.parent.children));

    // }

    if (ln.keysSize >= minLeafSize) {
      fixKeys(this.root);
      return; // dont need do anything if the size fits
    }

    // check if can borrow from left
    if (ln.left != null && ln.left.keysSize > minLeafSize && ln.left.parent == ln.parent) {
      // System.out.println("Fixing");
      borrowLeft(ln, ln.left);
      fixKeys(this.root);
      return;
    }
    // check if can borrow from right
    if (ln.right != null && ln.right.keysSize > minLeafSize && ln.right.parent == ln.parent) {
      // System.out.println("Fixingg");
      borrowRight(ln, ln.right);
      fixKeys(this.root);
      return;
    }

    // else, merge
    if (ln.left != null && ln.left.parent == ln.parent) {
      // System.out.println("Fixinggg");
      mergeLeaf(ln.left, ln);
      // recursively work on the parent node if parent node is smaller
      if (ln.parent.keysSize < (ln.parent.maxKeys) / 2) {
        // System.out.println("hh");
        fixParent(ln.parent);
      }
      fixKeys(this.root);
      return;
    }
    if (ln.right != null && ln.right.parent == ln.parent) {
      // System.out.println("Fixingggg");
      mergeLeaf(ln, ln.right);
      if (ln.parent.keysSize < (ln.parent.maxKeys) / 2) {
        // System.out.println("hi");
        fixParent(ln.parent);
      }
      fixKeys(this.root);
      return;
    }
  }

  public void fixParent(InternalNode node) {
    int minInternalKeys = (node.maxKeys) / 2;
    if (node.left != null && node.parent == node.left.parent) {
      if (node.left.keysSize - 1 >= minInternalKeys) {
        // System.out.println("borrow left");
        borrowLeftInternal(node, node.left);
      }
    } else if (node.right != null && node.parent == node.right.parent) {
      borrowRightInternal(node, node.right);
      // System.out.println("borrow right");
    } else if (node.left != null && node.parent == node.left.parent) {
      // System.out.println("merge left");
      mergeInternal(node.left, node);
    } else if (node.right != null && node.parent == node.right.parent) {
      // System.out.println("merge right");
      mergeInternal(node, node.right);
    } else {
      // node is root
      if (node.childrenSize == 1) {
        this.root = (InternalNode) node.children[0];
      }
    }
  }

  public void mergeInternal(InternalNode node1, InternalNode node2) {
    // merge keys
    for (int i = 0, j = node1.keysSize; i < node2.keysSize; i++, j++) {
      node1.keys[j] = node2.keys[i];
    }
    // merge children and set parent
    for (int i = 0, j = node1.childrenSize; i < node2.childrenSize; i++, j++) {
      node1.children[j] = node2.children[i];
      node1.children[j].parent = node1;
    }

    int indexOfNode = node2.parent.getChildIndex(node2);
    // we want to delete node 2 to fix the parent
    InternalNode parent = node1.parent;

    // shift keys
    for (int i = indexOfNode - 1; i < parent.keysSize - 1; i++) {
      parent.keys[i] = parent.keys[i + 1];
    }
    // shift children
    for (int i = indexOfNode; i < parent.childrenSize; i++) {
      parent.children[i] = parent.children[i + 1];
    }
    parent.keysSize--;
    parent.childrenSize--;

    // now the parent is fixed, fix the keys
    fixKeys(this.root);
    return;
  }

  public void borrowLeftInternal(InternalNode node, InternalNode leftNode) {
    // find the keys to borrow
    short keyShift = leftNode.keys[leftNode.keysSize - 1];
    Node childrenShift = leftNode.children[leftNode.childrenSize - 1];
    childrenShift.parent = node;

    leftNode.keysSize--;
    leftNode.childrenSize--;

    // shift right node keys
    for (int i = 1; i < node.keysSize; i++) {
      node.keys[i] = node.keys[i - 1];
    }
    // shift right children
    for (int i = 1; i < node.childrenSize; i++) {
      node.children[i] = node.children[i - 1];
    }

    // first index for key and pointer is the shift
    node.keys[0] = keyShift;
    node.children[0] = childrenShift;

    node.keysSize++;
    node.childrenSize++;

    // fix the key
    fixKeys(root);
    return;
  }

  public void borrowRightInternal(InternalNode node, InternalNode rightNode) {
    // find the keys to borrow
    short keyShift = rightNode.keys[0];
    Node childrenShift = rightNode.children[0];
    childrenShift.parent = node;

    // shift right node keys
    for (int i = 0; i < rightNode.keysSize - 1; i++) {
      rightNode.keys[i] = rightNode.keys[i + 1];
    }
    // shift right children
    for (int i = 0; i < rightNode.childrenSize - 1; i++) {
      rightNode.children[i] = rightNode.children[i + 1];
    }

    rightNode.keysSize--;
    rightNode.childrenSize--;

    node.keys[node.keysSize] = keyShift;
    node.children[node.childrenSize] = childrenShift;

    node.keysSize++;
    node.childrenSize++;

    // fix the key
    fixKeys(root);
    return;
  }

  public void borrowRight(LeafNode node, LeafNode rightnode) {
    // find rightmost record
    short keyShift = rightnode.keys[0];
    NBARecord recordShift = rightnode.records[0];
    rightnode.delete(keyShift, recordShift);
    node.insert(keyShift, recordShift);
  }

  public void borrowLeft(LeafNode node, LeafNode leftnode) {
    short keyShift = leftnode.keys[leftnode.keysSize - 1];
    NBARecord recordShift = leftnode.records[leftnode.recordsSize - 1];
    leftnode.delete(keyShift, recordShift);
    node.insert(keyShift, recordShift);
    // InternalNode parent = node.parent;
    // for(int i = 0; i < parent.size - 1; i++){
    // parent.keys[i] = parent.children[i + 1].keys[0]; // set key of parent to
    // always be the leftmost
    // }
    // return;
  }

  // fix the tree to have the correct key
  public short fixKeys(Node node) {
    if (node instanceof LeafNode) {
      return node.keys[0];
    } // if is leaf, just tell the above guy your smallest number
    else if (node instanceof InternalNode) {
      InternalNode thisNode = (InternalNode) node;
      short res =
          fixKeys(
              thisNode
                  .children[0]); // you wanna tell the above guy the smallest number at your side so
      // just dfs left
      for (int i = 0; i < thisNode.keysSize; i++) {
        thisNode.keys[i] =
            fixKeys(
                thisNode.children[i + 1]); // fix every key with the smallest branch of the child
        // node too
      }
      return res;
    }
    return 1;
  }

  public void mergeLeaf(LeafNode node1, LeafNode node2) {
    for (int i = node1.keysSize, j = 0; i < node1.keysSize + node2.keysSize; i++) {
      node1.keys[i] = node2.keys[j];
      node1.records[i] = node2.records[j];
      j++;
    }

    node1.keysSize += node2.keysSize;
    node1.recordsSize += node2.recordsSize;
    // System.out.println("woohoo");

    // we update the parent
    // look for the left node
    InternalNode parent = node1.parent;
    int rightIndex = parent.getChildIndex(node2);

    // shift keys
    for (int i = rightIndex - 1; i < parent.keysSize - 1; i++) {
      parent.keys[i] = parent.keys[i + 1];
    }
    // shift children
    for (int i = rightIndex; i < parent.childrenSize - 1; i++) {
      parent.children[i] = parent.children[i + 1];
    }
    parent.keysSize--;
    parent.childrenSize--;
    node1.right = node2.right;
    if (node1.right != null) node1.right.left = node1;
    return;
  }

  public ArrayList<NBARecord> queryKey(short key) {
    ArrayList<NBARecord> results = new ArrayList<NBARecord>();

    // empty tree
    if (this.firstLeaf == null) return results;

    LeafNode ln = getLeafNode(key);

    int rightMost = ln.keyUpperBound(key) - 1;

    // no match as every key is biggr
    if (rightMost == -1) {
      return results;
    }

    // since getLeafNode gets the right most key,
    // we can traverse keys to the left find all matches
    while (ln != null && ln.keys[rightMost] == key) {
      results.add(ln.records[rightMost]);
      this.setNodeAccessed(ln);
      this.setBlockIndexAccessed(ln.records[rightMost].getBlockIndex());
      rightMost--;
      if (rightMost == -1) {
        ln = ln.left;
        rightMost = ln.keysSize - 1;
      }
    }
    return results;
  }

  public ArrayList<NBARecord> queryKeyRange(short startKey, short endKey) {
    ArrayList<NBARecord> results = new ArrayList<NBARecord>();

    // empty tree
    if (this.firstLeaf == null) return results;

    LeafNode ln = getLeafNode(endKey);

    int rightMost = ln.keyUpperBound(endKey) - 1;

    // no match in current leaf node, check prev leaf node
    if (rightMost == -1) {
      ln = ln.left;
      rightMost = ln.keysSize - 1;
    }

    // since getLeafNode gets the right most key,
    // we can traverse keys to the left find all matches
    while (ln != null && ln.keys[rightMost] >= startKey) {
      results.add(ln.records[rightMost]);
      this.setNodeAccessed(ln);
      this.setBlockIndexAccessed(ln.records[rightMost].getBlockIndex());
      rightMost--;
      if (rightMost == -1) {
        ln = ln.left;
        rightMost = ln.keysSize - 1;
      }
    }
    return results;
  }

  public int getMaxNodeSize() {
    return this.maxNodeSize;
  }

  private int getNumNodes(Node root) {
    if (root instanceof LeafNode) return 1;
    if (root == null) return 0;
    InternalNode r = (InternalNode) root;
    int total = 1;
    for (int i = 0; i < r.childrenSize; i++) total += this.getNumNodes(r.children[i]);
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
    for (int i = 0; i < this.root.keysSize; i++) ret.add(this.root.keys[i]);
    return ret;
  }

  public int getNumRecords() {
    int total = 0;
    LeafNode cursor = this.firstLeaf;
    while (cursor != null) {
      total += cursor.recordsSize;
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
    while (ln != null) {
      System.out.println(Arrays.toString(Arrays.copyOfRange(ln.keys, 0, ln.keysSize)));
      ln = ln.right;
    }
    System.out.println();
  }

  public void printInternalNodes() {
    LinkedList<InternalNode> q = new LinkedList<InternalNode>();
    int level = 0;
    q.add(this.root);
    q.add(null);
    System.out.println("Level " + level);
    while (!q.isEmpty()) {
      InternalNode cur = q.poll();
      if (cur == null) {
        if (q.isEmpty()) break;
        level++;
        System.out.println("Level " + level);
        q.add(null);
        continue;
      }
      System.out.println(Arrays.toString(Arrays.copyOfRange(cur.keys, 0, cur.keysSize)));

      for (int i = 0; i < cur.childrenSize; i++) {
        if (cur.children[i] instanceof InternalNode) {
          q.add((InternalNode) cur.children[i]);
        }
      }
    }
  }

  public void printKeysOfNodesAccessed() {
    for (Node node : this.profiler.nodesAccessed) {
      if (node instanceof InternalNode) {
        InternalNode n = (InternalNode) node;
        System.out.println(
            "Accessed Internal node with keys: "
                + Arrays.toString(Arrays.copyOfRange(n.keys, 0, n.keysSize)));
      } else if (node instanceof LeafNode) {
        LeafNode n = (LeafNode) node;
        System.out.println(
            "Accessed Leaf node with keys: "
                + Arrays.toString(Arrays.copyOfRange(n.keys, 0, n.keysSize)));
      }
    }
  }

  private void assertInternalNodeStructure(InternalNode node) throws IllegalStateException {
    this.assertParent(node);
    // check keys are sorted
    for (int i = 0; i < node.keysSize - 1; i++) {
      if (node.keys[i] > node.keys[i + 1]) {
        this.printInternalNodes();
        System.out.println();
        this.printRecords();

        throw new IllegalStateException(
            String.format(
                "Internal Node key at at position %d has key %d which is larger than key %d at"
                    + " position %d",
                i, node.keys[i], node.keys[i + 1], i + 1));
      }
    }

    for (int i = 0; i < node.childrenSize; i++) {
      if (node.children[i] instanceof InternalNode)
        this.assertInternalNodeStructure((InternalNode) node.children[i]);
      else break;
    }
  }

  private void assertParent(InternalNode node) throws IllegalStateException {
    if (node.parent == null) return;
    for (int i = 0; i < node.childrenSize - 1; i++) {
      if (node.children[i].parent != node.children[i + 1].parent) {
        node.children[i].parent.printKeys();
        node.children[i + 1].parent.printKeys();
        throw new IllegalStateException("wtf");
      }
    }
  }

  private void assertLeafNodeStructure() throws IllegalStateException {
    LeafNode cursor = this.firstLeaf;
    while (cursor != null) {
      // check keys in leaf node is sorted
      for (int i = 0; i < cursor.keysSize - 1; i++) {
        if (cursor.keys[i] > cursor.keys[i + 1]) {
          this.printInternalNodes();
          throw new IllegalStateException(
              String.format(
                  "Leaf Node key at position %d has key %d which is larger than key %d at position"
                      + " %d",
                  i, cursor.keys[i], cursor.keys[i + 1], i + 1));
        }
      }
      // check left sibling has smaller keys
      if (cursor.left != null) {
        short leftLargestKey = cursor.left.keys[cursor.left.keysSize - 1];
        if (leftLargestKey > cursor.keys[0]) {
          this.printInternalNodes();
          throw new IllegalStateException(
              String.format(
                  "Leaf node has key %d which is smaller than left sibling key %d",
                  cursor.keys[0], leftLargestKey));
        }
      }
      cursor = cursor.right;
    }
  }

  public void assertTreeStructure() throws IllegalStateException {
    this.assertInternalNodeStructure(this.root);
    this.assertLeafNodeStructure();
  }
}
