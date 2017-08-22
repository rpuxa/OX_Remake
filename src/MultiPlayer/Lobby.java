package MultiPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Lobby extends JPanel {

    Lobby(ArrayList<Player> players, long id) {
        setSize(310,300);
        setLayout(null);
        JLabel online = new JLabel("Игроки онлайн:");
        online.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        JButton updateList = new JButton("Обновить");
        add(updateList);
        add(online);
        online.setBounds(0,0,200,20);
        updateList.setBounds(210,2,90,20);

        int number = 1;
        for (Player player : players) {
            JLabel rating = new JLabel("" + player.getRating());
            rating.setFont(new Font("Times New Roman", Font.PLAIN, 17));
            JLabel name = new JLabel(player.getName());
            name.setCursor(new Cursor(Cursor.HAND_CURSOR));
            name.setFont(new Font("Times New Roman", Font.BOLD, 20));
            JLabel invite = new JLabel("пригласить");
            invite.setCursor(new Cursor(Cursor.HAND_CURSOR));
            invite.setFont(new Font("Times New Roman", Font.BOLD, 15));
            invite.setForeground(Color.blue);
            add(rating);
            add(name);
            if (player.getId() != id)
                add(invite);
            rating.setBounds(10,20+25*number,50,25);
            name.setBounds(60,20+25*number,150,25);
            invite.setBounds(210,20+25*number,100,25);

            invite.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new InviteWindow(player);
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

            name.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        ConnectServer.out.writeObject(new ServerCommand(player,ConnectServer.GET_PLAYER_PROFILE));
                        ConnectServer.out.flush();
                    } catch (Exception e1) {
                    }
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
            number++;
        }

        updateList.addActionListener(e -> {
            try {
                ConnectServer.out.writeObject(new ServerCommand(null,ConnectServer.UPDATE_PLAYER_LIST));
                ConnectServer.out.flush();
            } catch (Exception e1) {
            }
        });
    }
}
