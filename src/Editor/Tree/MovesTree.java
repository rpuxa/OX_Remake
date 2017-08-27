package Editor.Tree;

import Jogl.Position;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class MovesTree implements Serializable {
    private Move firstMove;
    private ArrayList<Integer> cursor = new ArrayList<>();
    private static final int NEXT_MOVE = 1;
    private static final int ALTERNATIVE_MOVE = 2;
    private static final int CURSOR_BACK = 0;
    private static final int CURSOR_NEXT = 1;

    public static void main(String[] args) {
        MovesTree movesTree = new MovesTree(Position.make_position_empty(true,true));
        movesTree.add(Position.make_position_empty(true,true));
        movesTree.add(Position.make_position_empty(true,true));
        movesTree.add(Position.make_position_empty(true,true));
        movesTree.setCursor(CURSOR_BACK);
        movesTree.add(Position.make_position_empty(true,true));
        movesTree.setCursor(CURSOR_BACK);
        movesTree.add(Position.make_position_empty(true,true));
    }

    public MovesTree(Position startPosition) {
        firstMove = new Move(startPosition);
    }

    public void add(Position position) {
        Move move = getMove(cursor);
        if (move.getNexMove() == null) {
            cursor.add(NEXT_MOVE);
            move.setNexMove(new Move(Position.make_position_from_position(position)));
        } else {
            cursor.add(ALTERNATIVE_MOVE);
            while (move.getAlternativeMove() != null) {
                move = move.getAlternativeMove();
                cursor.add(ALTERNATIVE_MOVE);
            }
            move.setAlternativeMove(new Move(Position.make_position_from_position(position)));
        }
    }

    public void setCursor(int action) {
        if (action == CURSOR_BACK) {
            try {
                cursor.remove(cursor.size() - 1);
            } catch (IndexOutOfBoundsException e) {
            }
        } else if (action == CURSOR_NEXT) {
            ArrayList<Integer> cursor = new ArrayList<>(this.cursor);
            cursor.add(NEXT_MOVE);
            if (getMove(cursor) != null)
                this.cursor = cursor;
        }
    }

    private Move getMove(ArrayList<Integer> cursor){
        Move move = firstMove;
        for (int i : cursor)
            if (i == NEXT_MOVE)
                move = move.getNexMove();
            else if (i == ALTERNATIVE_MOVE)
                move = move.getAlternativeMove();
        return move;
    }
}

class Move implements Serializable {
    private Position currentPos;
    private Move nexMove, alternativeMove;

    public Move(Position currentPos) {
        this.currentPos = currentPos;
    }

    public Move[] toArray() {
        return new Move[]{alternativeMove, nexMove};
    }

    public Position getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(Position currentPos) {
        this.currentPos = currentPos;
    }

    public Move getNexMove() {
        return nexMove;
    }

    public void setNexMove(Move nexMove) {
        this.nexMove = nexMove;
    }

    public Move getAlternativeMove() {
        return alternativeMove;
    }

    public void setAlternativeMove(Move alternativeMove) {
        this.alternativeMove = alternativeMove;
    }

    private String positionsToCordinates(Position pos0, Position pos1, int number) {
        long black = pos0.bitBoard.black ^ pos1.bitBoard.black,
                white = pos0.bitBoard.white ^ pos1.bitBoard.white;
        assert black == 0 || white == 0;
        String move;
        if (white == 0)
            move = "''";
        else
            move = "'";
        long mask = black | white;
        int bit = (int) Math.round(Math.log(mask) / Math.log(2));
        int column = bit & 15;
        char letter = (char) ('A' + column >> 2);
        char num = (char) ('1' + (3 - column & 4));
        return "" + ((number + 1)/2) + move + " " + letter + num + ", ";
    }

    private static final Font font = new Font("Times New Roman", Font.BOLD, 15);

    public ArrayList<JLabel> toJLabelsArrayList(Position lastPos, int number) {
        ArrayList<JLabel> list = new ArrayList<>();
        JLabel label = new JLabel(positionsToCordinates(lastPos, currentPos, number));
        label.setFont(font);
        list.add(label);
        if (getAlternativeMove() != null) {
            label = new JLabel(" (");
            label.setFont(font);
            list.add(label);
            list.addAll(getAlternativeMove().toJLabelsArrayList(currentPos, number));
            label = new JLabel(")");
            label.setFont(font);
            list.add(label);
        }
        if (getNexMove() != null)
            list.addAll(getNexMove().toJLabelsArrayList(currentPos, number + 1));
        return list;
    }
}


