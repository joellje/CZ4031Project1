import java.util.Date;

public class DateCompressor {
	private static final long MAGIC=86400000L;

	public static Integer compress(Date date) {
		return (int) (date.getTime() / MAGIC);
	}

	public static Date uncompress(Integer dateCompressed) {
		return new Date((long) dateCompressed * MAGIC);
	}
}
