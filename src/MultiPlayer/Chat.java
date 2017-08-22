package MultiPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class Chat extends JPanel {

    private JTextArea textChat;
    private JTextArea textMessage;
    private static final String ENTER_MESSAGE = "Введите сообщение...";
    public static Profile myProfile;
    public Profile opponentProfile;
    private Boolean lastMessageFromOpponent = null;
    private ObjectOutputStream out;

    Chat() {
        textChat = new JTextArea("", 19, 17);
        textChat.setLineWrap(true);
        textChat.setEnabled(false);
        textChat.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
        JScrollPane pane1 = new JScrollPane(textChat);
        textMessage = new JTextArea(ENTER_MESSAGE, 3, 20);
        textMessage.setLineWrap(true);
        textMessage.setEnabled(false);
        textChat.setDisabledTextColor(new Color(0, 0, 0));
        textMessage.setDisabledTextColor(new Color(131, 131, 131));
        textMessage.setForeground(new Color(131, 131, 131));
        textMessage.addCaretListener(e -> {
            if (Objects.equals(ENTER_MESSAGE, textMessage.getText())) {
                try {
                    textMessage.setText("");
                    textMessage.setForeground(new Color(0, 0, 0));
                } catch (Exception ignore) {
                }
            }
        });
        textMessage.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER && !Objects.equals(textMessage.getText(), "")) {
                    try {
                        out.writeObject(new ServerCommand(textMessage.getText(), MultiPlayer.ConnectServer.CHAT_MESSAGE));
                        out.flush();
                        textChat.append(((lastMessageFromOpponent == null || lastMessageFromOpponent) ? myProfile.nick + ":\n" : "") + textMessage.getText());
                        lastMessageFromOpponent = false;
                        textMessage.setText("");
                    } catch (Exception ignore) {
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        textMessage.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (Objects.equals(textMessage.getText(), "")) {
                    textMessage.setText(ENTER_MESSAGE);
                    textMessage.setEnabled(false);
                    textMessage.setForeground(new Color(131, 131, 131));
                }
            }
        });

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                textMessage.setEnabled(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        JScrollPane pane2 = new JScrollPane(textMessage);
        add(pane1, BorderLayout.CENTER);
        add(pane2, BorderLayout.CENTER);

        setSize(241, 510);
        setVisible(false);
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public void setLoc(Dimension d) {
        super.setBounds((int)d.getWidth()-241,(int)d.getHeight()-510,241,510);
    }

    void setOpponentProfile(Profile profile) {
        this.opponentProfile = profile;
        textChat.setText("");
        textMessage.setText("");
        textMessage.setEnabled(false);
        textMessage.setText(ENTER_MESSAGE);
        textMessage.setEnabled(false);
        textMessage.setForeground(new Color(131, 131, 131));
        setVisible(true);
    }

    void showMessage(String massage) {
        textChat.append(((lastMessageFromOpponent == null || !lastMessageFromOpponent) ? opponentProfile.nick + ":\n" : "") + massage);
        lastMessageFromOpponent = true;
    }
}
