package tileengine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile(' ', new Color(255, 250, 250),
            new Color(255, 250, 250), "you", "src/assets/pianobyow.png", 0);
    public static final TETile WALL = new TETile('♥', new Color(198, 80, 118), new Color(231, 157, 181),
            "wall", "src/assets/brickwall.jpg", 1); //proj3/src/assets/pianobyow.png
    public static final TETile FLOOR = new TETile('*', new Color(240, 219, 225), new Color(247, 231, 236), "floor", 2);
    public static final TETile NOTHING = new TETile(' ', Color.black, new Color(255, 250, 250), "nothing", 3);
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass", 4);
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water", 5);
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower", 6);
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", 7);
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door", 8);
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand", 9);
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain", 10);
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree", 11);

    public static final TETile CELL = new TETile('█',
            Color.white, Color.black, "cell", 12);
    public static final TETile BADITEM = new TETile('▲', Color.RED, Color.black, "poison", "src/assets/poison.png", 14);
    public static final TETile GOODITEM = new TETile('♠',
            Color.GREEN, Color.white, "treasure", "src/assets/treasure.png", 15);

    public static final TETile HUDBACKGROUND = new TETile(' ', Color.white, Color.white, "hud", 16);
    public static final TETile DARKNESS = new TETile(' ', Color.black, Color.black, "?", 15);

    // match background to avatar background color (blends in)
}

