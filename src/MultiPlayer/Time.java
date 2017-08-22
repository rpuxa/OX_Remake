package MultiPlayer;

import javax.swing.*;
import java.awt.*;

public class Time extends JPanel {

    JLabel timeWhiteLabel;
    JLabel timeBlackLabel;
    private volatile long timeWhite;
    private volatile long timeBlack;
    private boolean moveWhite;
    private boolean start = false;

    Time() {
        setLayout(null);
        setSize(420, 110);
        setBackground(new Color(50, 50, 50));

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        panel2.setBackground(new Color(0,0,0));
        timeWhiteLabel = new JLabel("10:20:3");
        timeBlackLabel = new JLabel("10:20:3");
        timeWhiteLabel.setFont(new Font("Times New Roman", Font.BOLD, 50));
        timeBlackLabel.setForeground(new Color(255,255,255));
        timeBlackLabel.setFont(new Font("Times New Roman", Font.BOLD, 50));
        timeWhiteLabel.setForeground(new Color(0,0,0));

        panel1.add(timeWhiteLabel);
        panel1.setSize(200,100);
        add(panel1);

        panel2.add(timeBlackLabel);
        panel2.setSize(200,100);
        add(panel2);


        panel1.setBounds(10, 10, 200, 100);
        panel2.setBounds(210, 10, 200, 100);

         setVisible(false);
    }

    public void setTime(long time, boolean white) {
        long microSeconds = (time / 100) % 10,
                seconds = (time / 1000) % 60,
                minutes = time / 1000 / 60;
        String st_seconds = ((seconds < 10) ? "0" : "") + seconds;
        String st_minutes = ((minutes < 10) ? "0" : "") + minutes;
        String st_time = st_minutes + ":" + st_seconds + ":" + microSeconds;
        if (white)
            timeWhiteLabel.setText(st_time);
        else
            timeBlackLabel.setText(st_time);
    }

    public void setBeginTime(int seconds) {
        int time = seconds * 1000;
        setTime(time, true);
        setTime(time, false);
        timeBlack = time;
        timeWhite = time;
        this.setVisible(true);
    }

    public void startTime(boolean white, boolean ourTime) {
        moveWhite = white;
        long timeNow = System.currentTimeMillis();
        while (true) {
            boolean lessThanZero = ((white) ? timeWhite : timeBlack) - (System.currentTimeMillis() - timeNow) <= 0;
            if (!lessThanZero)
                setTime(((white) ? timeWhite : timeBlack) - (System.currentTimeMillis() - timeNow), white);
            if (moveWhite != white || !start)
                break;
            if (lessThanZero && ourTime){
                JLabel label = ((white) ? timeWhiteLabel : timeBlackLabel);
                label.setText("Zeit");
                ConnectServer.zeit();
                return;
            }
            try {
                Thread.sleep(99);
            } catch (InterruptedException e) {
            }
        }
        if (ourTime)
            if (white)
                timeWhite -= System.currentTimeMillis() - timeNow;
            else
                timeBlack -= System.currentTimeMillis() - timeNow;
    }

    public void setLoc(Dimension d) {
        this.setBounds((int)(d.getWidth() / 2 - this.getWidth() / 2),(int) (d.getHeight() - this.getHeight()), this.getWidth(), this.getHeight());
    }

    @Override
    public void setVisible(boolean b){
        super.setVisible(b);
        start = b;
        if (!b){
            setTime(0, true);
            setTime(0, false);
        }
    }

    public long getTimeBlack() {
        return timeBlack;
    }

    public long getTimeWhite() {
        return timeWhite;
    }

    public void setTimeWhite(long timeWhite) {
        this.timeWhite = timeWhite;
        setTime(timeWhite,true);
    }

    public void setTimeBlack(long timeBlack) {
        this.timeBlack = timeBlack;
        setTime(timeBlack,false);
    }

    public void stop(){
        start = false;
    }
}
