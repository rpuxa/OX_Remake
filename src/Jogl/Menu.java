package Jogl;

import Editor.ChangePosition.Change;
import Editor.Analyze.Analyze;
import Editor.Sandbox;
import Engine.Ai;
import Engine.BitBoard;
import MultiPlayer.AudioChat.RecordVoice;
import MultiPlayer.ConnectServer;
import Oxpos.SaveAndLoad;
import Tutorial.Tutorial;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import PlayEngine.ChoseDif;

import static Jogl.JavaRenderer.fastMode;
import static MultiPlayer.ConnectServer.*;

public class Menu {

    static int[] textures = new int[36];

    private static final int NEW_GAME = 1;
    private static final int FON = 2;
    private static final int SANDBOX = 3;
    private static final int MULTIPLAYER = 4;
    private static final int PLAY_WITH_ENGINE = 5;
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
    private static final int START_GAME = 22;
    private static final int PROFILE = 23;
    private static final int LOG_OUT = 24;
    private static final int OPPONENT_RECORDING = 25;
    private static final int YOU_RECORDING = 26;
    private static final int PLAYBACK = 27;
    private static final int EDITOR = 28;
    private static final int SAVE_POSITION = 29;
    private static final int LOAD_POSITON = 30;
    private static final int START_ANALYZE = 31;
    private static final int STOP_ANALYZE = 32;
    private static final int DELETE = 33;
    private static final int CLEAR_ALL = 34;


    private static GL2 gl;
    public static boolean thinking = false;
    static boolean changingPos = false;
    private static Button new_game;
    private static Button sandbox;
    private static Button multiplayer;
    private static Button play_button;
    private static Button white;
    private static Button black;
    public static Button back;
    private static Button exit;
    private static Button tutorial_button;
    private static Button arrow_back;
    private static Button arrow_forward;
    public static Button resign;
    public static Button offer;
    public static Button rematch;
    public static Button profile;
    public static Button logOut;
    private static Button editor_button;
    private static Button start_analyze;
    private static Button stop_analyze;
    public static Button back_sandbox;
    private static Button delete;
    private static Button clear;
    public static Button save;
    private static Button load;
    private static RadioButton color_move_white;
    private static RadioButton color_move_black;
    public static Thread play = new Thread();
    private static Thread editor = new Thread();
    private static Thread multi = new Thread();
    static Thread tutorial = new Thread();
    static Thread sandbox_thread = new Thread();
    static Thread screensaver = new Thread();
    public static boolean isInterrupted = false;
    public static String message;

    static void create() {
        new_game = new Button(textures[NEW_GAME], -.875, .8, 0.2, 78.0 / 200, true) {
            @Override
            public void click() {
                interrupt();
                new ChoseDif(true);
                save.visible = true;
            }
        };

        sandbox = new Button(textures[SANDBOX], -.875, .7, 0.2, 78.0 / 200, true) {
            @Override
            public void click() {
                interrupt();
                play_button.visible = true;
                editor_button.visible = true;
                start_analyze.visible = true;
                arrow_back.visible = true;
                arrow_forward.visible = true;
                save.visible = true;
                load.visible = true;
                sandbox_thread = new Thread(new Sandbox());
                sandbox_thread.start();
            }
        };

        multiplayer = new Button(textures[MULTIPLAYER], -.875, .6, 0.2, 78.0 / 200, true) {
            @Override
            public void click() {
                interrupt();
                multi = new Thread(new ConnectServer());
                multi.start();
            }
        };

        play_button = new Button(textures[PLAY_WITH_ENGINE], -.875, .8, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                interrupt();
                new ChoseDif(false);
                back.visible = false;
                back_sandbox.visible = true;
                save.visible = true;
            }
        };

