package MultiPlayer;

import Jogl.JavaDia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class Chat extends JFrame {

    private JPanel contents;
    private JScrollPane pane1;
    private JScrollPane pane2;
    private JTextArea textChat;
    private JTextArea textMessage;
    private static final String ENTER_MESSAGE = "Введите сообщение...";
    public static String myNick;
    private String opponentNick;
    private DataOutputStream out;

    Chat(DataOutputStream out) {
        super("Чат");
        this.out = out;
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        contents = new JPanel();

        textChat = new JTextArea("",11,20);
        textChat.setLineWrap(true);
        textChat.setEnabled(false);
        pane1 = new JScrollPane(textChat);
        textMessage = new JTextArea(ENTER_MESSAGE,3,20);
        textMessage.setLineWrap(true);
        textMessage.setEnabled(false);
        textChat.setDisabledTextColor(new Color(0,0,0));
        textMessage.setDisabledTextColor(new Color(131, 131, 131));
        textMessage.setForeground(new Color(131, 131, 131));
        textMessage.addCaretListener(e -> {
            if (Objects.equals(ENTER_MESSAGE, textMessage.getText())) {
                try {
                    textMessage.setText("");
                    textMessage.setSelectedTextColor(new Color(0, 0, 0));
                } catch (Exception ignore) {
                }
            }
        });
        textMessage.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER && !Objects.equals(textMessage.getText(), "")){
                    textChat.setText(textChat.getText() + myNick + ":\n"+ textMessage.getText());
                    try {
                    out.writeUTF(ConnectServer.CHAT_MESSAGE + textMessage.getText());
                    out.flush();
                    } catch (Exception ignore){
                        ignore.printStackTrace();
                    }

                    textMessage.setText("");
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

        textMessage.addMouseListener(new MouseListener() {
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

        pane2 = new JScrollPane(textMessage);
        contents.add(pane1, BorderLayout.CENTER);
        contents.add(pane2, BorderLayout.CENTER);
        contents.add(textMessage);

        setContentPane(contents);

        setSize(240, 250);

        setResizable(false);

        setUndecorated(true);

        setVisible(true);
    }

    void setOpponentNick(String nick) {
        this.opponentNick = nick;
    }

    void showMessage(String massage){
        textChat.setText(textChat.getText() + opponentNick + ":\n"+ massage);
    }

    static public void setMyNick(String nick){
        if (myNick == null){
            myNick = nick;
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Config/nick"))) {
                oos.writeObject(nick);
            } catch (Exception ignore) {
            }
        }
    }
}
