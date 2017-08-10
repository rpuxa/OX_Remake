package MultiPlayer;

import Engine.BitBoard;
import Engine.Play;
import Jogl.*;
import Jogl.Menu;

import javax.swing.*;

import static Jogl.Menu.massage;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public class ConnectServer implements Runnable {

    public static InetAddress ipAddress;
    private final static int server_port = 7158;
    private final static String BEGIN_GAME_FOR_WHITE = "whte";
    private final static String BEGIN_GAME_FOR_BLACK = "blck";
    private final static String LOST_CONNECTION = "lost";
    private final static String CHECK_CONNECTION = "chck";
    final static String CHAT_MESSAGE = "chat";
    private final static String SET_NICK = "nick";
    private final static String SEND_MOVE = "move";
    private final static String RESIGN = "rsgn";
    private final static String OFFER_DRAW = "ofdr";
    private final static String REMATCH = "rmat";
    private final static String REMATCH_WHITE = REMATCH+"w";
    private final static String REMATCH_BLACK = REMATCH+"b";
    private final static String ACCEPT_DRAW = "acdr";
    public static Thread moveListener = new Thread();
    public static Chat chat;
    public static DataOutputStream out;
    public static DataInputStream in;
    public static boolean isEndGame = false;

    @Override
    public void run() {
        try {
            if (ipAddress == null)
                ipAddress = InetAddress.getByName("localhost");
            Socket socket;
            massage = "Connecting Server...";
            while (true) {
                try {
                    socket = new Socket(ipAddress, server_port);
                    break;
                } catch (ConnectException ignored) {
                }
                if (Menu.isInterrupted)
                    return;
            }
            massage = "Waiting for players. Please wait...";

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            while (true) {
                try {
                    String server_command = in.readUTF();
                    switch (server_command.substring(0, 4)) {
                        case BEGIN_GAME_FOR_WHITE: {
                            beginGame(true);
                            break;
                        }
                        case BEGIN_GAME_FOR_BLACK: {
                           beginGame(false);
                           break;
                        }
                        case LOST_CONNECTION: {
                            endGame();
                            Menu.resign.visible = false;
                            Menu.offer.visible = false;
                            Menu.rematch.visible = false;
                            massage = "You opponent left.";
                            return;
                        }
                        case CHECK_CONNECTION: {
                            break;
                        }
                        case SET_NICK: {
                            chat.setOpponentNick(server_command.substring(4));
                            break;
                        }
                        case CHAT_MESSAGE: {
                            chat.showMessage(server_command.substring(4));
                            Menu.sounds("message");
                            break;
                        }
                        case SEND_MOVE: {
                            int move = Integer.parseInt(server_command.substring(4));
                            BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
                            boolean isTurnWhite = JavaRenderer.position.isTurnWhite;
                            bitBoard = BitBoard.makeMove(bitBoard, isTurnWhite, (byte) move);
                            JavaRenderer.position.balls.add(new Ball(move, isTurnWhite));
                            JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
                            JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                            Play.checkEnd(bitBoard);
                            break;
                        }

                        case RESIGN: {
                            endGame();
                            UIManager.put("OptionPane.okButtonText", "ОК");
                            JOptionPane.showConfirmDialog(null, "Ваш противник сдался! Вы победили!", "Сетевая игра", JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION);
                            break;
                        }

                        case OFFER_DRAW: {
                            UIManager.put("OptionPane.yesButtonText"   , "Согласиться"    );
                            UIManager.put("OptionPane.noButtonText"    , "Отказаться"   );
                            if (JOptionPane.showConfirmDialog(null, "Ваш оппонент предлагает ничью", "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                                endGame();
                                acceptDraw();
                            }
                            break;
                        }
                        case ACCEPT_DRAW: {
                            endGame();
                            Menu.resign.visible = true;
                            Menu.offer.visible = true;
                            Menu.rematch.visible = false;
                            UIManager.put("OptionPane.okButtonText", "ОК");
                            JOptionPane.showConfirmDialog(null, "Ваш оппонент согласился на ничью", "Сетевая игра", JOptionPane.PLAIN_MESSAGE, JOptionPane.CLOSED_OPTION);
                            break;
                        }

                        case REMATCH: {
                            UIManager.put("OptionPane.yesButtonText", "Согласиться");
                            UIManager.put("OptionPane.noButtonText", "Отказаться");
                            if (Objects.equals(server_command.substring(4), "w")) {
                                if (JOptionPane.showConfirmDialog(null, "Ваш оппонент предлагает реванш за белых (он за белых)", "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                                    try {
                                        out.writeUTF(BEGIN_GAME_FOR_WHITE);
                                        out.flush();
                                    } catch (Exception ignore){
                                    }
                                    beginGame(false);
                                }
                            } else if (JOptionPane.showConfirmDialog(null, "Ваш оппонент предлагает реванш за черных (он за черных)", "Сетевая игра", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                                try {
                                    out.writeUTF(BEGIN_GAME_FOR_BLACK);
                                    out.flush();
                                } catch (Exception ignore){
                                }
                                beginGame(true);
                            }
                            break;
                        }
                    }
                } catch (IOException e) {
                    massage = "Lost connection with server.";
                }
                if (Menu.isInterrupted)
                    return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beginGame(boolean white) throws IOException {
        isEndGame = false;
        massage = "Game started! You play for " + ((white) ? "white" : "black");
        Menu.show_turn = true;
        JavaRenderer.position = Position.make_position_empty(white, true);
        moveListener = new Thread(new MoveListener(out));
        moveListener.start();
        if (chat == null)
            chat = new Chat(out);
        out.writeUTF(SET_NICK + Chat.myNick);
        Menu.sounds("begin_game");
        Menu.resign.visible = true;
        Menu.offer.visible = true;
        Menu.rematch.visible = false;
        Point point = JavaDia.frame.getLocation();
        ConnectServer.chat.setLocation((int)(JavaDia.frame.getWidth() - ConnectServer.chat.getWidth() + point.getX()),(int)(JavaDia.frame.getHeight()-ConnectServer.chat.getHeight()+point.getY()));
        ConnectServer.chat.setAlwaysOnTop(true);
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            }
            massage = null;
        }).start();
    }

    public static void resign(){
        try {
            endGame();
            out.writeUTF(RESIGN);
            out.flush();
        } catch (Exception ignore){
        }
    }

    public static void offer(){
        try {
            out.writeUTF(OFFER_DRAW);
            out.flush();
        } catch (Exception ignore){
        }
    }

    private static void acceptDraw(){
        try {
            out.writeUTF(ACCEPT_DRAW);
            out.flush();
        } catch (Exception ignore){
        }
    }

    public static void rematch(boolean white){
        try {
            if (white)
                out.writeUTF(REMATCH_WHITE);
            else
                out.writeUTF(REMATCH_BLACK);
            out.flush();
        } catch (Exception ignore){
        }
    }

    private static void endGame(){
        isEndGame = true;
        Menu.resign.visible = false;
        Menu.offer.visible = false;
        Menu.rematch.visible = true;
    }


    class MoveListener implements Runnable {

        DataOutputStream out;

        MoveListener(DataOutputStream out) {
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
                        if (Play.checkEnd(bitBoard))
                            endGame();

                        out.writeUTF(SEND_MOVE + move);
                        out.flush();
                    }
                    Thread.sleep(100);
                    if (Menu.isInterrupted)
                        return;
                }
            } catch (Exception ignored) {
            }
        }
    }
}