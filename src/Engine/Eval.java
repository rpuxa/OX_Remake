package Engine;

import java.util.ArrayList;

class Eval {

    private static long[] sides = new long[2];


    static int evaluate(BitBoard bitBoard) {

        sides[0] = bitBoard.white;
        sides[1] = bitBoard.black;
        long all = bitBoard.white | bitBoard.black;

        ArrayList<DeadLines> deadLines = new ArrayList<>();

        int score = 0;

        boolean white = true;

        for (long side : sides) {
            for (int i = 10; i < 60; i++)
                if (i>=20 && i<60) {
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
                    deadLines.add(new DeadLines(countSides, mask_empty_cell, white));
                }
            white = false;
        }
        int preparedWhiteDeadLinesHeight2 = 0;
        int preparedBlackDeadLinesHeight2 = 0;
        boolean preparedBlackDeadLinesHeight13 = false;

        for (DeadLines deadLine : deadLines)
            if (deadLine.count == 3){
                if (deadLine.white && deadLine.height == 2)
                    preparedWhiteDeadLinesHeight2++;
                else if (!deadLine.white && deadLine.height == 2)
                    preparedBlackDeadLinesHeight2++;
                if (!deadLine.white && deadLine.height != 2)
                    preparedBlackDeadLinesHeight13 = true;
                score += (deadLine.white) ? 100 : -100;
            } else if (deadLine.count == 2)
                    score += (deadLine.white) ? 10 : -10;
               else if (deadLine.count == 1)
                    score += (deadLine.white) ? 1 : -1;

        if (preparedWhiteDeadLinesHeight2 > preparedBlackDeadLinesHeight2)
            score += 1000;
        else if (preparedBlackDeadLinesHeight13)
            score -= 1000;

        return score;
    }
}

class DeadLines {
    private static double log2 = Math.log(2);

    int count;
    boolean white;
    int height;


    DeadLines(int count, long mask_empty_cell, boolean white){
        this.count = count;
        this.white = white;
        this.height = (int)Math.round(Math.log(mask_empty_cell)/log2) >> 4;
    }
}
