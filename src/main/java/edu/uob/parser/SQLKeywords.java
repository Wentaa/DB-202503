package edu.uob.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for identifying reserved SQL-like keywords.
 * Ensures that keywords are recognized and handled correctly in query parsing.
 */
public class SQLKeywords {
    // Set of reserved keywords in the simplified SQL-like language
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "ADD",
            "INSERT", "INTO", "VALUES", "SELECT", "FROM", "WHERE",
            "UPDATE", "SET", "DELETE", "JOIN", "AND", "ON", "OR", "LIKE",
            "TRUE", "FALSE", "NULL"
    ));

    /**
     * Checks if a given word is a reserved SQL-like keyword.
     * The comparison is case-insensitive.
     *
     * @param word The word to check.
     * @return `true` if the word is a reserved keyword, otherwise `false`.
     */
    public static boolean isKeyword(String word) {
        return KEYWORDS.contains(word.toUpperCase());
    }
}
