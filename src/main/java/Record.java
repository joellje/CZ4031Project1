import java.util.Date;

public class Record {
	private int gameDateEstCompressed;
	private int teamIdHome;
	private short ptsHome;
	private short fgPctHome;
	private short ftPctHome;
	private short fg3PctHome;
	private short astHome;
	private short rebHome;
	private boolean homeTeamWins;

	public Record(
		Date gameDateEst,
		int teamIdHome,
		short ptsHome,
		short fgPctHome,
		short ftPctHome,
		short fg3PctHome,
		short astHome,
		short rebHome,
		boolean homeTeamWins
	) {
		this.setGameDateEst(gameDateEst);
		this.teamIdHome = teamIdHome;
		this.ptsHome = ptsHome;
		this.fgPctHome = fgPctHome;
		this.ftPctHome = ftPctHome;
		this.fg3PctHome = fg3PctHome;
		this.astHome = astHome;
		this.rebHome = rebHome;
		this.homeTeamWins = homeTeamWins;
	}

	//getters
	public Date getGameDateEst() {
		return DateCompressor.uncompress(this.gameDateEstCompressed);
	}
	public int getTeamIdHome() {
		return this.teamIdHome;
	}
	public float getPtsHome(){
		return PctCompressor.uncompress(this.ptsHome);
	}
	public float getFgPctHome(){
		return PctCompressor.uncompress(this.fgPctHome);
	}
	public float getFtPctHome(){
		return PctCompressor.uncompress(this.ftPctHome);
	}
	public float getFg3PctHome(){
		return PctCompressor.uncompress(this.fg3PctHome);
	}
	public float getAstHome(){
		return PctCompressor.uncompress(this.astHome);
	}
	public float getRebHome(){
		return PctCompressor.uncompress(this.rebHome);
	}
	public boolean getHomeTeamWins() {
		return this.homeTeamWins;
	}

	//setters
	public void setGameDateEst(Date gameDateEst) {
		this.gameDateEstCompressed = DateCompressor.compress(gameDateEst);
	}
	public void setTeamIdHome(int teamIdHome) {
		this.teamIdHome = teamIdHome;
	}
	public void setPtsHome(short ptsHome) {
		this.ptsHome = ptsHome;
	}
	public void setFgPctHome(short fgPctHome) {
		this.fgPctHome = fgPctHome;
	}
	public void setFtPctHome(short ftPctHome) {
		this.ftPctHome = ftPctHome;
	}
	public void setFg3PctHome(short fg3PctHome) {
		this.fg3PctHome = fg3PctHome;
	}
	public void setAstHome(short astHome) {
		this.astHome = astHome;
	}
	public void setRebHome(short rebHome) {
		this.rebHome = rebHome;
	}
	public void setHomeTeamWins(boolean homeTeamWins) {
		this.homeTeamWins = homeTeamWins;
	}
}
