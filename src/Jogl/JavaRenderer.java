package Jogl;

import Engine.Mask;
import MultiPlayer.ConnectServer;
import Tutorial.Tutorial;
import Utils.BitUtils;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.Texture;
import javafx.geometry.Point3D;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.jogamp.opengl.GL.GL_VIEWPORT;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW_MATRIX;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION_MATRIX;

public class JavaRenderer implements GLEventListener {

    private static final GLU glu = new GLU();
    private static final double pi = 3.141592;
    static final double sensitivity = 180.0;
    static final double wheel_sensitivity = 0.2f;
    public static float distance = -5.0f;
    public static Integer column_chosen;
    private static Integer column_select = null;
    public static double AbsAngleX;
    static double AbsAngleY;
    public static double AbsAngleZ;
    private static boolean human_plays_for_white = true;
    private static boolean isTurnWhite = true;
    static int moveNumber = 0;
    static ArrayList<Position> history_positions = new ArrayList<>();
    public static Position start_position = Position.make_position_empty(human_plays_for_white, isTurnWhite);
    public static Position position = Position.make_position_from_position(start_position);
    static Double mouse_at[];
    static boolean mouse_press;
    static double[] mouse_click;
    static boolean mouse_double_click;
    private static boolean check_double_click;
    public static Integer analyzed_column;
    public static Integer score;
    public static Integer depth;
    static double speed = 0.001;
    static Point mouse_cords_viewport;
    static boolean isScreensaverOn = true;

    private static float h;

    public void display(GLAutoDrawable gLDrawable) {
        final GL2 gl = gLDrawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        Menu.display(gl);

        if (analyzed_column != null && depth != null && score != null) {
                boolean mate = score > 20000 || score < -20000;

                String analyze = "depth: " + depth + " score: " + ((mate) ? "#" + (30000*Math.abs(score)/score - score):"" + score);
                Menu.printStr(analyze,-.72, -.97);
            }

        glu.gluPerspective(50, h, 1f, 10f);

        gl.glTranslatef(.0f, 0.0f, distance);

        gl.glRotatef((float) (180 * AbsAngleX / pi), 1.0f, 0.0f, 0.0f);

        gl.glRotatef((float) (180 * AbsAngleY / pi), 0.0f, 1.0f, 0.0f);

        gl.glRotatef((float) (180 * AbsAngleZ / pi), 0.0f, 0.0f, 1.0f);

        if (analyzed_column != null)
            arrow(gl, -0.6f + 0.4f * (3 - analyzed_column % 4), -0.6f + 0.4f * (analyzed_column / 4), 2.25f);

        if (isScreensaverOn){
            AbsAngleY = 0;
            AbsAngleX = -pi/4;
            distance = -5.0f;
            AbsAngleZ += 0.01;
        }

        cylinder(gl);

        Point3D[] points = null;

        column_select = null;

        if (mouse_at != null &&
                position.end_game == 0 && (Menu.analyze.isAlive() || Menu.white.visible || ((Menu.play.isAlive() || ConnectServer.moveListener.isAlive() || Tutorial.canMove) && position.human_plays_for_white == position.isTurnWhite))) {
            int viewport[] = new int[4];
            float projection[] = new float[16];
            float modelView[] = new float[16];
            float cords1[] = new float[3];
            float cords2[] = new float[3];
            float vx, vy;

            gl.glGetIntegerv(GL_VIEWPORT, viewport, 0);
            gl.glGetFloatv(GL_PROJECTION_MATRIX, projection, 0);
            gl.glGetFloatv(GL_MODELVIEW_MATRIX, modelView, 0);

            vx = mouse_cords_viewport.x;
            vy = mouse_cords_viewport.y;

            glu.gluUnProject(vx, vy, -1, modelView, 0, projection, 0, viewport, 0, cords1, 0);
            glu.gluUnProject(vx, vy, 1, modelView, 0, projection, 0, viewport, 0, cords2, 0);
            points = new Point3D[]{new Point3D(cords1[0], cords1[1], cords1[2]), new Point3D(cords2[0], cords2[1], cords2[2])};

            check_double_click = mouse_double_click;
        }

        for (float x = 0; x < 4; x++)
            for (float y = 0; y < 4; y++)
                cylinder(gl, 1.2f, 0.03f, -0.6f + 0.4f * x, -0.6f + 0.4f * y, 0.66f, false, (int) (4 * y + 3 - x), points);

        if (check_double_click)
            mouse_double_click = false;

        int count_columns[] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        Ball[] ballsArray = position.balls.toArray(new Ball[0]);

        for (Ball ball : ballsArray)
            if (ball.onGround)
                count_columns[ball.getColumn()]++;
        for (Ball ball : ballsArray) {
            if (ball.onGround)
                ellipse(gl, ball.white, ball.x, ball.y, ball.z, ball.getColumn());
            else {
                ball.speed += speed;
                if (ball.z - ball.speed <= -0.15 + 0.3 * count_columns[ball.getColumn()]) {
                    ball.onGround = true;
                    ball.z = (float) (-0.15 + 0.3 * count_columns[ball.getColumn()]);
                    Menu.sounds("move");
                } else {
                    ball.z -= ball.speed;
                }
                ellipse(gl, ball.white, ball.x, ball.y, ball.z, ball.getColumn());
            }
        }
    }

