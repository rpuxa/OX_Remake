package PlayEngine;

import javax.swing.*;
import java.awt.*;
import Jogl.Menu;

public class ChoseDif extends JFrame {

    private int depth = 2;
    private int time = 0;
    private int color = 0;

    public ChoseDif(boolean startPosition){
        super("Игра с движком");
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
        JLabel choseDif = new JLabel("Выберите сложность");
        choseDif.setFont(new Font("Times New Roman", Font.BOLD, 20));
        JComboBox<String> dif = new JComboBox<>(new String[]{"Очень лёгкий","Лёгкий","Средний","Сложный","Эксперт"});
        JButton play = new JButton("Играть");
        JButton cancel = new JButton("Отмена");

        contentPane.add(choseColor);
        contentPane.add(white);
        contentPane.add(black);
        contentPane.add(rand);
        contentPane.add(choseDif);
        contentPane.add(dif);
        contentPane.add(play);
        contentPane.add(cancel);

        choseColor.setBounds(75,10,200,20);
        white.setBounds(10,30,80,20);
        black.setBounds(105,30,80,20);
        rand.setBounds(200,30,80,20);
        choseDif.setBounds(55,60,200,20);
        dif.setBounds(10,90,280,20);
        play.setBounds(10,130,120,20);
        cancel.setBounds(170,130,120,20);

        white.addActionListener(e -> this.color = 1);
        black.addActionListener(e -> this.color = -1);
        rand.addActionListener(e -> this.color = 0);

        dif.addActionListener(e -> {
            JComboBox box = (JComboBox)e.getSource();
            switch ((String)box.getSelectedItem()){
                case "Очень лёгкий": {
                    depth = 2;
                    break;
                }
                case "Лёгкий":{
                    depth = 4;
                    break;
                }
                case "Средний":{
                    depth = 6;
                    time = 1000;
                    break;
                }
                case "Сложный": {
                    depth = 8;
                    time = 5000;
                    break;
                }
                case "Эксперт": {
                    time = 15000;
                    break;
                }
            }
        });

        play.addActionListener(e ->{
            Menu.play = new Thread(new Play(depth,time,this.color,startPosition));
            Menu.play.start();
            setVisible(false);
        });

        cancel.addActionListener(e -> {
            if (startPosition)
                new Thread(Menu.back::click).start();
            else
                new Thread(Menu.back_sandbox::click).start();
            setVisible(false);
        });

        setSize(300,200);
        setVisible(true);
    }

    public int[] getAll(){
        return new int[]{depth,time,color};
    }
}
