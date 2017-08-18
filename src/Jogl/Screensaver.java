package Jogl;

import Engine.Ai;
import Engine.BitBoard;

import java.util.Random;

import static PlayEngine.Play.checkEnd;

public class Screensaver implements Runnable {

    private static final Random random = new Random();

    @Override
    public void run() {
        JavaRenderer.speed = 0.0005;
        JavaRenderer.position = Position.make_position_empty(true,true);
        try {
            while (true) {
                BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
                boolean white = JavaRenderer.position.isTurnWhite;

                int[] num = new Ai(white).bfs(bitBoard, 2 * random.nextInt(2) + 2 + ((white) ? 1 : 0), !white,0);

                bitBoard = BitBoard.makeMove(bitBoard, white, (byte) num[0]);

                JavaRenderer.position.balls.add(new Ball(num[0], white));
                JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;

                while (!JavaRenderer.position.allOnGround()) {
                    if (Menu.isInterrupted)
                        return;
                    Thread.sleep(100);
                }

                if (checkEnd(bitBoard)) {
                    Thread.sleep(2000);
                    if (Menu.isInterrupted)
                        return;
                    JavaRenderer.position = Position.make_position_empty(true,true);
                }
            }
        } catch (InterruptedException ignored) {
        }
    }
}
