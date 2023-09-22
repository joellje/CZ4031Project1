public class PctCompressor {
	private static final short PRECISION = 1000;

	public static short compress(float pct) {
		return (short) (pct * PRECISION);
	}

	public static float uncompress(short pctCompressed) {
		return (float) (pctCompressed / PRECISION);
	}
}
