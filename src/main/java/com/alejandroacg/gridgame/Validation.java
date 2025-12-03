package com.alejandroacg.gridgame;

/**
 * Utility class containing all input-validation logic for GridGame.
 *
 * <p>This class provides simple, reusable validation methods that check whether
 * user input conforms to expected formats. It contains no user interaction
 * (no printing and no input reading), ensuring clean separation of concerns:
 * <ul>
 *     <li>{@code Validation} → only checks values</li>
 *     <li>{@code Main} or menus → handle messages and retry loops</li>
 * </ul>
 *
 * <p>The methods in this class are intentionally minimal and stateless so they
 * can be reused across the application without side effects.</p>
 */
public class Validation {
    /**
     * Checks whether a string contains only digits (0–9).
     *
     * <p>This method does not attempt to parse the number. It only validates
     * the format.</p>
     *
     * @param a the string to validate
     * @return {@code true} if the string contains only digits, {@code false} otherwise
     */
    public static boolean isItInt(String a) {
        return a.matches("[0-9]+");
    }

    /**
     * Checks whether an integer is within a specific inclusive range.
     *
     * @param value the number to validate
     * @param min   the minimum allowed value
     * @param max   the maximum allowed value
     * @return {@code true} if {@code value} is between {@code min} and {@code max}, inclusive
     */
    public static boolean isIntInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Checks whether a string represents a Yes/No response.
     *
     * @param input the string to validate
     * @return {@code true} if the input equals "Y" or "N" (case-insensitive)
     */
    public static boolean isItYN(String input) {
        return input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("N");
    }

    /**
     * Checks whether the user entered the secret cheat command.
     *
     * <p>This method does not modify player state directly—handling the cheat
     * activation is responsibility of the caller.</p>
     *
     * @param input the input string to test
     * @return {@code true} if the input equals "cheat" (case-insensitive)
     */
    public static boolean checkCheat(String input) {
        return input.equalsIgnoreCase("cheat");
    }

    /**
     * Validates a movement command entered by the user.
     *
     * <p>A valid move has the format:</p>
     * <pre>
     *     {distance}{direction}
     *     Example: "2A"
     * </pre>
     *
     * <h3>Validation Rules</h3>
     * <ol>
     *     <li><b>Length error</b>: Input must be exactly 2 characters → return {@code 1}</li>
     *     <li><b>Distance error</b>: First char must be between {@code 1} and {@code rangeOfMovement} → return {@code 2}</li>
     *     <li><b>Direction error</b>: Second char must be one of {@code A, W, S, D} → return {@code 3}</li>
     * </ol>
     *
     * <p>If the move is valid, the method returns {@code 0}.</p>
     *
     * @param input            the movement string to validate
     * @param rangeOfMovement  maximum allowed movement distance based on difficulty
     * @return a status code: {@code 0} if the move is valid, or {@code 1–3} indicating the type of error
     */
    public static int validateMove(String input, int rangeOfMovement) {
        input = input.toUpperCase();

        // Error 1: longitud incorrecta
        if (input.length() != 2) return 1;

        // Error 2: distancia no válida
        char d = input.charAt(0);
        int distance = d - '0';
        if (distance < 1 || distance > rangeOfMovement) {
            return 2;
        }

        // Error 3: dirección incorrecta
        char dir = input.charAt(1);
        if ("AWSD".indexOf(dir) == -1) return 3;

        return 0;
    }
}
