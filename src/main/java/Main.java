import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) {
    System.out.println("Application Start");
    System.out.println("=================================");

    NBADatabase database = new NBADatabase();

    database.loadFromFile(Paths.get("").toAbsolutePath().getParent().getParent().getParent().toString() + "/games.txt");

    System.out.println();
    System.out.println("EXPERIMENT 1");
    System.out.println("Total Number of records: " + database.getNumberOfRecords() + " records");
    System.out.println("Size of a record: " + database.getSizeOfRecord() + " bytes");
    System.out.println("Number of records stored in a block: " + database.getRecordsInBlock() + " records");
    System.out.println("Number of blocks for storing data: " + database.getNumberOfBlocks() + " blocks");

    database.bulkLoad();
  }
}
