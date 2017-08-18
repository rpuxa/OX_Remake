package Editor.ChangePosition;

import Engine.BitBoard;
import PlayEngine.Play;
import Jogl.Ball;
import Jogl.JavaRenderer;
import Jogl.Menu;
import Jogl.Position;

public class Change implements Runnable {

    public static boolean isTurnWhite = true;
    public static boolean delete = false;
    public static BitBoard bitBoard;

    @Override
    public void run() {
        JavaRenderer.position = Position.make_position_from_position(JavaRenderer.sandbox_position);
        bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
        while (true){
            while (Play.checkEnd(bitBoard) && !delete || JavaRenderer.column_chosen == null) {
                if (Menu.isInterrupted) {
                    JavaRenderer.sandbox_position = Position.make_position_from_position(JavaRenderer.position);
                    JavaRenderer.game.clear();
                    JavaRenderer.moveNumber = 0;
                    return;
                }
                try {
                    Thread.sleep(98);
                } catch (InterruptedException ignored) {
                }
            }
            int n = JavaRenderer.column_chosen;
            JavaRenderer.column_chosen = null;
            if (delete) {
                bitBoard = BitBoard.unMakeMove(bitBoard, (byte) n);
                removeBall(n);
            } else {
                bitBoard = BitBoard.makeMove(bitBoard, isTurnWhite, (byte) n);
                JavaRenderer.position.balls.add(new Ball(n, isTurnWhite));
            }
            JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
        }
    }

    private static void removeBall(int n){
        for (int i = 135; i >= 15 ; i-=30) {
            for (int j = 0; j < JavaRenderer.position.balls.size(); j++) {
                Ball ball = JavaRenderer.position.balls.get(j);
                if (n == ball.column && Math.round(ball.z * 100) == i) {
                    JavaRenderer.position.balls.remove(j);
                    return;
                }
            }
        }
    }
}
