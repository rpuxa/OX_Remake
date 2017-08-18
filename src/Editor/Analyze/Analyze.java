package Editor.Analyze;

import Editor.Sandbox;
import Engine.Ai;
import Engine.BitBoard;
import Jogl.JavaRenderer;
import Jogl.Menu;
import PlayEngine.Play;

public class Analyze implements Runnable {

    @Override
    public void run() {

        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
        boolean isTurnWhite = JavaRenderer.position.isTurnWhite;

        int[] result;

        if (!Play.checkEnd(JavaRenderer.position.bitBoard))
            for (int depth = 2; depth < 40; depth++) {
                result = new Ai(isTurnWhite).alphaBetaStart(bitBoard, isTurnWhite ? 1 : 0, depth + (isTurnWhite ? 1 : 0));
                if (Menu.isInterrupted || Sandbox.analyze_interrupt)
                    return;
                JavaRenderer.analyzed_column = result[0];
                JavaRenderer.score = result[1];
                JavaRenderer.depth = depth;
            }
    }
}