    private void arrow(GL2 gl, float x, float y, float z) {
        final float size = .2f;
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f(0, 1, 0);
        gl.glVertex3f(x + size / 12, y + size / 12, z + size / 2);
        gl.glVertex3f(x + size / 12, y - size / 12, z + size / 2);
        gl.glVertex3f(x - size / 12, y - size / 12, z + size / 2);
        gl.glVertex3f(x - size / 12, y + size / 12, z + size / 2);


        gl.glVertex3f(x + size / 12, y + size / 12, z + size / 2);
        gl.glVertex3f(x + size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y + size / 12, z + size / 2);

        gl.glVertex3f(x + size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y + size / 12 + size / 6, z);
        gl.glVertex3f(x + size / 12, y + size / 12 + size / 6, z);

        gl.glVertex3f(x - size / 12, y + size / 12 + size / 6, z);
        gl.glVertex3f(x + size / 12, y + size / 12 + size / 6, z);
        gl.glVertex3f(x + size / 12, y, z - size / 2);
        gl.glVertex3f(x - size / 12, y, z - size / 2);

        gl.glVertex3f(x + size / 12, y, z - size / 2);
        gl.glVertex3f(x - size / 12, y, z - size / 2);
        gl.glVertex3f(x - size / 12, y - size / 12 - size / 6, z);
        gl.glVertex3f(x + size / 12, y - size / 12 - size / 6, z);

        gl.glVertex3f(x - size / 12, y - size / 12 - size / 6, z);
        gl.glVertex3f(x + size / 12, y - size / 12 - size / 6, z);
        gl.glVertex3f(x + size / 12, y - size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y - size / 12, z - size / 6);

        gl.glVertex3f(x - size / 12, y - size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y - size / 12, z + size / 2);
        gl.glVertex3f(x + size / 12, y - size / 12, z + size / 2);
        gl.glVertex3f(x + size / 12, y - size / 12, z - size / 6);
        gl.glEnd();

        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(x + size / 12, y - size / 12, z + size / 2);
        gl.glVertex3f(x + size / 12, y + size / 12, z + size / 2);
        gl.glVertex3f(x + size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x + size / 12, y - size / 12, z - size / 6);
        gl.glEnd();

        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex3f(x + size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x + size / 12, y + size / 12 + size / 6, z);
        gl.glVertex3f(x + size / 12, y, z - size / 2);

        gl.glVertex3f(x + size / 12, y - size / 12, z - size / 6);
        gl.glVertex3f(x + size / 12, y - size / 12 - size / 6, z);
        gl.glVertex3f(x + size / 12, y, z - size / 2);

        gl.glVertex3f(x + size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x + size / 12, y - size / 12, z - size / 6);
        gl.glVertex3f(x + size / 12, y, z - size / 2);

        gl.glEnd();

        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(x - size / 12, y - size / 12, z + size / 2);
        gl.glVertex3f(x - size / 12, y + size / 12, z + size / 2);
        gl.glVertex3f(x - size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y - size / 12, z - size / 6);
        gl.glEnd();

        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex3f(x - size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y + size / 12 + size / 6, z);
        gl.glVertex3f(x - size / 12, y, z - size / 2);

        gl.glVertex3f(x - size / 12, y - size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y - size / 12 - size / 6, z);
        gl.glVertex3f(x - size / 12, y, z - size / 2);

        gl.glVertex3f(x - size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y - size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y, z - size / 2);

        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glColor3f(0, 0, 0);
        gl.glVertex3f(x + size / 12, y + size / 12, z + size / 2);
        gl.glVertex3f(x + size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x + size / 12, y + size / 12 + size / 6, z);
        gl.glVertex3f(x + size / 12, y, z - size / 2);
        gl.glVertex3f(x + size / 12, y - size / 12 - size / 6, z);
        gl.glVertex3f(x + size / 12, y - size / 12, z - size / 6);
        gl.glVertex3f(x + size / 12, y - size / 12, z + size / 2);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3f(x - size / 12, y + size / 12, z + size / 2);
        gl.glVertex3f(x - size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y + size / 12 + size / 6, z);
        gl.glVertex3f(x - size / 12, y, z - size / 2);
        gl.glVertex3f(x - size / 12, y - size / 12 - size / 6, z);
        gl.glVertex3f(x - size / 12, y - size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y - size / 12, z + size / 2);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f(x + size / 12, y + size / 12, z + size / 2);
        gl.glVertex3f(x - size / 12, y + size / 12, z + size / 2);
        gl.glVertex3f(x + size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y + size / 12, z - size / 6);
        gl.glVertex3f(x + size / 12, y + size / 12 + size / 6, z);
        gl.glVertex3f(x - size / 12, y + size / 12 + size / 6, z);
        gl.glVertex3f(x + size / 12, y, z - size / 2);
        gl.glVertex3f(x - size / 12, y, z - size / 2);
        gl.glVertex3f(x + size / 12, y - size / 12 - size / 6, z);
        gl.glVertex3f(x - size / 12, y - size / 12 - size / 6, z);
        gl.glVertex3f(x + size / 12, y - size / 12, z - size / 6);
        gl.glVertex3f(x - size / 12, y - size / 12, z - size / 6);
        gl.glVertex3f(x + size / 12, y - size / 12, z + size / 2);
        gl.glVertex3f(x - size / 12, y - size / 12, z + size / 2);
        gl.glEnd();
    }

