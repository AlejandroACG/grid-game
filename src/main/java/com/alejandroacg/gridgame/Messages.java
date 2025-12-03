package com.alejandroacg.gridgame;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Centralized manager for all localized text used throughout GridGame.
 *
 * <p>This class loads language-specific message files from the project's
 * {@code resources} directory (e.g., {@code Messages_en.properties},
 * {@code Messages_es.properties}) and provides formatted strings to the rest
 * of the application.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *     <li>Loading the appropriate {@link ResourceBundle} based on the selected
 *     {@link Language}.</li>
 *     <li>Retrieving localized text using a message key.</li>
 *     <li>Formatting messages using {@link MessageFormat} when parameters are provided.</li>
 * </ul>
 *
 * <p>This ensures that all UI text is consistently localized and easy to
 * maintain or expand to additional languages.</p>
 */
public class Messages {
    /** Holds the currently active set of localized messages. */
    private static ResourceBundle bundle;

    /**
     * Sets the active language for the game.
     *
     * <p>When this method is called, the internal {@link ResourceBundle}
     * is updated so that all subsequent calls to {@link #get(String, Object...)}
     * return messages in the chosen language.</p>
     *
     * @param lang the selected language (English or Spanish)
     */
    public static void setLanguage(Language lang) {
        Locale locale = switch (lang) {
            case EN -> Locale.ENGLISH;
            case ES -> new Locale("es", "ES");
        };
        bundle = ResourceBundle.getBundle("Messages", locale);
    }

    /**
     * Retrieves a localized message by key and formats it with optional arguments.
     *
     * <p>For example, if the message file contains:
     * <pre>
     * welcome = Welcome, {0}!
     * </pre>
     * calling <br>
     * <code>Messages.get("welcome", "Alice")</code><br>
     * will return:
     * <pre>
     * Welcome, Alice!
     * </pre>
     *
     * @param key  the message identifier inside the locale file
     * @param args optional arguments to format inside the message
     * @return the formatted localized string
     */
    public static String get(String key, Object... args) {
        String pattern = bundle.getString(key);
        return MessageFormat.format(pattern, args);
    }
}
