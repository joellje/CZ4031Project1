package CZ4031Project1;

import java.time.LocalDate;

public class DateCompressor {
  public static int compress(LocalDate date) {
    return (int) date.toEpochDay();
  }

  public static LocalDate uncompress(int dateCompressed) {
    return LocalDate.ofEpochDay(dateCompressed);
  }
}
