package Jogl;

import Utils.BitUtils;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.Texture;

import java.io.File;
import java.io.IOException;

public class JavaRenderer implements GLEventListener {
    private static final GLU glu = new GLU();
    private static final double pi = 3.141592;
    static final double sensitivity = 180.0;
    static final double wheel_sensitivity = 0.2f;
    static float distance = -5.0f;
    static Integer column_select;
    public static Integer column_chosen;
    static double AbsAngleX;
    static double AbsAngleY;
    static double AbsAngleZ;
    private static boolean human_plays_for_white = true;
    private static boolean isTurnWhite = true;
    public static Position start_position = Position.make_position_empty(human_plays_for_white,isTurnWhite);
    public static Position position = Position.make_position_from_position(start_position);
    static Double mouse_at[];
    static boolean mouse_press;
    static boolean mouse_click;
    public static Integer analyzed_column;
    public static Integer score;
    private float h;
    static double speed = 0.005;


    public void display(GLAutoDrawable gLDrawable) {
        final GL2 gl = gLDrawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        Menu.display(gl);

        glu.gluPerspective(50.0f, h, 1.0, 1000.0);

        gl.glTranslatef(.0f, 0.0f, distance);

        gl.glRotatef((float) (180 * AbsAngleX / pi), 1.0f, 0.0f, 0.0f);

        gl.glRotatef((float) (180 * AbsAngleY / pi), 0.0f, 1.0f, 0.0f);

       gl.glRotatef((float) (180 * AbsAngleZ / pi), 0.0f, 0.0f, 1.0f);

        gl.glBegin(GL.GL_LINES);
        gl.glColor3f(1.0f, 0.0f, 0);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 2, 0);
        gl.glEnd();

        if (analyzed_column != null){
            arrow(gl,-0.6f+0.4f*(3 - analyzed_column%4),-0.6f+0.4f*(analyzed_column/4),2.25f);
        }

        cylinder(gl, 0.12f, 1.0f, 0, 0, 0, true, 0);

        for (float x = 0; x < 4; x++)
            for (float y = 0; y < 4; y++)
                cylinder(gl, 1.2f, 0.03f, -0.6f + 0.4f * x, -0.6f + 0.4f * y, 0.66f, false, (int) (4 * y + 3 - x));

        int count_columns[] = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

        Jogl.Ball[] ballsArray = position.balls.toArray(new Jogl.Ball[0]);

