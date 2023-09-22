public class PctCompressor {
	private static final short PRECISION = 1000;

	public static short compress(double pct) {
		return (short) (pct * PRECISION);
	}

	public static double uncompress(short pctCompressed) {
		return  (double) pctCompressed / PRECISION;
	}
}
