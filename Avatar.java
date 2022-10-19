package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Avatar {
    private int xCoordinate;
    private int yCoordinate;
    private int numHeart;
    private int numKey;
    private TETile tile;


    public Avatar(int x, int y, char c) {
        this.xCoordinate = x;
        this.yCoordinate = y;
        if (c == 'W' || c == 'w') {
            tile = Tileset.WATER;
        }
        if (c == 'F' || c == 'f') {
            tile = Tileset.FLOWER;
        }
        if (c == 'T' || c == 't') {
            tile = Tileset.TREE;
        }
        if (c == 'D' || c == 'd') {
            tile = Tileset.AVATAR;
        }
        numHeart = 5;
        numKey = 0;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public TETile getTile() {
        return tile;
    }

    public void markAvatar(TETile[][] tiles) {
        tiles[xCoordinate][yCoordinate] = tile;
    }

    public int getNumHeart() {
        return numHeart;
    }

    public int getNumKey() {
        return numKey;
    }

    public void incrementKey() {
        numKey += 1;
    }

    public void decrementHeart() {
        numHeart -= 1;
    }

    public boolean stillAlive() {
        return numHeart > 0;
    }

    public boolean meetWinCondition() {return numKey > 5;}





    public void moveEast() {
        xCoordinate += 1;
    }

    public void moveWest() {
        xCoordinate -= 1;
    }

    public void moveSouth() {
        yCoordinate -= 1;
    }

    public void moveNorth() {
        yCoordinate += 1;
    }
}


