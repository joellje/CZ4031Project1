package CZ4031Project1.storage;

public class BlockFactory {
  private int maxBlockSize;

  public BlockFactory(int maxBlockSize) {
    this.maxBlockSize = maxBlockSize;
  }

  public Block createBlock(NBARecord[] records) throws Exception {
    if (records.length > this.maxBlockSize) {
      System.out.println("Number of records exceeds size of block.");
      throw new Exception("Number of records exceeds size of block.");
    }

    return new Block(records, this.maxBlockSize);
  }
}
