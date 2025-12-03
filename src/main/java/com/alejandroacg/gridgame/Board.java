package com.alejandroacg.gridgame;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Represents a game board associated with a single player.
 *
 * <p>Each board contains:</p>
 * <ul>
 *     <li>A grid of characters defining the state of the map</li>
 *     <li>A randomly assigned player start position</li>
 *     <li>Randomly placed enemies, health pickups, a bomb, and a goal</li>
 * </ul>
 *
 * <p>Boards are player-specific: each participant in multiplayer mode receives
 * their own independent board.</p>
 */
public class Board {
    /** 2D matrix storing all board tiles. */
    private final char[][] board;

    /** Marker used to represent empty cells. */
    private final char emptyMarker = ' ';

    /** Marker representing an enemy tile. */
    private final char enemyMarker = 'E';

    /** Marker for health pickup tiles. */
    private final char healthMarker = 'H';

    /** Marker for the goal tile. */
    private final char goalMarker = 'G';

    /** Marker for the bomb pickup tile. */
    private final char bombMarker = 'B';

    /**
     * Initializes a new board and populates it with:
     * <ul>
     *     <li>Player starting position</li>
     *     <li>A goal placed at a safe distance</li>
     *     <li>A difficulty-dependent number of enemies</li>
     *     <li>Two health pickups</li>
     *     <li>One bomb pickup</li>
     * </ul>
     *
     * @param difficulty difficulty configuration (board size, enemy count…)
     * @param player     the player to place on the board
     */
    public Board(Difficulty difficulty, Player player) {
        char[][] board = new char[difficulty.getBoardSize()][difficulty.getBoardSize()];

        // Initialize all cells as empty
        for (char[] chars : board) {
            Arrays.fill(chars, getEmptyMarker());
        }

        // Place player and interactable elements
        startLocation(board, player);
        goalLocation(board, player, getGoalMarker(), difficulty);
        for (int i = 0; i < difficulty.getNumEnemies(); i++) {
            place(board, player, getEnemyMarker());
        }
        for (int i = 0; i < 2; i++) {
            place(board, player, getHealthMarker());
        }
        place(board, player, getBombMarker());

        this.board = board;
    }

