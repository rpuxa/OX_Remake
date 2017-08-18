package Oxpos;

import Jogl.Position;

import java.io.Serializable;
import java.util.ArrayList;

public class OxposFile implements Serializable {
    private ArrayList<Position> game;
    private int moveNuber;

    public OxposFile(ArrayList<Position> game, int moveNuber) {
        this.game = game;
        this.moveNuber = moveNuber;
    }

    public ArrayList<Position> getGame() {
        return game;
    }

    public int getMoveNuber() {
        return moveNuber;
    }

    public void setGame(ArrayList<Position> game) {
        this.game = game;
    }

    public void setMoveNuber(int moveNuber) {
        this.moveNuber = moveNuber;
    }
}
