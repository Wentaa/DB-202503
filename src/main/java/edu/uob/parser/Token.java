package edu.uob.parser;

/**
 * Represents a single token in the SQL-like query parsing process.
 * A token consists of a type (e.g., keyword, identifier, operator) and a value (e.g., "SELECT", "table_name").
 */
public class Token {
    private TokenType type;  // The type of token (e.g., keyword, identifier, operator)
    private String value;    // The actual string value of the token

    /**
     * Constructs a `Token` with a specified type and value.
     *
     * @param type  The type of the token (e.g., KEYWORD, IDENTIFIER, NUMBER).
     * @param value The string representation of the token.
     */
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Retrieves the token type.
     *
     * @return The `TokenType` of this token.
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Retrieves the string value of the token.
     *
     * @return The token's value as a string.
     */
    public String getValue() {
        return value;
    }

    /**
     * Converts the token to a string representation for debugging.
     *
     * @return A string representing the token's type and value.
     */
    @Override
    public String toString() {
        return "Token{type=" + type + ", value='" + value + "'}";
    }
}
