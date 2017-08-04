package Engine;

import Jogl.Ball;
import Jogl.JavaRenderer;
import Jogl.Menu;
import Jogl.Position;

public class Play implements Runnable {

    public void run() {
        boolean first = true;
        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
        boolean white = JavaRenderer.position.human_plays_for_white;
        boolean isTurnWhite = JavaRenderer.position.isTurnWhite;
        while (true){
            if (!first || isTurnWhite == white) {
                System.out.println(0);
                while (JavaRenderer.column_chosen == null) {
                    if (Menu.isInterrupted)
                        return;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
                System.out.println(3);


                int n = JavaRenderer.column_chosen;
                JavaRenderer.column_chosen = null;
                bitBoard = BitBoard.makeMove(bitBoard, white, (byte) n);

                JavaRenderer.position.balls.add(new Ball(n, white));
                JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                JavaRenderer.position.isTurnWhite = ! JavaRenderer.position.isTurnWhite;

                if (checkEnd(bitBoard))
                    break;

                first = false;
            }

            System.out.println(1);
            int[] num = new Ai().bfs(bitBoard,2 + ((white) ? 0 : 1), white);
            System.out.println(2);

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
        return false;
    }
}
