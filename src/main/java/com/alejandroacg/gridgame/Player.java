package com.alejandroacg.gridgame;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents a player participating in a GridGame match.
 *
 * <p>Each player has:</p>
 * <ul>
 *     <li>A unique <b>name</b> chosen by the user</li>
 *     <li>A <b>marker</b> used to display the player on the board</li>
 *     <li>A pair of <b>coordinates</b> representing their current position</li>
 *     <li>A <b>health</b> value that determines survival</li>
 *     <li>A boolean <b>cheat mode</b> toggle, enabling special visibility</li>
 *     <li>A one-use <b>bomb</b> power-up</li>
 *     <li>A one-use <b>count</b> ability, which reveals how many enemies remain</li>
 * </ul>
 *
 * <p>Player objects are created during initialization and remain active
 * until they die or the game ends.</p>
 */
public class Player {
    /** Current health of the player. When it reaches 0, the player dies. */
    private int health;

    /** Player's chosen display name (1–8 characters, unique among players). */
    private final String name;

    /** Character used to represent the player on the board. */
    private final char marker;

    /** Internal storage of the player's (y, x) board position. */
    private int[] position = new int[2];

    /** Whether cheat mode is active (reveals enemies on the board). */
    private boolean cheatMode = false;

    /** Whether the player currently possesses a bomb. */
    private boolean hasBomb = false;

    /** Whether the player still has their one-time "count enemies" ability. */
    private boolean hasCount = true;


    /**
     * Creates a new player with initial stats defined by the difficulty level.
     *
     * @param difficulty the difficulty settings providing starting health
     * @param marker     the character used to represent this player
     * @param name       the player's chosen name
     */
    public Player(Difficulty difficulty, char marker, String name) {
        health = difficulty.getInitialHealth();
        this.marker = marker;
        this.name = name;
    }

    /**
     * Builds a list of players by asking the user to input unique valid names.
     *
     * <p>Names must be:</p>
     * <ul>
     *     <li>Non-null</li>
     *     <li>Non-empty</li>
     *     <li>No longer than 8 characters</li>
     *     <li>Unique among players</li>
     * </ul>
     *
     * @return a list of initialized {@link Player} objects
     */
    public static ArrayList<Player> fillPlayersList(int numPlayers, Difficulty difficulty) {
        Scanner keyboard = new Scanner(System.in);
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            String name;
            do {
                System.out.print(Messages.get("ask_player_name", i + 1));
                name = keyboard.nextLine();
                name = name.trim();

                if (isNameInvalid(name)) {
                    System.out.println(Messages.get("invalid_name"));
                } else if (isNameTaken(name, players)) {
                    System.out.println(Messages.get("name_already_taken"));
                }
            } while (isNameInvalid(name));
            Player player = new Player(difficulty, String.valueOf(i + 1).charAt(0), name);
            players.add(player);
        }
        return players;
    }

    /**
     * Checks whether a given name is invalid based on length and emptiness rules.
     *
     * @param name name to validate
     * @return {@code true} if invalid; {@code false} otherwise
     */
    public static boolean isNameInvalid(String name) {
        return !(name != null && !name.isEmpty() && name.length() <= 8);
    }

    /**
     * Ensures that no two players share the same name.
     *
     * @param name    name being tested
     * @param players existing players
     * @return {@code true} if the name is already taken; otherwise {@code false}
     */
    private static boolean isNameTaken(String name, ArrayList<Player> players) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /** @return the player's name */
    public String getName() {
        return name;
    }

    /**
     * Produces a colored string representing the player's marker,
     * for display inside the game board.
     *
     * @return colored marker text
     */
    public String getMarkerColor() {
        return Ansi.colorize(" " + marker + " ", Attribute.BLACK_TEXT(), Attribute.BRIGHT_BLUE_BACK(), Attribute.ENCIRCLED(), Attribute.BOLD());
    }

    /** Sets the player's health value. */
    public void setHealth(int health) {
        this.health = health;
    }

    /** @return the player's current health */
    public int getHealth() {
        return health;
    }

    /** Sets the player's position as a (y, x) array. */
    public void setPosition(int[] position) {
        this.position = position;
    }

    /** Sets the player's position using direct coordinates. */
    public void setPosition(int y, int x) {
        this.position[0] = y;
        this.position[1] = x;
    }

    /** @return the player's (y, x) coordinates */
    public int[] getPosition() {
        return position;
    }

    /** @return the player's current row index (Y coordinate) */
    public int getY() {
        return Integer.parseInt(String.valueOf(getPosition()[0]));
    }

    /** @return the player's current column index (X coordinate) */
    public int getX() {
        return Integer.parseInt(String.valueOf(getPosition()[1]));
    }

    /**
     * Enables or disables cheat mode for this player.
     *
     * @param cheatMode {@code true} to enable cheat mode; {@code false} to disable it
     */
    public void setCheatMode(boolean cheatMode) {
        this.cheatMode = cheatMode;
    }

    /**
     * Indicates whether cheat mode is currently active for this player.
     *
     * @return {@code true} if cheat mode is enabled; {@code false} otherwise
     */
    public boolean isCheatMode() {
        return cheatMode;
    }

    /**
     * Sets whether the player possesses a bomb.
     *
     * @param hasBomb {@code true} if the player has a bomb
     */
    public void setHasBomb(boolean hasBomb) {
        this.hasBomb = hasBomb;
    }

    /**
     * Indicates whether the player currently has a bomb available.
     *
     * @return {@code true} if the player has a bomb; {@code false} otherwise
     */
    public boolean hasBomb() {
        return hasBomb;
    }

    /**
     * Indicates whether the player still has their one-time "count enemies" ability.
     *
     * @return {@code true} if the player can still use count; {@code false} if already spent
     */
    public boolean hasCount() {
        return hasCount;
    }

    /**
     * Marks the player as having or not having the "count enemies" ability.
     *
     * @param hasCount {@code true} if the player should have the ability; {@code false} if it is spent
     */
    public void setHasCount(boolean hasCount) {
        this.hasCount = hasCount;
    }

    /**
     * Checks whether all but one player are dead in a multiplayer game.
     *
     * <p>This is used to determine if the match should end with the last
     * remaining player as the winner.</p>
     *
     * @param numPlayers total number of players in the match
     * @param players    list of all players
     * @return {@code true} if only a single player is still alive; {@code false} otherwise
     */
    public static boolean areTheyDead(int numPlayers, ArrayList<Player> players) {
        int deathPlayers = 0;
        for (int i = 0; i < numPlayers; i++) {
            if (isDead(players.get(i))) {
                deathPlayers += 1;
            }
        }
        return deathPlayers == (numPlayers - 1);
    }

    /**
     * Checks whether a given player has no remaining health.
     *
     * @param player the player to test
     * @return {@code true} if the player's health is 0; {@code false} otherwise
     */
    public static boolean isDead(Player player) {
        return player.getHealth() == 0;
    }
}
