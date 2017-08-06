package Jogl;

import ChangePosition.Change;
import Engine.Ai;
import Engine.Analyze;
import Engine.Play;
import MultiPlayer.ConnectServer;
import Tutorial.Tutorial;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Menu {

    static int[] textures = new int[15];

    private static final int NEW_GAME = 1;
    private static final int FON = 2;
    private static final int ANALYZE = 3;
    private static final int MULTIPLAYER = 4;
    private static final int CHANGE = 5;
    private static final int EXIT = 6;
    private static final int BACK = 7;
    private static final int WHITE = 8;
    private static final int BLACK = 9;
    private static final int BLACK_WINS = 10;
    private static final int WHITE_WINS = 11;
    private static final int DRAW = 12;
    private static final int WAIT = 13;
    private static final int TUTORIAL = 14;

    private static GL2 gl;
    public static boolean thinking = false;
    public static boolean show_turn = false;
    private static Button new_game;
    private static Button analyze_button;
    private static Button multiplayer;
    private static Button change;
    static Button white;
    private static Button black;
    public static Button back;
    private static Button exit;
    private static Button tutorial_button;
    // public static TextBox chat;
    static Thread play = new Thread();
    static Thread analyze = new Thread();
    private static Thread replace = new Thread();
    static Thread multi = new Thread();
    private static Thread tutorial = new Thread();
    public static boolean isInterrupted = false;
    public static String massage;

    static void create() {
        new_game = new Button(NEW_GAME, textures[NEW_GAME], -.875, .8, 0.2, 78.0 / 200, true) {
            @Override
            public void click() {
                interrupt();
                show_turn = true;
                play = new Thread(new Play());
                play.start();
            }
        };

        analyze_button = new Button(ANALYZE, textures[ANALYZE], -.875, .7, 0.2, 78.0 / 200, true) {
            @Override
            public void click() {
                interrupt();
                analyze = new Thread(new Analyze());
                analyze.start();
            }
        };

        multiplayer = new Button(MULTIPLAYER, textures[MULTIPLAYER], -.875, .6, 0.2, 78.0 / 200, true) {
            @Override
            public void click() {
                interrupt();
                multi = new Thread(new ConnectServer());
                multi.start();
            }
        };

        change = new Button(CHANGE, textures[CHANGE], -.875, .5, 0.2, 78.0 / 200, true) {
            @Override
            public void click() {
                interrupt();
                white.visible = true;
                black.visible = true;
                JavaRenderer.speed = 1;
                replace = new Thread(new Change());
                replace.start();
            }
        };

        white = new Button(WHITE, textures[WHITE], -.875, .8, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                Change.isTurnWhite = true;
            }
        };

        black = new Button(BLACK, textures[BLACK], -.875, .7, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                Change.isTurnWhite = false;
            }
        };

        back = new Button(BACK, textures[BACK], -.875, -.7, 0.2, 78.0 / 200, false) {

            @Override
            public void click() {
                interrupt();
                back.visible = false;
                new_game.visible = true;
                analyze_button.visible = true;
                multiplayer.visible = true;
                change.visible = true;
                tutorial_button.visible = true;
                massage = null;
                JavaRenderer.position = Position.make_position_from_position(JavaRenderer.start_position);
            }
        };

        exit = new Button(EXIT, textures[EXIT], -.875, -.8, 0.2, 78.0 / 200, true) {

            @Override
            public void click() {
                JavaDia.bQuit = true;
                System.exit(0);
            }
        };

        tutorial_button = new Button(TUTORIAL, textures[TUTORIAL], -.875, .4, 0.2, 78.0 / 200, true) {

            @Override
            public void click() {
                if (JOptionPane.showConfirmDialog(null,"Хотите начать туториал по игре?","Туториал к игре",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    interrupt();
                    back.visible = false;
                    tutorial = new Thread(new Tutorial());
                    tutorial.start();
                }
            }
        };
    }

    private static void interrupt() {
        show_turn = false;
        white.visible = false;
        black.visible = false;
        while (analyze.isAlive() || play.isAlive() || multi.isAlive() || replace.isAlive() || tutorial.isAlive()) {
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
        new_game.visible = false;
        analyze_button.visible = false;
        multiplayer.visible = false;
        change.visible = false;
        back.visible = true;
        tutorial_button.visible = false;
    }

    static void display(GL2 gl) {
        Menu.gl = gl;
        square(textures[FON], -1, 1, -0.75, -1);
        square(textures[FON], 1, 1, 0.75, -1);
            new_game.display();
            analyze_button.display();
            multiplayer.display();
            change.display();
            back.display();
            white.display();
            black.display();
            exit.display();
            tutorial_button.display();

            if (JavaRenderer.position.allOnGround()) {
                if (JavaRenderer.position.end_game == Position.WHITE_WINS)
                    square(textures[WHITE_WINS], .4, 1, 0.75, .9);
                else if (JavaRenderer.position.end_game == Position.BLACK_WINS)
                    square(textures[BLACK_WINS], .4, 1, 0.75, .9);
                else if (JavaRenderer.position.end_game == Position.DRAW)
                    square(textures[DRAW], .4, 1, 0.75, .9);
            }
        if (thinking)
            square(textures[WAIT], 0.36, -0.77, .75, -1);

        if (massage != null)
            printStr(massage, -.72, -.97);

        if (show_turn) {
            String side = (JavaRenderer.position.human_plays_for_white) ? "white" : "black";
            String turn = (JavaRenderer.position.isTurnWhite) ? "white" : "black";
            printStr("Turn: " + turn, -.72, .95);
            printStr("You are playing for : " + side, -.72, .90);
        }
    }

    static void printStr(String str, double x, double y){
        gl.glRasterPos2d(x,y);
        gl.glColor3f(1,1,1);
        for (int i = 0; i < str.length(); i++)
            new GLUT().glutBitmapCharacter(GLUT.BITMAP_TIMES_ROMAN_24, str.charAt(i));
    }

    static void sounds(String sound){
        try {
            File soundFile = new File(sound);
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.setFramePosition(0);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
            exc.printStackTrace();
        }
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

    public abstract static class Button extends Button_class {
        Button(int name, int texture, double xc, double yc, double width, double ratio, boolean visible) {
            super.name = name;
            super.x1 = xc - width / 2;
            super.y1 = yc + width * ratio / 2;
            super.x2 = xc + width / 2;
            super.y2 = yc - width * ratio / 2;
            super.texture = texture;
            super.visible = visible;
        }
    }

    private abstract static class Button_class {
        int name = 0;
        double x1 = 0;
        double y1 = 0;
        double x2 = 0;
        double y2 = 0;
        int texture = 0;
        public boolean visible;

        void display() {
            if (visible) {
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
                        if (JavaRenderer.mouse_click != null && JavaRenderer.mouse_click[0] >= x1 && JavaRenderer.mouse_click[0] <= x2 && JavaRenderer.mouse_click[1] <= y1 && JavaRenderer.mouse_click[1] >= y2) {
                            sounds("Sounds/click.wav");
                            JavaRenderer.mouse_click = null;
                            click();
                        }
                        return;
                    }
                } catch (NullPointerException ignore) {
                }

                Menu.square(texture, x1, y1, x2, y2);
            }
        }

        public abstract void click();
    }

   public static class TextBox {
        double x1;
        double y1;
        double x2;
        double y2;
        String text = "";
        boolean writable;
        boolean slider_visible;
        double slider_position = 0;
        boolean active;
        boolean updated = false;

        private ArrayList<String> strings = new ArrayList<>();
        private double width = Math.abs(x1-x2);
        private double height = Math.abs(y1-y2);

        TextBox(double x1, double y1, double x2, double y2, boolean writable, boolean slider_visible) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.writable = writable;
            this.slider_visible = slider_visible;
        }

        private static final double letter_size = 0.01;

        void display(){
            double mouse_x = JavaRenderer.mouse_at[0];
            double mouse_y = JavaRenderer.mouse_at[1];

            if (updated){
                ArrayList<String> words = new ArrayList<>();
                words.add("");
                for (char c : text.toCharArray())
                    if (c == ' ')
                        words.add("");
                     else
                        words.add(words.size() - 1,words.get(words.size() - 1) + c);

            }


        }
    }
}
