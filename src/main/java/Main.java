import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		System.out.println("Application Start");
		System.out.println("=================================");

		Disk disk = new Disk();
		
		disk.initWithData(Paths.get("").toAbsolutePath().getParent().getParent().getParent().toString() + "/games.txt");

		System.out.println();
		System.out.println("EXPERIMENT 1");
		System.out.println("Total Number of records: " + disk.getNumberOfRecords() + " records");
		System.out.println("Size of a record: " + disk.getSizeOfRecord() + " bytes");
		System.out.println("Number of records stored in a block: " + disk.getRecordsInBlock() + " records");
		System.out.println("Number of blocks for storing data: " + disk.getNumberOfBlocks() + " blocks");

		disk.bulkLoad();
	}
}