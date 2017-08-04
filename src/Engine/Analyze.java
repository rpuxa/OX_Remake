package Engine;

import Jogl.Ball;
import Jogl.JavaRenderer;
import Jogl.Menu;

class AnalyzePosition implements Runnable {
        @Override
        public void run() {
            BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
            boolean isTurnWhite = JavaRenderer.position.isTurnWhite;

            int[] result;

            for (int depth = 2; true; depth++) {
                result = new Ai().alphaBetaStart(bitBoard, isTurnWhite ? 1 : 0, depth + (isTurnWhite ? 1 : 0));
                if (Menu.isInterrupted)
                    return;
                JavaRenderer.analyzed_column = result[0];
                JavaRenderer.score = result[1];
            }
        }
    }

public class Analyze implements Runnable {

    @Override
    public void run() {
        Thread analyzePosition = new Thread(new AnalyzePosition());
        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
        boolean isTurnWhite = JavaRenderer.position.isTurnWhite;
        analyzePosition.start();
        while (true) {
            while (JavaRenderer.column_chosen == null) {
                if (Menu.isInterrupted)
                    return;
                try {
                    Thread.sleep(99);
                } catch (InterruptedException ignored) {
                }
            }
            while (analyzePosition.isAlive()) {
                analyzePosition.interrupt();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            int n = JavaRenderer.column_chosen;
            JavaRenderer.column_chosen = null;
            bitBoard = BitBoard.makeMove(bitBoard, isTurnWhite, (byte) n);
            JavaRenderer.position.balls.add(new Ball(n, isTurnWhite));
            JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
            isTurnWhite = !isTurnWhite;
            JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
            JavaRenderer.analyzed_column = null;

            analyzePosition = new Thread(new AnalyzePosition());
            analyzePosition.start();

            if (Play.checkEnd(bitBoard))
                break;
        }
    }
}

