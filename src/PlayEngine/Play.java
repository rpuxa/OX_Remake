package PlayEngine;

import Engine.Ai;
import Engine.BitBoard;
import Jogl.Ball;
import Jogl.JavaRenderer;
import Jogl.Menu;
import Jogl.Position;

import java.util.Random;

public class Play implements Runnable {

    private int depth;
    private int time;
    private boolean startPos;

    private static Random rand = new Random();

    public Play(int depth, int time, int color, boolean startPosition) {
        this.depth = depth;
        this.time = time;
        if (color == 0)
            JavaRenderer.position.human_plays_for_white = rand.nextBoolean();
        else
            JavaRenderer.position.human_plays_for_white = color == 1;
        startPos = startPosition;
    }

    public void run() {
        boolean first = true;
        if (startPos)
            JavaRenderer.position = Position.make_position_empty(JavaRenderer.position.human_plays_for_white,true);
        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
        boolean white = JavaRenderer.position.human_plays_for_white;
        boolean isTurnWhite = JavaRenderer.position.isTurnWhite;
        while (true){
            if (!first || isTurnWhite == white) {
                while (JavaRenderer.column_chosen == null) {
                    if (Menu.isInterrupted)
                        return;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }


                int n = JavaRenderer.column_chosen;
                JavaRenderer.column_chosen = null;
                bitBoard = BitBoard.makeMove(bitBoard, white, (byte) n);

                JavaRenderer.position.balls.add(new Ball(n, white));
                JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                JavaRenderer.position.isTurnWhite = ! JavaRenderer.position.isTurnWhite;

                if (checkEnd(bitBoard))
                    break;
            }
            first = false;

            Menu.thinking = true;
            int[] num = new Ai(white).bfs(bitBoard,depth + ((white) ? 0 : 1), white,time);
            Menu.thinking = false;


            if (Menu.isInterrupted)
                return;

            bitBoard = BitBoard.makeMove(bitBoard,!white,(byte)num[0]);

            while (!JavaRenderer.position.allOnGround()){
                if (Menu.isInterrupted)
                    return;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }

            }

            JavaRenderer.position.balls.add(new Ball(num[0],!white));
            JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
            JavaRenderer.position.isTurnWhite = ! JavaRenderer.position.isTurnWhite;

            if (checkEnd(bitBoard))
                break;

        }
    }


    public static boolean checkEnd(BitBoard bitBoard){
        if (bitBoard.win(true)!=0) {
            JavaRenderer.position.end_game = Position.WHITE_WINS;
            JavaRenderer.position.mask = bitBoard.win(true);
            return true;
        }
        if (bitBoard.win(false)!=0){
            JavaRenderer.position.end_game = Position.BLACK_WINS;
            JavaRenderer.position.mask = bitBoard.win(false);
            return true;
        }
        if ((bitBoard.white | bitBoard.black) == ~0L){
            JavaRenderer.position.end_game = Position.DRAW;
            return true;
        }
        JavaRenderer.position.end_game = 0;
        JavaRenderer.position.mask = 0;
        return false;
    }
}
