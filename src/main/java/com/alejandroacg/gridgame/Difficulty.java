package com.alejandroacg.gridgame;

/**
 * Represents the difficulty settings applied to a GridGame match.
 *
 * <p>Each difficulty level determines:</p>
 *
 * <ul>
 *     <li>The total number of enemies placed on the board</li>
 *     <li>The size of the board (width and height)</li>
 *     <li>The initial amount of health each player starts with</li>
 *     <li>The maximum movement distance allowed in a single action</li>
 * </ul>
 *
 * <p>The constructor receives an integer (1–3) and maps it to predefined
 * difficulty profiles:</p>
 *
 * <ul>
 *     <li><b>1 – Easy:</b> Small board, few enemies, generous health.</li>
 *     <li><b>2 – Normal:</b> Medium board, more enemies, standard health.</li>
 *     <li><b>3 – Hard:</b> Large board, many enemies, reduced health.</li>
 * </ul>
 *
 * <p>The {@code rangeOfMovement} field is currently fixed at 3 but is kept
 * configurable for potential future extensions.</p>
 */
public class Difficulty {
    /** Total number of enemies that will be randomly placed on the board. */
    private final int numEnemies;

    /** Size of the board (NxN). */
    private final int boardSize;

    /** Amount of health each player begins the match with. */
    private final int initialHealth;

    /**
     * Maximum number of tiles a player may move in a single action.
     * <p>Although constant for now, it is exposed via a getter so it can
     * easily become difficulty-dependent later.</p>
     */
    private final int rangeOfMovement = 3;

    /**
     * Constructs a new difficulty configuration based on a numeric level.
     *
     * @param difficulty difficulty level ({@code 1 = Easy}, {@code 2 = Normal}, {@code 3 = Hard})
     */
    public Difficulty (int difficulty) {
        if (difficulty == 1) {
            numEnemies = 8;
            boardSize = 6;
            initialHealth = 4;

        } else if (difficulty == 2) {
            numEnemies = 32;
            boardSize = 12;
            initialHealth = 4;

        } else {
            numEnemies = 128;
            boardSize = 24;
            initialHealth = 3;
        }
    }

    /**
     * Returns the number of enemies to place on the board.
     *
     * @return number of enemies
     */
    public int getNumEnemies() {
        return numEnemies;
    }

    /**
     * Returns the board dimension (NxN).
     *
     * @return the square board size
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Returns the initial health each player starts with.
     *
     * @return player starting health
     */
    public int getInitialHealth() {
        return initialHealth;
    }

    /**
     * Returns the maximum allowed movement distance per move.
     *
     * @return movement range (currently always {@code 3})
     */
    public int getRangeOfMovement() {
        return rangeOfMovement;
    }
}
