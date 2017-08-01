package Jogl;

import Engine.BitBoard;

import java.util.ArrayList;


public class Position {
    public BitBoard bitBoard;
    public boolean human_plays_for_white;
    public int end_game;
    public final ArrayList<Ball> balls;

    public static final int WHITE_WINS = 1;
    public static final int BLACK_WINS = 2;
    public static final int DRAW = 3;

    public Position(BitBoard bitBoard, boolean human_plays_for_white, int end_game, ArrayList<Ball> balls) {
        this.bitBoard = bitBoard;
        this.human_plays_for_white = human_plays_for_white;
        this.end_game = end_game;
        this.balls = balls;
    }

    public static Position make_position_empty(boolean human_plays_for_white) {
        return new Position(BitBoard.make_bitboard_empty(),human_plays_for_white,0,new ArrayList<>());
    }

    public boolean allOnGround() {
        for (Jogl.Ball ball : balls)
            if (!ball.onGround)
                return false;
        return true;
    }

}