    /**
     * Creates a board for each player in the match.
     *
     * @param numPlayers number of players
     * @param difficulty difficulty settings
     * @param players    list of players
     * @return list of boards, one per player
     */
    public static ArrayList<Board> fillBoardsList(int numPlayers, Difficulty difficulty, ArrayList<Player> players) {
        ArrayList<Board> boards = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            Board board = new Board(difficulty, players.get(i));
            boards.add(board);
        }
        return boards;
    }


    /**
     * Randomly assigns a starting location to the player.
     */
    private void startLocation(char[][] board, Player player) {
        Random random = new Random();
        int i, j;
        i = random.nextInt((board.length));
        j = random.nextInt((board[0].length));
        int[] coordinates = {i, j};
        player.setPosition(coordinates);
    }

    /**
     * Randomly places the goal on the board, ensuring:
     * <ul>
     *     <li>It does not overlap with the player</li>
     *     <li>It is not adjacent or too close to the player</li>
     * </ul>
     *
     * <p>The distance constraint increases in harder difficulties.</p>
     */
    // TODO: Improve goal-distance logic.
    //       Currently we measure Euclidean distance directly on the grid, but this ignores wrap-around (overboard)
    //       movement. As a result, the goal can occasionally spawn much closer than intended if the shortest
    //       path crosses the board’s boundaries. We must replace the direct Point2D.distance(...) check with a
    //       toroidal-distance calculation that accounts for wrap-around on both axes.
    private void goalLocation(char[][] board, Player player, char marker, Difficulty difficulty) {
        Random random = new Random();
        int i, j;
        do {
            i = random.nextInt((board.length));
            j = random.nextInt((board[0].length));

        } while (!(board[i][j] == getEmptyMarker()) || i == player.getY() && j == player.getX() ||
                Point2D.distance(j, i, player.getX(), player.getY()) < (difficulty.getBoardSize() - 1) / 2.0);
        board[i][j] = marker;
    }

    /**
     * Randomly places a marker on the board in an empty tile
     * that is not the player's current position.
     */
    protected void place(char[][] board, Player player, char marker) {
        Random random = new Random();
        int i, j;
        do {
            i = random.nextInt((board.length));
            j = random.nextInt((board[0].length));
        } while (!(board[i][j] == getEmptyMarker()) || i == player.getY() && j == player.getX());
        board[i][j] = marker;
    }

    /**
     * Handles a full movement action:
     * <ol>
     *     <li>Moves the player across the board</li>
     *     <li>Determines what was landed on</li>
     *     <li>Redistributes health pickups</li>
     *     <li>Prints the updated board</li>
     * </ol>
     *
     * @param player the player being moved
     * @param move   validated move string (e.g., "2A")
     * @return an integer describing the landing result:<br>
     *         {@code 0} enemy hit, {@code 1} health found,<br>
     *         {@code 2} bomb found, {@code 3} empty tile
     */
    public int move(Player player, String move) {
        playerMovement(player, move);

        int landing = moveLanding(player);

        randomRedistribution(getHealthMarker(), player);

        print(player);

        return landing;
    }

    /**
     * Applies the movement expressed as:
     * <pre>
     *     [distance][direction]
     *     Example: "3W"
     * </pre>
     *
     * Movement wraps around the board edges ("pac-man style").
     */
    private void playerMovement(Player player, String move) {
        int y = player.getY(), x = player.getX();
        int dist = Integer.parseInt(String.valueOf(move.charAt(0)));
        char dir = move.charAt(1);
        if (dir == 'S') {
            y = overboard(y + dist);
        } else if (dir == 'W') {
            y = overboard(y - dist);
        } else if (dir == 'D') {
            x = overboard(x + dist);
        } else {
            x = overboard(x - dist);
        }
        player.setPosition(y, x);
    }

    /**
     * Wraps coordinates around the board edges.
     *
     * <p>If the player moves past the border, they reappear on the other side.</p>
     */
    private int overboard(int coordinate) {
        if (coordinate > board.length-1) {
            coordinate -= board.length;
        } else if (coordinate < 0) {
            coordinate += board.length;
        }
        return (coordinate);
    }

    /**
     * Determines what happens when the player lands on the new tile.
     *
     * @return landing code:
     *         <ul>
     *             <li>{@code 0} enemy — lose 1 health</li>
     *             <li>{@code 1} health pickup — gain 1 health</li>
     *             <li>{@code 2} bomb pickup — gain bomb</li>
     *             <li>{@code 3} empty tile — nothing happens</li>
     *         </ul>
     */
    private int moveLanding(Player player) {
        int y = player.getY(), x = player.getX(), landing;
        if (board[y][x] == getEnemyMarker()) {
            player.setHealth(player.getHealth()-1);
            board[y][x] = getEmptyMarker();
            landing = 0;
        } else if (board[y][x] == getHealthMarker()) {
            player.setHealth(player.getHealth()+1);
            board[y][x] = getEmptyMarker();
            landing = 1;
        } else if (board[y][x] == getBombMarker()) {
            player.setHasBomb(true);
            board[y][x] = getEmptyMarker();
            landing = 2;
        } else {
            landing = 3;
        }
        return landing;
    }

    /**
     * Removes all markers of a given type and redistributes them randomly.
     *
     * <p>This mechanic keeps health pickups moving around the board.</p>
     */
    private void randomRedistribution(char marker, Player player) {
        int count = 0;

        // Remove existing markers
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == marker) {
                    count++;
                    board[i][j] = getEmptyMarker();
                }
            }
        }

        // Redistribute them
        while (count-- > 0) {
            place(board, player, marker);
        }
    }

    /**
     * Destroys all enemies in a 5×5 area centered on the player.
     *
     * <p>The bomb is consumed after use.</p>
     */
    public void bomb(Player player) {
        for (int i = player.getY()-2; i <= player.getY()+2; i++) {
            for (int j = player.getX()-2; j <= player.getX()+2; j++) {
                if (i >= 0 && j>= 0 && i < board.length && j < board.length) {
                    if (board[i][j] == getEnemyMarker()) {
                        board[i][j] = getEmptyMarker();
                    }
                }
            }
        }
        player.setHasBomb(false);
        print(player);
    }

    /**
     * Prints the entire board using normal or cheat-mode rendering.
     */
    public void print(Player player) {
        System.out.println();
        if (player.isCheatMode()) {
            printCheat(player);
        } else {
            printNormal(player);
        }
    }

    /**
     * Standard board rendering: enemies appear hidden.
     */
    private void printNormal(Player player) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i == player.getY() && j == player.getX()) {
                    System.out.print(player.getMarkerColor() + "   ");
                } else if (board[i][j] == getEnemyMarker()) {
                    System.out.print(getEmptyMarkerColor() + "   ");
                } else if (board [i][j] == getHealthMarker()) {
                    System.out.print(getHealthMarkerColor() + "   ");
                } else if (board [i][j] == getBombMarker()) {
                    System.out.print(getBombMarkerColor() + "   ");
                } else if (board [i][j] == getGoalMarker()) {
                    System.out.print(getGoalMarkerColor() + "   ");
                } else {
                    System.out.print(getEmptyMarkerColor() + "   ");
                }
            }
            System.out.println("\n");
        }
    }

    /**
     * Cheat-mode rendering: enemies are revealed.
     */
    private void printCheat(Player player) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i == player.getY() && j == player.getX()) {
                    System.out.print(player.getMarkerColor() + "   ");
                } else if (board[i][j] == getEnemyMarker()) {
                    System.out.print(getEnemyMarkerColor() + "   ");
                } else if (board [i][j] == getHealthMarker()) {
                    System.out.print(getHealthMarkerColor() + "   ");
                } else if (board [i][j] == getBombMarker()) {
                    System.out.print(getBombMarkerColor() + "   ");
                } else if (board [i][j] == getGoalMarker()) {
                    System.out.print(getGoalMarkerColor() + "   ");
                } else {
                    System.out.print(getEmptyMarkerColor() + "   ");
                }
            }
            System.out.println("\n");
        }
    }

    /**
     * Returns the underlying 2D board matrix.
     *
     * @return the board grid where all tiles are stored
     */
    public char[][] getBoard() {
        return board;
    }

    /**
     * @return the character used to represent empty tiles on the board
     */
    public char getEmptyMarker() {
        return emptyMarker;
    }

    /**
     * Returns a stylized, colored representation of an empty tile.
     * Used when printing the board.
     *
     * @return ANSI-colored string for empty cells
     */
    public String getEmptyMarkerColor() {
        return Ansi.colorize(" " + emptyMarker + " ", Attribute.REVERSE(), Attribute.FRAMED(), Attribute.BOLD());
    }

    /**
     * @return the character representing an enemy tile
     */
    public char getEnemyMarker() {
        return enemyMarker;
    }

    /**
     * Returns a stylized, colored representation of an enemy tile.
     * Visible only in cheat mode.
     *
     * @return ANSI-colored string for enemy cells
     */
    public String getEnemyMarkerColor() {
        return Ansi.colorize(" " + enemyMarker + " ", Attribute.BLACK_TEXT(), Attribute.BRIGHT_RED_BACK(), Attribute.ENCIRCLED(), Attribute.BOLD());
    }

    /**
     * @return the character representing a health pickup tile
     */
    public char getHealthMarker() {
        return healthMarker;
    }

    /**
     * Returns a stylized, colored representation of a health pickup tile.
     *
     * @return ANSI-colored string for health cells
     */
    public String getHealthMarkerColor() {
        return Ansi.colorize(" " + healthMarker + " ", Attribute.BLACK_TEXT(), Attribute.BRIGHT_GREEN_BACK(), Attribute.ENCIRCLED(), Attribute.BOLD());
    }

    /**
     * @return the character representing the goal tile
     */
    public char getGoalMarker() {
        return goalMarker;
    }

    /**
     * Returns a stylized, colored representation of the goal tile.
     *
     * @return ANSI-colored string for the goal cell
     */
    public String getGoalMarkerColor() {
        return Ansi.colorize(" " + goalMarker + " ", Attribute.BLACK_TEXT(), Attribute.BRIGHT_YELLOW_BACK(), Attribute.ENCIRCLED(), Attribute.BOLD());
    }

    /**
     * @return the character representing the bomb pickup tile
     */
    public char getBombMarker() {
        return bombMarker;
    }

    /**
     * Returns a stylized, colored representation of a bomb pickup tile.
     *
     * @return ANSI-colored string for bomb cells
     */
    public String getBombMarkerColor() {
        return Ansi.colorize(" " + bombMarker + " ", Attribute.BLACK_TEXT(), Attribute.WHITE_BACK(), Attribute.ENCIRCLED(), Attribute.BOLD());
    }
}
