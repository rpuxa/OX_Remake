package MultiPlayer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.ObjectOutputStream;

public class ProfileWindow{

    ProfileWindow(Profile profile, ObjectOutputStream out) {
        JFrame frame = new JFrame("Профиль игрока " + profile.nick);

        final Dimension screenSize = frame.getToolkit().getScreenSize();

        frame.setResizable(false);

        frame.setSize(400, 300);

        final int centerX = screenSize.width / 2, centerY = screenSize.height / 2;
        frame.setLocation(centerX - 300 / 2, centerY - 300 / 2);

        Container contentPane = frame.getContentPane();

        contentPane.setLayout(null);

        ImageIcon image = profile.photo;

        if (image == null)
            image = new ImageIcon("Images/no_photo.jpg");
        image = new ImageIcon(image.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));

        JPanel photo = new JPanel();
        JLabel photo_label = new JLabel(image);
        photo.add(photo_label).setBounds(10,10,27,30);

        JLabel linked_label = new JLabel("изменить");
        linked_label.setFont(new Font("Times New Roman", Font.BOLD, 15));
        linked_label.setForeground(new Color(0, 0, 0xFF));
        linked_label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel nick = new JLabel(profile.nick);
        nick.setFont(new Font("Times New Roman", Font.BOLD, 27));
        nick.setForeground(new Color(119, 16, 200));

        JLabel date = new JLabel("Дата регистрации: " + profile.date_registration);

        JLabel rating_label = new JLabel("Рейтинг ЭЛО: ");
        JLabel rating = new JLabel("" + profile.rating);
        rating.setFont(new Font("Times New Roman", Font.BOLD, 40));
        JLabel statistic = new JLabel("Статистика:");
        statistic.setFont(new Font("Times New Roman", Font.BOLD, 25));

        JLabel wins_label = new JLabel("Победы:");
        JLabel draws_label = new JLabel("Ничьи:");
        JLabel loses_label = new JLabel("Поражения:");

        JLabel wins = new JLabel("" + profile.wins);
        wins.setFont(new Font("Times New Roman", Font.BOLD, 40));
        JLabel draws = new JLabel("" + profile.draws);
        draws.setFont(new Font("Times New Roman", Font.BOLD, 40));
        JLabel loses = new JLabel("" + profile.loses);
        loses.setFont(new Font("Times New Roman", Font.BOLD, 40));

        contentPane.add(photo);
        if (out != null)
            contentPane.add(linked_label);
        contentPane.add(nick);
        contentPane.add(date);
        contentPane.add(rating_label);
        contentPane.add(rating);
        contentPane.add(statistic);
        contentPane.add(wins_label);
        contentPane.add(draws_label);
        contentPane.add(loses_label);
        contentPane.add(wins);
        contentPane.add(draws);
        contentPane.add(loses);

        photo.setBounds(20, 20, 120, 120);
        linked_label.setBounds(45, 145, 100, 20);
        nick.setBounds(170, 20, 190, 40);
        date.setBounds(170, 50, 190, 40);
        rating_label.setBounds(170, 100, 100, 40);
        rating.setBounds(270, 75, 100, 80);
        statistic.setBounds(127, 155, 200, 40);
        wins_label.setBounds(30, 180, 100, 40);
        draws_label.setBounds(174, 180, 100, 40);
        loses_label.setBounds(310, 180, 100, 40);
        wins.setBounds(30, 210, 100, 40);
        draws.setBounds(174, 210, 100, 40);
        loses.setBounds(310, 210, 100, 40);

        linked_label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                UIManager.put(
                        "FileChooser.saveButtonText", "Сохранить");
                UIManager.put(
                        "FileChooser.cancelButtonText", "Отмена");
                UIManager.put(
                        "FileChooser.fileNameLabelText", "Наименование файла");
                UIManager.put(
                        "FileChooser.filesOfTypeLabelText", "Типы файлов");
                UIManager.put(
                        "FileChooser.lookInLabelText", "Директория");
                UIManager.put(
                        "FileChooser.saveInLabelText", "Сохранить в директории");
                UIManager.put(
                        "FileChooser.folderNameLabelText", "Путь директории");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Картинки(*.png, *.jpg)", "png", "jpg");
                JFileChooser fileOpen = new JFileChooser();
                fileOpen.setFileFilter(filter);
                int ret = fileOpen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = fileOpen.getSelectedFile();
                        ImageIcon image = new ImageIcon(file.getAbsolutePath());
                        image = new ImageIcon(image.getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
                        profile.photo = image;
                        photo_label.setIcon(image);
                        out.writeObject(new ServerCommand(profile,ConnectServer.UPDATE_PROFILE));
                        out.flush();
                    } catch (Exception e1){
                        JOptionPane.showMessageDialog(frame, "Ошибка в загрузке фото", "Профиль", JOptionPane.ERROR_MESSAGE);
                    }
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

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
          frame.setVisible(false);
          frame.dispose();
            }
        });

        frame.setVisible(true);
    }


}
