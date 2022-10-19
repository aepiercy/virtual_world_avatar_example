package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.List;
import java.util.Random;

public class Room {
    private Position bottomLeft;
    private int width;
    private int height;
    private Random random;

    private int floorLeftBound;
    private int floorRightBound;
    private int floorUpperBound;
    private int floorLowerBound;

    private int wallLeftBound;
    private int wallRightBound;
    private int wallUpperBound;
    private int wallLowerBound;

    public Room(Position bottomLeft, int width, int height, Random random) {
        this.bottomLeft = bottomLeft;
        this.width = width;
        this.height = height;
        this.random = random;

        floorLeftBound = bottomLeft.getX();
        floorRightBound = bottomLeft.getX() + width - 1;
        floorLowerBound = bottomLeft.getY();
        floorUpperBound = bottomLeft.getY() + height - 1;

        wallLeftBound = bottomLeft.getX() - 1;
        wallRightBound = bottomLeft.getX() + width;
        wallLowerBound = bottomLeft.getY() - 1;
        wallUpperBound = bottomLeft.getY() + height;
    }


    public void makeRoom(TETile[][] tiles) {
        for (int i = floorLeftBound; i <= floorRightBound; i++) {
            for (int j = floorLowerBound; j <= floorUpperBound; j++) {
                tiles[i][j] = Tileset.FLOOR;
            }
        }

        for (int i = wallLeftBound; i <= wallRightBound; i++) {
            if (tiles[i][wallLowerBound] == null) {
                tiles[i][wallLowerBound] = Tileset.WALL;
            }
            if (tiles[i][wallUpperBound] == null) {
                tiles[i][wallUpperBound] = Tileset.WALL;
            }
        }

        for (int j = wallLowerBound; j <= wallUpperBound; j++) {
            if (tiles[wallLeftBound][j] == null) {
                tiles[wallLeftBound][j] = Tileset.WALL;
            }
            if (tiles[wallRightBound][j] == null) {
                tiles[wallRightBound][j] = Tileset.WALL;
            }
        }
    }

    public boolean overlap(Room another) {
        if (wallLeftBound <= another.wallRightBound
            && wallRightBound >= another.wallLeftBound
            && wallLowerBound <= another.wallUpperBound
            && wallUpperBound >= another.wallLowerBound) {
            return true;
        }
        return false;
    }

    public boolean overlap(List<Room> rooms) {
        if (rooms.isEmpty()) {
            return false;
        }
        for (Room room : rooms) {
            if (overlap(room)) {
                return true;
            }
        }
        return false;
    }

    public void connect(Room another, TETile[][] tiles) {
        if (floorLeftBound <= another.floorRightBound
                && floorRightBound >= another.floorLeftBound) {
            int maxX = Math.min(floorRightBound, another.floorRightBound);
            int minX = Math.max(floorLeftBound, another.floorLeftBound);
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = ((floorLowerBound < another.floorLowerBound)
                    ? floorUpperBound : another.floorUpperBound);
            int length = ((floorLowerBound < another.floorLowerBound)
                    ? another.floorLowerBound - floorUpperBound + 1
                    : floorLowerBound - another.floorUpperBound + 1);
            Hallway hallway = new Hallway(new Position(x, y), false, length);
            hallway.makeHallway(tiles);
        } else if (floorLowerBound <= another.floorUpperBound
                && floorUpperBound >= another.floorLowerBound) {
            int maxY = Math.min(floorUpperBound, another.floorUpperBound);
            int minY = Math.max(floorLowerBound, another.floorLowerBound);
            int x = ((floorLeftBound < another.floorLeftBound)
                    ? floorRightBound : another.floorRightBound);
            int y = random.nextInt(maxY - minY + 1) + minY;
            int length = ((floorLeftBound < another.floorLeftBound)
                    ? another.floorLeftBound - floorRightBound + 1
                    : floorLeftBound - another.floorRightBound + 1);
            Hallway hallway = new Hallway(new Position(x, y), true, length);
            hallway.makeHallway(tiles);
        } else {
            int x = 0;
            int y = 0;
            switch (random.nextInt(2)) {
                case 0:
                    x = random.nextInt(width) + floorLeftBound;
                    y = random.nextInt(another.height) + another.floorLowerBound;
                    break;
                case 1:
                    x = random.nextInt(another.width) + another.floorLeftBound;
                    y = random.nextInt(height) + floorLowerBound;
                    break;
                default:
            }
            Room helper = new Room(new Position(x, y), 1, 1, random);
            helper.makeRoom(tiles);
            connect(helper, tiles);
            another.connect(helper, tiles);
        }
    }
    public int distance(Room another) {
        return bottomLeft.distance(another.bottomLeft);
    }

    public Room closest(List<Room> rooms) {
        if (rooms.isEmpty()) {
            return null;
        }
        int min = 0;
        Room result = null;
        for (Room room: rooms) {
            if (distance(room) < min || min == 0) {
                min = distance(room);
                result = room;
            }
        }
        return result;
    }

    public Avatar gererateAvatar(TETile[][] tiles, char c) {
        Avatar result = null;

        for (int i = floorLeftBound; i <= floorRightBound; i++) {
            if (tiles[i][wallLowerBound] == Tileset.WALL) {
                tiles[i][wallLowerBound] = Tileset.LOCKED_DOOR;
                result = new Avatar(i, floorLowerBound, c);
                result.markAvatar(tiles);
                break;
            }
        }
        return result;
    }

    public Enemy generateEnemy() {
        int x = random.nextInt(floorRightBound - floorLeftBound + 1) + floorLeftBound;
        int y = random.nextInt(floorUpperBound - floorLowerBound + 1) + floorLowerBound;
        return new Enemy(x, y);
    }
}
