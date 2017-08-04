package ChangePosition;

import Engine.BitBoard;
import Jogl.Ball;
import Jogl.JavaRenderer;
import Jogl.Menu;
import Jogl.Position;

public class Change implements Runnable {

    public static boolean isTurnWhite = true;

    @Override
    public void run() {
        JavaRenderer.position = Position.make_position_from_position(JavaRenderer.start_position);
        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
        while (true){
            while (JavaRenderer.column_chosen == null) {
                if (Menu.isInterrupted)
                    JavaRenderer.start_position = Position.make_position_from_position(JavaRenderer.position);
                    return;
                }
                try {
                    Thread.sleep(98);
                } catch (InterruptedException ignored) {
                }

            int n = JavaRenderer.column_chosen;
            JavaRenderer.column_chosen = null;

            bitBoard = BitBoard.makeMove(bitBoard, isTurnWhite, (byte) n);
            JavaRenderer.position.balls.add(new Ball(n, isTurnWhite));
            JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);

        }
    }
}
