package Editor;

import Editor.Analyze.Analyze;
import Engine.BitBoard;
import Jogl.Ball;
import Jogl.JavaRenderer;
import Jogl.Menu;
import Jogl.Position;
import PlayEngine.Play;

public class Sandbox implements Runnable {

    public static boolean analyze_interrupt = false;
    public static boolean start_analyze = false;
    public static Thread analyzePosition = new Thread();

    @Override
    public void run() {
        JavaRenderer.position = Position.make_position_from_position(JavaRenderer.sandbox_position);
        analyze_interrupt = false;
        if (start_analyze) {
            analyzePosition = new Thread(new Analyze());
            analyzePosition.start();
        }
        Position.add_to_history_positions(JavaRenderer.position,true);
        while (true) {
            while (JavaRenderer.column_chosen == null) {
                if (Menu.isInterrupted) {
                    analyzeInterrupt();
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
            JavaRenderer.position.bitBoard = BitBoard.makeMove(JavaRenderer.position.bitBoard, JavaRenderer.position.isTurnWhite, (byte) n);
            JavaRenderer.position.balls.add(new Ball(n, JavaRenderer.position.isTurnWhite));
            JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
            JavaRenderer.position.human_plays_for_white = !JavaRenderer.position.human_plays_for_white;
            JavaRenderer.analyzed_column = null;

            if (start_analyze) {
                analyzePosition = new Thread(new Analyze());
                analyzePosition.start();
            }

            Play.checkEnd(JavaRenderer.position.bitBoard);
            Position.add_to_history_positions(JavaRenderer.position,false);


            if (Play.checkEnd(JavaRenderer.position.bitBoard)){
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

                while (Play.checkEnd(JavaRenderer.position.bitBoard)) {
                    if (Menu.isInterrupted) {
                        analyzeInterrupt();
                        return;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }

    private static void analyzeInterrupt(){
        while (analyzePosition.isAlive()) {
            analyze_interrupt = true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        analyze_interrupt = false;
        start_analyze = false;
    }
}
