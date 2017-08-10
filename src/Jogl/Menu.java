package Jogl;

import ChangePosition.Change;
import Engine.Ai;
import Engine.Analyze;
import Engine.Play;
import MultiPlayer.Chat;
import MultiPlayer.ConnectServer;
import Tutorial.Tutorial;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static MultiPlayer.ConnectServer.chat;

public class Menu {

    static int[] textures = new int[22];

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
    private static final int RADIO_BUTTON_ON = 15;
    private static final int RADIO_BUTTON_OFF = 16;
    private static final int ARROW_RIGHT = 17;
    private static final int ARROW_LEFT = 18;
    private static final int RESIGN = 19;
    private static final int OFFER_DRAW = 20;
    private static final int REMATCH = 21;


    private static GL2 gl;
    public static boolean thinking = false;
    public static boolean show_turn = false;
    private static boolean changingPos = false;
    private static Button new_game;
    private static Button analyze_button;
    private static Button multiplayer;
    private static Button change;
    static Button white;
    private static Button black;
    public static Button back;
    private static Button exit;
    private static Button tutorial_button;
    private static Button arrow_back;
    private static Button arrow_forward;
    public static Button resign;
    public static Button offer;
    public static Button rematch;
    private static RadioButton color_play_white;
    private static RadioButton color_move_white;
    private static RadioButton color_play_black;
    private static RadioButton color_move_black;
    static Thread play = new Thread();
    static Thread analyze = new Thread();
    private static Thread replace = new Thread();
    private static Thread multi = new Thread();
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
                arrow_back.visible = true;
                arrow_forward.visible = true;
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
                arrow_back.visible = true;
                arrow_forward.visible = true;
                changingPos = true;
                color_move_white.visible = true;
                color_move_black.visible = true;
                color_play_white.visible = true;
                color_play_black.visible = true;
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
                try {
                    chat.setVisible(false);
                    chat = null;
                } catch (NullPointerException ignore){
                }
                JavaRenderer.position = Position.make_position_from_position(JavaRenderer.start_position);
                JavaRenderer.history_positions.clear();
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
                UIManager.put("OptionPane.yesButtonText"   , "Да"    );
                UIManager.put("OptionPane.noButtonText"    , "Нет"   );
                if (JOptionPane.showConfirmDialog(null, "Хотите начать туториал по игре?", "Туториал к игре", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    interrupt();
                    back.visible = false;
                    tutorial = new Thread(new Tutorial());
                    tutorial.start();
                }
            }
        };

        color_move_white = new RadioButton(-.98,.5,true,false,0,"white") {
            @Override
            void click() {
                JavaRenderer.position.isTurnWhite = true;
            }
        };

        color_move_black = new RadioButton(-.98,.4,false,false,0,"black") {
            @Override
            void click() {
                JavaRenderer.position.isTurnWhite = false;
            }
        };

        color_play_white = new RadioButton(-.98,.2,true,false,1,"white") {
            @Override
            void click() {
                JavaRenderer.position.human_plays_for_white = true;
            }
        };

        color_play_black = new RadioButton(-.98,.1,false,false,1,"black") {
            @Override
            void click() {
                JavaRenderer.position.human_plays_for_white = false;
            }
        };

        arrow_forward = new Button(ARROW_RIGHT, textures[ARROW_RIGHT], -.775, -.5, 0.095, 1.0/1.68, false) {

            @Override
            public void click() {
                try {
                    JavaRenderer.position = Position.make_position_from_position(JavaRenderer.history_positions.get(JavaRenderer.moveNumber + 1));
                    JavaRenderer.moveNumber++;
                } catch (Exception ignore){
                }

            }
        };

        arrow_back = new Button(ARROW_LEFT, textures[ARROW_LEFT], -.975, -.5, 0.095, 1.0/1.68, false) {

            @Override
            public void click() {
                try {
                    JavaRenderer.position = Position.make_position_from_position(JavaRenderer.history_positions.get(JavaRenderer.moveNumber - 1));
                    JavaRenderer.moveNumber--;
                } catch (Exception ignore){
                }

            }
        };

        resign = new Button(RESIGN, textures[RESIGN], -.875, .8, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                UIManager.put("OptionPane.yesButtonText"   , "Сдаться"    );
                UIManager.put("OptionPane.noButtonText"    , "Продолжить игру"   );
                if (JOptionPane.showConfirmDialog(null, "Вы точно хотите сдаться?", "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    ConnectServer.resign();
                }
            }
        };

        offer = new Button(OFFER_DRAW, textures[OFFER_DRAW], -.875, .7, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                UIManager.put("OptionPane.yesButtonText"   , "Предложить ничью"    );
                UIManager.put("OptionPane.noButtonText"    , "Продолжить игру"   );
                if (JOptionPane.showConfirmDialog(null, "Вы хотите предложить ничью?", "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    ConnectServer.offer();
                }
            }
        };

        rematch = new Button(REMATCH, textures[REMATCH], -.875, .8, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                UIManager.put("OptionPane.yesButtonText", "Белых");
                UIManager.put("OptionPane.noButtonText", "Черных");
                UIManager.put("OptionPane.cancelButtonText", "Отмена");
                final int a = JOptionPane.showConfirmDialog(null, "Вы предлагаете реванш за", "Сетевая игра", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (a == JOptionPane.YES_OPTION)
                    ConnectServer.rematch(true);
                if (a == JOptionPane.NO_OPTION)
                    ConnectServer.rematch(false);
            }
        };
    }

    private static void interrupt() {
        ConnectServer.isEndGame = false;
        rematch.visible = false;
        resign.visible = false;
        offer.visible = false;
        show_turn = false;
        arrow_back.visible = false;
        arrow_forward.visible = false;
        white.visible = false;
        black.visible = false;
        changingPos = false;
        try {
            ConnectServer.in.close();
            ConnectServer.out.close();
        } catch (Exception ignore){
        }
        while (analyze.isAlive() || play.isAlive() || multi.isAlive() || replace.isAlive() || tutorial.isAlive() || ConnectServer.moveListener.isAlive()) {
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
        color_move_white.visible = false;
        color_move_black.visible = false;
        color_play_white.visible = false;
        color_play_black.visible = false;
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
        color_move_black.display();
        color_play_black.display();
        color_play_white.display();
        color_move_white.display();
        arrow_back.display();
        arrow_forward.display();
        resign.display();
        rematch.display();
        offer.display();

        if (changingPos) {
            printStr("Next Move:", -.98, .55, true);
            printStr("Color you play:", -.98, .25, true);
        }

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

        printStr(massage, -.72, -.97);

        try {
            if (show_turn) {
                String move_white = (!JavaRenderer.position.isTurnWhite) ? "" : " - moves";
                String move_black = (JavaRenderer.position.isTurnWhite) ? "" : " - moves";
                String white, black;
                if (ConnectServer.moveListener.isAlive()) {
                    white = (JavaRenderer.position.human_plays_for_white) ? Chat.myNick : chat.opponentNick;
                    black = (!JavaRenderer.position.human_plays_for_white) ? Chat.myNick : chat.opponentNick;
                    System.out.println(white + " " + black);
                } else {
                    String comp = "OX3DGameEngine";
                    white = (JavaRenderer.position.human_plays_for_white) ? "You" : comp;
                    black = (!JavaRenderer.position.human_plays_for_white) ? "You" : comp;
                }
                printStr("white: " + white + ((JavaRenderer.position.end_game != Position.WHITE_WINS) ? move_white : " - wins!"), -.72, .95);
                printStr("   vs", -.72, .90);
                printStr("black: " + black + (((JavaRenderer.position.end_game != Position.BLACK_WINS)) ? move_black : " - wins!"), -.72, .85);
            }
        } catch (NullPointerException ignore) {
        }
    }

    static void printStr(String str, double x, double y) {
        printStr(str,x,y,false);
    }

    static void printStr(String str, double x, double y, boolean black) {
        if (black)
            gl.glColor3f(0, 0, 0);
        else
            gl.glColor3f(1, 1, 1);
        if (str != null) {
            gl.glRasterPos2d(x, y);
            for (int i = 0; i < str.length(); i++)
                new GLUT().glutBitmapCharacter(GLUT.BITMAP_TIMES_ROMAN_24, str.charAt(i));
        }
    }

    public static void sounds(String sound) {
        try {
            File soundFile = new File("Sounds/" + sound + ".wav");
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
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
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

    public abstract static class Button_class {
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
                            sounds("click");
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

    private abstract static class RadioButton {
        double x;
        double y;
        boolean active;
        boolean visible;
        int family;
        String text;

       private static ArrayList<RadioButton> radioButtons = new ArrayList<>();

        RadioButton(double x, double y, boolean activate, boolean visible, int family, String text) {
            this.x = x;
            this.y = y;
            this.active = activate;
            this.visible = visible;
            this.family = family;
            this.text = text;
            radioButtons.add(this);
        }

        void display() {
            double size = .02;
            if (visible) {
                gl.glEnable(GL2.GL_ALPHA_TEST);
                gl.glAlphaFunc(GL2.GL_GREATER, 0.8f);
                if (active)
                    square(textures[RADIO_BUTTON_ON], x + size, y + size, x - size, y - size);
                else
                    square(textures[RADIO_BUTTON_OFF], x + size, y + size, x - size, y - size);
                gl.glDisable(GL2.GL_ALPHA_TEST);
                printStr(text,x+size+.01,y-size,true);
                if (JavaRenderer.mouse_click != null && JavaRenderer.mouse_click[0] >= x - size && JavaRenderer.mouse_click[0] <= x + size && JavaRenderer.mouse_click[1] <= y + size && JavaRenderer.mouse_click[1] >= y - size) {
                    sounds("click");
                    JavaRenderer.mouse_click = null;
                    activate();
                    click();
                }
            }
        }

        void activate() {
            for (RadioButton button : radioButtons)
                if (button.family == family)
                    button.active = false;
            active = true;
        }

        abstract void click();
    }
}
