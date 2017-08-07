package Jogl;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.jogamp.opengl.awt.GLCanvas;


public class JavaDia implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    public static Thread displayT = new Thread(new JavaDia());
    static boolean bQuit = false;
    private static Point mouse_start_cords;
    private static int button = 0;
    private static Double mouse_start_angle;
    private static GLCanvas canvas = new GLCanvas();
    private static final double pi = 3.141592;

    public void run() {
        Frame frame = new Frame("OX_OpenGL");
        int size = frame.getExtendedState();
        canvas.addGLEventListener(new JavaRenderer());
        frame.add(canvas);
        frame.setUndecorated(false);
        size |= Frame.MAXIMIZED_BOTH;
        frame.setExtendedState(size);
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

        frame.setVisible(true);
        canvas.requestFocus();
        while( !bQuit ) {
            canvas.display();
            try {
                Thread.sleep(1000/30);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
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
            if (e.getWhen() - when_click <= 500) {
                JavaRenderer.mouse_double_click = true;
                when_click = 0;
            } else
                when_click = e.getWhen();
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