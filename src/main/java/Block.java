import java.util.ArrayList;

public class Block {
  private Record[] records;

  private short size = 0;

  public Block(Record[] records, int maxBlockSize) throws Exception {
    this.records = new Record[maxBlockSize];

    for (int i = 0; i < records.length; i++) {
      this.records[i] = records[i];
      this.size++;
    }
  }

  // getters
  public Record[] getRecords() {
    return this.records;
  }

  // setters
  public void setRecords(Record[] records) {
    this.records = records;
  }

  public int getSize() {
    return this.size;
  }
}