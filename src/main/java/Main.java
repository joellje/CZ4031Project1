public class Main {
	public static void main(String[] args) {
		int numberOfRecordsInABlock = 19;
		int recordSize = 21;

		System.out.println("Application Start");
		System.out.println("=================================");

		Disk disk = new Disk();
		disk.initWithData("games.txt");

		System.out.println();
		System.out.println("EXPERIMENT 1");
		System.out.println("Total Number of records: " + disk.getNumberOfRecords() + " records");
		System.out.println("Size of a record: " + recordSize + " bytes");
		System.out.println("Number of records stored in a block: " + numberOfRecordsInABlock + " records");
		System.out.println("Number of blocks for storing data: " + disk.getNumberOfBlocks() + " blocks");

		disk.bulkLoad();
	}
}