package core;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;


public class GameCharacter {
    private int x;
    private int y;
    private int health;
    private String name;

    private int goodItemsCollected;
    private ArrayList<String> goodItemX;
    private ArrayList<String> goodItemY;
    private ArrayList<String> badItemX;
    private ArrayList<String> badItemY;


    // Interface for handling visibility logic

    public GameCharacter(String name, int startX, int startY, int initialHealth) {
        this.name = name;
        this.x = startX;
        this.y = startY;
        this.health = initialHealth;
        this.goodItemsCollected = 0;
        this.goodItemX = new ArrayList<>();
        this.goodItemY = new ArrayList<>();
        this.badItemX = new ArrayList<>();
        this.badItemY = new ArrayList<>();
    }


    public void setHealth(int newHealth) {
        this.health = Math.max(0, Math.min(newHealth, 100)); // Ensure health is within 0 and 100
        if (this.health == 0) {
            System.out.println("Character has died.");
        }
    }

    public void adminsetHealth(int h) {
        health = h;
    }

    public void move(int dx, int dy, TETile[][] tiles) {
        int newX = x + dx;
        int newY = y + dy;

        if (canMoveTo(tiles, newX, newY)) {
            // Check and handle tiles for items before moving
            checkAndHandleItems(tiles[newX][newY], newX, newY);

            // Move to the new position
            moveTo(tiles, newX, newY);

        }
    }

    private boolean canMoveTo(TETile[][] tiles, int newX, int newY) {
        return newX >= 0 && newY >= 0 && newX < tiles.length && newY < tiles[0].length
                && !tiles[newX][newY].description().equals("wall");
    }

    private void checkAndHandleItems(TETile tile, int newX, int newY) {
        if (tile.equals(Tileset.BADITEM)) {
            health -= 15;  // Decrease health
            tile = Tileset.FLOOR;  // Clear the bad item
            badItemX.add(Integer.toString(newX));
            badItemY.add(Integer.toString(newY));
            System.out.println("Health decreased to: " + health);
        } else if (tile.equals(Tileset.GOODITEM)) {
            health += 5; // Optionally increase health
            goodItemsCollected++;
            tile = Tileset.FLOOR;  // Clear the good item
            goodItemX.add(Integer.toString(newX));
            goodItemY.add(Integer.toString(newY));
            System.out.println("Good item collected. Total: " + goodItemsCollected);
        }
    }

    public void moveTo(TETile[][] tiles, int newX, int newY) {
        tiles[this.x][this.y] = Tileset.FLOOR;  // Clear old position
        this.x = newX;  // Update position
        this.y = newY;
        tiles[newX][newY] = Tileset.AVATAR;  // Set new position
    }

    public int getGoodItemsCollected() {
        return goodItemsCollected;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getPosition() {
        return new Point(x, y);
    }

    public int getHealth() {
        return health;
    }

    public ArrayList<String> getGoodX() {
        return goodItemX;
    }

    public ArrayList<String> getGoodY() {
        return goodItemY;
    }

    public ArrayList<String> getBadX() {
        return badItemX;
    }

    public ArrayList<String> getBadY() {
        return badItemY;
    }

    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        System.out.println("Position set to: (" + newX + ", " + newY + ")");
    }

    public void setGoodItemsCollected(int count) {
        this.goodItemsCollected = count;
    }

    public void setGood(ArrayList<Integer> gx, ArrayList<Integer> gy) {
        for (int i = 0; i < gx.size(); i++) {
            goodItemX.add(Integer.toString(gx.get(i)));
            goodItemY.add(Integer.toString(gy.get(i)));
        }
    }

    public void setBad(ArrayList<Integer> bx, ArrayList<Integer> by) {
        for (int i = 0; i < bx.size(); i++) {
            badItemX.add(Integer.toString(bx.get(i)));
            badItemY.add(Integer.toString(by.get(i)));
        }
    }

}