            for (Jogl.Ball ball : ballsArray)
                if (ball.onGround)
                    count_columns[ball.getColumn()]++;
            for (Jogl.Ball ball : ballsArray) {
                if (ball.onGround)
                    ellipse(gl, ball.white, ball.x, ball.y, ball.z,ball.getColumn());
                else {
                    ball.speed += speed;
                    if (ball.z - ball.speed <= -0.15 + 0.3 * count_columns[ball.getColumn()]) {
                        ball.onGround = true;
                        ball.z = (float) (-0.15 + 0.3 * count_columns[ball.getColumn()]);
                    } else {
                        ball.z -= ball.speed;
                    }
                    ellipse(gl, ball.white, ball.x, ball.y, ball.z,ball.getColumn());
                }
            }
    }

    private void arrow(GL2 gl, float x, float y, float z){
        final float size = .2f;
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(x + size/12, y + size/12, z + size/2);
        gl.glVertex3f(x + size/12, y - size/12, z + size/2);
        gl.glVertex3f(x - size/12, y - size/12, z + size/2);
        gl.glVertex3f(x - size/12, y + size/12, z + size/2);


        gl.glVertex3f(x + size/12, y + size/12, z + size/2);
        gl.glVertex3f(x + size/12, y + size/12, z - size/6);
        gl.glVertex3f(x - size/12, y + size/12, z - size/6);
        gl.glVertex3f(x - size/12, y + size/12, z + size/2);

        gl.glVertex3f(x + size/12, y + size/12, z - size/6);
        gl.glVertex3f(x - size/12, y + size/12, z - size/6);
        gl.glVertex3f(x - size/12, y + size/12 + size/6, z);
        gl.glVertex3f(x + size/12, y + size/12 + size/6, z);

        gl.glVertex3f(x - size/12, y + size/12 + size/6, z);
        gl.glVertex3f(x + size/12, y + size/12 + size/6, z);
        gl.glVertex3f(x + size/12, y, z - size/2);
        gl.glVertex3f(x - size/12, y, z - size/2);

        gl.glVertex3f(x + size/12, y, z - size/2);
        gl.glVertex3f(x - size/12, y, z - size/2);
        gl.glVertex3f(x - size/12, y - size/12 - size/6, z);
        gl.glVertex3f(x + size/12, y - size/12 - size/6, z);

        gl.glVertex3f(x - size/12, y - size/12 - size/6, z);
        gl.glVertex3f(x + size/12, y - size/12 - size/6, z);
        gl.glVertex3f(x + size/12, y - size/12, z - size/6);
        gl.glVertex3f(x - size/12, y - size/12, z - size/6);

        gl.glVertex3f(x - size/12, y - size/12, z - size/6);
        gl.glVertex3f(x - size/12, y - size/12, z + size/2);
        gl.glVertex3f(x + size/12, y - size/12, z + size/2);
        gl.glVertex3f(x + size/12, y - size/12, z - size/6);
        gl.glEnd();

        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(x + size/12, y - size/12, z + size/2);
        gl.glVertex3f(x + size/12, y + size/12, z + size/2);
        gl.glVertex3f(x + size/12, y + size/12, z - size/6);
        gl.glVertex3f(x + size/12, y - size/12, z - size/6);
        gl.glEnd();

        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex3f(x + size/12, y + size/12, z - size/6);
        gl.glVertex3f(x + size/12, y + size/12 + size/6, z);
        gl.glVertex3f(x + size/12, y, z - size/2);

        gl.glVertex3f(x + size/12, y - size/12, z - size/6);
        gl.glVertex3f(x + size/12, y - size/12 - size/6, z);
        gl.glVertex3f(x + size/12, y, z - size/2);

        gl.glVertex3f(x + size/12, y + size/12, z - size/6);
        gl.glVertex3f(x + size/12, y - size/12, z - size/6);
        gl.glVertex3f(x + size/12, y, z - size/2);

        gl.glEnd();

        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(x - size/12, y - size/12, z + size/2);
        gl.glVertex3f(x - size/12, y + size/12, z + size/2);
        gl.glVertex3f(x - size/12, y + size/12, z - size/6);
        gl.glVertex3f(x - size/12, y - size/12, z - size/6);
        gl.glEnd();

        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex3f(x - size/12, y + size/12, z - size/6);
        gl.glVertex3f(x - size/12, y + size/12 + size/6, z);
        gl.glVertex3f(x - size/12, y, z - size/2);

        gl.glVertex3f(x - size/12, y - size/12, z - size/6);
        gl.glVertex3f(x - size/12, y - size/12 - size/6, z);
        gl.glVertex3f(x - size/12, y, z - size/2);

        gl.glVertex3f(x - size/12, y + size/12, z - size/6);
        gl.glVertex3f(x - size/12, y - size/12, z - size/6);
        gl.glVertex3f(x - size/12, y, z - size/2);

        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glColor3f(0,0,0);
        gl.glVertex3f(x + size/12, y + size/12, z + size/2);
        gl.glVertex3f(x + size/12, y + size/12, z - size/6);
        gl.glVertex3f(x + size/12, y + size/12 + size/6, z);
        gl.glVertex3f(x + size/12, y, z - size/2);
        gl.glVertex3f(x + size/12, y - size/12 - size/6, z);
        gl.glVertex3f(x + size/12, y - size/12, z - size/6);
        gl.glVertex3f(x + size/12, y - size/12, z + size/2);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3f(x - size/12, y + size/12, z + size/2);
        gl.glVertex3f(x - size/12, y + size/12, z - size/6);
        gl.glVertex3f(x - size/12, y + size/12 + size/6, z);
        gl.glVertex3f(x - size/12, y, z - size/2);
        gl.glVertex3f(x - size/12, y - size/12 - size/6, z);
        gl.glVertex3f(x - size/12, y - size/12, z - size/6);
        gl.glVertex3f(x - size/12, y - size/12, z + size/2);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(x + size/12, y + size/12, z + size/2);
        gl.glVertex3f(x - size/12, y + size/12, z + size/2);
        gl.glVertex3f(x + size/12, y + size/12, z - size/6);
        gl.glVertex3f(x - size/12, y + size/12, z - size/6);
        gl.glVertex3f(x + size/12, y + size/12 + size/6, z);
        gl.glVertex3f(x - size/12, y + size/12 + size/6, z);
        gl.glVertex3f(x + size/12, y, z - size/2);
        gl.glVertex3f(x - size/12, y, z - size/2);
        gl.glVertex3f(x + size/12, y - size/12 - size/6, z);
        gl.glVertex3f(x - size/12, y - size/12 - size/6, z);
        gl.glVertex3f(x + size/12, y - size/12, z - size/6);
        gl.glVertex3f(x - size/12, y - size/12, z - size/6);
        gl.glVertex3f(x + size/12, y - size/12, z + size/2);
        gl.glVertex3f(x - size/12, y - size/12, z + size/2);
        gl.glEnd();
    }

    private void ellipse(GL2 gl, boolean white, float x, float y, float z, int colomn) {
        float size = 0.11f;

        int height = (int)((z+.15)/.3 - 1);

        int bit_num = 16*height + colomn;

        gl.glBegin(GL2.GL_QUADS);
        if (white)
            gl.glColor3f(0.91f, 0.88f, 0.87f);
        else
            gl.glColor3f(0.25f, 0.18f, 0.0f);

        if (BitUtils.getBit(position.mask,bit_num))
            gl.glColor3f(1,0,0);

        for (float r2 = 0.01f; r2 < 2; r2 += 0.2f) {
            double r = Math.sqrt(1 - r2 * r2 / 2);
            double r3 = Math.sqrt(1 - (r2 + 0.2f) * (r2 + 0.2f) / 2);
            for (int j2 = 0; j2 < 10; j2++) {
                int j = 36 * j2;
                gl.glVertex3f(x + (float) (size * r * Math.cos(j * pi / 180)), y + (float) (size * r * Math.sin(j * pi / 180)), z + size * r2);
                gl.glVertex3f(x + (float) (size * r * Math.cos((36 + j) * pi / 180)), y + (float) (size * r * Math.sin((36 + j) * pi / 180)), z + size * r2);
                gl.glVertex3f(x + (float) (size * (r3) * Math.cos((36 + j) * pi / 180)), y + (float) (size * (r3) * Math.sin((36 + j) * pi / 180)), z + size * (r2 + 0.2f));
                gl.glVertex3f(x + (float) (size * (r3) * Math.cos(j * pi / 180)), y + (float) (size * (r3) * Math.sin(j * pi / 180)), z + size * (r2 + 0.2f));

                gl.glVertex3f(x + (float) (size * r * Math.cos(j * pi / 180)), y + (float) (size * r * Math.sin(j * pi / 180)), z - size * r2);
                gl.glVertex3f(x + (float) (size * r * Math.cos((36 + j) * pi / 180)), y + (float) (size * r * Math.sin((36 + j) * pi / 180)), z - size * r2);
                gl.glVertex3f(x + (float) (size * (r3) * Math.cos((36 + j) * pi / 180)), y + (float) (size * (r3) * Math.sin((36 + j) * pi / 180)), z - size * (r2 + 0.2f));
                gl.glVertex3f(x + (float) (size * (r3) * Math.cos(j * pi / 180)), y + (float) (size * (r3) * Math.sin(j * pi / 180)), z - size * (r2 + 0.2f));
            }
        }
        gl.glEnd();

        gl.glBegin(GL2.GL_LINES);
        if (!white)
            gl.glColor3f(0.65f, 0.25f, 0.0f);
        else
            gl.glColor3f(0.25f, 0.18f, 0.0f);
        for (float r2 = 0.01f; r2 < 2; r2 += 0.2f) {
            double r = Math.sqrt(1 - r2 * r2 / 2);
            for (int j2 = 0; j2 < 10; j2++) {
                int j = 36 * j2;
                gl.glVertex3f(x + (float) (size * r * Math.cos(j * pi / 180)), y + (float) (size * r * Math.sin(j * pi / 180)), z + size * r2);
                gl.glVertex3f(x + (float) (size * r * Math.cos((36 + j) * pi / 180)), y + (float) (size * r * Math.sin((36 + j) * pi / 180)), z + size * r2);

                gl.glVertex3f(x + (float) (size * r * Math.cos(j * pi / 180)), y + (float) (size * r * Math.sin(j * pi / 180)), z - size * r2);
                gl.glVertex3f(x + (float) (size * r * Math.cos((36 + j) * pi / 180)), y + (float) (size * r * Math.sin((36 + j) * pi / 180)), z - size * r2);
            }
        }
        gl.glEnd();
    }

    private void cylinder(GL2 gl, float height, float radius, float x, float y, float z, boolean dark, int num) {
        gl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i < 360; i++) {
            color_brown(gl, height, radius, x, y, z, dark, num);
            gl.glVertex3f(x + (float) (radius * Math.cos(i * pi / 180)), y + (float) (radius * Math.sin(i * pi / 180)), z + height / 2);
        }
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        for (int i = 0; i < 360; i++) {
            gl.glColor3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3f(x + (float) (radius * Math.cos(i * pi / 180)), y + (float) (radius * Math.sin(i * pi / 180)), z + height / 2);
        }
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        for (int i = 0; i < 360; i++) {
            gl.glColor3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3f(x + (float) (radius * Math.cos(i * pi / 180)), y + (float) (radius * Math.sin(i * pi / 180)), z - height / 2);
        }
        gl.glEnd();

        gl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i < 360; i++) {
            gl.glColor3f(0.1f, 0.1f, 0.1f);
            gl.glVertex3f(x + (float) (radius * Math.cos(i * pi / 180)), y + (float) (radius * Math.sin(i * pi / 180)), z - height / 2);
        }
        gl.glEnd();

        gl.glBegin(GL2.GL_QUADS);
        for (int i = 0; i < 360; i++) {
            color_brown(gl, height, radius, x, y, z, dark, num);
            gl.glVertex3f(x + (float) (radius * Math.cos(i * pi / 180)), y + (float) (radius * Math.sin(i * pi / 180)), z + height / 2);
            gl.glColor3f(0.1f, 0.1f, 0.1f);
            gl.glVertex3f(x + (float) (radius * Math.cos(i * pi / 180)), y + (float) (radius * Math.sin(i * pi / 180)), z - height / 2);
            gl.glColor3f(0.1f, 0.1f, 0.1f);
            gl.glVertex3f(x + (float) (radius * Math.cos((i + 1) * pi / 180)), y + (float) (radius * Math.sin((i + 1) * pi / 180)), z - height / 2);
            color_brown(gl, height, radius, x, y, z, dark, num);
            gl.glVertex3f(x + (float) (radius * Math.cos((i + 1) * pi / 180)), y + (float) (radius * Math.sin((i + 1) * pi / 180)), z + height / 2);
        }
        gl.glEnd();

    }

    private void color_brown(GL2 gl, float height, float radius, float x, float y, float z, boolean dark, int num) {
        if (dark)
            gl.glColor3f(0.254f, 0.1f, 0.0f);
        else {
           /*  double ax = AbsAngleX;
             double ay = AbsAngleY;
             double az = (180*AbsAngleZ/pi);
             double y1 =  -distance*Math.sin(AbsAngleX);
             double x1 = distance*Math.sin(AbsAngleY);
             double y2 = -x1*Math.sin(AbsAngleZ) + y1*Math.cos(AbsAngleZ);
             double x2 = x1*Math.cos(AbsAngleZ) + y1*Math.sin(AbsAngleZ);
             double z1 = (Math.sqrt(distance*distance-x2*x2-y2*y2));
             if (z1 == NaN)
                 z1 = 0;
            for (double t = 1; t >= 0.0 ; t-=0.01)
            if (check(height, radius, x, y, z,x2*t,y2*t,z1*t)) {
                gl.glColor3f(1.0f, 0.0f, 0.0f);
                return;
            }*/
            if (column_select != null && column_select == num)
                gl.glColor3f(1.0f, 0.0f, 0.0f);
            else
                gl.glColor3f(0.97f, 0.43f, 0.08f);
        }
    }

    boolean check(float height, float radius, float x, float y, float z, double x1, double y1, double z1) {
        return z1 >= z - height / 2 && z1 <= z + height / 2 && ((x - x1) * (x - x1) + (y - y1) * (y - y1) <= radius * radius);
    }


    public void init(GLAutoDrawable gLDrawable) {
        final GL2 gl = gLDrawable.getGL().getGL2();
        gl.glShadeModel(GL2.GL_SMOOTH);

        gl.glClearColor(0f,0f,0f,1.0f);
        gl.glClearDepth(1.0f);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT,GL2.GL_NICEST);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        try{
            String[] names = {"NewGame","fon","Analyze","MultiPlayer","Change","Exit","Back","White","Black","BlackWins","WhiteWins","Draw"};
            int type = 1;
            for (String name : names){
                File im = new File("Images/" + name + ".png");
                Texture t = TextureIO.newTexture(im, true);
                Menu.textures[type] = t.getTextureObject(gl);
                type++;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

        Menu.create();
    }

    public void reshape(GLAutoDrawable gLDrawable, int x,
                        int y, int width, int height) {
        final GL2 gl = gLDrawable.getGL().getGL2();
        if (height <= 0) {
            height = 1;
        }
        h = (float) width / (float) height;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void dispose(GLAutoDrawable arg0) {

    }
}