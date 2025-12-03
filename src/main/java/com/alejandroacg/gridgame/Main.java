package com.alejandroacg.gridgame;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * Entry point of GridGame.
 *
 * <p>This class orchestrates the main game loop, including:</p>
 * <ul>
 *     <li>Language selection and message localization</li>
 *     <li>Difficulty and player setup</li>
 *     <li>Turn management for single and multiplayer modes</li>
 *     <li>Handling player actions (move, count, bomb, legend, cheat)</li>
 *     <li>Detecting win/lose conditions and restarting the game</li>
 * </ul>
 */
public class Main {
    /** Shared scanner used for all console input throughout the program. */
    private static final Scanner keyboard = new Scanner(System.in);

    /**
     * Application entry point.
     *
     * <p>Initializes language, configures a match, runs the main game loop and
     * optionally restarts the game if the user chooses to play again.</p>
     *
     * @param args standard command-line arguments (unused)
     */
    public static void main(String[] args) {

        boolean exitApp;

        // Detect system language and set default game language
        Locale systemLocale = Locale.getDefault();
        String defaultLang = systemLocale.getLanguage();

        Language detectedLanguage = defaultLang.equalsIgnoreCase("es") ? Language.ES : Language.EN;
        Messages.setLanguage(detectedLanguage);

        // Let player choose language. Use English as default if input is not "ES"
        System.out.print(Messages.get("choose_language_prompt"));
        String langInput = keyboard.nextLine().trim().toUpperCase();
        if (langInput.equals("ES")) {
            Messages.setLanguage(Language.ES);
        } else {
            Messages.setLanguage(Language.EN);
        }

        System.out.println(Messages.get("welcome"));

        // Main application loop (restarts entire game if player chooses to play again)
        do {
            System.out.print(Messages.get("ask_player_amount"));
            int numPlayers = readIntInRange(1,9);

            Game.setMode(numPlayers == 1 ? Mode.SP : Mode.MP);

            System.out.print(Messages.get("ask_difficulty"));
            int difficultyInput = readIntInRange(1, 3);

            Difficulty difficulty = new Difficulty(difficultyInput);

            // Create the players
            ArrayList<Player> players = Player.fillPlayersList(numPlayers, difficulty);

            // Create the boards (one per player)
            ArrayList<Board> boards = Board.fillBoardsList(numPlayers, difficulty, players);

            // Start the match loop
            do {
                Game.setGameOver(false);
                Game.setWinner(-1);

                // Player turns
                for (int i = 0; i < numPlayers; i++) {
                    // Multiplayer: if only one player is alive, the game ends
                    if (Game.getMode() == Mode.MP) {
                        if (Player.areTheyDead(numPlayers, players)) {
                            Game.setWinner(i);
                            System.out.println(Messages.get("players_are_dead", players.get(i).getName()));
                            Game.setGameOver(true);
                            break;
                        }
                        // Skip turn if current player is already dead
                        if (players.get(i).getHealth() == 0) {
                            continue;
                        }
                        // Single player: if the player is dead, the game ends
                    } else if (Player.isDead(players.get(i))) {
                        System.out.println(Messages.get("player_is_dead", players.get(i).getName()));
                        Game.setGameOver(true);
                        break;
                    }

                    // Turn begins
                    System.out.println(Messages.get(
                            players.get(i).getHealth() > 1 ? "turn_begins_lives" : "turn_begins_life",
                            players.get(i).getName(), players.get(i).getHealth()));

                    // Auxiliary loop to stay in the menu (legend, cheat toggling, etc.)
                    boolean stayInMenu;
                    boolean printLegend = false;
                    do {
                        stayInMenu = false;

                        // Print board (and legend if requested)
                        boards.get(i).print(players.get(i));
                        if (printLegend) {
                            printLegend(players.get(i), boards.get(i));
                            printLegend = false;
                        }

                        // Compute menu range dynamically based on player abilities
                        int[] range = Game.getMenuRange(players.get(i));
                        int min = range[0];
                        int max = range[1];

                        System.out.println(Messages.get("main_menu_head", players.get(i).getName()));

                        if (players.get(i).hasCount()) {
                            System.out.println(Messages.get("main_menu_count"));
                        }

                        System.out.println(Messages.get("main_menu_options"));

                        if (players.get(i).hasBomb()) {
                            System.out.println(Messages.get("main_menu_bomb"));
                        }

                        System.out.print(Messages.get("main_menu_foot"));

                        int option;

                        // Menu input loop (includes cheat detection)
                        while (true) {
                            String input = keyboard.nextLine().trim();

                            // Secret cheat: toggle cheat mode from menu input
                            if (Validation.checkCheat(input)) {
                                switchCheatMode(players.get(i));
                                option = -1;
                                break;
                            }

                            if (!Validation.isItInt(input)) {
                                System.out.print(Messages.get("invalid_characters"));
                                continue;
                            }

                            option = Integer.parseInt(input);

                            if (!Validation.isIntInRange(option, min, max)) {
                                System.out.print(Messages.get("invalid_range", min, max));
                                continue;
                            }

                            break;
                        }

                        // If cheat was activated, remain in menu without consuming the turn
                        if (option == -1) {
                            stayInMenu = true;
                            continue;
                        }

                        // Count remaining enemies (one-time ability, costs 1 health)
                        if (option == 0) {
                            players.get(i).setHasCount(false);
                            players.get(i).setHealth(players.get(i).getHealth()-1);
                            printCountResult(players.get(i), boards.get(i));

                        // Move
                        } else if (option == 1) {
                            System.out.print(Messages.get("movement_prompt"));

                            String move = null;

                            // Movement input loop (with cheat support and detailed validation)
                            while (true) {
                                String input = keyboard.nextLine().trim();

                                // Secret cheat: can also be activated here
                                if (Validation.checkCheat(input)) {
                                    switchCheatMode(players.get(i));
                                    stayInMenu = true;
                                    break;
                                }

                                int result = Validation.validateMove(input, difficulty.getRangeOfMovement());

                                if (result == 0) {
                                    move = input.toUpperCase();
                                    break;
                                } else if (result == 1) {
                                    System.out.print(Messages.get("invalid_format_1"));
                                } else if (result == 2) {
                                    System.out.print(Messages.get("invalid_format_2"));
                                } else if (result == 3) {
                                    System.out.print(Messages.get("invalid_format_3"));
                                }
                            }

                            // Only move if the input was valid and not interrupted by cheat
                            if (move != null) {
                                int landing = boards.get(i).move(players.get(i), move);

                                if (landing == 0) {
                                    System.out.println(Messages.get("enemy_met", players.get(i).getName()));
                                } else if (landing == 1) {
                                    System.out.println(Messages.get("potion_found", players.get(i).getName()));
                                } else if (landing == 2) {
                                    System.out.println(Messages.get("bomb_found", players.get(i).getName()));
                                }
                            }

                            // Legend: show legend and remain in menu
                        } else if (option == 2) {
                            printLegend = true;
                            stayInMenu = true;

                            // Use bomb: clear nearby enemies and consume bomb
                        } else if (option == 3) {
                            boards.get(i).bomb(players.get(i));
                            System.out.println(Messages.get("bomb_used", players.get(i).getName()));
                        }
                    } while (stayInMenu);

                    // Check if the player reached the goal
                    if (boards.get(i).getBoard()[players.get(i).getY()][players.get(i).getX()] == boards.get(i).getGoalMarker()) {
                        Game.setGameOver(true);
                        System.out.print(Messages.get("congratulations"));
                        Game.setWinner(i);
                        break;
                    }

                    // Report remaining lives and wait for the player to continue
                    isPlayerAlive(players.get(i));
                    if (Game.getMode() == Mode.MP) {
                        System.out.print(Messages.get("next_turn_mp"));
                    } else {
                        System.out.print(Messages.get("next_turn_sp"));
                    }
                    keyboard.nextLine();
                    System.out.println();
                }
            } while (!Game.isGameOver());

            // End-of-game messages
            if (Game.getMode() == Mode.MP) {
                System.out.println(Messages.get("winner", players.get(Game.getWinner()).getName()));
            } else if (Game.getMode() == Mode.SP && Game.getWinner() >= 0) {
                System.out.println(Messages.get("winner"));
            } else {
                System.out.println(Messages.get("game_over"));
            }

            // Offer to play again. Exit if refused
            System.out.print(Messages.get("play_again"));
            exitApp = readYN().equalsIgnoreCase("N");

        } while (!exitApp);
        System.out.println(Messages.get("thanks"));
    }

