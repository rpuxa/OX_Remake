package Engine;

import Jogl.Ball;
import Jogl.JavaRenderer;
import Jogl.Menu;
import Jogl.Position;

class AnalyzePosition implements Runnable {
        @Override
        public void run() {
            BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
            boolean isTurnWhite = JavaRenderer.position.isTurnWhite;

            int[] result;

            for (int depth = 2; true; depth++) {
                result = new Ai(isTurnWhite).alphaBetaStart(bitBoard, isTurnWhite ? 1 : 0, depth + (isTurnWhite ? 1 : 0));
                if (Menu.isInterrupted || Analyze.analyze_interrupt)
                    return;
                JavaRenderer.analyzed_column = result[0];
                JavaRenderer.score = result[1];
                JavaRenderer.depth = depth;
                if (depth > 39)
                    return;
            }
        }
    }

public class Analyze implements Runnable {

    static boolean analyze_interrupt = false;

    @Override
    public void run() {
        JavaRenderer.position = Position.make_position_from_position(JavaRenderer.start_position);
        analyze_interrupt = false;
        Thread analyzePosition = new Thread(new AnalyzePosition());
        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
        boolean isTurnWhite = JavaRenderer.position.isTurnWhite;
        analyzePosition.start();
        while (true) {
            while (JavaRenderer.column_chosen == null) {
                if (Menu.isInterrupted) {
                    analyze_interrupt = false;
                    return;
                }
                try {
                    Thread.sleep(99);
                } catch (InterruptedException ignored) {
                }
            }
            while (analyzePosition.isAlive()) {
                analyze_interrupt = true;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            analyze_interrupt = false;

            int n = JavaRenderer.column_chosen;
            JavaRenderer.column_chosen = null;
            bitBoard = BitBoard.makeMove(bitBoard, isTurnWhite, (byte) n);
            JavaRenderer.position.balls.add(new Ball(n, isTurnWhite));
            JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
            JavaRenderer.position.human_plays_for_white = !JavaRenderer.position.human_plays_for_white;
            isTurnWhite = !isTurnWhite;
            JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
            Position.add_to_history_positions(JavaRenderer.position);
            JavaRenderer.analyzed_column = null;

            analyzePosition = new Thread(new AnalyzePosition());
            analyzePosition.start();

            if (Play.checkEnd(bitBoard)){
                JavaRenderer.analyzed_column = null;
                while (analyzePosition.isAlive()) {
                    analyze_interrupt = true;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
                analyze_interrupt = false;
                JavaRenderer.analyzed_column = null;
                return;
            }
        }
    }
}

