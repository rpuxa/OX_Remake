package main;

import Engine.BitBoard;
import Engine.Mask;
import Jogl.JavaDia;


public class Main {

    public static void main(String[] args) {
        JavaDia.displayT.start();
        Mask.calculate();
        BitBoard.zKeys_gen();
    }

}
