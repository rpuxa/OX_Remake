package Jogl;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;

import MultiPlayer.AudioChat.RecordVoice;
import MultiPlayer.ConnectServer;
import MultiPlayer.ServerCommand;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;

import static Jogl.JavaRenderer.fastMode;
import static Jogl.JavaRenderer.isScreensaverOn;
import static Jogl.JavaRenderer.speed;
import static MultiPlayer.AudioChat.RecordVoice.targetDataLine;
import static MultiPlayer.ConnectServer.*;


public class JavaDia implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    public static Thread displayT = new Thread(new JavaDia());
    static boolean bQuit = false;
    private static Point mouse_start_cords;
    private static int button = 0;
    private static Double mouse_start_angle;
    private static GLCanvas canvas = new GLCanvas();
    private static final double pi = 3.141592;
    public static JFrame frame;
    static JFrame loading;
    public static JPanel scroll_lobby = new JPanel();
    static {
        new Thread(() -> {
            loading = new JFrame();
            loading.add(new JLabel(new ImageIcon("Images/loading.gif")));
            loading.setUndecorated(true);
            loading.setBackground(new Color(0, 0, 0, 0));
            final Dimension screenSize = loading.getToolkit().getScreenSize();
            final int centerX = screenSize.width / 2, centerY = screenSize.height / 2;
            loading.setLocation(centerX - 500 / 2, centerY - 500 / 2);
            loading.setSize(500, 500);
            loading.setAlwaysOnTop(true);
            //loading.setVisible(true);
        }).start();
    }

    public static void main(String[] args) {
    }

    public void run() {
        frame = new JFrame("OX3D_OpenGL");
        int size = frame.getExtendedState();
        canvas.addGLEventListener(new JavaRenderer());
        frame.setLayout(null);
        frame.add(ConnectServer.chat);
        scroll_lobby.add(ConnectServer.lobby);
        ConnectServer.lobby.setBounds(0,0,310,300);
        scroll_lobby.setLayout(null);
        scroll_lobby.setSize(310,300);
        scroll_lobby.setVisible(false);
        frame.add(scroll_lobby);
        frame.add(ConnectServer.time);
        frame.add(ConnectServer.versus);
        frame.add(canvas);
        ConnectServer.versus.setBounds(0,0,241,200);
        size |= Frame.MAXIMIZED_BOTH;
        frame.setExtendedState(size);
        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                canvas.setBounds(0,0,JavaDia.frame.getWidth(),JavaDia.frame.getHeight());
                try{
                    chat.setLoc(JavaDia.frame.getSize());
                } catch (NullPointerException ignore){
                }
                try{
                    time.setLoc(JavaDia.frame.getSize());
                } catch (NullPointerException ignore){
                }
                try{
                    scroll_lobby.setLocation(frame.getWidth()-scroll_lobby.getWidth(),0);
                } catch (NullPointerException ignore){
                }
                try {
                    Menu.resign.setLocation(frame.getHeight(),.1);
                    Menu.offer.setLocation(frame.getHeight(),.2);
                    Menu.rematch.setLocation(frame.getHeight(),.1);
                } catch (Exception e1) {
                }
            }
            @Override
            public void componentMoved(ComponentEvent e) {}
            @Override
            public void componentShown(ComponentEvent e) {}
            @Override
            public void componentHidden(ComponentEvent e) {}
        });
        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                bQuit = true;
                System.exit(0);
            }
        });

        canvas.requestFocus();
        frame.setMinimumSize(new Dimension(800,600));
        Menu.screensaver = new Thread(new Screensaver());
        Menu.screensaver.start();
        frame.setVisible(true);
        while( !bQuit )
            canvas.display();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R && ConnectServer.opp_found && !RecordVoice.pressed_R) {
            RecordVoice.pressed_R = true;
            new Thread(RecordVoice::record).start();
        }

        if (e.getKeyCode() == KeyEvent.VK_F) {
            fastMode ^= true;
            if (!isScreensaverOn && !Menu.changingPos)
            if (fastMode)
                speed = 1;
            else
                speed = .001;
            Menu.sounds("click");
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R && ConnectServer.opp_found) {
            RecordVoice.pressed_R = false;
            targetDataLine.stop();
            targetDataLine.close();
            try {
                File outputFile = new File("Sounds/rec.wav");
                byte[] fileInArray = new byte[(int)outputFile.length()];
                FileInputStream f = new FileInputStream(outputFile);
                f.read(fileInArray);
                ConnectServer.out.writeObject(new ServerCommand(fileInArray, ConnectServer.CHAT_AUDIO));
                ConnectServer.out.flush();
            } catch (Exception ignored){
                ignored.printStackTrace();
            }
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    private static long when_click;

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Point point = e.getPoint();
            JavaRenderer.mouse_click = new double[2];
            JavaRenderer.mouse_click[0] = 2*point.getX()/canvas.getSize().getWidth()-1;
            JavaRenderer.mouse_click[1] = 2*(canvas.getSize().getHeight() - point.getY())/canvas.getSize().getHeight()-1;
            if (!fastMode) {
                if (e.getWhen() - when_click <= 500) {
                    JavaRenderer.mouse_double_click = true;
                    when_click = 0;
                } else
                    when_click = e.getWhen();
            } else
                JavaRenderer.mouse_double_click = true;
        }

    }

    public void mousePressed(MouseEvent e){
        button = e.getButton();
        if (button == MouseEvent.BUTTON1)
            JavaRenderer.mouse_press = true;
    }

    public void mouseReleased(MouseEvent e){
        mouse_start_angle = null;
        mouse_start_cords = null;
        if (button == MouseEvent.BUTTON1)
            JavaRenderer.mouse_press = false;
        button = 0;
    }

    public void mouseEntered(MouseEvent e){
    }

    public void mouseExited(MouseEvent e){
        JavaRenderer.mouse_at = null;
    }

    public void mouseDragged(MouseEvent e){
        Point mouse_now = e.getPoint();
        Dimension size = canvas.getSize();
        mouse_now.y = (int) (size.getHeight() -mouse_now.getY());
        if (mouse_start_cords != null && button == MouseEvent.BUTTON1) {
            JavaRenderer.AbsAngleY += ((mouse_now.getX() - mouse_start_cords.getX()) / size.getWidth())*JavaRenderer.sensitivity*pi/180;
            JavaRenderer.AbsAngleX -= ((mouse_now.getY() - mouse_start_cords.getY()) / size.getHeight())*JavaRenderer.sensitivity*pi/180;
        }
        if (mouse_start_angle !=null && button == MouseEvent.BUTTON3)
            JavaRenderer.AbsAngleZ += get_angle(mouse_now,size) - mouse_start_angle;
        mouse_start_angle = get_angle(mouse_now,size);
        mouse_start_cords = new Point(e.getPoint());
        mouse_start_cords.y = (int) (size.getHeight() - mouse_start_cords.getY());
    }

    public void mouseMoved(MouseEvent e){
        JavaRenderer.mouse_at = new Double[2];
        Point point = e.getPoint();
        JavaRenderer.mouse_cords_viewport = new Point(point.x, (int) (canvas.getSize().getHeight()-point.y-1));
        JavaRenderer.mouse_at[0] = 2*point.getX()/canvas.getSize().getWidth()-1;
        JavaRenderer.mouse_at[1] = 2*(canvas.getSize().getHeight() - point.getY())/canvas.getSize().getHeight()-1;
    }

    public void mouseWheelMoved(MouseWheelEvent e){
        int rotation = e.getWheelRotation();
        if (rotation == 1 && JavaRenderer.distance >= -6.0f)
            JavaRenderer.distance -= JavaRenderer.wheel_sensitivity;
        if (rotation == -1 && JavaRenderer.distance <= -3.0f)
            JavaRenderer.distance += JavaRenderer.wheel_sensitivity;
    }

    private static double get_angle(Point point, Dimension size){
        Point center = new Point((int)size.getWidth()/2,(int)size.getHeight()/2);
        int x = (int) (point.getX() - center.getX());
        int y = (int) (point.getY() - center.getY());
        return Math.atan2(y,x);
    }
}