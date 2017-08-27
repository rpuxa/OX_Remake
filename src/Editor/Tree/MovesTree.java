package Editor.Tree;

import Editor.Sandbox;
import Jogl.JavaDia;
import Jogl.JavaRenderer;
import Jogl.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.ArrayList;

import static Editor.Tree.MovesTree.ALTERNATIVE_MOVE;
import static Editor.Tree.MovesTree.NEXT_MOVE;

public class MovesTree implements Serializable {
    private Move firstMove;
    private ArrayList<Integer> cursor = new ArrayList<>();
    static final int NEXT_MOVE = 1;
    static final int ALTERNATIVE_MOVE = 2;
    private static final int CURSOR_BACK = 0;
    private static final int CURSOR_NEXT = 1;
    public static TreePanel treePanel = new TreePanel(new ArrayList<>());

    public MovesTree(Position startPosition) {
        firstMove = new Move(startPosition);
        update();
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
        update();
    }

    public void update(){
        JavaDia.scroll_tree.remove(treePanel);
        treePanel = new TreePanel(getLabels());
        JavaDia.scroll_tree.add(treePanel);
    }

    public void setVisible(boolean bFlag){
        JavaDia.scroll_tree.setVisible(bFlag);
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

    public void setCursor(ArrayList<Integer> cursor) {
        this.cursor = new ArrayList<>(cursor);
        JavaRenderer.position = Position.make_position_from_position(getMove(cursor).getCurrentPos());
    }

    private Move getMove(ArrayList<Integer> cursor){
        if (cursor.isEmpty())
            return firstMove;
        Move move = firstMove;
        for (int i : cursor)
            if (i == NEXT_MOVE)
                move = move.getNexMove();
            else if (i == ALTERNATIVE_MOVE)
                move = move.getAlternativeMove();
        return move;
    }

    public ArrayList<JLabel> getLabels(){
        return firstMove.toJLabelsArrayList(firstMove.getCurrentPos(),1,true, true, new ArrayList<>());
    }
}

class Move implements Serializable {
    private Position currentPos;
    private Move nexMove, alternativeMove;

    public Move(Position currentPos) {
        this.currentPos = Position.make_position_from_position(currentPos);
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
        char letter = (char) ('A' + (column >>> 2));
        char num = (char) ('1' + (3 - (column % 4)));
        return ((number + 1)/2) + move + " " + letter + num;
    }

    private static final Font font = new Font("Times New Roman", Font.ITALIC, 18);
    private static final Font main_font = new Font("Times New Roman", Font.BOLD, 18);

    public ArrayList<JLabel> toJLabelsArrayList(Position lastPos, int number, boolean mainMove, boolean mainLine, ArrayList<Integer> cursor) {
        ArrayList<JLabel> list = new ArrayList<>();
        addLabel(list,(mainMove) ? "Start," : positionsToCordinates(lastPos, currentPos, number),mainLine, currentPos, cursor);
        if (getAlternativeMove() != null) {
            addLabel(list," (",false,null,null);
            ArrayList<Integer> cursor1 = new ArrayList<>(cursor);
            cursor1.add(ALTERNATIVE_MOVE);
            list.addAll(getAlternativeMove().toJLabelsArrayList(lastPos, number,false,false, cursor1));
            addLabel(list," )",false,null,null);
        }
        if (getNexMove() != null) {
            addLabel(list,", ",mainLine,null,null);
            ArrayList<Integer> cursor1 = new ArrayList<>(cursor);
            cursor1.add(NEXT_MOVE);
            list.addAll(getNexMove().toJLabelsArrayList(currentPos, number + 1, false,mainLine,cursor1));
        }
        return list;
    }

    private void addLabel(ArrayList<JLabel> list, String st, boolean mainLine, Position pos, ArrayList<Integer> cursor){
        JLabel label = new JLabel(st);
        if (mainLine)
            label.setFont(main_font);
        else
            label.setFont(font);
        label.setOpaque(true);
        if (pos != null){
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (JLabel label1 : TreePanel.moves)
                        label1.setBackground(new Color(238,238,238));
                    label.setBackground(new Color(208, 185, 42));
                    Sandbox.tree.setCursor(cursor);
                }
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}
            });
        }
        list.add(label);
    }
}


