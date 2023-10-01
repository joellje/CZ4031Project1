import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NBADatabase {
  private Disk disk;

  private Node root;
  private int numberOfLayers;

  public NBADatabase() {
    this.disk = new Disk();
  }

  public void loadFromFile(String path) {
    this.disk.initWithData(path);
  }

  public void bulkLoad() {
    ArrayList<Record> allRecords = this.disk.getRecords();

    Collections.sort(allRecords, new SortingFunction());

    ArrayList<Node> NodeArrayList = new ArrayList<Node>();
    LeafNode prev = null;

    for (int i = 0; i < allRecords.size(); i = i + 39) {
      int blockSize = Math.min(39, allRecords.size() - i);
      short[] keys = new short[blockSize];
      Record[] records = new Record[blockSize];
      for (int j = 0; j < blockSize; j++) {
        keys[j] = PctCompressor.compress(allRecords.get(i + j).getFgPctHome());
        records[j] = allRecords.get(i + j);
      }
      LeafNode cur = new LeafNode(keys, records, prev, null);
      NodeArrayList.add(cur);
      if (prev != null) prev.setNextLeafNode(cur);
      prev = cur;
    }

    System.out.println("No of leaf nodes: " + NodeArrayList.size() + " leaf nodes");
    this.root = recurseBPlusTree(NodeArrayList);
  }

  class SortingFunction implements Comparator<Record> {
    public int compare(Record a, Record b) {
      return Double.compare(a.getFgPctHome(), b.getFgPctHome());
    }
  }

  public Node recurseBPlusTree(ArrayList<Node> al) {
    ArrayList<Node> newArrayList = new ArrayList<Node>();
    for (int i = 0; i < al.size(); i = i + 40) {
      int blockSize = Math.min(40, al.size() - i);
      short[] keys = new short[blockSize - 1];
      Node[] children = new Node[blockSize];
      Node root = new Node(keys, children);
      newArrayList.add(root);

      for (int j = 0; j < blockSize; j++) {
        Node node = al.get(i + j);
        children[j] = node;
        node.setParent(root);
        if (j != 0) {
          Node temp = node;
          while (!temp.getIsLeafNode()) {
            temp = temp.getChildren()[0];
          }
          keys[j - 1] = temp.getKeys()[0];
        }
      }
    }

    if (al.size() > 40) {
      System.out.println(
          "Layer " + this.getNumberOfLayers() + " has " + newArrayList.size() + " nodes.");
      this.incNumberOfLayers();
      return recurseBPlusTree(newArrayList);
    } else {
      this.incNumberOfLayers();
      System.out.println(
          "There are " + this.getNumberOfLayers() + " layers, including the root node layer.");
      return root;
    }
  }

  public int getNumberOfLayers() {
    return this.numberOfLayers;
  }

  public Node getRoot() {
    return this.root;
  }

  public int getNumberOfBlocks() {
    return this.disk.getNumberOfBlocks();
  }

  public int getNumberOfRecords() {
    return this.disk.getNumberOfRecords();
  }

  public int getSizeOfRecord() {
    return this.disk.getSizeOfRecord();
  }

  public int getRecordsInBlock() {
    return this.disk.getRecordsInBlock();
  }

  // setters
  public void incNumberOfLayers() {
    this.numberOfLayers++;
  }
}