    /**
     * Reads an integer from input and enforces that it lies within the given range.
     *
     * <p>Shows appropriate error messages for invalid or out-of-range input.</p>
     *
     * @param min minimum accepted value (inclusive)
     * @param max maximum accepted value (inclusive)
     * @return a valid integer value within the specified range
     */
    private static int readIntInRange(int min, int max) {
        int value;

        while (true) {

            String input = keyboard.nextLine().trim();

            if (!Validation.isItInt(input)) {
                System.out.print(Messages.get("invalid_characters"));
                continue;
            }

            value = Integer.parseInt(input);

            if (!Validation.isIntInRange(value, min, max)) {
                System.out.print(Messages.get("invalid_range", min, max));
                continue;
            }

            return value;
        }
    }

    /**
     * Reads a Yes/No response from the user.
     *
     * <p>Only accepts "Y" or "N" (case-insensitive). Any other value triggers
     * an error message and a retry.</p>
     *
     * @return {@code "Y"} or {@code "N"} in uppercase
     */
    private static String readYN() {
        while (true) {

            String input = keyboard.nextLine().trim();

            if (Validation.isItYN(input)) {
                return input.toUpperCase();
            }

            System.out.print(Messages.get("invalid_YN"));
        }
    }

    /**
     * Counts remaining enemies on the player's board and prints the result.
     *
     * <p>This is used by the one-time "count" ability and consumes one life.</p>
     *
     * @param p the player using the ability
     * @param b the board to scan for enemies
     */
    private static void printCountResult(Player p, Board b) {
        int count = 0;
        for (char[] row : b.getBoard()) {
            for (char c : row) {
                if (c == b.getEnemyMarker()) count++;
            }
        }

        if (count > 1) {
            System.out.println(Messages.get("enemies_left", p.getName(), count));
        } else if (count == 1) {
            System.out.println(Messages.get("enemy_left", p.getName()));
        } else {
            System.out.println(Messages.get("no_enemies_left", p.getName()));
        }
    }

