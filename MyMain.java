package byow.Core;

import java.util.Random;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.ArrayList;


public class MyMain {

/**
 * Draws a world that contains RANDOM tiles.
 */
    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;
    private static final int MAXROOMWIDTH = 8;
    private static final int MAXROOMHEIGHT = 8;

    private static final long SEED = 5208;
    public static final Random RANDOM = new Random(SEED);



    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];

        ArrayList<Room> rooms = new ArrayList<>();

        while (rooms.size() < 40) {
            Room room = new Room(new Position(RANDOM.nextInt(WIDTH - MAXROOMWIDTH - 2) + 1,
                                               RANDOM.nextInt(HEIGHT - MAXROOMHEIGHT - 2) + 1),
                            RANDOM.nextInt(MAXROOMWIDTH) + 1,
                            RANDOM.nextInt(MAXROOMHEIGHT) + 1,
                                   RANDOM);
            if (!room.overlap(rooms)) {
                room.makeRoom(tiles);
                rooms.add(room);
            }
        }

        Room current = rooms.get(0);
        rooms.remove(current);

        while (!rooms.isEmpty()) {
            Room next = current.closest(rooms);
            current.connect(next, tiles);
            rooms.remove(next);
            current = next;
        }



        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (tiles[i][j] == null) {
                    tiles[i][j] = Tileset.NOTHING;
                }
            }
        }

        ter.renderFrame(tiles);
    }
}
