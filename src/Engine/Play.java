package Engine;

import Jogl.Ball;
import Jogl.JavaDia;
import Jogl.JavaRenderer;
import Jogl.Position;

import java.util.Scanner;

import static Utils.BitUtils.*;

public class Play implements Runnable {

    public void run() {
        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
        boolean white = JavaRenderer.position.human_plays_for_white;
        while (true){
            while (JavaRenderer.column_chosen == null);


            int n = JavaRenderer.column_chosen;
            JavaRenderer.column_chosen = null;
            bitBoard = BitBoard.makeMove(bitBoard,white,(byte)n);

            while (!JavaRenderer.canAdd);
            JavaRenderer.canIterate = false;
            JavaRenderer.position.balls.add(new Ball(n,white));
            JavaRenderer.canIterate = true;

            if (checkEnd(bitBoard))
                break;

            int[] num = new Ai().bfs(bitBoard,2 + ((white) ? 0 : 1), white);

            bitBoard = BitBoard.makeMove(bitBoard,!white,(byte)num[0]);

            while (!JavaRenderer.position.allOnGround());

            while (!JavaRenderer.canAdd);
            JavaRenderer.canIterate = false;
            JavaRenderer.position.balls.add(new Ball(num[0],!white));
            JavaRenderer.canIterate = true;

            if (checkEnd(bitBoard))
                break;

        }
    }

    static boolean checkEnd(BitBoard bitBoard){
        if (bitBoard.win(true)) {
            JavaRenderer.position.end_game = Position.WHITE_WINS;
            return true;
        }
        if (bitBoard.win(false)){
            JavaRenderer.position.end_game = Position.BLACK_WINS;
            return true;
        }
        if ((bitBoard.white | bitBoard.black) == ~0L){
            JavaRenderer.position.end_game = Position.DRAW;
            return true;
        }
        return false;
    }
}
