package Tutorial;

import Engine.Ai;
import Engine.BitBoard;
import Engine.Play;
import Jogl.Ball;
import Jogl.JavaRenderer;
import Jogl.Menu;
import Jogl.Position;

import javax.swing.*;

public class Tutorial implements Runnable {

    public static boolean canMove = false;

    @Override
    public void run() {
        JavaRenderer.position = Position.make_position_empty(true,true);

        new Stage() {
            @Override
            void set() {
                text = "Добро пожаловать в игру крестики нолики в кубе!\nЭтот туториал ознокомит вас с правилами игры";
            }

        }.start();

        new Stage() {
            @Override
            void set() {
                text = "Игроки кидают \"бусинки\" поочереди в один из столбиков!\nЦель игры составить ряд из 4 бусинок своего цвета.\nПри этом не важно как будут выстроенны бусинки (по диагонали, в стобик и.т.п),\n главное, чтобы они составляли пряму линию";
            }

        }.start();

        new Stage() {

            @Override
            void set() {
                text = "Игра происходит на доске с 16 столбиками, на которые нанизываются бусинки.\nЗадача:\nДержа ЛКМ попробуйте изменить угол обзора доски.";
            }
        }.start();
        try {
            changeView1();
        } catch (InterruptedException ignored) {
        }

        new Stage() {

            @Override
            void set() {
                text = "Отлично! Теперь удерживая ПКМ разверните доску";
            }
        }.start();
        try {
            changeView2();
        } catch (InterruptedException ignored) {
        }

        new Stage() {

            @Override
            void set() {
                text = "А теперь попробуйте изменить размер доски колесиком мыши";
            }
        }.start();
        try {
            changeView3();
        } catch (InterruptedException ignored) {
        }

        canMove = true;

        new Stage() {

            @Override
            void set() {
                text = "Для того чтобы осуществить ход нужно\n навести мышь на нужный столбик и сделать двойной щелчок.\nЗадача:\nСделать ход в любое место.";
            }
        }.start();

        try {
            makeMove();
        } catch (InterruptedException ignored) {
        }



        new Stage(1) {

            @Override
            void set() {
                text = "А сейчас попробуйте решить пару простейший задач, для усвоения правил игры\nЗадача:\nРешить несколько упражнений точным ходом\n(вы играете за белых)";
                bitBoard = new BitBoard(0b1011,0b100110000000000);
            }
        }.start();

        new Stage(1) {

            @Override
            void set() {
                text = "Задача решена!";
                bitBoard = new BitBoard(0b1000100000001,0b1110);
            }
        }.start();

        new Stage(1) {

            @Override
            void set() {
                text = "Задача решена!";
                bitBoard = new BitBoard(0b1000000000100001,0b10000000000000110);
            }
        }.start();


        new Stage(1) {

            @Override
            void set() {
                text = "Задача решена!";
                bitBoard = new BitBoard(0b1100_00000000_00001010_00000000_00000001L,262222L);
            }
        }.start();

        new Stage(1) {

            @Override
            void set() {
                text = "Задача решена!";
                bitBoard = new BitBoard(0b10000_00000000_00000001_00000000_00010000_00000000_00000000_00010001L,17592202825984L);
            }
        }.start();

        new Stage(1) {

            @Override
            void set() {
                text = "Задача решена!";
                bitBoard = new BitBoard(0b1_00000000_00100000_00000100_00000001_00000000_00000001L,4297065504L);
            }
        }.start();
      canMove = true;

        new Stage(2) {

            @Override
            void set() {
                text = "Отлично! Но чтобы победить в игре в с сильным противником\n нужно уметь создавать двойные угрозы (узлы), от которых нельзя защитиься\nЗадача:\nНайдите победу в 2 хода создав узел";
                bitBoard = new BitBoard(8274L,7169L);
            }
        }.start();

        new Stage(2) {

            @Override
            void set() {
                text = "Задача решена!";
                bitBoard = new BitBoard(36912L,2626L);
            }
        }.start();

        Menu.back.visible = true;
        canMove = false;

        new Stage() {

            @Override
            void set() {
                text = "Вы изучили все основы игры!\nНажмите кнопку \"<-back\" и \"New Game\",\nчтобы попробовать сразиться с компьютером.";
            }
        }.start();
    }

    private static void changeView1() throws InterruptedException {
        double x = JavaRenderer.AbsAngleX;
        while (x == JavaRenderer.AbsAngleX)
            Thread.sleep(100);
        Thread.sleep(2000);
    }

    private static void changeView2() throws InterruptedException {
        double z = JavaRenderer.AbsAngleZ;
        while (z == JavaRenderer.AbsAngleZ)
            Thread.sleep(100);
        Thread.sleep(2000);
    }

    private static void changeView3() throws InterruptedException {
        double dist = JavaRenderer.distance;
        while (dist == JavaRenderer.distance)
            Thread.sleep(100);
        Thread.sleep(2000);
    }

    private static void makeMove() throws InterruptedException {
        while (JavaRenderer.position.balls.size() == 0 || !JavaRenderer.position.allOnGround()) {
            if (JavaRenderer.column_chosen != null) {
                int n = JavaRenderer.column_chosen;
                JavaRenderer.column_chosen = null;
                JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
                JavaRenderer.position.balls.add(new Ball(n, true));
            }
            Thread.sleep(100);
        }
    }


    private abstract class Stage {
        String text;
        int action;
        BitBoard bitBoard;
        Position position;

        Stage(){
        }

        Stage(int action){
            this.action = action;
        }

        abstract void set();

        void start() {
            set();
            JOptionPane.showMessageDialog(null, text, "Туториал к игре", JOptionPane.PLAIN_MESSAGE);
            if (action != 0) {
                position = Position.make_position_from_bitboard(bitBoard);
                JavaRenderer.position = Position.make_position_from_position(position);
            }
            if (action == 1)
                makeMove(1);
            if (action == 2)
                makeMove(2);
            if (action == 3)
                makeMove(3);
        }


        void makeMove(int count){
            int moves;
            while (true) {
                BitBoard bitBoard = BitBoard.make_bitboard_from_bitboard(this.bitBoard);
                JavaRenderer.position = Position.make_position_from_position(position);
                moves = 0;
                while (moves < count)
                    try {
                        if (JavaRenderer.column_chosen != null) {
                            int n = JavaRenderer.column_chosen;
                            JavaRenderer.column_chosen = null;
                            bitBoard = BitBoard.makeMove(bitBoard, true, (byte) n);
                            JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
                            JavaRenderer.position.balls.add(new Ball(n, true));
                            JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                            moves++;
                            if (!Play.checkEnd(bitBoard)) {
                                while (!JavaRenderer.position.allOnGround())
                                    Thread.sleep(100);
                                n = new Ai(false).alphaBetaStart(bitBoard, 0, 6)[0];
                                bitBoard = BitBoard.makeMove(bitBoard, false, (byte) n);
                                JavaRenderer.position.isTurnWhite = !JavaRenderer.position.isTurnWhite;
                                JavaRenderer.position.balls.add(new Ball(n, false));
                                JavaRenderer.position.bitBoard = BitBoard.make_bitboard_from_bitboard(bitBoard);
                                Play.checkEnd(bitBoard);
                            }
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                    while (!JavaRenderer.position.allOnGround())
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                if (JavaRenderer.position.end_game != Position.WHITE_WINS) {
                    JavaRenderer.position = Position.make_position_from_position(position);
                    JOptionPane.showMessageDialog(null, "Неправильно! Попробуйте снова", "Туториал к игре", JOptionPane.PLAIN_MESSAGE);
                }
                else return;
            }
        }

    }
}
