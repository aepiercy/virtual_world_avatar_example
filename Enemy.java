package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Enemy {
    private int xCoordinate;
    private int yCoordinate;

    public Enemy(int x, int y) {
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    public Enemy(Position position) {
        this.xCoordinate = position.getX();
        this.yCoordinate = position.getY();
    }

    public void markEnemy(TETile[][] tiles) {
        if (tiles[xCoordinate][yCoordinate] != Tileset.AVATAR){
            tiles[xCoordinate][yCoordinate] = Tileset.ENEMY;
        }
    }
}
