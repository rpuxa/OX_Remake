package Engine;

import java.util.ArrayList;

class Eval {

    static long[] sides = new long[2];
    static double log2 = Math.log(2);

    static int evaluate(BitBoard bitBoard) {

        sides[0] = bitBoard.white;
        sides[1] = bitBoard.black;
        long all = bitBoard.white | bitBoard.black;

        ArrayList<DeadD> deadDS = new ArrayList<>();

        int score = 0;

        boolean white = true;

        for (long side : sides) {
            for (int i = 10; i < 60; i++)
                if ((white && ((i>=20 && i<30) || i>=40)) || (!white && (i<20 || i>=30))) {
                    long[][] d = Mask.under_diagonals[i];
                    int count = 0;
                    long mask_empty_cell = 0;
                    int countSides = Long.bitCount(Mask.diagonals[i] & side);
                    if (countSides != 0 && countSides == Long.bitCount(Mask.diagonals[i] & all))
                        for (int j = 0; j < 4; j++) {
                            count += (d[j][1] & side) != 0 || (d[j][0] != 0 && (d[j][0] & all) == 0) ? 1 : 0;
                            if (d[j][0] != 0 && (d[j][0] & all) == 0)
                                mask_empty_cell = d[j][1];
                        }
                        if (count==4)
                    deadDS.add(new DeadD(countSides, mask_empty_cell, white));
                }
            white = false;
        }

        for (DeadD deadD : deadDS)
            if (deadD.count == 3){
                int height = (int)Math.round(Math.log(deadD.mask_empty_cell)/log2) >> 4;
                if ((height == 2) == deadD.white)
                    score += (deadD.white) ? 100 : -100;
            } else if (deadD.count == 2)
                    score += (deadD.white) ? 10 : -10;
               else if (deadD.count == 1)
                    score += (deadD.white) ? 1 : -1;

        return score;
    }
}

class DeadD{
    int count;
    long mask_empty_cell;
    boolean white;


    DeadD(int count, long mask_empty_cell, boolean white){
        this.count = count;
        this.mask_empty_cell = mask_empty_cell;
        this.white = white;
    }
}
