package Engine;

import static Utils.BitUtils.getBit;
import static Utils.BitUtils.setBit;

public class Mask {
    static long column[] = new long[16];
    public static long diagonals[] = new long[76];
    static long under_diagonals[][][] = new long[76][4][2];
    static {
        for (int z = 0; z < 4; z++)
            for (int y = 0; y < 4; y++)
                for (int x = 0; x < 4; x++) {
                    int bit_num = 16 * z + 4 * y + x;
                    int column_num = 4 * y + x;
                    column[column_num] = setBit(column[column_num], bit_num);
                    diagonals[10 * z + y] = setBit(diagonals[10 * z + y], bit_num);
                    diagonals[10 * z + x + 4] = setBit(diagonals[10 * z + x + 4], bit_num);
                    if (x == y)
                        diagonals[10 * z + 8] = setBit(diagonals[10 * z + 8], bit_num);
                    if (x + y == 3)
                        diagonals[10 * z + 9] = setBit(diagonals[10 * z + 9], bit_num);
                    if (y == z)
                        diagonals[40 + x] = setBit(diagonals[40 + x], bit_num);
                    if (x == z)
                        diagonals[44 + y] = setBit(diagonals[44 + y], bit_num);
                    if (y + z == 3)
                        diagonals[48 + x] = setBit(diagonals[48 + x], bit_num);
                    if (x + z == 3)
                        diagonals[52 + y] = setBit(diagonals[52 + y], bit_num);
                    if (x == y && x == z)
                        diagonals[56] = setBit(diagonals[56], bit_num);
                    if (x == y && x == 3 - z)
                        diagonals[57] = setBit(diagonals[57], bit_num);
                    if (x + y == 3 && x == 3 - z)
                        diagonals[58] = setBit(diagonals[58], bit_num);
                    if (x + y == 3 && x == z)
                        diagonals[59] = setBit(diagonals[59], bit_num);
                    diagonals[60 + column_num] = setBit(diagonals[60 + column_num], bit_num);
                }
        for (int i = 10; i < 60; i++){
            long d = diagonals[i];
            int n = 0;
            for (int j = 0; j < 64; j++)
                if (getBit(d, j)) {
                    if (j>=16)
                        under_diagonals[i][n][0] = setBit(under_diagonals[i][n][0],j-16);
                    under_diagonals[i][n][1] = setBit(under_diagonals[i][n][1],j);
                    n++;
                }
        }
    }
}