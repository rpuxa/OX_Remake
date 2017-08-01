package Utils;

public class BitUtils {

    public static boolean getBit(long mask, int to) {
        return ((mask >> to) & 1) != 0;
    }

    public static long setBit(long mask, int to) {
        return mask | (1L << to);
    }

    public static long zeroBit(long mask, int to) {
        return mask & ~(1L << to);
    }

    public static byte zeroBit(byte mask, int to) {
        return (byte) (mask & ~(1 << to));
    }

    public static long swapBit(long mask, int to) {
        return mask ^ (1L << to);
    }
}
