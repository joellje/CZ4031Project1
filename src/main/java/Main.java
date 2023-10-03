import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) {
    System.out.println("Application Start");
    System.out.println("=================================");

    NBADatabase database = new NBADatabase();

    database.loadFromFile(
        Paths.get("").toAbsolutePath().getParent().getParent().getParent().toString()
            + "/games.txt");

    System.out.println("\nEXPERIMENT 1");
    System.out.println("Total Number of records: " + database.getNumberOfRecords() + " records");
    System.out.println("Size of a record: " + database.getSizeOfRecord() + " bytes");
    System.out.println(
        "Number of records stored in a block: " + database.getRecordsInBlock() + " records");
    System.out.println(
        "Number of blocks for storing data: " + database.getNumberOfBlocks() + " blocks");

    System.out.println("\nEXPERIMENT 2");
    System.out.println("n is 39.");
    database.bulkLoad();
    database.getRootNodeKeys();

    System.out.println("\nEXPERIMENT 3");
    database.experiment3Linear();
  }
}
