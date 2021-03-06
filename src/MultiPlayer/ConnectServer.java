package MultiPlayer;

import Engine.BitBoard;
import PlayEngine.Play;
import Jogl.*;
import Jogl.Menu;
import MultiPlayer.Accout.LoginOrNewAccount;
import MultiPlayer.Accout.NewAccount;

import javax.sound.sampled.*;
import javax.swing.*;

import static Jogl.Menu.message;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectServer implements Runnable {

    public static InetAddress ipAddress;
    private final static int server_port = 7158;
    private static final int CHECK_CONNECTION = 0;
    private static final int GIVE_LOGIN_AND_PASS = 1;
    private static final int SET_PROFILE = 2;
    private static final int CREATE_NEW_ACCOUNT = 3;
    private static final int LOGIN = 4;
    public static final int ACCOUNT_ALREADY_EXISTS = 5;
    public static final int UNCORRECTED_LOGIN_OR_PASSWORD = 6;
    private static final int ACCOUNT_CREATED = 7;
    static final int UPDATE_PROFILE = 8;
    private static final int BEGIN_GAME_FOR_WHITE = 10;
    private static final int BEGIN_GAME_FOR_BLACK = 11;
    private static final int LOST_CONNECTION = 12;
    private static final int SET_OPPONENT_PROFILE = 13;
    static final int CHAT_MESSAGE = 14;
    private static final int SEND_MOVE = 15;
    private static final int RESIGN = 16;
    private static final int OFFER_DRAW = 17;
    private static final int REMATCH = 18;
    private static final int REMATCH_WHITE = 0;
    private static final int REMATCH_BLACK = 1;
    private static final int ACCEPT_DRAW = 19;
    private final static int LOG_OUT = 20;
    public final static int CHAT_AUDIO = 21;
    public final static int CHAT_AUDIO_OPPONENT_RECORDING = 22;
    private final static int SET_TIME = 23;
    private final static int TIME_END = 24;
    final static int UPDATE_PLAYER_LIST = 25;
    final static int INVITE_OPPONENT = 26;
    private final static int START_GAME_WITH_OPPONENT = 27;
    private final static int PLAYER_NOT_FOUND = 28;
    final static int GET_PLAYER_PROFILE = 29;
    private final static int SET_YOUR_ID = 30;
    private final static int INVITE_ACCEPT = 31;
    public static Thread moveListener = new Thread();
    public static Chat chat = new Chat();
    public static Versus versus = new Versus();
    public static Time time = new Time();
    public static Lobby lobby = new Lobby(new ArrayList<>(),0);
    public static ObjectOutputStream out;
    public static ObjectInputStream in;
    public static boolean isEndGame = false;
    public static boolean opp_found = false;
    public static boolean playback = false;
    public static boolean opp_rec = false;
    private static Thread ourTime = new Thread();
    private static Thread oppTime = new Thread();
    private long ID = 0;
    private int playTime = 600;

    @Override
    public void run() {
        try {
            if (ipAddress == null)
                ipAddress = InetAddress.getByName("localhost");
            Socket socket;
            message = "Connecting Server...";
            while (true) {
                try {
                    socket = new Socket(ipAddress, server_port);
                    break;
                } catch (ConnectException ignored) {
                }
                if (Menu.isInterrupted)
                    return;
            }

            message = null;
            JavaDia.scroll_lobby.setVisible(true);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                try {
                    ServerCommand server_command = (ServerCommand) in.readObject();
                    Object data = server_command.getData();
                    switch (server_command.getCommand()) {
                        case BEGIN_GAME_FOR_WHITE: {
                            if (data != null)
                                playTime = (int) data;
                            beginGame(true);
                            break;
                        }
                        case BEGIN_GAME_FOR_BLACK: {
                            if (data != null)
                                playTime = (int) data;
                            beginGame(false);
                            break;
                        }
                        case LOST_CONNECTION: {
                            endGame(null);
                            Menu.resign.visible = false;
                            Menu.offer.visible = false;
                            Menu.rematch.visible = false;
                            opp_found = false;
                            message = "You opponent left.";
                            chat.showMessage(message);
                            return;
                        }
                        case CHECK_CONNECTION: {
                            break;
                        }
                        case SET_OPPONENT_PROFILE: {
                            chat.setOpponentProfile((Profile) data);
                            showVersus();
                            break;
                        }

                        case SET_PROFILE: {
                            Chat.myProfile = (Profile) data;
                            Menu.logOut.visible = true;
                            Menu.profile.visible = true;
                            break;
                        }

                        case CHAT_MESSAGE: {
                            chat.showMessage((String) data);
                            Menu.sounds("message");
                            break;
                        }

                        case CHAT_AUDIO_OPPONENT_RECORDING: {
                            opp_rec = true;
                            break;
                        }

                        case CHAT_AUDIO: {
                            while (playback)
                                Thread.sleep(10);
                            File file = new File("Sounds/opp_rec.wav");
                            try {
                                FileOutputStream os = new FileOutputStream(file);

                                os.write((byte[]) data);

                                os.close();

                                try {
                                    AudioInputStream ais = AudioSystem.getAudioInputStream(file);
                                    Clip clip = AudioSystem.getClip();
                                    clip.open(ais);
                                    clip.setFramePosition(0);
                                    clip.start();
                                    opp_rec = false;
                                    playback = true;
                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(clip.getMicrosecondLength()/1000);
                                            playback = false;
                                        } catch (InterruptedException ignored) {
                                        }
                                    }).start();

                                } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ignored) {
                                }
                            } catch (Exception ignored) {
                            }
                            break;
                        }
                        case SEND_MOVE: {
                            int move = (int) data;
                            BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
                            boolean isTurnWhite = JavaRenderer.position.isTurnWhite;
                            bitBoard = BitBoard.makeMove(bitBoard, isTurnWhite, (byte) move);
                            JavaRenderer.position.balls.add(new Ball(move, isTurnWhite));
                            JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
                            JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                            if (!Play.checkEnd(bitBoard)) {
                                versus.swapMove();
                            } else {
                                endGame(null);
                                addResult(checkEnd());
                            }
                            Position.add_to_history_positions(JavaRenderer.position,false);
                            break;
                        }

                        case SET_TIME: {
                            long oppTime = (long) (data);
                            ourTime = new Thread(() -> time.startTime(JavaRenderer.position.human_plays_for_white,true));
                            ourTime.start();
                            ConnectServer.oppTime.join();
                            if (JavaRenderer.position.human_plays_for_white)
                                time.setTimeBlack(oppTime);
                            else
                                time.setTimeWhite(oppTime);
                            break;
                        }

                        case TIME_END: {
                            endGame((!JavaRenderer.position.human_plays_for_white) ? Position.BLACK_WINS : Position.WHITE_WINS);
                            JLabel label = (JavaRenderer.position.human_plays_for_white) ? time.timeBlackLabel : time.timeWhiteLabel;
                            label.setText("Zeit");
                            UIManager.put("OptionPane.okButtonText", "ОК");
                            addResult(1);
                            JOptionPane.showConfirmDialog(null, "У противника вышло время! Вы победили!", "Сетевая игра", JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION);
                            break;
                        }

                        case RESIGN: {
                            endGame((!JavaRenderer.position.human_plays_for_white) ? Position.BLACK_WINS : Position.WHITE_WINS);
                            UIManager.put("OptionPane.okButtonText", "ОК");
                            JOptionPane.showConfirmDialog(null, "Ваш противник сдался! Вы победили!", "Сетевая игра", JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION);
                            addResult(1);
                            break;
                        }

                        case OFFER_DRAW: {
                            UIManager.put("OptionPane.yesButtonText"   , "Согласиться"    );
                            UIManager.put("OptionPane.noButtonText"    , "Отказаться"   );
                            if (JOptionPane.showConfirmDialog(null, "Ваш оппонент предлагает ничью", "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                                endGame(Position.DRAW);
                                acceptDraw();
                            }
                            break;
                        }
                        case ACCEPT_DRAW: {
                            endGame(Position.DRAW);
                            UIManager.put("OptionPane.okButtonText", "ОК");
                            JOptionPane.showConfirmDialog(null, "Ваш оппонент согласился на ничью", "Сетевая игра", JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION);
                            addResult(0);
                            break;
                        }

                        case REMATCH: {
                            UIManager.put("OptionPane.yesButtonText", "Согласиться");
                            UIManager.put("OptionPane.noButtonText", "Отказаться");
                            if ((int)data == REMATCH_WHITE) {
                                if (JOptionPane.showConfirmDialog(null, "Ваш оппонент предлагает реванш за белых (он за белых)", "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                                    try {
                                        out.writeObject(new ServerCommand(null,BEGIN_GAME_FOR_WHITE));
                                        out.flush();
                                    } catch (Exception ignore){
                                    }
                                    beginGame(false);
                                }
                            } else if (JOptionPane.showConfirmDialog(null, "Ваш оппонент предлагает реванш за черных (он за черных)", "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                                try {
                                    out.writeObject(new ServerCommand(null,BEGIN_GAME_FOR_BLACK));
                                    out.flush();
                                } catch (Exception ignore){
                                }
                                beginGame(true);
                            }
                            break;
                        }

                        case GIVE_LOGIN_AND_PASS: {
                            int message = 0;
                            while (true) {
                                LoginOrNewAccount l = new LoginOrNewAccount(message);
                                if (l.getOperation() == LoginOrNewAccount.CANCELED)
                                    new Thread(() -> Menu.back.click()).start();
                                else if (l.getOperation() == LoginOrNewAccount.LOGIN) {
                                    out.writeObject(new ServerCommand(l.getData(), LOGIN));
                                } else if (l.getOperation() == LoginOrNewAccount.NEW_ACCOUNT) {
                                    out.writeObject(new ServerCommand(l.getData(), CREATE_NEW_ACCOUNT));
                                }

                                out.flush();
                                ServerCommand serverCommand1 = (ServerCommand) in.readObject();
                                if (serverCommand1.getCommand() == ACCOUNT_ALREADY_EXISTS)
                                    message = ACCOUNT_ALREADY_EXISTS;
                                else if (serverCommand1.getCommand() == ACCOUNT_CREATED){
                                    Menu.profile.visible = true;
                                    Menu.logOut.visible = true;
                                    out.writeObject(new ServerCommand(NewAccount.profile,UPDATE_PROFILE));
                                    out.flush();
                                    Chat.myProfile = Profile.make_profile_from_profile(NewAccount.profile);
                                    JOptionPane.showMessageDialog(JavaDia.frame, "Поздравляем! Учетная запись создана", "Сетевая игра", JOptionPane.INFORMATION_MESSAGE);
                                    break;
                                } else if (serverCommand1.getCommand() == UNCORRECTED_LOGIN_OR_PASSWORD) {
                                    message = UNCORRECTED_LOGIN_OR_PASSWORD;
                                } else if (serverCommand1.getCommand() == SET_PROFILE){
                                    Menu.profile.visible = true;
                                    Menu.logOut.visible = true;
                                    Chat.myProfile = Profile.make_profile_from_profile((Profile) serverCommand1.getData());
                                    break;
                                }
                            }
                            break;
                        }

                        case UPDATE_PLAYER_LIST: {
                            JavaDia.scroll_lobby.remove(lobby);
                            lobby = new Lobby((ArrayList<Player>) data,ID);
                            JavaDia.scroll_lobby.add(lobby);
                            lobby.setBounds(0,0,310,300);
                            JavaDia.scroll_lobby.repaint();
                            JavaDia.scroll_lobby.revalidate();
                            break;
                        }

                        case GET_PLAYER_PROFILE:{
                            new ProfileWindow((Profile) data,null);
                            break;
                        }

                        case SET_YOUR_ID:{
                            ID = (long) data;
                            break;
                        }

                        case PLAYER_NOT_FOUND:{
                            JOptionPane.showMessageDialog(null, "Игрок не найден!\nВозможно он вышел из игры", "Лобби", JOptionPane.ERROR_MESSAGE);
                            break;
                        }

                        case INVITE_OPPONENT:{
                            UIManager.put("OptionPane.yesButtonText", "Согласиться");
                            UIManager.put("OptionPane.noButtonText", "Отказаться");
                            Invite invite = (Invite) data;
                            String color;
                            if (invite.getSide() == Invite.RANDOM)
                                color = "произвольный";
                            else if (invite.getSide() == Invite.WHITE)
                                color = "черный";
                            else
                                color = "белый";

                            String[] message = {"Оппонент " + invite.getPlayer().getName() + " с рейтингом " + invite.getPlayer().getRating(),
                            "приглашает вас в игру с данными настройками:",
                            "Ваш цвет: " + color,
                            "Контроль времени: " + invite.getTime()/60 +"min. " + invite.getTime()%60 + "sec."};
                            if (JOptionPane.showConfirmDialog(null, message, "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                                out.writeObject(new ServerCommand(invite,START_GAME_WITH_OPPONENT));
                                out.flush();
                            }
                            break;
                        }

                        case INVITE_ACCEPT: {
                            out.writeObject(new ServerCommand(null,INVITE_ACCEPT));
                            out.flush();
                            break;
                        }
                    }
                } catch (IOException e) {
                    message = "Lost connection with server.";
                    opp_found = false;
                    Thread.sleep(1000);
                }
                if (Menu.isInterrupted) {
                    opp_found = false;
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beginGame(boolean white) throws IOException {
        Menu.save.visible = true;
        JavaRenderer.lastMove = -1;
        opp_found = true;
        isEndGame = false;
        message = "Game started! You play for " + ((white) ? "white" : "black");
        JavaRenderer.position = Position.make_position_empty(white, true);
        moveListener = new Thread(new MoveListener(out));
        moveListener.start();
        chat.setOut(out);
        showVersus();
        JavaDia.scroll_lobby.setVisible(false);
        JavaDia.scroll_lobby.remove(lobby);
        Menu.logOut.visible = false;
        time.setBeginTime(playTime);
        Menu.sounds("begin_game");
        Menu.resign.visible = true;
        Menu.offer.visible = true;
        Menu.rematch.visible = false;
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            message = null;
        }).start();
        Position.add_to_history_positions(JavaRenderer.position,true);

    }

    private static void showVersus(){
        try {
            if (JavaRenderer.position.human_plays_for_white)
                versus.setProfiles(Chat.myProfile, chat.opponentProfile);
            else
                versus.setProfiles(chat.opponentProfile, Chat.myProfile);
        } catch (NullPointerException ignored) {
        }

        try {
            versus.setVisible(true);
        } catch (NullPointerException ignored) {
        }
    }

    public static void resign(){
        try {
            endGame((JavaRenderer.position.human_plays_for_white) ? Position.BLACK_WINS : Position.WHITE_WINS);
            out.writeObject(new ServerCommand(null,RESIGN));
            out.flush();
            addResult(-1);
        } catch (Exception ignore){
        }
    }

    public static void offer(){
        try {
            out.writeObject(new ServerCommand(null,OFFER_DRAW));
            out.flush();
        } catch (Exception ignore){
        }
    }

    private static void acceptDraw(){
        try {
            out.writeObject(new ServerCommand(null,ACCEPT_DRAW));
            out.flush();
            addResult(0);
        } catch (Exception ignore){
        }
    }

    public static void rematch(boolean white){
        try {
            if (white)
                out.writeObject(new ServerCommand(REMATCH_WHITE,REMATCH));
            else
                out.writeObject(new ServerCommand(REMATCH_BLACK,REMATCH));
            out.flush();
        } catch (Exception ignore){
        }
    }

    private static void endGame(Integer result){
        if (result != null)
            JavaRenderer.position.end_game = result;
        time.stop();
        isEndGame = true;
        Menu.resign.visible = false;
        Menu.offer.visible = false;
        Menu.rematch.visible = true;
    }

    static void zeit(){
        try {
            out.writeObject(new ServerCommand(null,TIME_END));
            out.flush();
        } catch (IOException e) {
        }
        endGame((JavaRenderer.position.human_plays_for_white) ? Position.BLACK_WINS : Position.WHITE_WINS);
        UIManager.put("OptionPane.okButtonText", "ОК");
        addResult(-1);
        JOptionPane.showConfirmDialog(null, "У вас вышло время. Вы проиграли.", "Сетевая игра", JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION);
    }

    public static void showProfile(){
        new ProfileWindow(Chat.myProfile,out);
    }

    public static void logOut(){
        try {
            out.writeObject(new ServerCommand(null,LOG_OUT));
            out.flush();
        } catch (IOException ignored) {
        }
        new Thread(() -> Menu.back.click()).start();
    }

    private static void addResult(int result) {
        if (result == -1) {
            chat.opponentProfile.wins++;
            Chat.myProfile.loses++;
        } else if (result == 1) {
            chat.opponentProfile.loses++;
            Chat.myProfile.wins++;
        } else {
            chat.opponentProfile.draws++;
            Chat.myProfile.draws++;
        }
        int myRating = Chat.myProfile.rating, oppRating = chat.opponentProfile.rating;
        Chat.myProfile.rating = ratingCount(myRating, oppRating, (double) (result) / 2 + 0.5);
        chat.opponentProfile.rating = ratingCount(oppRating, myRating, (double) (-result) / 2 + 0.5);
            try {
                out.writeObject(new ServerCommand(Chat.myProfile, UPDATE_PROFILE));
                out.flush();
            } catch (IOException ignored) {
            }

    }

    private static int ratingCount(double r1, double r2, double result){
        return (int) Math.round(r1 + 40*(result - 1.0/(1 + Math.pow(10,(r2 - r1)/400))));
    }

    private static int checkEnd(){
        if (JavaRenderer.position.end_game == Position.DRAW)
            return 0;
        if (JavaRenderer.position.human_plays_for_white && JavaRenderer.position.end_game == Position.WHITE_WINS || !JavaRenderer.position.human_plays_for_white && JavaRenderer.position.end_game == Position.BLACK_WINS)
            return 1;
       return -1;
    }

    class MoveListener implements Runnable {

        ObjectOutputStream out;

        MoveListener(ObjectOutputStream out) {
            this.out = out;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (isEndGame)
                        return;

                    if (JavaRenderer.column_chosen != null) {
                        int move = JavaRenderer.column_chosen;
                        JavaRenderer.column_chosen = null;
                        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
                        boolean isTurnWhite = JavaRenderer.position.isTurnWhite;
                        bitBoard = BitBoard.makeMove(bitBoard, isTurnWhite, (byte) move);
                        JavaRenderer.position.balls.add(new Ball(move, isTurnWhite));
                        JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
                        JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                        if (Play.checkEnd(bitBoard)) {
                            addResult(checkEnd());
                            endGame(null);
                        } else
                            versus.swapMove();
                        Position.add_to_history_positions(JavaRenderer.position,false);
                        out.writeObject(new ServerCommand(move,SEND_MOVE));
                        out.flush();
                        oppTime = new Thread(() -> time.startTime(!JavaRenderer.position.human_plays_for_white,false));
                        oppTime.start();
                        ourTime.join();
                        out.writeObject(new ServerCommand((JavaRenderer.position.human_plays_for_white) ? time.getTimeWhite() : time.getTimeBlack(),SET_TIME));
                        out.flush();
                    }
                    Thread.sleep(100);
                    if (Menu.isInterrupted)
                        return;
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }
}