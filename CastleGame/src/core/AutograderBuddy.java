package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.util.ArrayList;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        long seed = 0;
        int seedEnd = input.length();
        input = input.toLowerCase();
        World ourWorld = null;
        boolean load = false;
        if (input.charAt(0) == 'l') {
            load = true;
        } else if (input.charAt(0) == 'n') {
            seedEnd = input.indexOf('s');
            seed = Long.parseLong(input.substring(1, seedEnd));
        } else {
            seedEnd = input.indexOf('s');
            seed = Long.parseLong(input.substring(0, seedEnd));
        }
        if (load) {
            String[] saveContents = FileUtils.readFile("SaveFile.txt").split("\n");
            System.out.println("read");
            seed = Long.parseLong(saveContents[0]);
            ourWorld = new World(seed);
            int x = Integer.parseInt(saveContents[1]);
            int y = Integer.parseInt(saveContents[2]);
            int health = Integer.parseInt(saveContents[3]);
            ArrayList<Integer> goodX = new ArrayList<Integer>();
            ArrayList<Integer> goodY = new ArrayList<Integer>();
            ArrayList<Integer> badX = new ArrayList<Integer>();
            ArrayList<Integer> badY = new ArrayList<Integer>();
            populateLists(saveContents, goodX, goodY, badX, badY);
            ourWorld.updateState(x, y, health, goodX, goodY, badX, badY);
        } else {
            ourWorld = new World(seed);
            if (seedEnd == input.length()) {
                return ourWorld.getTiles();
            }
            input = input.substring(seedEnd + 1);
        }
        GameLoop ourGame = new GameLoop(ourWorld, seed, false, true, false);
        boolean colon = false;
        for (int i = 0; i < input.length(); i++) {
            char command = input.charAt(i);
            switch (command) {
                case 'w':
                    colon = false;
                    ourGame.moveCharacter(0, 1, 0);
                    break;
                case 's':  // Move down
                    colon = false;
                    ourGame.moveCharacter(0, -1, 0);
                    break;
                case 'a':  // Move left
                    colon = false;
                    ourGame.moveCharacter(-1, 0, 0);
                    break;
                case 'd':  // Move right
                    colon = false;
                    ourGame.moveCharacter(1, 0, 0);
                    break;
                case 'q': // Save and quit (if applicable)
                    if (colon) {
                        colon = false;
                        ourGame.saveWorld();
                        return ourWorld.getTiles();
                    }
                    break;
                case ':':
                    colon = true;
                    break;
                default:
                    colon = false;
                    break;
            }
        }
        ourGame.saveWorld();
        return ourWorld.getTiles();
    }

    public static void populateLists(String[] saveContents, ArrayList<Integer> goodX,
                              ArrayList<Integer> goodY, ArrayList<Integer> badX, ArrayList<Integer> badY) {
        if (!saveContents[4].equals(",")) {
            String[] goodXStr = saveContents[4].substring(1).split(",");
            for (String item : goodXStr) {
                goodX.add(Integer.parseInt(item));
            }
            String[] goodYStr = saveContents[5].substring(1).split(",");
            for (String item : goodYStr) {
                goodY.add(Integer.parseInt(item));
            }
        }
        if (!saveContents[6].equals(",")) {
            String[] badXStr = saveContents[6].substring(1).split(",");
            for (String item : badXStr) {
                badX.add(Integer.parseInt(item));
            }
            String[] badYStr = saveContents[7].substring(1).split(",");
            for (String item : badYStr) {
                badY.add(Integer.parseInt(item));
            }
        }
    }


    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
