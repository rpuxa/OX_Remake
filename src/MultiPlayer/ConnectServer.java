package MultiPlayer;

import Engine.BitBoard;
import Engine.Play;
import Jogl.Ball;
import Jogl.JavaRenderer;
import Jogl.Menu;
import Jogl.Position;
import static Jogl.Menu.massage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public class ConnectServer implements Runnable {

    public static InetAddress ipAddress;
    private final static int server_port = 7158;
    private static String server_command;
    private static String BEGIN_GAME_FOR_WHITE = "white";
    private static String BEGIN_GAME_FOR_BLACK = "black";
    private static String GAME_OVER = "close";
    private static String LOST_CONNECTION = "lost";
    private static String CHECK_CONNECTION = "check";
    private static Thread moveListner;
    private static Thread serverListner;

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

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            serverListner = new Thread(new ServerListener(in));
            serverListner.start();

            while (true){
                if (server_command != null){
                    if (Objects.equals(server_command, BEGIN_GAME_FOR_WHITE)) {
                        massage = "Opponent found! You play for white";
                        Menu.show_turn = true;
                        JavaRenderer.position = Position.make_position_empty(true, true);
                        moveListner = new Thread(new MoveListener(out));
                        moveListner.start();
                    }
                    else if (Objects.equals(server_command, BEGIN_GAME_FOR_BLACK)){
                        massage = "Opponent found! You play for black";
                        Menu.show_turn = true;
                        JavaRenderer.position = Position.make_position_empty(false, true);
                        moveListner = new Thread(new MoveListener(out));
                        moveListner.start();
                    }
                    else if (Objects.equals(server_command, LOST_CONNECTION)){
                        massage = "You opponent lost connection. You win!";
                        return;
                    }else if (Objects.equals(server_command, CHECK_CONNECTION)) {
                        server_command = null;
                    } else {
                        int move = Integer.parseInt(server_command);
                        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
                        boolean isTurnWhite = JavaRenderer.position.isTurnWhite;
                        bitBoard = BitBoard.makeMove(bitBoard, isTurnWhite, (byte) move);
                        JavaRenderer.position.balls.add(new Ball(move, isTurnWhite));
                        JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
                        JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                        Play.checkEnd(bitBoard);
                    }
                    server_command = null;
                }
                if (Menu.isInterrupted){
                    Thread.sleep(300);
                    in.close();
                    return;
                }
                Thread.sleep(500);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class ServerListener implements Runnable {

        DataInputStream in;

        ServerListener(DataInputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    server_command = in.readUTF();
                    Thread.sleep(100);
                    if (Menu.isInterrupted)
                        return;
                }
            } catch (Exception ignored) {
                massage = "Lost connection with server.";
            }
        }
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
                    if (JavaRenderer.position.end_game != 0){
                        out.writeUTF(GAME_OVER);
                        out.flush();
                        return;
                    }
                    if (JavaRenderer.column_chosen != null){
                        int move = JavaRenderer.column_chosen;
                        JavaRenderer.column_chosen = null;
                        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
                        boolean isTurnWhite = JavaRenderer.position.isTurnWhite;
                        bitBoard = BitBoard.makeMove(bitBoard, isTurnWhite, (byte) move);
                        JavaRenderer.position.balls.add(new Ball(move, isTurnWhite));
                        JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
                        JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                        Play.checkEnd(bitBoard);

                        out.writeUTF("" + move);
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
