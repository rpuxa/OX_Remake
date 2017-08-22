package MultiPlayer;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class InviteWindow extends JFrame {


    private int side = 0;

    public InviteWindow(Player player) {
        super("Вызов игрока " + player.getName());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        final Dimension screenSize = getToolkit().getScreenSize();
        setResizable(false);
        final int centerX = screenSize.width / 2, centerY = screenSize.height / 2;
        setLocation(centerX - 200 / 2, centerY - 200 / 2);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);
        JLabel choseColor = new JLabel("Выберите цвет");
        choseColor.setFont(new Font("Times New Roman", Font.BOLD, 20));
        ButtonGroup color = new ButtonGroup();
        JRadioButton white = new JRadioButton("Белые");
        JRadioButton black = new JRadioButton("Черные");
        JRadioButton rand = new JRadioButton("Рандом",true);
        color.add(white);
        color.add(black);
        color.add(rand);
        JLabel choseTimer = new JLabel("Время на игру");
        choseTimer.setFont(new Font("Times New Roman", Font.BOLD, 20));

        JTextField min_text = new JTextField("05");
        min_text.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        min_text.setHorizontalAlignment(JTextField.RIGHT);
        min_text.setDocument(new PlainDocument(){
            String chars = "0123456789";
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if(chars.contains(str)){
                    if (getLength() < 2)
                    super.insertString( offs, str, a);
                }
            }
        });
        JLabel min_label = new JLabel("min");

        JTextField sec_text = new JTextField("00");
        sec_text.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        sec_text.setHorizontalAlignment(JTextField.RIGHT);
        sec_text.setDocument(new PlainDocument(){
            String chars = "0123456789";
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if(chars.contains(str)){
                    if (getLength() < 2)
                        super.insertString( offs, str, a);
                }
            }
        });
        JLabel sec_label = new JLabel("sec");

        JButton accept = new JButton("Пригласить");
        JButton cancel = new JButton("Отмена");

        contentPane.add(choseColor);
        contentPane.add(choseTimer);
        contentPane.add(white);
        contentPane.add(black);
        contentPane.add(rand);
        contentPane.add(min_text);
        contentPane.add(min_label);
        contentPane.add(sec_text);
        contentPane.add(sec_label);
        contentPane.add(accept);
        contentPane.add(cancel);

        choseColor.setBounds(75,5,200,20);
        white.setBounds(10,30,80,20);
        black.setBounds(105,30,80,20);
        rand.setBounds(200,30,80,20);
        choseTimer.setBounds(75,55,200,20);
        min_text.setBounds(10,80,30,20);
        min_label.setBounds(45,80,50,20);
        sec_text.setBounds(120,80,30,20);
        sec_label.setBounds(155,80,50,20);
        accept.setBounds(10,130,120,20);
        cancel.setBounds(170,130,120,20);

        accept.addActionListener(e -> {
            int min,sec;
            try {
                min = Integer.parseInt(min_text.getText());
                if (min == 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException e1) {
                JOptionPane.showMessageDialog(null, "Минуты должны быть не равны нулю", "Приглашение", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                sec = Integer.parseInt(sec_text.getText());
                if (sec > 59) {
                    JOptionPane.showMessageDialog(null, "Секунды должны быть меньше 60", "Приглашение", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e1) {
                sec = 0;
            }
            try{
            ConnectServer.out.writeObject(new ServerCommand(new Invite(60*min + sec,side,null,player.getId()),ConnectServer.INVITE_OPPONENT));
            ConnectServer.out.flush();
            this.setVisible(false);
            } catch (Exception e1){
            }
        });
        cancel.addActionListener(e -> this.setVisible(false));
        white.addActionListener(e -> side = Invite.WHITE);
        black.addActionListener(e -> side = Invite.BLACK);
        rand.addActionListener(e -> side = Invite.RANDOM);

        setSize(300,200);
        setVisible(true);
    }
}
