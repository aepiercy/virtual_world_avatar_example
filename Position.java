package byow.Core;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int distance(Position another) {
        int anotherX = another.getX();
        int anotherY = another.getY();
        return Math.abs(x - anotherX) + Math.abs(y - anotherY);
    }
}
