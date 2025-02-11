package core;
import tileengine.TETile;
import tileengine.TERenderer;

import java.util.ArrayList;
import java.util.Scanner;
import edu.princeton.cs.algs4.StdDraw;
import utils.FileUtils;

import java.awt.Font;



public class MainMenu {
    private TERenderer ter;
    private World world;
    private TETile[][] worldTiles;
    private Scanner scanner;

    public MainMenu() {
        this.ter = new TERenderer();
        ter.initialize(World.WIDTH, World.HEIGHT);
        this.world = null;
        this.worldTiles = null;
        this.scanner = new Scanner(System.in);  // Use for console input if needed
    }

    private LanguageManager languageManager = new LanguageManager(); // Assume this is declared at the class level

    public void displayMenu() {
        StdDraw.clear(StdDraw.PINK);
        StdDraw.setFont(new Font("G", Font.BOLD, 40));
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(World.WIDTH / 2.0, World.HEIGHT - 10, languageManager.getText("title"));
        StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 + 2, languageManager.getText("new_game"));
        StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0, languageManager.getText("load_game"));
        StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 2, languageManager.getText("quit"));
        StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 4, languageManager.getText("change_language"));
        StdDraw.show();
        waitForUserInput();
    }

    public void startNewGame(long seed) {
        showInstructions();
        world = new World(seed);
        TETile[][] finalWorldFrame = world.getTiles();
        //ter.renderFrame(finalWorldFrame);

        GameLoop gameLoop = new GameLoop(world, seed, false, languageManager.isInEnglish());

        //StdDraw.clear(StdDraw.PINK);
        //StdDraw.pause(1500);
        //StdDraw.clear(StdDraw.PINK);

        gameLoop.start();
    }


    private void waitForUserInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                System.out.println("Key pressed: " + key);  // Debug output to check which key is pressed
                switch (Character.toLowerCase(key)) {
                    default:
                        break;
                    case 'n':
                        if (languageManager.isInEnglish()) {
                            System.out.println("Starting new game...");  // Additional debug output
                        } else {
                            System.out.println("Comenzando un nuevo juego...");  // Additional debug output
                        }
                        collectSeedAndStartGame();
                        break;
                    case 'l':
                        loadGame();
                        break;
                    case 'q':
                        System.exit(0);
                        break;
                    case 'c':
                        toggleLanguage();
                        break;
                }
            }
        }
    }

    public void showInstructions() {
        StdDraw.clear(StdDraw.PINK);

        // Central alignment for texts and items
        double centerX = World.WIDTH / 2.0;
        double centerY = World.HEIGHT / 2.0;
        if (languageManager.isInEnglish()) {
            // Title for the instructions screen
            StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(centerX, centerY + 10, "Instructions");

            // Display good item and description
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
            StdDraw.text(centerX, centerY + 3, "Treasure: Increases Health and leads to Victory");

            // Display bad item and description
            StdDraw.text(centerX, centerY, "Poison: Decreases Health");

            //Display avatar and description

            StdDraw.text(centerX, centerY - 3, "You: the avatar, Sweet Piano the sheep");

            // Instructions for winning the game
            StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
            StdDraw.text(centerX, centerY - 7, "Collect all 3 Treasure to Win!");
            StdDraw.text(centerX, centerY - 9, "Avoid Poison to Stay Alive.");

            // Instructions for game controls
            StdDraw.setFont(new Font("Arial", Font.ITALIC, 16));
            StdDraw.text(centerX, centerY - 12,
                    "Use WASD to Move, c to change language, e for a challenge, and :q to quit + save");

            // Display 'Press any key to start' message at the bottom
            StdDraw.setFont(new Font("Arial", Font.ITALIC, 16));
            StdDraw.text(centerX, centerY - 14, "Press any key to start...");
        } else {
            StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(centerX, centerY + 10, "Instrucciones");

            // Display good item and description
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
            StdDraw.text(centerX, centerY + 3, "Tesoro: Aumenta la Salud y Conduce a la Victoria");

            // Display bad item and description
            StdDraw.text(centerX, centerY, "Veneno: Disminuye la Salud");

            //Display avatar and description

            StdDraw.text(centerX, centerY - 3, "Tú: el avatar, Sweet Piano la oveja");

            // Instructions for winning the game
            StdDraw.setFont(new Font("Arial", Font.BOLD, 18));
            StdDraw.text(centerX, centerY - 7, "¡Recoge 3 buenos artículos para ganar!");
            StdDraw.text(centerX, centerY - 9, "Evite los artículos malos para mantenerse vivo.");

            // Instructions for game controls
            StdDraw.setFont(new Font("Arial", Font.ITALIC, 16));
            StdDraw.text(centerX, centerY - 12,
                    "Utilice WASD para moverse, c para cambiar el idioma, e para un desafío y :q para salir + guardar");

            // Display 'Press any key to start' message at the bottom
            StdDraw.setFont(new Font("Arial", Font.ITALIC, 16));
            StdDraw.text(centerX, centerY - 14, "Presione cualquier tecla para comenzar...");
        }
        StdDraw.show();

        // Wait for the player to press a key to start the game
        while (!StdDraw.hasNextKeyTyped()) {
            StdDraw.pause(50); // Pause to reduce CPU usage while waiting
        }
        StdDraw.nextKeyTyped(); // Clear the key press
    }



    private void toggleLanguage() {
        if (languageManager.getText("title").equals("CS61B: The Game <3")) {
            languageManager.setLanguage("Spanish");
        } else {
            languageManager.setLanguage("English");
        }
        displayMenu(); // Redraw menu with the updated language
    }

    private void collectSeedAndStartGame() {
        StringBuilder seedBuilder = new StringBuilder();
        StdDraw.clear(StdDraw.PINK);

        // Instructions and title at the top
        if (languageManager.isInEnglish()) {
            StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0, "Enter Seed (Press 'S' when done): ");
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 14));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 4, "Leave blank & press 'S' for random seed.");
        } else {
            StdDraw.setFont(new Font("Arial", Font.BOLD, 20));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0,
                    "Ingrese la Semilla (Presione 'S' cuando haya terminado): ");
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 14));
            StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 4,
                    "Déjelo en blanco y presione 'S' para obtener una semilla aleatoria.");
        }
        StdDraw.show();

        // Loop to handle seed input
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (Character.isDigit(key)) {
                    seedBuilder.append(key);

                    // Clear the area where the seed is displayed before displaying the new seed
                    StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                    StdDraw.filledRectangle(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 2, seedBuilder.length(), 1);

                    // Draw the seed string
                    StdDraw.setPenColor(StdDraw.WHITE);
                    StdDraw.setFont(new Font("Arial", Font.PLAIN, 18));
                    StdDraw.text(World.WIDTH / 2.0, World.HEIGHT / 2.0 - 2, seedBuilder.toString());
                    StdDraw.show();
                    // pause 
                } else if (key == 'S' || key == 's') {
                    break;
                }
            }
            StdDraw.pause(50);
        }

        long seed;
        if (seedBuilder.length() > 0) {
            seed = Long.parseLong(seedBuilder.toString());
        } else {
            seed = new java.util.Random().nextLong(); // Generate a random seed if none entered
        }
        startNewGame(seed);
    }


    public void loadGame() {
        if (!FileUtils.fileExists("SaveFile.txt")) {
            return;
        }
        String[] saveContents = FileUtils.readFile("SaveFile.txt").split("\n");
        System.out.println("read");
        ter.initialize(World.WIDTH, World.HEIGHT);
        long seed = Long.parseLong(saveContents[0]);
        world = new World(seed);
        int x = Integer.parseInt(saveContents[1]);
        int y = Integer.parseInt(saveContents[2]);
        int health = Integer.parseInt(saveContents[3]);
        ArrayList<Integer> goodX = new ArrayList<Integer>();
        ArrayList<Integer> goodY = new ArrayList<Integer>();
        ArrayList<Integer> badX = new ArrayList<Integer>();
        ArrayList<Integer> badY = new ArrayList<Integer>();
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

        TETile[][] finalWorldFrame = world.updateState(x, y, health, goodX, goodY, badX, badY);
        ter.renderFrame(finalWorldFrame);

        GameLoop gameLoop = new GameLoop(world, seed, false, languageManager.isInEnglish());
        gameLoop.start();
    }

}
