package MultiPlayer;

import java.io.Serializable;

public class Invite implements Serializable {
    private int time;
    private int side;
    private Player player;
    private long toPlayerId;

    public static int WHITE = 1;
    public static int RANDOM = 0;
    public static int BLACK = -1;

    public Invite(int time, int side, Player player, long toPlayerId) {
        this.time = time;
        this.side = side;
        this.player = player;
        this.toPlayerId = toPlayerId;
    }

    public int getTime() {
        return time;
    }

    public int getSide() {
        return side;
    }

    public Player getPlayer() {
        return player;
    }

    public long getToPlayerId() {
        return toPlayerId;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
