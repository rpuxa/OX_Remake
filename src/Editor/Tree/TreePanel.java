package Editor.Tree;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TreePanel extends JPanel {

    static ArrayList<JLabel> moves;

    public TreePanel(ArrayList<JLabel> moves){
        TreePanel.moves = moves;
        setLayout(new FlowLayout());
        setSize(241,510);
        for (JLabel move : moves)
            add(move);

    }
}
