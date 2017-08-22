package MultiPlayer;

import Jogl.JavaRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Versus extends JPanel{

    private JLabel wMoves;
    private JLabel bMoves;
    private Profile wProfile;
    private Profile bProfile;
    private JLabel wRating;
    private JLabel bRating;
    private JLabel wNick;
    private JLabel bNick;
    private JLabel wPhoto_label;
    private JLabel bPhoto_label;

    Versus() {
        create();
    }

    private void create() {
        setLayout(null);
        setSize(241, 200);

        JLabel white = new JLabel("Белые:");
        white.setFont(new Font("Times New Roman", Font.ITALIC, 13));

        JPanel wPhoto = new JPanel();
        wPhoto_label = new JLabel();
        wPhoto.add(wPhoto_label).setBounds(10,10,27,30);

        JLabel black = new JLabel("Черные:");
        black.setFont(new Font("Times New Roman", Font.ITALIC, 13));

        JPanel bPhoto = new JPanel();
        bPhoto_label = new JLabel();
        bPhoto.add(bPhoto_label).setBounds(10,10,27,30);

        wNick = new JLabel();
        wNick.setFont(new Font("Times New Roman", Font.BOLD, 27));
        wNick.setForeground(new Color(0, 0, 0xFF));
        wNick.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bNick = new JLabel();
        bNick.setFont(new Font("Times New Roman", Font.BOLD, 27));
        bNick.setForeground(new Color(0, 0, 0xFF));
        bNick.setCursor(new Cursor(Cursor.HAND_CURSOR));

        wRating = new JLabel();
        bRating = new JLabel();

        wMoves = new JLabel("Ходят белые");
        bMoves = new JLabel("Ходят черные");
        bMoves.setVisible(false);

        JLabel vs = new JLabel("VS");
        vs.setFont(new Font("Times New Roman", Font.BOLD, 50));
        vs.setForeground(new Color(255, 72, 0));

        add(wPhoto);
        add(white);
        add(bPhoto);
        add(black);
        add(wNick);
        add(bNick);
        add(wRating);
        add(bRating);
        add(wMoves);
        add(bMoves);
        add(vs);


        wPhoto.setBounds(10, 15, 70, 70);
        white.setBounds(10, 3, 70, 15);
        bPhoto.setBounds(10, 115, 70, 70);
        black.setBounds(10, 187, 70, 15);
        wNick.setBounds(90, 15, 150, 30);
        bNick.setBounds(90, 155, 150, 30);
        wRating.setBounds(90, 40, 80, 30);
        bRating.setBounds(90, 135, 80, 30);
        wMoves.setBounds(150, 40, 90, 30);
        bMoves.setBounds(150, 135, 90, 30);
        vs.setBounds(90, 27, 150, 150);

        setVisible(false);

        wNick.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openProfile(wProfile);
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

        bNick.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    openProfile(bProfile);
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

    void swapMove(){
        if (JavaRenderer.position.isTurnWhite) {
            wMoves.setVisible(true);
            bMoves.setVisible(false);
        } else {
            wMoves.setVisible(false);
            bMoves.setVisible(true);
        }
    }

    void setProfiles(Profile profile1, Profile profile2){
        wProfile = profile1;
        bProfile = profile2;
        wRating.setText("" + wProfile.rating);
        bRating.setText("" + bProfile.rating);
        bNick.setText("" + bProfile.nick);
        wNick.setText("" + wProfile.nick);

        ImageIcon wImage = wProfile.photo;
        if (wImage == null)
            wImage = new ImageIcon("Images/no_photo.jpg");
        wImage = new ImageIcon(wImage.getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT));

        wPhoto_label.setIcon(wImage);

        ImageIcon bImage = bProfile.photo;

        if (bImage == null)
            bImage = new ImageIcon("Images/no_photo.jpg");
        bImage = new ImageIcon(bImage.getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT));

        bPhoto_label.setIcon(bImage);
    }

    private void openProfile(Profile profile){
        new ProfileWindow(profile,null);
    }
}
