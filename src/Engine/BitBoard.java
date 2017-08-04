package Engine;

import java.util.ArrayList;
import java.util.Random;

import static Utils.BitUtils.*;


public class BitBoard {
    long white;
    long black;

    BitBoard(long white, long black) {
        this.white = white;
        this.black = black;
    }

    public static BitBoard make_bitboard_empty() {
        return new BitBoard(0, 0);
    }

    public static BitBoard make_bitboard_from_bitboard(BitBoard bitBoard) {
        return new BitBoard(bitBoard.white, bitBoard.black);
    }

    ArrayList<Byte> getMoves(boolean isTurnWhite) {
        return getMoves(isTurnWhite, false);
    }

    ArrayList<Byte> getMoves(boolean isTurnWhite, boolean checks) {
        ArrayList<Byte> moves = new ArrayList<>();
        long all = white | black;
        for (byte i = 0; i < 16; i++)
            if (Long.bitCount(all & Mask.column[i]) < 4)
                moves.add(i);
        return sortMoves(moves, isTurnWhite, checks);
    }

    ArrayList<Byte> sortMoves(ArrayList<Byte> moves, boolean isTurnWhite, boolean checks) {
        ArrayList<Byte> moves_checks = new ArrayList<>();
        ArrayList<Byte> moves_defence = new ArrayList<>();
        ArrayList<Byte> moves_generals = new ArrayList<>();
            for (byte move : moves) {
                boolean check = false;
            BitBoard bitBoard = make_bitboard_from_bitboard(makeMove(this,isTurnWhite,move));
            if (bitBoard.win(isTurnWhite)){
                ArrayList<Byte> sort = new ArrayList<>();
                sort.add(move);
                return sort;
            }
                long all = bitBoard.white | bitBoard.black;
               BitBoard bitBoard1 = make_bitboard_from_bitboard(makeMove(this,!isTurnWhite,move));
                if (bitBoard1.win(!isTurnWhite)){
                    moves_defence.add(move);
                } else {
                    for (byte i = 0; i < 16; i++)
                        if (Long.bitCount(all & Mask.column[i]) < 4 && makeMove(bitBoard, isTurnWhite, i).win(isTurnWhite)) {
                            moves_checks.add(move);
                            check = true;
                            break;
                        }
                    if (!check && !checks)
                        moves_generals.add(move);
                }
        }
        ArrayList<Byte> sort = new ArrayList<>();

        if (moves_defence.size() == 1)
            return moves_defence;
        if (moves_defence.size() >= 2){
            ArrayList<Byte> move_defence = new ArrayList<>();
            move_defence.add(moves_defence.get(0));
            return move_defence;
        }

        for (byte move : moves_checks)
            sort.add(move);
        for (byte move : moves_generals)
            sort.add(move);

        int[] hash = Ai.history_moves.get(getKey());

        if (hash != null)
            for (int i = hash.length - 1; i >= 0; i--)
                for (int j = sort.size() - 1; j >= 0; j--)
                    if (hash[i] == sort.get(j)) {
                        sort.add(0, sort.remove(j));
                        break;
                    }

        return sort;
    }

    public static BitBoard makeMove(BitBoard bitBoard2, boolean white, byte move){
        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard2);
        long all = bitBoard.white | bitBoard.black;
        int z = Long.bitCount(all & Mask.column[move]);
        if (white)
            bitBoard.white = setBit(bitBoard.white,16*z+move);
        else
            bitBoard.black = setBit(bitBoard.black,16*z+move);
        return bitBoard;
    }

    boolean win(boolean white) {
        for (int i = 0; i < Mask.diagonals.length; i++) {
            if (white && Long.bitCount(this.white & Mask.diagonals[i]) == 4)
                return true;
            if (!white && Long.bitCount(black & Mask.diagonals[i]) == 4)
                return true;
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof BitBoard)) return false;

        BitBoard other = (BitBoard) obj;

        return white == other.white && black == other.black;
    }

    public static void zKeys_gen(){
        Random rand = new Random();
        for (int i = 0; i < 64; i++)
            for (int j = 0; j < 2; j++)
                zKeys_int[i][j] = rand.nextInt();
        zKeys_long_gen();
    }

    static void zKeys_long_gen(){
        Random rand = new Random();
        for (int i = 0; i < 64; i++)
            for (int j = 0; j < 2; j++)
                zKeys_long[i][j] = rand.nextLong();
    }

    private static int zKeys_int[][] = new int[64][2];
    private static long zKeys_long[][] = new long[64][2];

    long getKey() {
        long hash = 0L;
        for (int i = 0; i < 64; ++i) {
            if (getBit(white, i))
                hash ^= zKeys_long[i][0];
            else if (getBit(black, i))
                hash ^= zKeys_long[i][1];
        }
        return hash;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < 64; ++i) {
            if (getBit(white,i))
                hash ^= zKeys_int[i][0];
            else if (getBit(black,i))
                hash ^= zKeys_int[i][1];
        }
        return hash;
    }

}
