
import java.time.LocalDate;

// test data
// GAME_DATE_EST	TEAM_ID_home	PTS_home	FG_PCT_home	FT_PCT_home	FG3_PCT_home	AST_home	REB_home	HOME_TEAM_WINS
// 22/12/2022	1610612740	126	0.484	0.926	0.382	25	46	1
// 22/12/2022	1610612762	120	0.488	0.952	0.457	16	40	1

// for testing purposes, can be deleted after the actual Main.java is created
public class Main {
	public static void main(String[] args) {
//		Record r1 = new Record(LocalDate.of(2022, 12, 22), 1610612740, 126, 0.484, 0.926, 0.382, 25, 46, 1);
//		Record r2 = new Record(LocalDate.of(2022, 12, 22), 1610612762, 120, 0.488, 0.952, 0.457, 16, 40, 1);
//		Record[] records = new Record[]{r1, r2};
//		Block block = new Block(records);
//
//		System.out.println("GAME_DATE_EST	TEAM_ID_home	PTS_home	FG_PCT_home	FT_PCT_home	FG3_PCT_home	AST_home	REB_home	HOME_TEAM_WINS");
//		for (Record r : block.getRecords()) {
//			if (r != null) {
//				System.out.println(r.getGameDateEst() + "\t" + r.getTeamIdHome() + "\t" + r.getPtsHome() + "\t" + r.getFgPctHome() + "\t" + r.getFtPctHome() + "\t" + r.getFg3PctHome() + "\t" + r.getAstHome() + "\t" + r.getRebHome() + "\t" + r.getHomeTeamWins());
//			}
//		}

		System.out.println("Application Start");
		System.out.println("=================================");
		// in bytes

		Disk disk = new Disk();
		disk.initWithData("games.txt");

	}
}