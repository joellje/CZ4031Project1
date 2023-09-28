import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Disk {
    private final int SIZE_OF_MEMORY = 104857600;
    private final int SIZE_OF_BLOCK = 400;
    private final int SIZE_OF_RECORD = 23;
    private final int RECORDS_IN_BLOCK = SIZE_OF_BLOCK / SIZE_OF_RECORD;
    private final int NUMBER_OF_BLOCKS = SIZE_OF_MEMORY / SIZE_OF_BLOCK;

    private int recordIndex;
    private int numberOfRecords;
    private Block[] blocks;
    private BlockFactory blockFactory;
    private int blockIndex;
    private Node root;
    private int numberOfLayers;
    public Disk() {
        blocks = new Block[NUMBER_OF_BLOCKS];
        blockFactory = new BlockFactory(RECORDS_IN_BLOCK);
    }

    public void initWithData(String path) {
        try {
            boolean isFirstLine = true;
            Record[] records = new Record[RECORDS_IN_BLOCK];
            Reader input = new FileReader(path);
            try (BufferedReader br = new BufferedReader(input)) {
                String line;
                recordIndex = 0;

                // Read each line from the file
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    // parse data
                    String[] data = line.split("\t");
                    LocalDate parsedDate = parseDateOrNull(data[0]);
                    int teamIdHome = parseIntOrNull(data[1]);
                    int ptsHome = parseIntOrNull(data[2]);
                    double fgPctHome = parseDoubleOrNull(data[3]);
                    double ftPctHome = parseDoubleOrNull(data[4]);
                    double fg3PctHome = parseDoubleOrNull(data[5]);
                    int astHome = parseIntOrNull(data[6]);
                    int rebHome = parseIntOrNull(data[7]);
                    int homeTeamWins = parseIntOrNull(data[8]);

                    // create Record object and set in records AL
                    Record record = new Record(parsedDate, teamIdHome, ptsHome, fgPctHome, ftPctHome, fg3PctHome, astHome, rebHome, homeTeamWins);
                    records[recordIndex++] = record;
                    numberOfRecords++;

                    if (recordIndex == RECORDS_IN_BLOCK) {
                        blocks[blockIndex++] = blockFactory.createBlock(records);
                        records = new Record[RECORDS_IN_BLOCK];
                        recordIndex = 0;
                    }
                }
            }
            if (recordIndex != 0) {
                blocks[blockIndex++] = blockFactory.createBlock(records);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Other error.");
            e.printStackTrace();
        }
    }

    public void bulkLoad() {
        ArrayList<Record> RecordArrayList = new ArrayList<Record>();
        for (int i = 0; i < blockIndex; i ++) {
            for (int j = 0; j < this.blocks[i].getSize(); j++) {
                Record record = this.blocks[i].getRecords()[j];
                if (record != null) {
                    RecordArrayList.add(record);
                }
            }
        }

        Collections.sort(RecordArrayList, new SortingFunction());

        ArrayList<Node> NodeArrayList = new ArrayList<Node>();
        LeafNode prev = null;

        for (int i = 0; i < RecordArrayList.size(); i = i + 39) {
            int blockSize = Math.min(39, RecordArrayList.size() - i);
            short[] keys = new short[blockSize];
            Record[] records = new Record[blockSize];
            for (int j = 0; j < blockSize; j++) {
                keys[j] = PctCompressor.compress(RecordArrayList.get(i+j).getFgPctHome());
                records[j] = RecordArrayList.get(i+j);
            }
            LeafNode cur = new LeafNode(keys, records, prev, null);
            NodeArrayList.add(cur);
            if (prev != null)
                prev.setNextLeafNode(cur);
            prev = cur;
        }

        System.out.println("No of leaf nodes: " + NodeArrayList.size() + " leaf nodes");
        this.root = recurseBPlusTree(NodeArrayList);
    }

    public Node recurseBPlusTree(ArrayList<Node> al) {
        ArrayList<Node> newArrayList = new ArrayList<Node>();
        for (int i = 0; i < al.size(); i = i + 40) {
            int blockSize = Math.min(40, al.size() - i);
            short[] keys = new short[blockSize-1];
            Node[] children = new Node[blockSize];
            Node root = new Node(keys, children);
            newArrayList.add(root);

            for (int j = 0; j < blockSize; j++) {
                Node node = al.get(i+j);
                children[j] = node;
                node.setParent(root);
                if (j != 0) {
                    Node temp = node;
                    while (!temp.getIsLeafNode()) {
                        temp = temp.getChildren()[0];
                    }
                    keys[j-1] = temp.getKeys()[0];
                }
            }
        }

        if (al.size() > 40) {
            System.out.println("Layer " + this.getNumberOfLayers() + " has " + newArrayList.size() + " nodes.");
            this.incNumberOfLayers();
            return recurseBPlusTree(newArrayList);
        } else {
            this.incNumberOfLayers();
            System.out.println("There are " + this.getNumberOfLayers() + " layers, including the root node layer.");
            return root;
        }
    }
    public static LocalDate parseDateOrNull(String dateString) {
        String[] patterns = {"dd/MM/yyyy", "d/M/yyyy", "dd/M/yyyy", "d/MM/yyyy" };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
            }
        }

        return null;
    }

    public static double parseDoubleOrNull(String value) {
        if (value == null || value.isEmpty()) {
            return -1;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static int parseIntOrNull(String value) {
        if (value == null || value.isEmpty()) {
            return -1;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    class SortingFunction implements Comparator<Record> {
        public int compare (Record a, Record b) {
            return Double.compare(a.getFgPctHome(), b.getFgPctHome());
        }
    }

    public Block[] getBlocks() {
        return this.blocks;
    }

    public int getNumberOfBlocks() {
        return this.blockIndex;
    }

    public int getNumberOfRecords() {
        return this.numberOfRecords;
    }

    public int getNumberOfLayers() {
        return this.numberOfLayers;
    }

    public Node getRoot() {
        return this.root;
    }

    public int getSizeOfRecord() {
        return this.SIZE_OF_RECORD;
    }

    public int getRecordsInBlock() {
        return this.RECORDS_IN_BLOCK;
    }

    // setters
    public void incNumberOfLayers() {
        this.numberOfLayers++;
    }
}
