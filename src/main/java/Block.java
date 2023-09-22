public class Block {
	private int BLOCK_SIZE = 19;
	private Record[] records = new Record[BLOCK_SIZE];

	public Block(Record[] records) {
		for (int i = 0; i < records.length; i++) {
			if (i >= BLOCK_SIZE) {
				System.out.println("Number of records exceeds size of block.");
			}

			this.records[i] = records[i];
		}
	}
	
	//getters
	public Record[] getRecords() {
		return this.records;
	}

	//setters
	public void setRecords(Record[] records) {
		this.records = records;
	}
}
