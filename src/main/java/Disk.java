import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Disk {
    private final int BLOCK_SIZE;
    private final int RECORD_SIZE;
    private final int RECORDS_IN_BLOCK;
    private final int MEMORY_SIZE;
    private final int NUMBER_OF_BLOCKS;

    private int recordIndex;
    private int numberOfRecords;
    private Block[] blocks;
    private int blockIndex;
    public Disk() {
        BLOCK_SIZE = 400;
        RECORD_SIZE = 21;
        RECORDS_IN_BLOCK = 19;
        MEMORY_SIZE = 104857600;
        NUMBER_OF_BLOCKS = MEMORY_SIZE / BLOCK_SIZE;

        blocks = new Block[NUMBER_OF_BLOCKS];
        recordIndex = 0;
    }

    public void initWithData(String path) {
        try {
            boolean isFirstLine = true;
            Record[] records = new Record[19];
            Reader input = new FileReader(path);
            BufferedReader br = new BufferedReader(input);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String line;

            // Read each line from the file
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
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

                Record record = new Record(parsedDate, teamIdHome, ptsHome, fgPctHome, ftPctHome, fg3PctHome, astHome, rebHome, homeTeamWins);
                records[recordIndex++] = record;
                numberOfRecords++;

                if (recordIndex == 10) {
                    blocks[blockIndex++] = new Block(records);
                    records = new Record[10];
                    recordIndex = 0;
                }
                System.out.println(record);
            }
            if (recordIndex != 0) {
                blocks[blockIndex++] = new Block(records);
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
}
