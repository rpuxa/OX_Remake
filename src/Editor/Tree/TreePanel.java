package Editor.Tree;

import Editor.Sandbox;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class TreePanel extends JPanel {

    static ArrayList<JLabel> moves;


    TreePanel(ArrayList<JLabel> moves){
        setBackground(Color.gray);
        TreePanel.moves = moves;
        setLayout(null);
        setSize(241,510);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Деверо ходов:");
        JButton back0 = new JButton("<");
        JButton back1 = new JButton("<<");
        JButton next0 = new JButton(">");
        JButton next1 = new JButton(">>");

        for (JLabel move : moves)
            panel.add(move);
        add(back1);
        back1.setBounds(5,480,53,25);
        add(back0);
        back0.setBounds(63,480,53,25);
        add(next0);
        next0.setBounds(121,480,53,25);
        add(next1);
        next1.setBounds(179,480,53,25);
        add(panel);
        panel.setBounds(5,20,231,460);
        add(label);
        label.setBounds(5,0,100,20);

        back0.addActionListener(e -> Sandbox.tree.setCursor(MovesTree.CURSOR_BACK));
        next0.addActionListener(e -> Sandbox.tree.setCursor(MovesTree.CURSOR_NEXT));
        back1.addActionListener(e -> Sandbox.tree.setCursor(MovesTree.CURSOR_BEGIN));
        next1.addActionListener(e -> Sandbox.tree.setCursor(MovesTree.CURSOR_END));
    }
}
