package Jogl;

public class Ball {
    float x;
    float y;
    float z;
    boolean onGround;
    double speed;
    private int column;
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
        onGround = false;
        speed = 0;
        this.white = white;
    }

    int getColumn() {
        return column;
    }
}