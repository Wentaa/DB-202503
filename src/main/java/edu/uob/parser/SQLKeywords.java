// SQLKeywords.java
package edu.uob.parser;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SQLKeywords {
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "ADD",
            "INSERT", "INTO", "VALUES", "SELECT", "FROM", "WHERE",
            "UPDATE", "SET", "DELETE", "JOIN", "AND", "ON", "OR", "LIKE",
            "TRUE", "FALSE", "NULL"
    ));

    public static boolean isKeyword(String word) {
        return KEYWORDS.contains(word.toUpperCase());
    }
}