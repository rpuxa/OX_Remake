package Jogl;

import Engine.BitBoard;
import java.util.ArrayList;

import static Utils.BitUtils.getBit;


public class Position {
    public BitBoard bitBoard;
    public boolean human_plays_for_white;
    public int end_game;
    public final ArrayList<Ball> balls;
    public boolean isTurnWhite;
    public long mask;

    public static final int WHITE_WINS = 1;
    public static final int BLACK_WINS = 2;
    public static final int DRAW = 3;

    private Position(BitBoard bitBoard, boolean human_plays_for_white, int end_game, ArrayList<Ball> balls, boolean isTurnWhite, long mask) {
        this.bitBoard = bitBoard;
        this.human_plays_for_white = human_plays_for_white;
        this.end_game = end_game;
        this.balls = balls;
        this.isTurnWhite = isTurnWhite;
        this.mask = mask;
    }

    public static Position make_position_empty(boolean human_plays_for_white, boolean isTurnWhite) {
        return new Position(BitBoard.make_bitboard_empty(),human_plays_for_white,0,new ArrayList<>(),isTurnWhite,0);
    }

    public static Position make_position_from_bitboard(BitBoard bitBoard) {
        return new Position(BitBoard.make_bitboard_from_bitboard(bitBoard),true,0,make_balls_from_bitboard(bitBoard),true,0);
    }

    public static Position make_position_from_position(Position position){
        return new Position(BitBoard.make_bitboard_from_bitboard(position.bitBoard),position.human_plays_for_white,position.end_game,new ArrayList<>(position.balls),position.isTurnWhite, position.mask);
    }

    public static void add_to_history_positions(Position position){
        for (int i = JavaRenderer.history_positions.size()-1; i > JavaRenderer.moveNumber; i--)
            JavaRenderer.history_positions.remove(i);
        JavaRenderer.history_positions.add(Position.make_position_from_position(position));
        JavaRenderer.moveNumber++;
    }

    private static ArrayList<Ball> make_balls_from_bitboard(BitBoard bitBoard){
        ArrayList<Ball> balls = new ArrayList<>();
        for (int i = 0; i < 64; i++)
            if (getBit(bitBoard.white,i))
                balls.add(new Ball(true,i));
            else if (getBit(bitBoard.black,i))
                balls.add(new Ball(false,i));
        return balls;
    }

    public boolean allOnGround() {
        for (Jogl.Ball ball : balls)
            if (!ball.onGround)
                return false;
        return true;
    }

}