        white = new Button(textures[WHITE], -.875, .8, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                Change.isTurnWhite = true;
                Change.delete = false;
            }
        };

        black = new Button(textures[BLACK], -.875, .7, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                Change.isTurnWhite = false;
                Change.delete = false;
            }
        };

        back = new Button(textures[BACK], -.875, -.7, 0.2, 78.0 / 200, false) {

            @Override
            public void click() {
                interrupt();
                back.visible = false;
                new_game.visible = true;
                sandbox.visible = true;
                multiplayer.visible = true;
                tutorial_button.visible = true;
                message = null;
                JavaRenderer.isScreensaverOn = true;
                try {
                    chat.setVisible(false);
                } catch (NullPointerException ignore) {
                }
                try {
                    versus.setVisible(false);
                } catch (NullPointerException ignore) {
                }
                try {
                    time.setVisible(false);
                } catch (NullPointerException ignore) {
                }
                try {
                    JavaDia.scroll_lobby.setVisible(false);
                    JavaDia.scroll_lobby.remove(lobby);
                } catch (NullPointerException ignore) {
                }
                screensaver = new Thread(new Screensaver());
                screensaver.start();

                JavaRenderer.position = Position.make_position_from_position(JavaRenderer.sandbox_position);
                JavaRenderer.game.clear();
                JavaRenderer.moveNumber = 0;
            }
        };

        exit = new Button(textures[EXIT], -.875, -.8, 0.2, 78.0 / 200, true) {

            @Override
            public void click() {
                JavaDia.bQuit = true;
                System.exit(0);
            }
        };

        tutorial_button = new Button(textures[TUTORIAL], -.875, .5, 0.2, 78.0 / 200, true) {

            @Override
            public void click() {
                UIManager.put("OptionPane.yesButtonText", "Да");
                UIManager.put("OptionPane.noButtonText", "Нет");
                if (JOptionPane.showConfirmDialog(null, "Хотите начать туториал по игре?", "Туториал к игре", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    interrupt();
                    back.visible = false;
                    tutorial = new Thread(new Tutorial());
                    tutorial.start();
                }
            }
        };

        color_move_white = new RadioButton(-.97, .3, true, false, 0, "white") {
            @Override
            void click() {
                JavaRenderer.position.isTurnWhite = true;
            }
        };

        color_move_black = new RadioButton(-.97, .2, false, false, 0, "black") {
            @Override
            void click() {
                JavaRenderer.position.isTurnWhite = false;
            }
        };

        arrow_forward = new Button(textures[ARROW_RIGHT], -.805, -.5, 0.095, 1.0, false) {

            @Override
            public void click() {
                try {
                    JavaRenderer.position = Position.make_position_from_position(JavaRenderer.game.get(JavaRenderer.moveNumber + 1));
                    JavaRenderer.moveNumber++;
                    arrowStartAnalyze();
                } catch (Exception ignore) {
                }
            }
        };

        arrow_back = new Button(textures[ARROW_LEFT], -.950, -.5, 0.095, 1.0, false) {

            @Override
            public void click() {
                try {
                    JavaRenderer.position = Position.make_position_from_position(JavaRenderer.game.get(JavaRenderer.moveNumber - 1));
                    JavaRenderer.moveNumber--;
                    arrowStartAnalyze();
                } catch (Exception ignore) {
                }
            }
        };

        resign = new Button(textures[RESIGN], -.875, .5, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                UIManager.put("OptionPane.yesButtonText", "Сдаться");
                UIManager.put("OptionPane.noButtonText", "Продолжить игру");
                if (JOptionPane.showConfirmDialog(null, "Вы точно хотите сдаться?", "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    ConnectServer.resign();
                }
            }
        };

        offer = new Button(textures[OFFER_DRAW], -.875, .4, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                UIManager.put("OptionPane.yesButtonText", "Предложить ничью");
                UIManager.put("OptionPane.noButtonText", "Продолжить игру");
                if (JOptionPane.showConfirmDialog(null, "Вы хотите предложить ничью?", "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    ConnectServer.offer();
                }
            }
        };

        rematch = new Button(textures[REMATCH], -.875, .5, 0.2, 78.0 / 200, false) {
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

        profile = new Button(textures[PROFILE], -.875, .8, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                ConnectServer.showProfile();
            }
        };

        logOut = new Button(textures[LOG_OUT], -.875, -.6, 0.2, 78.0 / 200, false) {

            @Override
            public void click() {
                UIManager.put("OptionPane.yesButtonText", "Выйти");
                UIManager.put("OptionPane.noButtonText", "Остаться");
                if (JOptionPane.showConfirmDialog(null, "Хотите выйти из профиля?", "Туториал к игре", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION)
                    ConnectServer.logOut();
            }
        };

        editor_button = new Button(textures[EDITOR], -.875, .6, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                interrupt();
                changingPos = true;
                white.visible = true;
                black.visible = true;
                back.visible = false;
                back_sandbox.visible = true;
                delete.visible = true;
                clear.visible = true;
                color_move_white.visible = true;
                color_move_black.visible = true;
                JavaRenderer.speed = 1;
                editor = new Thread(new Change());
                editor.start();
            }
        };

        back_sandbox = new Button(textures[BACK], -.875, -.7, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                interrupt();
                new Thread(() -> sandbox.click()).start();
                back_sandbox.visible = false;
            }
        };

        delete = new Button(textures[DELETE], -.875, .6, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                Change.delete = true;
            }
        };

        clear = new Button(textures[CLEAR_ALL], -.875, .5, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                JavaRenderer.position = Position.make_position_empty(true,color_move_white.active);
                Change.bitBoard = BitBoard.make_bitboard_empty();
            }
        };

        start_analyze = new Button(textures[START_ANALYZE], -.875, .7, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                start_analyze.visible = false;
                stop_analyze.visible = true;
                Sandbox.analyzePosition = new Thread(new Analyze());
                Sandbox.analyzePosition.start();
                Sandbox.start_analyze = true;
            }
        };

        stop_analyze = new Button(textures[STOP_ANALYZE], -.875, .7, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                start_analyze.visible = true;
                stop_analyze.visible = false;
                Sandbox.start_analyze = false;
                while (Sandbox.analyzePosition.isAlive()) {
                    Sandbox.analyze_interrupt = true;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
                JavaRenderer.analyzed_column = null;
                Sandbox.analyze_interrupt = false;
            }
        };

        save = new Button(textures[SAVE_POSITION], .875, .8, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                SaveAndLoad.save();
            }
        };

        load = new Button(textures[LOAD_POSITON], .875, .7, 0.2, 78.0 / 200, false) {
            @Override
            public void click() {
                SaveAndLoad.load();
            }
        };
    }

    private static void arrowStartAnalyze(){
        try {
            if (Sandbox.start_analyze) {
                Thread stop = new Thread(stop_analyze::click);
                stop.start();
                stop.join();
                new Thread(start_analyze::click).start();
            }
        } catch (Exception ignore){
        }
    }

    private static void interrupt() {
        JavaRenderer.lastMove = -1;
        ConnectServer.isEndGame = false;
        play_button.visible = false;
        logOut.visible = false;
        rematch.visible = false;
        resign.visible = false;
        offer.visible = false;
        arrow_back.visible = false;
        arrow_forward.visible = false;
        white.visible = false;
        black.visible = false;
        profile.visible = false;
        editor_button.visible = false;
        delete.visible = false;
        clear.visible = false;
        changingPos = false;
        start_analyze.visible = false;
        stop_analyze.visible = false;
        load.visible = false;
        save.visible = false;
        try {
            ConnectServer.in.close();
            ConnectServer.out.close();
        } catch (Exception ignore) {
        }

        Sandbox.tree.setVisible(false);
        while (sandbox_thread.isAlive() || play.isAlive() || multi.isAlive() || editor.isAlive() || tutorial.isAlive() || ConnectServer.moveListener.isAlive() || screensaver.isAlive()) {
            isInterrupted = true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        JavaRenderer.isScreensaverOn = false;
        JavaRenderer.position = Position.make_position_from_position(JavaRenderer.sandbox_position);
        isInterrupted = false;
        JavaRenderer.analyzed_column = null;
        JavaRenderer.score = null;
        if (!fastMode)
            JavaRenderer.speed = 0.001;
        else
            JavaRenderer.speed = 1;
        Ai.history_moves.clear();
        new_game.visible = false;
        sandbox.visible = false;
        multiplayer.visible = false;
        back.visible = true;
        tutorial_button.visible = false;
        color_move_white.visible = false;
        color_move_black.visible = false;
    }

    static void display(GL2 gl) {
        Menu.gl = gl;
        square(textures[FON], -1, 1, -0.75, -1);
        square(textures[FON], 1, 1, 0.75, -1);
        new_game.display();
        sandbox.display();
        multiplayer.display();
        back.display();
        white.display();
        black.display();
        exit.display();
        tutorial_button.display();
        color_move_black.display();
        color_move_white.display();
        arrow_back.display();
        arrow_forward.display();
        resign.display();
        rematch.display();
        offer.display();
        profile.display();
        logOut.display();
        play_button.display();
        editor_button.display();
        back_sandbox.display();
        delete.display();
        clear.display();
        start_analyze.display();
        stop_analyze.display();
        load.display();
        save.display();
        if (changingPos) {
            printStr("Next Move:", -.95, .35);
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

        if (ConnectServer.opp_rec)
            square(textures[OPPONENT_RECORDING], -.75, -.7, -.3, -.9);
        else if (ConnectServer.playback)
            square(textures[PLAYBACK], -.75, -.7, -.3, -.9);
        else if (RecordVoice.pressed_R)
            square(textures[YOU_RECORDING], -.75, -.7, -.3, -.9);

        printStr(message, -.72, -.85);

    }

    static void printStr(String str, double x, double y) {
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

    public abstract static class Button {
        double x1 = 0;
        double y1 = 0;
        double x2 = 0;
        double y2 = 0;
        int texture = 0;
        public boolean visible;
        double width;
        double ratio;

        Button(int texture, double xc, double yc, double width, double ratio, boolean visible) {
            x1 = xc - width / 2;
            y1 = yc + width * ratio / 2;
            x2 = xc + width / 2;
            y2 = yc - width * ratio / 2;
            this.texture = texture;
            this.visible = visible;
            this.width = width;
            this.ratio = ratio;
        }

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

        void setLocation(int height, double minus){
            double yc = 1 - 2 * 200.0/height - minus;
            y1 = yc + width * ratio / 2;
            y2 = yc - width * ratio / 2;
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
                printStr(text, x + size + .01, y - size);
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
