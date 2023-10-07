package CZ4031Project1;

import CZ4031Project1.bptree.BPlusTree;
import CZ4031Project1.storage.Disk;
import CZ4031Project1.storage.NBARecord;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Main {
  private static Disk disk;
  private static BPlusTree tree;

  private static LinearProfiler lp;

  public static void main(String[] args) {
    System.out.println("Application Start");
    System.out.println("=================================");

    disk = new Disk();
    tree = new BPlusTree(39, true);
    lp = new LinearProfiler();

    experiment1();
    experiment2();
    // tree.assertTreeStructure();
    experiment3();
    experiment4();

    // experiment5();
    // tree.printLeafs();

  }

  private static void experiment1() {
    disk.initWithData(
        Paths.get("").toAbsolutePath().getParent().getParent().getParent().toString()
            + "/games.txt");
    // disk.initWithData(
    // Paths.get("").toAbsolutePath().getParent().toString()
    // + "/games.txt");
    System.out.println("\n-----EXPERIMENT 1-----");
    System.out.println("Total Number of records: " + disk.getNumberOfRecords() + " records");
    System.out.println("Size of a record: " + disk.getSizeOfRecord() + " bytes");
    System.out.println(
        "Number of records stored in a block: " + disk.getRecordsInBlock() + " records");
    System.out.println(
        "Number of blocks for storing data: " + disk.getNumberOfBlocks() + " blocks");
  }

  private static void experiment2() {
    System.out.println("\n-----EXPERIMENT 2-----");
    for (NBARecord record : disk.getRecords()) {
      tree.insert(PctCompressor.compress(record.getFgPctHome()), record);
    }
    System.out.printf("n: %d.\n", tree.getMaxNodeSize());
    System.out.printf("Number of nodes in tree: %d.\n", tree.getNumNodes());
    System.out.printf("Number of levels in tree: %d.\n", tree.getLevels());
    System.out.printf("Keys of root node: %s.\n", tree.getRootNodeKeys().toString());
    System.out.println(tree.getNumRecords());
    // tree.printRecords();
  }

  private static void experiment3() {
    System.out.println("\n-----EXPERIMENT 3-----");

    System.out.println("Indexed scan");
    tree.startProfiling();
    ArrayList<NBARecord> indexedResults = tree.queryKey(PctCompressor.compress(0.5));
    tree.endProfiling();
    System.out.println("Total time taken: " + tree.getProfiledDurationNano() * Math.pow(10, -6));

    System.out.println("Number of index nodes accessed: " + tree.getNumNodesAccessed());
    System.out.println("Number of data blocks accessed: " + tree.getNumBlocksAccessed());
    double indexedAverage =
        indexedResults.stream().mapToDouble((r) -> r.getFg3PctHome()).average().getAsDouble();
    System.out.println("Indexed Average: " + indexedAverage);
    System.out.println("Number of results: " + indexedResults.size());

    lp.startProfiling();
    ArrayList<NBARecord> diskResults =
        (ArrayList<NBARecord>)
            disk.getRecords().stream()
                .filter((r) -> r.getFgPctHome() == 0.5)
                .collect(Collectors.toList());

    double linearAverage =
        diskResults.stream().mapToDouble((r) -> r.getFg3PctHome()).average().getAsDouble();

    System.out.println("Linear scan");
    System.out.println("Total time taken: " + lp.getProfiledDurationNano() * Math.pow(10, -6));
    System.out.println("Number of blocks accessed: " + disk.getNumberOfBlocks() + " blocks");
    System.out.println("Linear Average: " + linearAverage);
    System.out.println("Number of results: " + diskResults.size());
    lp.endProfiling();
  }

  private static void experiment4() {
    System.out.println("\n-----EXPERIMENT 4-----");

    System.out.println("Indexed scan");
    tree.startProfiling();
    ArrayList<NBARecord> indexedResults =
        tree.queryKeyRange(PctCompressor.compress(0.6), PctCompressor.compress(1.0));
    tree.endProfiling();
    System.out.println("Total time taken: " + tree.getProfiledDurationNano() * Math.pow(10, -6));

    System.out.println("Number of index nodes accessed: " + tree.getNumNodesAccessed());
    System.out.println("Number of data blocks accessed: " + tree.getNumBlocksAccessed());
    double indexedAverage =
        indexedResults.stream().mapToDouble((r) -> r.getFg3PctHome()).average().getAsDouble();
    System.out.println("Indexed Average: " + indexedAverage);
    System.out.println("Number of results: " + indexedResults.size());

    lp.startProfiling();
    ArrayList<NBARecord> diskResults =
        (ArrayList<NBARecord>)
            disk.getRecords().stream()
                .filter((r) -> 0.6 <= r.getFgPctHome() && r.getFgPctHome() <= 1.0)
                .collect(Collectors.toList());

    double linearAverage =
        diskResults.stream().mapToDouble((r) -> r.getFg3PctHome()).average().getAsDouble();

    System.out.println("Linear scan");
    System.out.println("Total time taken: " + lp.getProfiledDurationNano() * Math.pow(10, -6));
    System.out.println("Number of blocks accessed: " + disk.getNumberOfBlocks() + " blocks");
    System.out.println("Linear Average: " + linearAverage);
    System.out.println("Number of results: " + diskResults.size());
    lp.endProfiling();
  }

  private static void experiment5() {
    System.out.println("\n-----EXPERIMENT 5-----");
    System.out.println("Indexed scan");
    tree.startProfiling();
    short start = PctCompressor.compress(0);
    short end = PctCompressor.compress(0.35);
    tree.deleteRange(start, end);
    tree.endProfiling();
    System.out.println("Total number of nodes: " + tree.getNumNodes());
    System.out.println("Total number of levels: " + tree.getLevels());
    System.out.println("Root node keys: " + tree.getRootNodeKeys());
    System.out.println("Total time taken: " + tree.getProfiledDurationNano() * Math.pow(10, -6));
    lp.startProfiling();
    ArrayList<NBARecord> diskResults =
        (ArrayList<NBARecord>)
            disk.getRecords().stream()
                .filter((r) -> r.getFgPctHome() <= 0.35)
                .collect(Collectors.toList());

    System.out.println("Linear scan");
    lp.endProfiling();
    System.out.println("Total time taken: " + lp.getProfiledDurationNano() * Math.pow(10, -6));
    System.out.println("Number of blocks accessed: " + disk.getNumberOfBlocks() + " blocks");
  }
}
