
import java.time.LocalDate;

// test data
// GAME_DATE_EST	TEAM_ID_home	PTS_home	FG_PCT_home	FT_PCT_home	FG3_PCT_home	AST_home	REB_home	HOME_TEAM_WINS
// 22/12/2022	1610612740	126	0.484	0.926	0.382	25	46	1
// 22/12/2022	1610612762	120	0.488	0.952	0.457	16	40	1

// for testing purposes, can be deleted after the actual Main.java is created
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