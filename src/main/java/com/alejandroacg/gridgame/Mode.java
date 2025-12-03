package com.alejandroacg.gridgame;

/**
 * Represents the two possible gameplay modes of the GridGame.
 *
 * <ul>
 *     <li>{@code SP} – Single Player mode. Only one player participates, and the
 *     game ends when the player dies or reaches the goal.</li>
 *
 *     <li>{@code MP} – Multiplayer mode. Multiple players take turns on their own
 *     boards. The game ends when only one player remains alive or when someone
 *     reaches the goal.</li>
 * </ul>
 *
 * This enum is used globally to determine how turns, victory conditions,
 * and player interactions are handled.
 */
public enum Mode {
    SP,
    MP
}
