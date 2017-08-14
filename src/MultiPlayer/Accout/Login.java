package MultiPlayer.Accout;

import MultiPlayer.ConnectServer;
import MultiPlayer.Profile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Login {

    static Object pass_plus_login;
    static JFrame frame;

    Login(int message){
         frame = new JFrame("Вход");
         frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        final Dimension screenSize = frame.getToolkit().getScreenSize();

        frame.setResizable(false);

        frame.setSize(320, 250);

        final int centerX = screenSize.width / 2, centerY = screenSize.height / 2;
        frame.setLocation(centerX - 320 / 2, centerY - 250 / 2);

         Container contentPane = frame.getContentPane();

         contentPane.setLayout(null);

         JLabel login = new JLabel("Логин");
         JTextField login_field = new JTextField(16);
         JLabel pass1 = new JLabel("Пароль");
         JPasswordField pass1_field = new JPasswordField(16);
         JButton ok = new JButton("Продолжить");
         JButton cancel = new JButton("Отмена");
         JLabel label = new JLabel("Если у вас нет аккаунта,");
         JLabel linked_label = new JLabel("создайте");
         linked_label.setFont(new Font("Times New Roman", Font.BOLD, 15));
         linked_label.setForeground(new Color(0, 0, 0xFF));
         linked_label.setCursor(new Cursor(Cursor.HAND_CURSOR));


         contentPane.add(login);
         contentPane.add(login_field);
         contentPane.add(pass1);
         contentPane.add(pass1_field);
         contentPane.add(ok);
         contentPane.add(cancel);
         contentPane.add(label);
         contentPane.add(linked_label);

         login.setBounds(20, 20, 100, 20);
         login_field.setBounds(100, 20, 180, 20);
         pass1.setBounds(20, 60, 100, 20);
         pass1_field.setBounds(100, 60, 180, 20);
         ok.setBounds(20, 140, 120, 20);
         cancel.setBounds(160, 140, 120, 20);
         label.setBounds(20, 180, 145, 20);
         linked_label.setBounds(165, 180, 120, 20);

         ok.addActionListener(new AbstractAction() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 if (login_field.getText().length() < 4 || login_field.getText().length() > 16){
                     JOptionPane.showMessageDialog(frame, "Логин может быть не больше 16 символов и не меньше 4", "Сетевая игра", JOptionPane.ERROR_MESSAGE);
                     return;
                 }

                 if (pass1_field.getPassword().length < 4) {
                     JOptionPane.showMessageDialog(frame, "Пароль слишком мленький", "Сетевая игра", JOptionPane.ERROR_MESSAGE);
                     return;
                 }

                pass_plus_login = Profile.loginPlusPass(login_field.getText(),pass1_field.getPassword());
             }
         });

         linked_label.addMouseListener(new MouseListener() {
             @Override
             public void mouseClicked(MouseEvent e) {
                 frame.setVisible(false);
                 NewAccount.frame.setVisible(true);
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

         cancel.addActionListener(new AbstractAction() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 LoginOrNewAccount.isCanceled = true;
             }
         });

         frame.setVisible(true);

         if (message == ConnectServer.UNCORRECTED_LOGIN_OR_PASSWORD)
             JOptionPane.showMessageDialog(frame, "Неправильно введен логин или пароль!", "Сетевая игра", JOptionPane.ERROR_MESSAGE);
         else if (message == ConnectServer.ACCOUNT_ALREADY_EXISTS)
             JOptionPane.showMessageDialog(frame, "Аккаунт уже существует, попробуйте войти", "Сетевая игра", JOptionPane.ERROR_MESSAGE);

         while (pass_plus_login == null && NewAccount.profile == null){
             if (LoginOrNewAccount.isCanceled) {
                 break;
             }
             try {
                 Thread.sleep(100);
             } catch (InterruptedException ignored) {
             }
         }

         frame.setVisible(false);
     }
}
