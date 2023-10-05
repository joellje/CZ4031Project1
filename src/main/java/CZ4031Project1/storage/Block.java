package CZ4031Project1.storage;

public class Block {
  private NBARecord[] records;

  private short size = 0;

  public Block(NBARecord[] records, int maxBlockSize) throws Exception {
    this.records = new NBARecord[maxBlockSize];

    for (int i = 0; i < records.length; i++) {
      this.records[i] = records[i];
      this.size++;
    }
  }

  // getters
  public NBARecord[] getRecords() {
    return this.records;
  }

  // setters
  public void setRecords(NBARecord[] records) {
    this.records = records;
  }

  public int getSize() {
    return this.size;
  }
}
