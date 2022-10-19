package byow.Core;


import byow.TileEngine.Tileset;
import byow.TileEngine.TETile;

public class Hallway {
    private Position bottomLeft;
    private boolean horizontal;
    private int length;

    public Hallway(Position bottomLeft, boolean horizontal, int length) {
        this.bottomLeft = bottomLeft;
        this.horizontal = horizontal;
        this.length = length;
    }

    public void makeHallway(TETile[][] tiles) {
        int x = bottomLeft.getX();
        int y = bottomLeft.getY();
        if (horizontal) {
            for (int i = x; i < x + length; i++) {
                tiles[i][y] = Tileset.FLOOR;
                if (tiles[i][y + 1] == null) {
                    tiles[i][y + 1] = Tileset.WALL;
                }
                if (tiles[i][y - 1] == null) {
                    tiles[i][y - 1] = Tileset.WALL;
                }
            }
        } else {
            for (int j = y; j < y + length; j++) {
                tiles[x][j] = Tileset.FLOOR;
                if (tiles[x + 1][j] == null) {
                    tiles[x + 1][j] = Tileset.WALL;
                }
                if (tiles[x - 1][j] == null) {
                    tiles[x - 1][j] = Tileset.WALL;
                }
            }
        }
    }
}
