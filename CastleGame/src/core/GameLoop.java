package core;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.*;
import utils.FileUtils;

import java.awt.*;
import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GameLoop {
    private boolean isRunning;
    private World world;
    private TERenderer ter;
    private GameCharacter gameCharacter;
    private TETile[][] tiles;

    private boolean displayProjectedPath;

    private long seed;
    private boolean lineOfSightMode = false;
    private boolean inEnglish = true;
    private int sightRadius = 5;
    private boolean saveAndQuit = false;

    private boolean colPressed = false;
    private boolean chasingEntityMode = false;

    private HashMap<String, String> engToSpan = new HashMap<>();






    public GameLoop(World world, int startingX, int startingY, int initialHealth,
                    long seed, boolean entities, boolean english) {
        this.world = world;
        this.isRunning = true;
        this.ter = new TERenderer();
        this.gameCharacter = new GameCharacter("Player", startingX, startingY, initialHealth);
        this.tiles = world.getTiles();
        this.seed = seed;
        engToSpan.put("you", "tú");
        engToSpan.put("poison", "veneno");
        engToSpan.put("treasure", "tesoro");
        engToSpan.put("wall", "muro");
        engToSpan.put("nothing", "nada");
        engToSpan.put("floor", "piso");
        engToSpan.put("hud", "hud");
        engToSpan.put("?", "?");




        StdDraw.setCanvasSize(World.WIDTH * 16, World.HEIGHT * 16); // Adjust size as needed
        StdDraw.setXscale(0, World.WIDTH);
        StdDraw.setYscale(0, World.HEIGHT);

        chasingEntityMode = entities;
        inEnglish = english;
    }

    public GameLoop(World world, long seed, boolean entities, boolean english) {
        this.world = world;
        this.isRunning = true;
        this.ter = new TERenderer();
        this.gameCharacter = world.getGameCharacter();
        this.tiles = world.getTiles();
        this.seed = seed;
        engToSpan.put("you", "tú");
        engToSpan.put("poison", "veneno");
        engToSpan.put("treasure", "tesoro");
        engToSpan.put("wall", "muro");
        engToSpan.put("nothing", "nada");
        engToSpan.put("floor", "piso");
        engToSpan.put("hud", "hud");
        engToSpan.put("?", "?");




        StdDraw.setCanvasSize(World.WIDTH * 16, World.HEIGHT * 16); // Adjust size as needed
        StdDraw.setXscale(0, World.WIDTH);
        StdDraw.setYscale(0, World.HEIGHT);

        chasingEntityMode = entities;
        inEnglish = english;
    }

    public GameLoop(World world, long seed, boolean entities, boolean english, boolean draw) {
        this.world = world;
        this.isRunning = true;
        this.ter = new TERenderer();
        this.gameCharacter = world.getGameCharacter();
        this.tiles = world.getTiles();
        this.seed = seed;
        engToSpan.put("you", "tú");
        engToSpan.put("poison", "veneno");
        engToSpan.put("treasure", "tesoro");
        engToSpan.put("wall", "muro");
        engToSpan.put("nothing", "nada");
        engToSpan.put("floor", "piso");
        engToSpan.put("hud", "hud");
        engToSpan.put("?", "?");

        chasingEntityMode = entities;
        inEnglish = english;
    }


    public void start() {
        //MainMenu mainMenu = new MainMenu();
        //mainMenu.showInstructions();
        StdDraw.enableDoubleBuffering();

        while (isRunning) {
            //StdDraw.clear();  // Clear the screen at the start of the loop

            processInput();  // Handle user input

            if (saveAndQuit) {
                saveWorld();
                isRunning = false;
                saveAndQuit = true;
                System.exit(0);
            }

            //updateGameState();  // Update game state based on logic and interactions
            //renderWorld();  // Draw the game world

            //StdDraw.show();  // Display on screen everything that was drawn


            updateGameState();  // Update game state based on logic and interactions
            renderWorld();  // Draw the game world
            StdDraw.show();  // Display on screen everything that was drawn


            if (checkGameOver()) {
                System.out.println("Game Over! Health reached zero.");
                isRunning = false;
                displayGameOverScreen();
            }
            StdDraw.show();
        }
    }


    public TETile[][] getTiles() {
        return this.tiles;
    }

    public void toggleProjectedPathDisplay() {
        displayProjectedPath = !displayProjectedPath;
    }


    private void processInput() {
        while (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            switch (Character.toLowerCase(key)) {
                default:
                    colPressed = false;
                    break;
                case 'w':  // Move up
                    colPressed = false;
                    moveCharacter(0, 1);
                    break;
                case 's':  // Move down
                    colPressed = false;
                    moveCharacter(0, -1);
                    break;
                case 'a':  // Move left
                    colPressed = false;
                    moveCharacter(-1, 0);
                    break;
                case 'd':  // Move right
                    colPressed = false;
                    moveCharacter(1, 0);
                    break;
                case 'e':
                    colPressed = false;

                    //toggleChasingEntityMode(tiles);
                    //toggleProjectedPathDisplay();
                    lineOfSightMode = !lineOfSightMode;
                    renderWorld();
                    break;
                case 'c':
                    colPressed = false;

                    //toggleChasingEntityMode(tiles);
                    //toggleProjectedPathDisplay();
                    inEnglish = !inEnglish;
                    renderWorld();
                    break;
                case 'q': // Save and quit (if applicable)
                    if (colPressed) {
                        colPressed = false;
                        saveAndQuit = true;
                        return;
                    }
                    break;
                case ':':
                    colPressed = true;
                    break;
            }
            renderWorld();  // Re-render the world with updated visibility
        }
    }

    private void renderHUD() {
        // Get mouse position
        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();

        // Check if mouse is within bounds of the tile array
        if (mouseX >= 0 && mouseX < World.WIDTH && mouseY >= 0 && mouseY < World.HEIGHT) {
            int x = (int) mouseX;
            int y = (int) mouseY;

            if (tiles[x][y] != null) {
                String tileDescription = tiles[x][y].description();
                // Display tile description
                StdDraw.setPenColor(StdDraw.BLACK);
                if (!inEnglish) {
                    StdDraw.textLeft(30, World.HEIGHT - 0.8, "Teja: " + engToSpan.get(tileDescription));
                } else {
                    StdDraw.textLeft(30, World.HEIGHT - 0.8, "Tile: " + tileDescription);
                }
            }
        }

        // Display other HUD elements
        drawHealth();
        drawGoodItemsCount();
    }


    public void drawHealth() {
        StdDraw.setPenColor(StdDraw.BLACK);
        double x = 1;
        double y = World.HEIGHT - 0.8;
        StdDraw.setFont(new Font("Arial", Font.BOLD, 14));
        if (inEnglish) {
            StdDraw.textLeft(x, y, "Health: " + gameCharacter.getHealth());
        } else {
            StdDraw.textLeft(x, y, "Salud: " + gameCharacter.getHealth());
        }
    }

    public void drawGoodItemsCount() {
        StdDraw.setPenColor(StdDraw.BLACK);
        double x = 15;
        double y = World.HEIGHT - 0.8; // Adjust y position to be under health
        StdDraw.setFont(new Font("Arial", Font.BOLD, 14));
        if (inEnglish) {
            String goodItemsText = "Treasure: " + gameCharacter.getGoodItemsCollected() + " / 3";
            StdDraw.textLeft(x, y, goodItemsText);
        } else {
            String goodItemsText = "Tesoro: " + gameCharacter.getGoodItemsCollected() + " / 3";
            StdDraw.textLeft(x, y, goodItemsText);
        }
    }




    private void updateGameState() {
        if (gameCharacter.getGoodItemsCollected() >= 3) {
            isRunning = false; // Stop the game loop
            displayWinningScreen(); // Show the winning screen
        }
    }

    public void moveCharacter(int dx, int dy) {
        gameCharacter.move(dx, dy, tiles);
        renderWorld(); // Re-render the world
    }




    private void renderWorld() {
        // Call this method once before the game loop starts
        StdDraw.enableDoubleBuffering();

        StdDraw.clear();
        TETile[][] tilesa = getTiles();  // Assume this method retrieves the current tile grid
        Point charPos = gameCharacter.getPosition();
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                TETile tile = tilesa[x][y];
                if (lineOfSightMode && y < World.HEIGHT - 2) {
                    if (Math.abs(charPos.x - x) <= 5 && Math.abs(charPos.y - y) <= 5) {
                        if (Math.abs(charPos.x - x) <= 5 - (Math.abs(charPos.y - y))) {
                            tile.draw(x, y);
                        } else {
                            Tileset.DARKNESS.draw(x, y);
                        }
                    } else {
                        Tileset.DARKNESS.draw(x, y);
                    }
                } else {
                    tile.draw(x, y);
                }
                /**if (isChasingEntityPosition(x, y)) {
                 Tileset.SAND.draw(x, y); // Draw chasing entity tile
                 } else {
                 tile.draw(x, y);  // Draw other tiles
                 }**/
            }
        }

        drawHealth();  // Continue to draw health and other UI elements
        drawGoodItemsCount();
        renderHUD();


        // Now show everything that has been drawn
        StdDraw.show();
    }




    /**private void restartGame() {

     gameCharacter.setHealth(100);  // Reset health or other initial values
     gameCharacter.setPosition(5, 5);
     gameCharacter.setHealth(100);
     renderWorld();
     start();
     }**/


    /**private void waitForRestartOrQuit() {
     while (true) {
     if (StdDraw.hasNextKeyTyped()) {
     char key = StdDraw.nextKeyTyped();
     if (key == 'r' || key == 'R') {
     restartGame();  // Method to reset the game state and restart
     break;
     } else if (key == 'q' || key == 'Q') {
     System.exit(0);  // Exit the game
     }
     }
     StdDraw.pause(100);  // Pause to reduce CPU usage during wait
     }
     }**/


    private void displayGameOverScreen() {
        StdDraw.clear(StdDraw.PINK);
        StdDraw.setPenColor(StdDraw.WHITE);
        if (inEnglish) {
            StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0, "Game Over, You Lose");
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 2, "Press 'Q' to Quit");
        } else {
            StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0, "Se acabó el juego, pierdes");
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 2, "Presione 'Q' para salir");
        }
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'q' || key == 'Q') {
                    System.exit(0); // Exit the game
                }
            }
            StdDraw.pause(50); // Pause to reduce CPU usage during wait
        }
    }

    private void displayWinningScreen() {
        StdDraw.clear(StdDraw.PINK);
        StdDraw.setPenColor(StdDraw.WHITE);
        if (inEnglish) {
            StdDraw.setFont(new Font("Georgia", Font.BOLD, 30));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0, "You win the award for the best TA ever!");
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 2, "Press 'Q' to Quit");
        } else {
            StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0, "¡Tú ganas!");
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 2, "Presione 'Q' para salir");
        }
        StdDraw.show();

        // Wait for the user to press 'Q' to quit
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'q' || key == 'Q') {
                    System.exit(0); // Exit the game
                }
            }
            StdDraw.pause(50); // Pause to reduce CPU usage during wait
        }
    }






    private boolean checkGameOver() {
        int health = gameCharacter.getHealth();
        return health <= 0;
    }


    public void saveWorld() {
        File saveFile = new File("SaveFile.txt");
        try {
            saveFile.createNewFile();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        String toWrite = "";
        toWrite += Long.toString(this.seed) + "\n";
        toWrite += Integer.toString(this.gameCharacter.getX()) + "\n";
        toWrite += Integer.toString(this.gameCharacter.getY()) + "\n";
        toWrite += Integer.toString(this.gameCharacter.getHealth()) + "\n";
        ArrayList<String> goodX = gameCharacter.getGoodX();
        ArrayList<String> goodY = gameCharacter.getGoodY();
        ArrayList<String> badX = gameCharacter.getBadX();
        ArrayList<String> badY = gameCharacter.getBadY();
        toWrite += ",";
        for (int i = 0; i < goodX.size(); i++) {
            toWrite += goodX.get(i);
            toWrite += ",";
        }
        toWrite += "\n";
        toWrite += ",";
        for (int i = 0; i < goodY.size(); i++) {
            toWrite += goodY.get(i);
            toWrite += ",";
        }
        toWrite += "\n";
        toWrite += ",";
        for (int i = 0; i < badX.size(); i++) {
            toWrite += badX.get(i);
            toWrite += ",";
        }
        toWrite += "\n";
        toWrite += ",";
        for (int i = 0; i < badY.size(); i++) {
            toWrite += badY.get(i);
            toWrite += ",";
        }
        toWrite += "\n";
        FileUtils.writeFile("SaveFile.txt", toWrite);
    }

    public void moveCharacter(int dx, int dy, int noDraw) {
        gameCharacter.move(dx, dy, tiles);
    }

    public void swapVisbility() {
        lineOfSightMode = !lineOfSightMode;
    }

    public void swapLanguage() {
        inEnglish = !inEnglish;
    }


    public GameLoop(World world, long seed, int noDraw) {
        this.world = world;
        this.isRunning = true;
        this.gameCharacter = world.getGameCharacter();
        this.tiles = world.getTiles();
        this.seed = seed;
        engToSpan.put("you", "tú");
        engToSpan.put("poison", "veneno");
        engToSpan.put("treasure", "tesoro");
        engToSpan.put("wall", "muro");
        engToSpan.put("nothing", "nada");
        engToSpan.put("floor", "piso");
        engToSpan.put("hud", "hud");
        engToSpan.put("?", "?");
    }
}
