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

  public static void main(String[] args) {
    System.out.println("Application Start");
    System.out.println("=================================");

    disk = new Disk();
    tree = new BPlusTree(39);

    experiment1();
    experiment2();
    experiment3();
  }

  private static void experiment1() {
    disk.initWithData(
        Paths.get("").toAbsolutePath().getParent().getParent().getParent().toString()
            + "/games.txt");

    System.out.println("\nEXPERIMENT 1");
    System.out.println("Total Number of records: " + disk.getNumberOfRecords() + " records");
    System.out.println("Size of a record: " + disk.getSizeOfRecord() + " bytes");
    System.out.println(
        "Number of records stored in a block: " + disk.getRecordsInBlock() + " records");
    System.out.println(
        "Number of blocks for storing data: " + disk.getNumberOfBlocks() + " blocks");
  }

  private static void experiment2() {
    System.out.println("\nEXPERIMENT 2");
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
    System.out.println("\nEXPERIMENT 3");
    ArrayList<NBARecord> indexedResults = tree.queryKey(PctCompressor.compress(0.5));
    ArrayList<NBARecord> diskResults =
        (ArrayList<NBARecord>)
            disk.getRecords().stream()
                .filter((r) -> r.getFgPctHome() == 0.5)
                .collect(Collectors.toList());

    double indexedAverage =
        indexedResults.stream().mapToDouble((r) -> r.getFg3PctHome()).average().getAsDouble();
    double linearAverage =
        diskResults.stream().mapToDouble((r) -> r.getFg3PctHome()).average().getAsDouble();

    System.out.println("Indexed Average: " + indexedAverage);
    System.out.println("Linear Average: " + linearAverage);
  }
}
