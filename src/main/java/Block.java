public class Block {
	private int MAX_BLOCK_SIZE = 19;
	private Record[] records = new Record[MAX_BLOCK_SIZE];

	private int current_block_size = 0;

	public Block(Record[] records) throws Exception {
		for (int i = 0; i < records.length; i++) {
			if (i >= MAX_BLOCK_SIZE) {
				System.out.println("Number of records exceeds size of block.");
				throw new Exception("Number of records exceeds size of block.");

			}

			this.records[i] = records[i];
			this.current_block_size++;
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

	public int getSize(){
		return this.current_block_size;
	}
}
