package Jogl;

import ChangePosition.Change;
import Engine.Ai;
import Engine.Analyze;
import Engine.Play;
import MultiPlayer.ConnectServer;
import com.jogamp.opengl.GL2;

public class Menu {

    static int[] textures = new int[10];

    static final int NEW_GAME = 1;
    static final int FON = 2;
    static final int ANALYZE = 3;
    static final int MULTIPLAYER = 4;
    static final int CHANGE = 5;
    static final int EXIT = 6;
    static final int BACK = 7;
    static final int WHITE = 8;
    static final int BLACK = 9;

    static GL2 gl;
    static boolean back_active = false;
    static boolean changing = false;
    static Button new_game;
    static Button analyze_button;
    static Button multiplayer;
    static Button change;
    static Button white;
    static Button black;
    static Button back;
    static Thread play = new Thread();
    static Thread analyze = new Thread();
    static Thread replace = new Thread();
    public static Thread multi = new Thread();
    public static boolean isInterrupted = false;

    static void create() {
        new_game = new Button(NEW_GAME, textures[NEW_GAME], -.875, .8, 0.2, 78.0 / 200) {
            @Override
            public void click() {
                interrupt();
                play = new Thread(new Play());
                play.start();
            }
        };

        analyze_button = new Button(ANALYZE, textures[ANALYZE], -.875, .7, 0.2, 78.0 / 200) {
            @Override
            public void click() {
                interrupt();
                analyze = new Thread(new Analyze());
                analyze.start();
            }
        };

        multiplayer = new Button(MULTIPLAYER, textures[MULTIPLAYER], -.875, .6, 0.2, 78.0 / 200) {
            @Override
            public void click() {
                interrupt();
                multi = new Thread(new ConnectServer());
                multi.start();
            }
        };

        change = new Button(CHANGE, textures[CHANGE], -.875, .5, 0.2, 78.0 / 200) {
            @Override
            public void click() {
                interrupt();
                changing = true;
                JavaRenderer.speed = 1;
                replace = new Thread(new Change());
                replace.start();
            }
        };

        white = new Button(WHITE, textures[WHITE], -.875, .8, 0.2, 78.0 / 200) {
            @Override
            public void click() {
                Change.isTurnWhite = true;
            }
        };

        black = new Button(BLACK, textures[BLACK], -.875, .7, 0.2, 78.0 / 200) {
            @Override
            public void click() {
                Change.isTurnWhite = false;
            }
        };

        back = new Button(BACK, textures[BACK], -.875, -.8, 0.2, 78.0 / 200) {

            @Override
            public void click() {
                interrupt();
                back_active = false;
                JavaRenderer.position = Position.make_position_from_position(JavaRenderer.start_position);
            }
        };
    }

    static void interrupt() {
        JavaRenderer.mouse_click = false;
        changing = false;
        while (analyze.isAlive() || play.isAlive() || multi.isAlive() || replace.isAlive()) {
            isInterrupted = true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        isInterrupted = false;
        JavaRenderer.analyzed_column = null;
        JavaRenderer.score = null;
        JavaRenderer.speed = 0.005;
        Ai.history_moves.clear();
        back_active = true;
    }

    static void display(GL2 gl) {
        Menu.gl = gl;
        square(textures[FON], -1, 1, -0.75, -1);
        if (!back_active && !changing) {
            new_game.display();
            analyze_button.display();
            multiplayer.display();
            change.display();
        } else
            back.display();

        if (changing){
            white.display();
            black.display();
        }
        JavaRenderer.mouse_click = false;
    }

    static void square(int texture, double x1, double y1, double x2, double y2) {
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex2d(x1, y1);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex2d(x1, y2);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex2d(x2, y2);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex2d(x2, y1);
        gl.glEnd();
        gl.glDisable(GL2.GL_TEXTURE_2D);

    }

    private abstract static class Button extends Button_class {
        Button(int name, double x1, double y1, double x2, double y2, int texture) {
            super.name = name;
            super.x1 = x1;
            super.y1 = y1;
            super.x2 = x2;
            super.y2 = y2;
            super.texture = texture;
        }

        Button(int name, int texture, double xc, double yc, double width, double ratio) {
            super.name = name;
            super.x1 = xc - width / 2;
            super.y1 = yc + width * ratio / 2;
            super.x2 = xc + width / 2;
            super.y2 = yc - width * ratio / 2;
            super.texture = texture;
        }
    }

    private abstract static class Button_class {
        int name = 0;
        double x1 = 0;
        double y1 = 0;
        double x2 = 0;
        double y2 = 0;
        int texture = 0;

        void display() {
            final double xc = (x1 + x2) / 2, yc = (y1 + y2) / 2;
            final double zoom_press = .95;
            final double zoom_hover = 1.05;
            try {
                double mouse_x = JavaRenderer.mouse_at[0];
                double mouse_y = JavaRenderer.mouse_at[1];
                if (mouse_x >= x1 && mouse_x <= x2 && mouse_y <= y1 && mouse_y >= y2) {
                    if (JavaRenderer.mouse_press)
                        Menu.square(texture, xc + (zoom_press * (x1 - xc)), yc + (zoom_press * (y1 - yc)), xc + (zoom_press * (x2 - xc)), yc + (zoom_press * (y2 - yc)));
                    else
                        Menu.square(texture, xc + (zoom_hover * (x1 - xc)), yc + (zoom_hover * (y1 - yc)), xc + (zoom_hover * (x2 - xc)), yc + (zoom_hover * (y2 - yc)));
                    if (JavaRenderer.mouse_click) {
                        click();
                    }
                    return;
                }
            } catch (NullPointerException ignore) {
            }

            Menu.square(texture, x1, y1, x2, y2);
        }

        public abstract void click();
    }
}
