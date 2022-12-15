package byow.Core;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;


public class Engine {
    TERenderer ter = new TERenderer();
    private String commandSoFar = "";
    private TETile[][] finalWorldFrame;
    private Avatar avatar;
    private File saveFile;
    private ArrayList<Room> rooms;
    private int index;
    Random r;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static final int MAXROOMWIDTH = 8;
    private static final int MAXROOMHEIGHT = 8;



    public void displayMenu() {
        StdDraw.setCanvasSize(512, 768);
        StdDraw.setScale(0, 100);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 30));
        StdDraw.text(50, 80, "CS61B: THE GAME");
        StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 20));
        StdDraw.text(50, 50, "New Game (N)");
        StdDraw.text(50, 45, "Load Game (L)");
        StdDraw.text(50, 40, "Quit (Q)");
        StdDraw.show();
    }

    public void displayChangeAvartarAppearance() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(50, 70, "Change avatar appearance?");
        StdDraw.text(50, 60, "Yes (Y)");
        StdDraw.text(50, 50, "No (N)");
        StdDraw.show();
    }

    public void displayAvartarAppearanceOptions() {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(50, 70, "Enter desired appearance:");
        StdDraw.text(50, 60, "Flower (F)");
        StdDraw.text(50, 50, "Water (W)");
        StdDraw.text(50, 40, "Tree (T)");
        StdDraw.show();
    }

    public long getSeed() {
        String seedString = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                commandSoFar = commandSoFar + key;
                if (key == 'S' || key == 's') {
                    break;
                }
                seedString = seedString + key;
                StdDraw.clear(Color.BLACK);
                StdDraw.text(50, 50, "Enter a random seed:" + seedString);
                StdDraw.text(50, 45, "Press 'S' when done");
            }
        }
        return Long.parseLong(seedString);
    }

    public char getAppearance() {
        char desiredAppearance;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char choice = StdDraw.nextKeyTyped();
                commandSoFar = commandSoFar + choice;
                if (choice == 'Y' || choice == 'y') {
                    displayAvartarAppearanceOptions();
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char appearance = StdDraw.nextKeyTyped();
                            commandSoFar = commandSoFar + appearance;
                            if (appearance == 'F' || appearance == 'f' || appearance == 'W'
                                    || appearance == 'w' || appearance == 'T' || appearance == 't') {
                                desiredAppearance = appearance;
                                break;
                            }
                        }
                    }
                    break;
                } else if(choice == 'N' || choice == 'n'){
                    desiredAppearance = 'D';
                    break;
                }
            }
        }
        return desiredAppearance;
    }

    public void headsUpDisplay() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (0 <= x && x < WIDTH && 0 <= y && y < HEIGHT) {
            TETile tile = finalWorldFrame[x][y];
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textLeft(0.5, 29, tile.description());
            StdDraw.textLeft(15, 29, "hearts: " + avatar.getNumHeart());
            StdDraw.textLeft(30, 29, "Candles: " + avatar.getNumKey());
            StdDraw.show();
        }
    }


    public void initializeWorld() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'N' || key == 'n') {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(50, 50, "Enter a random seed:");
                    commandSoFar = Character.toString(key);
                    long seed = getSeed();
                    displayChangeAvartarAppearance();
                    char desiredAppearance = getAppearance();
                    generateWorld(seed, desiredAppearance);
                    ter.initialize(WIDTH, HEIGHT);
                    ter.renderFrame(finalWorldFrame, avatar);
                    break;
                }

                if (key == 'L' || key == 'l') {
                    commandSoFar = loadFile();
                    interactWithInputString(commandSoFar);
                    ter.initialize(WIDTH, HEIGHT);
                    ter.renderFrame(finalWorldFrame, avatar);
                    break;
                }

                if (key == 'Q' || key == 'q') {
                    System.exit(0);
                }
            }
        }
    }

    private boolean Encounter() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 20));
        StdDraw.text(40, 25, "The enemy flipped a coin. Guess it's head or tail");
        StdDraw.text(40, 20, "If you guess right, you get one candle. If not, you lose one heart");
        StdDraw.text(40, 15, "Head (H)");
        StdDraw.text(40, 10, "Tail (T)");
        StdDraw.show();
        int headOrTail = r.nextInt(2);
        int myGuess = 0;
        while(true) {
            if(StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                commandSoFar = commandSoFar + key;
                if (key == 'H' || key == 'h') {
                    myGuess = 1;
                    break;
                }
                if (key == 'T' || key == 't') {
                    myGuess = 0;
                    break;
                }
            }
        }
        return myGuess == headOrTail;
    }

    private void moveEast() {
        int xCoordinate = avatar.getxCoordinate();
        int yCoordinate = avatar.getyCoordinate();
        TETile tile = avatar.getTile();
        if (finalWorldFrame[xCoordinate + 1][yCoordinate] == Tileset.ENEMY) {
            boolean beatEnemy = Encounter();
            if (beatEnemy) {
                avatar.incrementKey();
                if (avatar.meetWinCondition()) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 40));
                    StdDraw.text(40, 15, "YOU WIN");
                    StdDraw.show();
                    while (true) {
                    }
                }
            } else {
                avatar.decrementHeart();
                if (!avatar.stillAlive()) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 40));
                    StdDraw.text(40, 15, "Game Over");
                    StdDraw.show();
                    while (true) {
                    }
                }
            }
        } else if (finalWorldFrame[xCoordinate + 1][yCoordinate] != Tileset.FLOOR) {
            return;
        }
        finalWorldFrame[xCoordinate][yCoordinate] = Tileset.FLOOR;
        finalWorldFrame[xCoordinate + 1][yCoordinate] = tile;
        avatar.moveEast();
    }


    private void moveWest() {
        int xCoordinate = avatar.getxCoordinate();
        int yCoordinate = avatar.getyCoordinate();
        TETile tile = avatar.getTile();
        if (finalWorldFrame[xCoordinate - 1][yCoordinate] == Tileset.ENEMY) {
            boolean beatEnemy = Encounter();
            if (beatEnemy) {
                avatar.incrementKey();
                if (avatar.meetWinCondition()) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 40));
                    StdDraw.text(40, 15, "YOU WIN");
                    StdDraw.show();
                    while (true) {
                    }
                }
            } else {
                avatar.decrementHeart();
                if (!avatar.stillAlive()) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 40));
                    StdDraw.text(40, 15, "Game Over");
                    StdDraw.show();
                    while (true) {
                    }
                }
            }
        } else if (finalWorldFrame[xCoordinate - 1][yCoordinate] != Tileset.FLOOR) {
            return;
        }
        finalWorldFrame[xCoordinate][yCoordinate] = Tileset.FLOOR;
        finalWorldFrame[xCoordinate - 1][yCoordinate] = tile;
        avatar.moveWest();
    }

    private void moveSouth() {
        int xCoordinate = avatar.getxCoordinate();
        int yCoordinate = avatar.getyCoordinate();
        TETile tile = avatar.getTile();
        if (finalWorldFrame[xCoordinate][yCoordinate - 1] == Tileset.ENEMY) {
            boolean beatEnemy = Encounter();
            if (beatEnemy) {
                avatar.incrementKey();
                if (avatar.meetWinCondition()) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 40));
                    StdDraw.text(40, 15, "YOU WIN");
                    StdDraw.show();
                    while (true) {
                    }
                }
            } else {
                avatar.decrementHeart();
                if (!avatar.stillAlive()) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 40));
                    StdDraw.text(40, 15, "Game Over");
                    StdDraw.show();
                    while (true) {
                    }
                }
            }
        } else if (finalWorldFrame[xCoordinate][yCoordinate - 1] != Tileset.FLOOR) {
            return;
        }
        finalWorldFrame[xCoordinate][yCoordinate] = Tileset.FLOOR;
        finalWorldFrame[xCoordinate][yCoordinate - 1] = tile;
        avatar.moveSouth();
    }

    private void moveNorth() {
        int xCoordinate = avatar.getxCoordinate();
        int yCoordinate = avatar.getyCoordinate();
        TETile tile = avatar.getTile();
        if (finalWorldFrame[xCoordinate][yCoordinate + 1] == Tileset.ENEMY) {
            boolean beatEnemy = Encounter();
            if (beatEnemy) {
                avatar.incrementKey();
                if (avatar.meetWinCondition()) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 40));
                    StdDraw.text(40, 15, "YOU WIN");
                    StdDraw.show();
                    while (true) {
                    }
                }
            } else {
                avatar.decrementHeart();
                if (!avatar.stillAlive()) {
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(new Font("TimesRomen", Font.PLAIN, 40));
                    StdDraw.text(40, 15, "Game Over");
                    StdDraw.show();
                    while (true) {
                    }
                }
            }
        } else if (finalWorldFrame[xCoordinate][yCoordinate + 1] != Tileset.FLOOR) {
            return;
        }
        finalWorldFrame[xCoordinate][yCoordinate] = Tileset.FLOOR;
        finalWorldFrame[xCoordinate][yCoordinate + 1] = tile;
        avatar.moveNorth();
    }

    public void move(char direction) {
        if (direction == 'D' || direction == 'd') {
            moveEast();
        }
        if (direction == 'A' || direction == 'a') {
            moveWest();
        }
        if (direction == 'S' || direction == 's') {
            moveSouth();
        }
        if (direction == 'W' || direction == 'w') {
            moveNorth();
        }
    }

    public void moveAround() {
        while (true) {
            StdDraw.clear(Color.BLACK);
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == ':') {
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char curr = StdDraw.nextKeyTyped();
                            if (curr == 'Q' || curr == 'q') {
                                saveWorld(commandSoFar);
                                System.exit(0);
                            }
                            break;
                        }
                    }
                } else {
                    commandSoFar = commandSoFar + key;
                    move(key);
                }
            }

            ter.renderFrame(finalWorldFrame, avatar);
            headsUpDisplay();

        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        displayMenu();
        initializeWorld();
        moveAround();
    }

    public void saveWorld(String string) {
        // create savefile.txt
        try {
            this.saveFile = new File("savefile.txt");
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // write text to the file
        try {
            FileWriter myWriter = new FileWriter("savefile.txt");
            myWriter.write(string);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadFile() {
        this.saveFile = new File("savefile.txt");
        if (!this.saveFile.exists()) {
            return null;
        }

        try {
            Scanner myReader = new Scanner(this.saveFile);
            while (myReader.hasNextLine()) {
                commandSoFar = commandSoFar + myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return commandSoFar;
    }

    public void generateWorld(long seed, char c) {
        finalWorldFrame = new TETile[WIDTH][HEIGHT];
        r = new Random(seed);
        rooms = new ArrayList<>();
        ArrayList<Room> roomsCopy = new ArrayList<>();

        while (rooms.size() < 30) {
            Room room = new Room(new Position(r.nextInt(WIDTH - MAXROOMWIDTH - 2) + 1,
                    r.nextInt(HEIGHT - MAXROOMHEIGHT - 2) + 1),
                    r.nextInt(MAXROOMWIDTH) + 1,
                    r.nextInt(MAXROOMHEIGHT) + 1,
                    r);
            if (!room.overlap(rooms)) {
                room.makeRoom(finalWorldFrame);
                rooms.add(room);
                roomsCopy.add(room);
            }
        }

        Room current = roomsCopy.get(0);
        roomsCopy.remove(current);

        while (!roomsCopy.isEmpty()) {
            Room next = current.closest(roomsCopy);
            current.connect(next, finalWorldFrame);
            roomsCopy.remove(next);
            current = next;
        }

        for (Room room : rooms) {
            avatar = room.gererateAvatar(finalWorldFrame, c);
            if (avatar != null) {
                break;
            }
        }

        for (int i = 0; i < 15; i++) {
            Room randomRoom = rooms.get(r.nextInt(rooms.size()));
            Enemy e = randomRoom.generateEnemy();
            e.markEnemy(finalWorldFrame);
        }

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (finalWorldFrame[i][j] == null) {
                    finalWorldFrame[i][j] = Tileset.NOTHING;
                }
            }
        }


    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //

        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        commandSoFar = input;
        char firstChar = commandSoFar.charAt(0);
        String savedCommand = "";
        index = 1;
        if (firstChar == 'N' || firstChar == 'n') {

            for (; index < commandSoFar.length(); index++) {
                if (commandSoFar.charAt(index) == 'S' || commandSoFar.charAt(index) == 's') {
                    break;
                }
            }
            String seedString = commandSoFar.substring(1, index);
            long seed = Long.parseLong(seedString);
            index += 1;

            char desiredAppearance = '\0';
            for (; index < commandSoFar.length(); index++) {
                if ((commandSoFar.charAt(index) == 'Y' || commandSoFar.charAt(index) == 'y')
                        || (commandSoFar.charAt(index) == 'N' || commandSoFar.charAt(index) == 'n')) {
                    break;
                }
            }

            if (commandSoFar.charAt(index) == 'N' || commandSoFar.charAt(index) == 'n') {
                desiredAppearance = 'd';
            } else if (commandSoFar.charAt(index) == 'Y' || commandSoFar.charAt(index) == 'y') {
                index += 1;
                for (; index < commandSoFar.length(); index++) {
                    if ((commandSoFar.charAt(index) == 'F' || commandSoFar.charAt(index) == 'f')
                            || (commandSoFar.charAt(index) == 'W' || commandSoFar.charAt(index) == 'w')
                            || (commandSoFar.charAt(index) == 'T' || commandSoFar.charAt(index) == 't')) {
                        break;
                    }
                }
                desiredAppearance = commandSoFar.charAt(index);
            }
            generateWorld(seed, desiredAppearance);
            index += 1;
        }

        if (firstChar == 'L' || firstChar == 'l') {
            savedCommand = loadFile();
            interactWithInputString(savedCommand);
            commandSoFar = input;
            index = 1;
        }

        for (; index < commandSoFar.length(); index++) {
            if (commandSoFar.charAt(index) == ':' && index + 1 < commandSoFar.length()) {
                if (commandSoFar.charAt(index + 1) == 'Q' || commandSoFar.charAt(index + 1) == 'q') {
                    if (firstChar == 'N' || firstChar == 'n') {
                        saveWorld(commandSoFar.substring(0, index - 1));
                    } else {
                        saveWorld(savedCommand + commandSoFar.substring(1, index - 1));
                    }
                    return finalWorldFrame;
                }
            }

            char c = commandSoFar.charAt(index);
            move(c, index);
        }
        return finalWorldFrame;
    }


    private void moveEast(int index) {
        int xCoordinate = avatar.getxCoordinate();
        int yCoordinate = avatar.getyCoordinate();
        TETile tile = avatar.getTile();
        if (finalWorldFrame[xCoordinate + 1][yCoordinate] == Tileset.ENEMY) {
            boolean beatEnemy = Encounter(index);
            if (beatEnemy) {
                avatar.incrementKey();
            } else {
                avatar.decrementHeart();
            }
        } else if (finalWorldFrame[xCoordinate + 1][yCoordinate] != Tileset.FLOOR) {
            return;
        }
        finalWorldFrame[xCoordinate][yCoordinate] = Tileset.FLOOR;
        finalWorldFrame[xCoordinate + 1][yCoordinate] = tile;
        avatar.moveEast();
    }


    private void moveWest(int index) {
        int xCoordinate = avatar.getxCoordinate();
        int yCoordinate = avatar.getyCoordinate();
        TETile tile = avatar.getTile();
        if (finalWorldFrame[xCoordinate - 1][yCoordinate] == Tileset.ENEMY) {
            boolean beatEnemy = Encounter(index);
            if (beatEnemy) {
                avatar.incrementKey();
            } else {
                avatar.decrementHeart();
            }
        } else if (finalWorldFrame[xCoordinate - 1][yCoordinate] != Tileset.FLOOR) {
            return;
        }
        finalWorldFrame[xCoordinate][yCoordinate] = Tileset.FLOOR;
        finalWorldFrame[xCoordinate - 1][yCoordinate] = tile;
        avatar.moveWest();
    }

    private void moveSouth(int index) {
        int xCoordinate = avatar.getxCoordinate();
        int yCoordinate = avatar.getyCoordinate();
        TETile tile = avatar.getTile();
        if (finalWorldFrame[xCoordinate][yCoordinate - 1] == Tileset.ENEMY) {
            boolean beatEnemy = Encounter(index);
            if (beatEnemy) {
                avatar.incrementKey();
            } else {
                avatar.decrementHeart();
            }
        }else if (finalWorldFrame[xCoordinate][yCoordinate - 1] != Tileset.FLOOR) {
            return;
        }
        finalWorldFrame[xCoordinate][yCoordinate] = Tileset.FLOOR;
        finalWorldFrame[xCoordinate][yCoordinate - 1] = tile;
        avatar.moveSouth();
    }

    private void moveNorth(int index) {
        int xCoordinate = avatar.getxCoordinate();
        int yCoordinate = avatar.getyCoordinate();
        TETile tile = avatar.getTile();
        if (finalWorldFrame[xCoordinate][yCoordinate + 1] == Tileset.ENEMY) {
            boolean beatEnemy = Encounter(index);
            if (beatEnemy) {
                avatar.incrementKey();
            } else {
                avatar.decrementHeart();
            }
        } else if (finalWorldFrame[xCoordinate][yCoordinate + 1] != Tileset.FLOOR) {
            return;
        }
        finalWorldFrame[xCoordinate][yCoordinate] = Tileset.FLOOR;
        finalWorldFrame[xCoordinate][yCoordinate + 1] = tile;
        avatar.moveNorth();
    }

    public void move(char direction, int index) {
        if (direction == 'D' || direction == 'd') {
            moveEast(index);
        }
        if (direction == 'A' || direction == 'a') {
            moveWest(index);
        }
        if (direction == 'S' || direction == 's') {
            moveSouth(index);
        }
        if (direction == 'W' || direction == 'w') {
            moveNorth(index);
        }
    }

    private boolean Encounter(int i) {
        int headOrTail = r.nextInt(2);
        int myGuess = 0;

        for (;index < commandSoFar.length(); index++) {
            if ((commandSoFar.charAt(index) == 'T' || commandSoFar.charAt(index) == 't')
                    || (commandSoFar.charAt(index) == 'H' || commandSoFar.charAt(index) == 'h')) {
                break;
            }
        }
        if (commandSoFar.charAt(index) == 'H' || commandSoFar.charAt(index) == 'h') {
            myGuess = 1;
        }
        return (myGuess == headOrTail);
    }
}