    private void ellipse(GL2 gl, boolean white, float x, float y, float z, int column) {
        float size = 0.11f;

        int height = (int) Math.round((z - .15) / .3);

        int bit_num = 16 * height + column;

        gl.glBegin(GL2.GL_QUADS);
        if (white)
            gl.glColor3f(0.91f, 0.88f, 0.87f);
        else
            gl.glColor3f(0.25f, 0.18f, 0.0f);

        if (BitUtils.getBit(position.mask, bit_num) && position.allOnGround())
            gl.glColor3f(1, 0, 0);

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

    private void cylinder(GL2 gl) {
        cylinder(gl, 0.12f, 1.0f, (float) 0, (float) 0, (float) 0, true, 0, null);
    }

    private void cylinder(GL2 gl, float height, float radius, float x, float y, float z, boolean dark, int num, Point3D[] points) {
        gl.glBegin(GL2.GL_POLYGON);
        for (int i = 0; i < 360; i++) {
            color(gl, height, radius, x, y, z, dark, num, points);
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
            color(gl, height, radius, x, y, z, dark, num, points);
            gl.glVertex3f(x + (float) (radius * Math.cos(i * pi / 180)), y + (float) (radius * Math.sin(i * pi / 180)), z + height / 2);
            gl.glColor3f(0.1f, 0.1f, 0.1f);
            gl.glVertex3f(x + (float) (radius * Math.cos(i * pi / 180)), y + (float) (radius * Math.sin(i * pi / 180)), z - height / 2);
            gl.glColor3f(0.1f, 0.1f, 0.1f);
            gl.glVertex3f(x + (float) (radius * Math.cos((i + 1) * pi / 180)), y + (float) (radius * Math.sin((i + 1) * pi / 180)), z - height / 2);
            color(gl, height, radius, x, y, z, dark, num, points);
            gl.glVertex3f(x + (float) (radius * Math.cos((i + 1) * pi / 180)), y + (float) (radius * Math.sin((i + 1) * pi / 180)), z + height / 2);
        }
        gl.glEnd();

    }

    private void color(GL2 gl, float height, float radius, float x, float y, float z, boolean dark, int num, Point3D[] points) {
        if (dark)
            gl.glColor3f(0.254f, 0.1f, 0.0f);
        else {
            if (points != null && column_chosen == null && (column_select == null || column_select == num)) {
                double x1 = points[0].getX();
                double y1 = points[0].getY();
                double z1 = points[0].getZ();
                double x2 = points[1].getX();
                double y2 = points[1].getY();
                double z2 = points[1].getZ();
                for (double t = 1; t >= 0.0; t -= 0.005) {
                    if (check(height, radius, x, y, z, (x2 - x1) * t + x1, (y2 - y1) * t + y1, (z2 - z1) * t + z1)) {
                        if (check_double_click && JavaRenderer.column_select != null && Long.bitCount((JavaRenderer.position.bitBoard.white | JavaRenderer.position.bitBoard.black) & Mask.diagonals[60 + JavaRenderer.column_select]) < 4) {
                            column_chosen = num;
                            check_double_click = false;
                            mouse_double_click = false;
                        }
                        column_select = num;
                        gl.glColor3f(1.0f, 0.0f, 0.0f);
                        return;
                    }
                }
            }
            gl.glColor3f(0.97f, 0.43f, 0.08f);
            if (analyzed_column != null && analyzed_column == num)
                gl.glColor3f(0, 1, 0);
        }
    }

    private boolean check(float height, float radius, float x, float y, float z, double x1, double y1, double z1) {
        return z1 >= z - height / 2 && z1 <= z + height / 2 && ((x - x1) * (x - x1) + (y - y1) * (y - y1) <= radius * radius);
    }


    public void init(GLAutoDrawable gLDrawable) {
        final GL2 gl = gLDrawable.getGL().getGL2();
        gl.glShadeModel(GL2.GL_SMOOTH);

        gl.glClearColor(0f, 0f, 0f, 1.0f);
        gl.glClearDepth(1.0f);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        try {
            String[] names = {"NewGame", "fon", "Analyze", "MultiPlayer", "Change", "Exit", "Back", "White", "Black", "BlackWins", "WhiteWins", "Draw", "wait"
                    ,"Tutorial","radio_on","radio_off","arrow_right","arrow_left","resign","offer","rematch","start","profile","log_out"};
            int type = 1;
            for (String name : names) {
                File im = new File("Images/" + name + ".png");
                Texture t = TextureIO.newTexture(im, true);
                Menu.textures[type] = t.getTextureObject(gl);
                type++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Menu.create();
    }

    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
        final GL2 gl = gLDrawable.getGL().getGL2();
        if (height <= 0) {
            height = 1;
        }
        h = (float) width / (float) height;
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void dispose(GLAutoDrawable arg0) {
    }
}