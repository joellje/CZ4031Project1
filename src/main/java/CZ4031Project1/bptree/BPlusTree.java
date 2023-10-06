package CZ4031Project1.bptree;

import CZ4031Project1.PctCompressor;
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

  public void deleteRange(short start, short end){
    ArrayList<NBARecord> toDelete = queryKeyRange(start, end);
    for(NBARecord record : toDelete){
      double fgpct = record.getFgPctHome();
      short key = PctCompressor.compress(fgpct);
      // System.out.println(record.getTeamIdHome());
      deleteKey(key , record);
    }
    return;
  }

  public void deleteKey(short key, NBARecord record){

    //empty tree
    if (this.firstLeaf == null){
      return;
    }
    // first find the Record
    LeafNode ln = getLeafNode(key);

    // delete the record
    int deleted = 0;
    // System.out.println("delete " + key);

    deleted = ln.delete(key, record);

    while(ln.keys[0] >= key && deleted == 0){
      // System.out.println("go left!");
      ln = ln.left;
      deleted = ln.delete(key, record);
    }

    while(ln.keys[ln.size - 1] <= key && deleted == 0){
      // System.out.println("go right!");

      ln = ln.right;
      deleted = ln.delete(key, record);
    }

    if(deleted == 0){
      System.out.println("Record with key does not exist!"); //if cannot find the record
      return;
    }

    //we have the leaf node with the key, and that key has been deleted. what do?
    //we wanna check if the leafnode size fits the minimum requirement
    int minleafsize = ln.maxSize / 2;
    int sizeDiff = minleafsize - (ln.size - 1);
    
    // if(ln.parent != null){
    //   System.out.println(Arrays.toString(ln.parent.children));

    // }

    if(sizeDiff <= 0){
      fixKeys(root);
      return; //dont need do anything if the size fits
    }

    //check if can borrow from left
    // check if can borrow from left
    if (ln.left != null && ln.left.size - 1 > minleafsize) {
      borrowLeft(ln, ln.left);
      fixKeys(this.root);
      return;

    }
    // check if can borrow from right
    if (ln.right != null && ln.right.size - 1> minleafsize) {
      borrowRight(ln, ln.right);
      fixKeys(this.root);
      return;
    }

    // else, merge
    if(ln.left != null && ln.left.parent == ln.parent){
      mergeLeaf(ln.left, ln);
      //recursively work on the parent node if parent node is smaller
      if(ln.parent.size - 1 < (ln.parent.maxSize - 1)/ 2){
        fixparent(ln.parent);
      }
      return;
    }
    if(ln.right != null && ln.right.parent == ln.parent){
      mergeLeaf(ln, ln.right);
      if(ln.parent.size - 1 < (ln.parent.maxSize - 1) / 2){
        fixparent(ln.parent);
      }
      return;
    }

  }
  public void fixparent(InternalNode node){
    if(node.parent != null){
      int indexOfNode = node.parent.getChildIndex(node);
      if(indexOfNode > 0 && node.parent.children[indexOfNode - 1].size - 1 > (node.maxSize - 1) / 2){
        borrowleftinternal(node, (InternalNode) node.parent.children[indexOfNode - 1]);
      }
      else if(indexOfNode < node.size - 1 && node.parent.children[indexOfNode + 1].size - 1 > (node.maxSize - 1) / 2){
        borrowrightinternal(node, (InternalNode) node.parent.children[indexOfNode + 1]);
      }
      else if(indexOfNode > 0){
        // merge
        mergeInternal((InternalNode) node.parent.children[indexOfNode - 1], node);
        if(node.parent.size < (node.maxSize - 1) / 2){
          fixparent(node.parent);
        }
      }
      else{
        System.out.println(indexOfNode);
        // System.out.println(node.parent.children[indexOfNode + 1]);
        mergeInternal(node, (InternalNode) node.parent.children[indexOfNode + 1]);
        if(node.parent.size < (node.maxSize - 1) / 2){
          fixparent(node.parent);
        }
      }
      return;
    }else{
      //if only 1 pointer
      System.out.println("root issues");
      if(node.size == 1){
        this.root = (InternalNode) node.children[0];
      }
      return;
    }
  }


  public void mergeInternal(InternalNode node1, InternalNode node2){
    //lets conduct the merge. we will merge towards the left
    short[] newkeys = new short[node1.maxSize - 1];
    Node[] newchildren = new Node[node1.maxSize];
    for(int i = 0; i < node1.size - 2; i++){
      newkeys[i] = node1.keys[i];
      newchildren[i] = node1.children[i];
    }
    newchildren[node1.size - 1] = node1.children[node1.size -1];
    //so above, node 1 is duplicated
    //below, we want to duplicate node2
    //we first copy node 2 over
    newkeys[node1.size - 1] = node2.keys[0]; //we just put in this number first. it will be duplicated but fixed later
    for(int i = 0; i < node2.size - 1; i++){
      int x = node1.size;
      newkeys[i + x] = node2.keys[i];
      newchildren[i + x] = node2.children[i];
    }

    newchildren[node1.size + node2.size - 1] = node2.children[node2.size - 1];
    node1.keys = newkeys;
    node1.children = newchildren;
    node1.size = node1.size + node2.size;
    for(Node child : node1.children){
      if(child != null){
        child.parent = node1;
    }
    }
    int indexOfNode = node1.parent.getChildIndex(node1);
    //we want to delete node 2 to fix the parent
    short[] newkeysparent = new short[node1.maxSize - 1];
    Node[] newchildrenparent = new Node[node1.maxSize];
    InternalNode parent = node1.parent;
    for(int i = 0; i < indexOfNode; i++){
      newkeysparent[i] = parent.keys[i];
      newchildrenparent[i] = parent.children[i];
    }

    newchildrenparent[indexOfNode] = parent.children[indexOfNode];
    for(int i = indexOfNode + 1; i < parent.size - 1; i++){
      newkeysparent[i - 1] = parent.keys[i];
      newchildrenparent[i] = parent.children[i + 1];
    }

    parent.keys = newkeysparent;
    parent.children = newchildrenparent;
    parent.size = parent.size - 1;
    for(Node child : parent.children){
      if(child != null){
      child.parent = parent;
    }
    }
    //now the parent is fixed, fix the keys
    fixKeys(parent);
    return;
  }



  public void borrowleftinternal(InternalNode node, InternalNode leftnode){
    // find the keys to borrow
    short keyShift = leftnode.keys[leftnode.size - 2];
    Node childrenShift = leftnode.children[leftnode.size - 1];


    //create new arrays
    short[] newleftkeys = new short[leftnode.maxSize - 1];
    Node[] newleftchildren = new Node[leftnode.maxSize];

    // duplicate the left node until the last key and the last pointer and update left
    for(int i = 0; i < leftnode.size - 2; i++){
      newleftkeys[i] = leftnode.keys[i];
      newleftchildren[i] = leftnode.children[i];
    }
    newleftchildren[leftnode.size - 2] = leftnode.children[leftnode.size - 2];
    leftnode.keys = newleftkeys;
    leftnode.children = newleftchildren;
    leftnode.size = leftnode.size-1; // left size decreases by 1
    for(Node child : leftnode.children){
      if(child != null){
        child.parent = leftnode;
      }
    }

    //create new keys and children for node
    short[] newkeys = new short[leftnode.maxSize - 1];
    Node[] newchildren = new Node[leftnode.maxSize];

    // first index for key and pointer is the shift
    newkeys[0] = keyShift;
    newchildren[0] = childrenShift;

    //update the node by duplicating everything but + 1 position
    for(int i = 0; i < node.size - 1; i++){
      newkeys[i + 1] = node.keys[i];
      newchildren[i + 1] = node.children[i];
    }
    newchildren[node.size] = node.children[node.size - 1];
    node.keys = newkeys;
    node.children = newchildren;
    node.size = node.size+1;
    for(Node child : node.children){
      if(child != null){
      child.parent = node;
    }
    }
    //fix the key
    fixKeys(root);
    return;
  }

  public void borrowrightinternal(InternalNode node, InternalNode rightnode){

    //key to shift is the first key and child of the right node
    short keyShift = rightnode.keys[0];
    Node childrenShift = rightnode.children[0];

    // initialise the new arrays
    short[] newrightkeys = new short[rightnode.maxSize - 1];
    Node[] newrightchildren = new Node[rightnode.maxSize];

    //push all rightkeys back by 1
    for(int i = 1; i < rightnode.size - 1; i++){
      newrightkeys[i - 1] = rightnode.keys[i];
      newrightchildren[i - 1] = rightnode.children[i];
    }
    newrightchildren[rightnode.size - 2] = rightnode.children[rightnode.size - 1];
    rightnode.size = rightnode.size - 1;
    //update right
    rightnode.keys = newrightkeys;
    rightnode.children = newrightchildren;
    for(Node child : rightnode.children){
      if(child != null){
      child.parent = rightnode;
    }
    }
    //initialise arrays for the node
    short[] newkeys = new short[rightnode.maxSize - 1];
    Node[] newchildren = new Node[rightnode.maxSize];

    // last key and child is the new ones
    newkeys[node.size - 1] = keyShift;
    newchildren[node.size] = childrenShift;

    for(int i = 0; i < node.size - 1; i++){
      newkeys[i] = node.keys[i];
      newchildren[i] = node.children[i];
    }
    newchildren[node.size - 1] = node.children[node.size - 1];
    node.keys = newkeys;
    node.children = newchildren;
    node.size = node.size+1;
    for(Node child : node.children){
      if(child != null){
      child.parent = node;
    }
    }
    fixKeys(root);
    return;
  }


  public void borrowRight(LeafNode node, LeafNode rightnode){
    // find rightmost record
    short keyShift = rightnode.keys[0];
    NBARecord recordShift = rightnode.records[0];
    rightnode.delete(keyShift, recordShift);
    node.insert(keyShift, recordShift);
  }

  public void borrowLeft(LeafNode node, LeafNode leftnode){
    short keyShift = leftnode.keys[leftnode.size - 1];
    NBARecord recordShift = leftnode.records[leftnode.size - 1];
    leftnode.delete(keyShift, recordShift);
    node.insert(keyShift, recordShift);
    // InternalNode parent = node.parent;
    // for(int i = 0; i < parent.size - 1; i++){
    //   parent.keys[i] = parent.children[i + 1].keys[0]; // set key of parent to always be the leftmost
    // }
    // return;
  }

  // fix the tree to have the correct key
  public short fixKeys(Node node){
    if(node instanceof LeafNode){
      return node.keys[0];
    } //if is leaf, just tell the above guy your smallest number
    else if(node instanceof InternalNode){
      InternalNode thisNode = (InternalNode) node;
      short res = fixKeys(thisNode.children[0]); //you wanna tell the above guy the smallest number at your side so just dfs left
      for(int i = 0; i < thisNode.size - 1; i++){
        thisNode.keys[i] = fixKeys(thisNode.children[i + 1]); //fix every key with the smallest branch of the child node too
      }
      return res;
    }
    return 1;
  }

  public void mergeLeaf(LeafNode node1, LeafNode node2){
    short[] key1 = node1.keys;
    short[] key2 = node2.keys;
    NBARecord[] records1 = node1.records;
    NBARecord[] records2 = node2.records;
    for(int i = node1.size; i < node1.size + node2.size; i++){
      int j = 0;
      key1[i] = key2[j];
      records1[i] = records2[j];
      j++;
    }

    node1.size = node1.size + node2.size;

    // we update the parent
    // look for the left node
    InternalNode parent = node1.parent;
    int left = 0;
    for(int i = 0; i < parent.size; i++){
      if(parent.children[i] == node1){
        left = i;
        break;
      }
    }
    short[] newkeys = new short[parent.maxSize - 1];
    Node[] newchildren = new Node[parent.maxSize];
    for(int j = 0; j < left; j ++ ){
      newkeys[j] = parent.keys[j];
      newchildren[j] = parent.children[j];
    }
    newchildren[left] = parent.children[left]; //keep node 1
    for(int j = left + 1; j < parent.size - 1; j++){
      newkeys[j - 1] = parent.keys[j];
      newchildren[j] = parent.children[j + 1];
    }
    parent.keys = newkeys;
    parent.children = newchildren;
    parent.size = parent.size - 1;
    node1.right = node2.right;
    for(Node child : parent.children){
      if(child != null){
      child.parent = parent;
    }
    }
    return;
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



  public ArrayList<NBARecord> queryKeyRange(short startKey, short endKey) {
    ArrayList<NBARecord> results = new ArrayList<NBARecord>();

    // empty tree
    if (this.firstLeaf == null) return results;

    LeafNode ln = getLeafNode(endKey);

    int rightMost = ln.keyUpperBound(endKey) - 1;

    // no match, get the rightmost record
    if (rightMost == -1) {
      rightMost = ln.size - 1;
    }

    // traverse left until reach first record with startKey
    while (ln != null && ln.keys[rightMost] >= startKey) {
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
    if (root == null) return 0;
    InternalNode r = (InternalNode) root;
    int total = 1;
    for (int i = 0; i < r.size - 1; i++) total += this.getNumNodes(r.children[i]);
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

  // public int numberOfLayers
}
