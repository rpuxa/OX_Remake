package MultiPlayer;

import Engine.BitBoard;
import Engine.Play;
import Jogl.Ball;
import Jogl.JavaRenderer;
import Jogl.Menu;
import Jogl.Position;

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

    @Override
    public void run() {
        try {
            if (ipAddress == null)
                ipAddress = InetAddress.getByName("localhost");
            Socket socket;
            System.out.println("Подключение к серверу ...");
            while (true) {
                try {
                    socket = new Socket(ipAddress, server_port);
                    break;
                } catch (ConnectException ignored) {
                }
                if (Menu.isInterrupted)
                    return;
            }
            System.out.println("Связь с сервером установленна.");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            new Thread(new ServerListener(in)).start();

            System.out.println("Ожидание игроков...");

            while (true){
                if (server_command != null){
                    if (Objects.equals(server_command, BEGIN_GAME_FOR_WHITE)) {
                        System.out.println("Оппонент найден! Вы играете за белых");
                        JavaRenderer.position = Position.make_position_empty(true, true);
                        new Thread(new MoveListener(out)).start();
                    }
                    else if (Objects.equals(server_command, BEGIN_GAME_FOR_BLACK)){
                        System.out.println("Оппонент найден! Вы играете за черных");
                        JavaRenderer.position = Position.make_position_empty(false, true);
                        new Thread(new MoveListener(out)).start();
                    }
                    else if (Objects.equals(server_command, LOST_CONNECTION)){
                        System.out.println("Ваш оппонент потерял соединение.");
                        return;
                    } else {
                        int move = Integer.parseInt(server_command);
                        BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(JavaRenderer.position.bitBoard);
                        boolean isTurnWhite = JavaRenderer.position.isTurnWhite;
                        bitBoard = BitBoard.makeMove(bitBoard, isTurnWhite, (byte) move);
                        JavaRenderer.position.balls.add(new Ball(move, isTurnWhite));
                        JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
                        JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                    }
                    server_command = null;
                }
                if (Menu.isInterrupted)
                    return;
                Thread.sleep(500);
            }


        } catch (Exception ignore) {
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
                    Thread.sleep(1000);
                    if (Menu.isInterrupted)
                        return;
                }
            } catch (Exception ignored) {
                System.out.println("Связь потеряна");
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
                        Menu.multi.interrupt();
                        out.flush();
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
