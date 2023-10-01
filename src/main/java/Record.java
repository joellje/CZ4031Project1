import java.time.LocalDate;

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
      LocalDate gameDateEst,
      int teamIdHome,
      int ptsHome,
      double fgPctHome,
      double ftPctHome,
      double fg3PctHome,
      int astHome,
      int rebHome,
      int homeTeamWins) {
    this.setGameDateEst(gameDateEst);
    this.setTeamIdHome(teamIdHome);
    this.setPtsHome(ptsHome);
    this.setFgPctHome(fgPctHome);
    this.setFtPctHome(ftPctHome);
    this.setFg3PctHome(fg3PctHome);
    this.setAstHome(astHome);
    this.setRebHome(rebHome);
    this.setHomeTeamWins(homeTeamWins);
  }

  // getters
  public LocalDate getGameDateEst() {
    return DateCompressor.uncompress(this.gameDateEstCompressed);
  }

  public int getTeamIdHome() {
    return this.teamIdHome;
  }

  public int getPtsHome() {
    return this.ptsHome;
  }

  public double getFgPctHome() {
    return PctCompressor.uncompress(this.fgPctHome);
  }

  public double getFtPctHome() {
    return PctCompressor.uncompress(this.ftPctHome);
  }

  public double getFg3PctHome() {
    return PctCompressor.uncompress(this.fg3PctHome);
  }

  public int getAstHome() {
    return this.astHome;
  }

  public int getRebHome() {
    return this.rebHome;
  }

  public int getHomeTeamWins() {
    if (this.homeTeamWins)
      return 1;
    else
      return 0;
  }

  // setters
  public void setGameDateEst(LocalDate gameDateEst) {
    this.gameDateEstCompressed = DateCompressor.compress(gameDateEst);
  }

  public void setTeamIdHome(int teamIdHome) {
    this.teamIdHome = teamIdHome;
  }

  public void setPtsHome(int ptsHome) {
    this.ptsHome = (short) ptsHome;
  }

  public void setFgPctHome(double fgPctHome) {
    this.fgPctHome = PctCompressor.compress(fgPctHome);
  }

  public void setFtPctHome(double ftPctHome) {
    this.ftPctHome = PctCompressor.compress(ftPctHome);
  }

  public void setFg3PctHome(double fg3PctHome) {
    this.fg3PctHome = PctCompressor.compress(fg3PctHome);
  }

  public void setAstHome(int astHome) {
    this.astHome = (short) astHome;
  }

  public void setRebHome(int rebHome) {
    this.rebHome = (short) rebHome;
  }

  public void setHomeTeamWins(int homeTeamWins) {
    this.homeTeamWins = homeTeamWins == 1;
  }

  public String toString() {
    return "Record{" +
        "gameDateEst=" + this.getGameDateEst() +
        ", teamIdHome=" + teamIdHome +
        ", ptsHome=" + ptsHome +
        ", fgPctHome=" + fgPctHome +
        ", ftPctHome=" + ftPctHome +
        ", fg3PctHome=" + fg3PctHome +
        ", astHome=" + astHome +
        ", rebHome=" + rebHome +
        ", homeTeamWins=" + homeTeamWins +
        '}';
  }

}