    /**
     * Toggles cheat mode for a player and prints a small visual indicator.
     *
     * <p>{@code ";)"} when enabling, {@code ";("} when disabling.</p>
     *
     * @param player the player whose cheat mode is being toggled
     */
    private static void switchCheatMode(Player player) {
        player.setCheatMode(!player.isCheatMode());
        System.out.println(player.isCheatMode() ? ";)" : ";(");
    }

    /**
     * Prints a message describing whether the player still has lives left
     * or has just run out of health.
     *
     * @param player the player being checked
     */
    private static void isPlayerAlive(Player player) {
        if (player.getHealth() == 0) {
            System.out.println(Messages.get("out_of_lives", player.getName()));
        } else if (player.getHealth() > 1) {
            System.out.println(Messages.get("lives_left", player.getName(), player.getHealth()));
        } else {
            System.out.println(Messages.get("life_left", player.getName()));
        }
    }

    /**
     * Prints the legend for board markers, including enemy markers if cheat mode is active.
     *
     * @param player the player whose marker and cheat status are used
     * @param board  the board providing marker visuals
     */
    private static void printLegend(Player player, Board board) {
        System.out.println(Messages.get("legend_main",
                board.getEmptyMarkerColor(),
                player.getMarkerColor(),
                player.getName(),
                board.getGoalMarkerColor(),
                board.getHealthMarkerColor(),
                board.getBombMarkerColor()));
        if (player.isCheatMode()) {
            System.out.println(Messages.get("legend_enemy", board.getEnemyMarkerColor()));
        }
    }
}
