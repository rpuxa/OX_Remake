package Jogl;

public class Ball {
    float x;
    float y;
    float z;
    boolean onGround;
    double speed;
    int column;
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

    int getColumn() {
        return column;
    }
}