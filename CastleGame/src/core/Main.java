package core;

import tileengine.TERenderer;
import tileengine.TETile;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(World.WIDTH, World.HEIGHT);

        MainMenu menu = new MainMenu();
        menu.displayMenu();

        String userInput = "N12345S";
        long seed = extractSeed(userInput);

        World worldInstance = new World(seed);
        TETile[][] worldTiles = worldInstance.getTiles();
        for (String arg : args) {
            worldTiles = AutograderBuddy.getWorldFromInput(arg);
        }

        Point startingPosition = worldInstance.getStartingPosition();
        ter.renderFrame(worldTiles);

        GameLoop gameLoop = new GameLoop(worldInstance, startingPosition.x, startingPosition.y, 100, seed, false, true);
        gameLoop.start();
    }

    private static long extractSeed(String userInput) {
        String seedStr = userInput.substring(1, userInput.indexOf('S'));
        return Long.parseLong(seedStr);
    }
}
