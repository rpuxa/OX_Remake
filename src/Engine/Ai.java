package Engine;

import Jogl.Menu;

import java.util.*;

public class Ai {
    Ai() {
    }

    private Map<BitBoard,Integer> history_score = new HashMap<>();
    public static Map<Long,int[]> history_moves = new HashMap<>();

    int[] bfs(BitBoard bitBoard, int depth, boolean white){
        int num[] = new int[2];
        for (int i = 2; i <= depth; i++) {
            num = alphaBetaStart(bitBoard,(white) ? 0:1,i);
            if (num[1] > 20000 || num[1] < -20000)
                break;
            history_score.clear();
        }
        return num;
    }

    int[] alphaBetaStart(BitBoard bitBoard, int depth, int maxDepth){
        int alpha = -100000, beta = 100000;
        ArrayList<int[]> sort = new ArrayList<>();
        byte best = 0;
        if (depth == 0){
            for (byte move : bitBoard.getMoves(false)) {
                int result = alphaBeta(BitBoard.makeMove(bitBoard,false,move), depth + 1, maxDepth, alpha, beta);
                if (result < beta){
                    sort.add(new int[]{result, move});
                    beta = result;
                    best = move;
                }
            }
            historyHashing(-1, sort, bitBoard.getKey());
            return new int[]{best,beta};
        } else {
            for (byte move : bitBoard.getMoves(true)) {
                int result = alphaBeta(BitBoard.makeMove(bitBoard,true,move), depth + 1, maxDepth, alpha, beta);
                if (result > alpha){
                    sort.add(new int[]{result, move});
                    alpha = result;
                    best = move;
                }
            }
            historyHashing(-1, sort, bitBoard.getKey());
            return new int[]{best,alpha};
        }
    }
    private int alphaBeta(BitBoard bitBoard, int depth, int maxDepth, int alpha, int beta){
        if (Menu.isInterrupted)
            return 0;
        if (bitBoard.win(true))
            return 30000 - depth;
        if (bitBoard.win(false))
            return -30000 + depth;
        if ((bitBoard.white | bitBoard.black) == ~0L)
            return 0;

        Integer hashScore = history_score.get(bitBoard);
        if (hashScore != null)
            return hashScore;

        if (depth >= maxDepth)
            return Eval.evaluate(BitBoard.make_bitboard_from_bitboard(bitBoard));
           // return quies(Engine.BitBoard.make_bitboard_from_bitboard(bitBoard),depth,alpha,beta);

       ArrayList<int[]> sort = new ArrayList<>();

        if ((depth & 1) == 0){
            for (byte move : bitBoard.getMoves(false)) {
                int result = alphaBeta(BitBoard.makeMove(bitBoard,false,move), depth + 1, maxDepth, alpha, beta);

                if (result < beta) {
                    sort.add(new int[]{result, move});
                    beta = result;
                }

                if (alpha >= beta)
                    break;
            }
            history_score.put(BitBoard.make_bitboard_from_bitboard(bitBoard),beta);
            historyHashing(1, sort, bitBoard.getKey());
            return beta;
        } else {
            for (byte move : bitBoard.getMoves(true)) {
                int result = alphaBeta(BitBoard.makeMove(bitBoard,true,move), depth + 1, maxDepth, alpha, beta);

                if (result > alpha) {
                    sort.add(new int[]{result, move});
                    alpha = result;
                }

                if (alpha >= beta)
                    break;
            }
            history_score.put(BitBoard.make_bitboard_from_bitboard(bitBoard),alpha);
           historyHashing(-1, sort, bitBoard.getKey());
            return alpha;
        }
    }

    private void historyHashing(int side, ArrayList<int[]> sort, long key) {
        int[][] arr = sort.toArray(new int[sort.size()][2]);
        Arrays.sort(arr, Comparator.comparingInt(a -> side * a[0]));
        int[] hash = new int[sort.size()];
        for (int i = 0; i < hash.length; i++)
            hash[i] = arr[i][1];
        history_moves.put(key, hash);
    }

    private static int quies(BitBoard bitBoard, int depth, int alpha, int beta) {
        if (bitBoard.win(true))
            return 30000 - depth;
        if (bitBoard.win(false))
            return -30000 + depth;
        if ((bitBoard.white | bitBoard.black) == ~0L)
            return 0;

        int val = Eval.evaluate(bitBoard);

        if ((depth & 1) == 0) {
            ArrayList<Byte> moves = bitBoard.getMoves(false,true);
            if (moves.size() != 1) {
                if (val < beta)
                    beta = val;
            } else if (moves.size() == 1)
                return quies(BitBoard.makeMove(BitBoard.make_bitboard_from_bitboard(bitBoard),false, moves.get(0)), depth + 1, alpha, beta);
            for (byte move : moves) {
                int result = quies(BitBoard.makeMove(BitBoard.make_bitboard_from_bitboard(bitBoard),false, move), depth + 1, alpha, beta);
                if (result < beta)
                    beta = result;
                if (alpha >= beta)
                    break;
                if (beta < -20000)
                    break;
            }
            return beta;
        } else {
            ArrayList<Byte> moves = bitBoard.getMoves(true,true);
            if (moves.size() != 1) {
            if (val > alpha)
                alpha = val;
            } else if (moves.size() == 1)
                return quies(BitBoard.makeMove(BitBoard.make_bitboard_from_bitboard(bitBoard),true, moves.get(0)), depth + 1, alpha, beta);
            for (byte move : moves) {
                int result = quies(BitBoard.makeMove(BitBoard.make_bitboard_from_bitboard(bitBoard),true, move), depth + 1, alpha, beta);
                if (result > alpha)
                    alpha = result;
                if (alpha >= beta)
                    break;
                if (alpha > 20000)
                    break;
            }
            return alpha;
        }
    }
}
