package Jogl;

import java.io.Serializable;

public class Ball implements Serializable {
    float x;
    float y;
    public float z;
    boolean onGround;
    double speed;
    public int column;
    boolean white;

    public Ball(int column, boolean white){
        x = -0.6f+0.4f*(3 - column%4);
        y = -0.6f+0.4f*(column/4);
        z = 2.0f;
        onGround = false;
        speed = 0;
        this.column = column;
        this.white = white;
    }

    Ball(boolean white, int bit){
        this.column = bit % 16;
        x = -0.6f+0.4f*(3 - column%4);
        y = -0.6f+0.4f*(column/4);
        z = (float) (0.15 + 0.3 * (bit / 16));
        onGround = true;
        speed = 0;
        this.white = white;
    }

    int getColumn() {
        return column;
    }
}