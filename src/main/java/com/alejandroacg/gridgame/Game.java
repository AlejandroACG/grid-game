package com.alejandroacg.gridgame;

/**
 * Manages the global state of a GridGame match, including game mode,
 * victory conditions, and turn termination.
 *
 * <p>This class contains only static fields and methods, acting as a
 * lightweight controller shared across the whole application.</p>
 *
 * <ul>
 *     <li>{@code gameOver} – Indicates whether the current match has ended.</li>
 *     <li>{@code winner} – Stores the index of the winning player (or {@code -1} if none yet).</li>
 *     <li>{@code mode} – Determines whether the match is Single Player ({@code SP}) or Multiplayer ({@code MP}).</li>
 * </ul>
 *
 * <p>The values stored here guide the main loop in {@code Main} and affect
 * how turns, checks, and end-game messages behave.</p>
 */
public class Game {
    /** Flag indicating whether the current match has reached an end state. */
    private static boolean gameOver;

    /** Index of the winning player, or {@code -1} if no winner has been determined yet. */
    private static int winner;

    /** Current gameplay mode (Single Player or Multiplayer). */
    private static Mode mode = Mode.SP;

    /**
     * Returns whether the game has ended.
     *
     * @return {@code true} if the match is over; {@code false} otherwise.
     */
    public static boolean isGameOver() {
        return gameOver;
    }


    /**
     * Sets the game-over flag.
     *
     * @param gameOver {@code true} to mark the match as completed.
     */
    public static void setGameOver(boolean gameOver) {
        Game.gameOver = gameOver;
    }

    /**
     * Returns the index of the winning player.
     *
     * @return a player index, or {@code -1} if no winner has been assigned.
     */
    public static int getWinner() {
        return winner;
    }

    /**
     * Assigns the winning player.
     *
     * @param winner the index of the victorious player.
     */
    public static void setWinner(int winner) {
        Game.winner = winner;
    }

    /**
     * Computes the valid menu option range for a given player.
     *
     * <p>The menu normally spans {@code 1–2}, but it may expand:</p>
     *
     * <ul>
     *     <li>If the player still has a {@code count} ability, the minimum becomes {@code 0}.</li>
     *     <li>If the player possesses a bomb, the maximum increases to include option {@code 3}.</li>
     * </ul>
     *
     * @param player the player whose abilities determine the menu bounds.
     * @return an array {@code {min, max}} describing the valid menu inputs.
     */
    public static int[] getMenuRange(Player player) {
        int min = 1, max = 2;

        if (player.hasCount()) min--;
        if (player.hasBomb()) max++;

        return new int[]{min, max};
    }

    /**
     * Sets the gameplay mode.
     *
     * @param newMode either {@link Mode#SP} or {@link Mode#MP}.
     */
    public static void setMode(Mode newMode) {
        mode = newMode;
    }

    /**
     * Retrieves the current gameplay mode.
     *
     * @return the active {@link Mode}.
     */
    public static Mode getMode() {
        return mode;
    }
}
