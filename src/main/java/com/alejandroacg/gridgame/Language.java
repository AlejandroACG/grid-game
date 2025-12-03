package com.alejandroacg.gridgame;

/**
 * Represents the supported languages for the GridGame.
 *
 * <ul>
 *     <li>{@code EN} – English language. All game messages are displayed
 *     in English.</li>
 *
 *     <li>{@code ES} – Spanish language. All game messages are displayed
 *     in Spanish.</li>
 * </ul>
 *
 * This enum is used during initialization to determine which localized
 * text set will be loaded by the {@code Messages} class.
 */
public enum Language {
    EN,
    ES
}
