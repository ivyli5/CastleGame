package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.util.ArrayList;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class World {

    // build your own world!
    static final int WIDTH = 50;
    static final int HEIGHT = 35;
    private TETile[][] tiles;
    private GameCharacter gameCharacter;

    private Random rand;

    public static class Room {
        int x;
        int y;
        int width;
        int height;

        public Point center() {
            return new Point(x + width / 2, y + height / 2);
        }

        public Room(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
        }

        public boolean intersects(Room other) {
            return (this.x <= other.x + other.width + 2 && this.x + this.width + 2 >= other.x
                    && this.y <= other.y + other.height + 2 && this.y + this.height + 2 >= other.y);
        }
    }

    public World(long seed) {
        this.tiles = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (y == 34 || y == 33) {
                    this.tiles[x][y] = Tileset.HUDBACKGROUND;
                } else {
                    this.tiles[x][y] = Tileset.NOTHING;
                }
            }
        }
        ArrayList<Room> rooms = new ArrayList<>();
        rand = new Random(seed);

        while (rooms.size() < rand.nextInt(10) + 5) {
            int width = rand.nextInt(12) + 5;
            int height = rand.nextInt(12) + 5;
            int x = rand.nextInt(WIDTH - width - 2) + 1;
            int y = rand.nextInt(HEIGHT - height - 4) + 1;
            Room room = new Room(x, y, width, height);
            boolean doesntIntersectsOthers = true;
            for (Room other : rooms) {
                if (room.intersects(other)) {
                    doesntIntersectsOthers = false;
                    break;
                }
            }
            if (doesntIntersectsOthers) {
                initRoom(this.tiles, room);
                rooms.add(room);
            }
        }

        for (int i = 0; i < rooms.size() - 1; i++) { //the list of rooms was generated
            // randomly so we can connect in order
            initHalls(this.tiles, rooms.get(i), rooms.get(i + 1), rand);
        }
        placeBadItems(this.tiles, rand, 10);
        placeGoodItems(this.tiles, rand, 3);
        Room startingRoom = rooms.get(0);
        this.gameCharacter = new GameCharacter(
                "Player",
                startingRoom.center().x,
                startingRoom.center().y,
                100
        );
        this.tiles[startingRoom.center().x][startingRoom.center().y] = Tileset.AVATAR;
    }


    private static void initRoom(TETile[][] world, Room room) {
        for (int x = room.x; x < room.x + room.width; x++) {
            for (int y = room.y; y < room.y + room.height; y++) {
                world[x][y] = Tileset.FLOOR;
            }
        }
        for (int x = room.x; x < room.x + room.width; x++) {
            world[x][room.y - 1] = Tileset.WALL;
            world[x][room.y + room.height] = Tileset.WALL;
        }
        for (int y = room.y; y < room.y + room.height; y++) {
            world[room.x - 1][y] = Tileset.WALL;
            world[room.x + room.width][y] = Tileset.WALL;
        }
    }


    private static void initHalls(TETile[][] world, Room r1, Room r2, Random rand) {
        // Center points of the rooms
        int x1 = r1.center().x;
        int y1 = r1.center().y;
        int x2 = r2.center().x;
        int y2 = r2.center().y;

        boolean horizontalFirst = rand.nextBoolean();

        if (horizontalFirst) {
            connect(world, false, Math.min(x1, x2), Math.max(x1, x2), y1, Tileset.FLOOR);
            connect(world, true, Math.min(y1, y2), Math.max(y1, y2), x2, Tileset.FLOOR);
        } else {
            connect(world, true, Math.min(y1, y2), Math.max(y1, y2), x1, Tileset.FLOOR);
            connect(world, false, Math.min(x1, x2), Math.max(x1, x2), y2, Tileset.FLOOR);
        }
        if (world[x1][y1] == Tileset.WALL) {
            world[x1][y1] = Tileset.LOCKED_DOOR;
        }
        if (world[x2][y2] == Tileset.WALL) {
            world[x2][y2] = Tileset.LOCKED_DOOR;
        }
    }

    private static void connect(TETile[][] world, boolean vertical, int start, int end, int other, TETile tile) {

        if (vertical) { //other is x axis, start/end is y
            for (int y = start; y <= end; y++) {

                if (world[other][y] != tile) {
                    world[other][y] = tile;
                }
                if (other - 1 >= 0 && world[other - 1][y] != tile) {
                    world[other - 1][y] = Tileset.WALL;
                }
                if (other + 1 < WIDTH && world[other + 1][y] != tile) {
                    world[other + 1][y] = Tileset.WALL;
                }
            }
        } else { //other is y axis, start/end is x
            for (int x = start; x <= end; x++) {
                if (world[x][other] != tile) {
                    world[x][other] = tile;
                }
                if (other - 1 >= 0 && world[x][other - 1] != tile) {
                    world[x][other - 1] = Tileset.WALL;
                }
                if (other + 1 < HEIGHT && world[x][other + 1] != tile) {
                    world[x][other + 1] = Tileset.WALL;
                }
            }
        }
    }

    public void placeBadItems(TETile[][] tiless, Random random, int numberOfBadItems) {
        Set<Point> placedPositions = new HashSet<>();
        int attempts = 0;
        while (placedPositions.size() < numberOfBadItems && attempts < numberOfBadItems * 10) {
            int x = RandomUtils.uniform(random, 0, World.WIDTH);
            int y = RandomUtils.uniform(random, 0, World.HEIGHT);
            if (tiless[x][y].equals(Tileset.FLOOR) && !placedPositions.contains(new Point(x, y))) {
                tiless[x][y] = Tileset.BADITEM;
                placedPositions.add(new Point(x, y));
                //System.out.println("Placed bad item at: " + x + ", " + y);
            }
            attempts++;
        }
        if (placedPositions.size() < numberOfBadItems) {
            System.out.println("Failed to place all bad items. Only placed: " + placedPositions.size());
        }
    }

    public void placeGoodItems(TETile[][] tiless, Random random, int numberOfGoodItems) {
        Set<Point> placedPositions = new HashSet<>();
        int attempts = 0;
        while (placedPositions.size() < numberOfGoodItems && attempts < numberOfGoodItems * 1000) {
            int x = RandomUtils.uniform(random, 0, World.WIDTH);
            int y = RandomUtils.uniform(random, 0, World.HEIGHT);
            if (tiless[x][y].equals(Tileset.FLOOR) && !placedPositions.contains(new Point(x, y))) {
                tiless[x][y] = Tileset.GOODITEM;
                placedPositions.add(new Point(x, y));
                //System.out.println("Placed good item at: " + x + ", " + y);
            }
            attempts++;
        }
        if (placedPositions.size() < numberOfGoodItems) {
            System.out.println("Failed to place all good items. Only placed: " + placedPositions.size());
        }
    }

    public Point getStartingPosition() {
        if (gameCharacter == null) {
            throw new IllegalStateException("Game character must be initialized first.");
        }
        return new Point(gameCharacter.getX(), gameCharacter.getY());
    }

    public interface VisibilityHandler {
        void updateVisibility();
    }

    public TETile[][] updateState(int x, int y, int health, ArrayList<Integer> goodX,
                                  ArrayList<Integer> goodY, ArrayList<Integer> badX, ArrayList<Integer> badY) {
        for (int i = 0; i < goodX.size(); i++) {
            tiles[goodX.get(i)][goodY.get(i)] = Tileset.FLOOR;
        }
        for (int i = 0; i < badX.size(); i++) {
            tiles[badX.get(i)][badY.get(i)] = Tileset.FLOOR;
        }
        gameCharacter.moveTo(tiles, x, y);
        gameCharacter.adminsetHealth(health);
        gameCharacter.setGoodItemsCollected(goodX.size());
        gameCharacter.setGood(goodX, goodY);
        gameCharacter.setBad(badX, badY);
        return tiles;
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    public GameCharacter getGameCharacter() {
        return this.gameCharacter;
    }

    public Point getEntityStartingPoint() {
        int x = rand.nextInt(WIDTH - 2) + 1;
        int y = rand.nextInt(HEIGHT - 4) + 1;
        while (tiles[x][y] != Tileset.FLOOR
                && tiles[x][y] != Tileset.GOODITEM && tiles[x][y] != Tileset.BADITEM) {
            x = rand.nextInt(WIDTH - 2) + 1;
            y = rand.nextInt(HEIGHT - 4) + 1;
        }
        return new Point(x, y);
    }
}
