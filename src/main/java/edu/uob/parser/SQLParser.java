package edu.uob.parser;

import edu.uob.commands.*;
import edu.uob.conditions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SQLParser {
    private List<Token> tokens;
    private int position;

    // SQL keywords
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "ADD",
            "INSERT", "INTO", "VALUES", "SELECT", "FROM", "WHERE",
            "UPDATE", "SET", "DELETE", "JOIN", "AND", "ON", "OR", "LIKE",
            "TRUE", "FALSE", "NULL"
    ));
    /**
     * Initializes the parser with a given input SQL query.
     * Tokenizes the input string and sets the initial parsing position.
     *
     * @param input The SQL-like query to be parsed.
     */
    public SQLParser(String input) {
        this.tokens = tokenize(input);
        this.position = 0;
    }

    /**
     * Tokenizes the input SQL-like string into a list of tokens.
     * Recognizes keywords, identifiers, numbers, string literals, and operators.
     *
     * @param input The query string to tokenize.
     * @return A list of tokens representing the parsed query.
     */

    private List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int pos = 0;

        while (pos < input.length()) {
            char c = input.charAt(pos);

            // Skip whitespace
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }

            // Handle identifiers and keywords
            if (Character.isLetter(c)) {
                int start = pos;
                while (pos < input.length() && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_')) {
                    pos++;
                }
                String word = input.substring(start, pos);

                // Check if it's a keyword
                if (KEYWORDS.contains(word.toUpperCase())) {
                    tokens.add(new Token(TokenType.KEYWORD, word.toUpperCase()));
                } else {
                    tokens.add(new Token(TokenType.IDENTIFIER, word));
                }
                continue;
            }

            // Handle numbers
            if (Character.isDigit(c) || (c == '-' && pos + 1 < input.length() && Character.isDigit(input.charAt(pos + 1)))) {
                int start = pos;
                boolean hasDecimal = false;

                // Handle negative numbers
                if (c == '-') {
                    pos++;
                }

                while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                    if (input.charAt(pos) == '.') {
                        if (hasDecimal) {
                            break; // Second decimal point, not part of the number
                        }
                        hasDecimal = true;
                    }
                    pos++;
                }

                tokens.add(new Token(TokenType.NUMBER, input.substring(start, pos)));
                continue;
            }

            // Handle string literals
            if (c == '\'') {
                int start = pos;
                pos++; // Skip opening quote

                while (pos < input.length() && input.charAt(pos) != '\'') {
                    pos++;
                }

                if (pos >= input.length()) {
                    throw new RuntimeException("Unterminated string literal");
                }

                pos++; // Skip closing quote
                tokens.add(new Token(TokenType.STRING_LITERAL, input.substring(start + 1, pos - 1)));
                continue;
            }

            // Handle operators and punctuation
            switch (c) {
                case ',':
                    tokens.add(new Token(TokenType.COMMA, ","));
                    pos++;
                    break;
                case ';':
                    tokens.add(new Token(TokenType.SEMICOLON, ";"));
                    pos++;
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LEFT_PAREN, "("));
                    pos++;
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RIGHT_PAREN, ")"));
                    pos++;
                    break;
                case '*':
                    tokens.add(new Token(TokenType.STAR, "*"));
                    pos++;
                    break;
                case '=':
                    if (pos + 1 < input.length() && input.charAt(pos + 1) == '=') {
                        tokens.add(new Token(TokenType.OPERATOR, "=="));
                        pos += 2;
                    } else {
                        tokens.add(new Token(TokenType.OPERATOR, "="));
                        pos++;
                    }
                    break;
                case '!':
                    if (pos + 1 < input.length() && input.charAt(pos + 1) == '=') {
                        tokens.add(new Token(TokenType.OPERATOR, "!="));
                        pos += 2;
                    } else {
                        throw new RuntimeException("Invalid character: " + c);
                    }
                    break;
                case '>':
                    if (pos + 1 < input.length() && input.charAt(pos + 1) == '=') {
                        tokens.add(new Token(TokenType.OPERATOR, ">="));
                        pos += 2;
                    } else {
                        tokens.add(new Token(TokenType.OPERATOR, ">"));
                        pos++;
                    }
                    break;
                case '<':
                    if (pos + 1 < input.length() && input.charAt(pos + 1) == '=') {
                        tokens.add(new Token(TokenType.OPERATOR, "<="));
                        pos += 2;
                    } else {
                        tokens.add(new Token(TokenType.OPERATOR, "<"));
                        pos++;
                    }
                    break;
                default:
                    // Skip unrecognized characters
                    pos++;
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private Token peek() {
        return tokens.get(position);
    }

    private Token advance() {
        if (position < tokens.size()) {
            return tokens.get(position++);
        }
        return tokens.get(tokens.size() - 1); // Return EOF token
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private boolean checkKeyword(String keyword) {
        if (isAtEnd()) return false;
        Token token = peek();
        return token.getType() == TokenType.KEYWORD && token.getValue().equals(keyword);
    }

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean matchKeyword(String keyword) {
        if (checkKeyword(keyword)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw new RuntimeException("Parse error: " + message + ", found: " + peek().getValue());
    }

    private Token consumeKeyword(String keyword, String message) {
        if (checkKeyword(keyword)) return advance();

        throw new RuntimeException("Parse error: " + message + ", found: " + peek().getValue());
    }

    // Main parsing method
    public Command parse() {
        Command command = parseCommand();
        consume(TokenType.SEMICOLON, "Expected ';' at end of command");
        return command;
    }

    private Command parseCommand() {
        // Check the command type
        if (matchKeyword("USE")) {
            return parseUseCommand();
        } else if (matchKeyword("CREATE")) {
            return parseCreateCommand();
        } else if (matchKeyword("DROP")) {
            return parseDropCommand();
        } else if (matchKeyword("ALTER")) {
            return parseAlterCommand();
        } else if (matchKeyword("INSERT")) {
            return parseInsertCommand();
        } else if (matchKeyword("SELECT")) {
            return parseSelectCommand();
        } else if (matchKeyword("UPDATE")) {
            return parseUpdateCommand();
        } else if (matchKeyword("DELETE")) {
            return parseDeleteCommand();
        } else if (matchKeyword("JOIN")) {
            return parseJoinCommand();
        } else {
            throw new RuntimeException("Unknown command: " + peek().getValue());
        }
    }

    private UseCommand parseUseCommand() {
        Token databaseToken = consume(TokenType.IDENTIFIER, "Expected database name");
        return new UseCommand(databaseToken.getValue());
    }

    private CreateCommand parseCreateCommand() {
        if (matchKeyword("DATABASE")) {
            Token databaseToken = consume(TokenType.IDENTIFIER, "Expected database name");
            return new CreateCommand(databaseToken.getValue(), null);
        } else if (matchKeyword("TABLE")) {
            Token tableToken = consume(TokenType.IDENTIFIER, "Expected table name");

            List<String> columnNames = new ArrayList<>();
            if (match(TokenType.LEFT_PAREN)) {
                // Parse column definitions
                do {
                    Token columnToken = consume(TokenType.IDENTIFIER, "Expected column name");
                    columnNames.add(columnToken.getValue());
                } while (match(TokenType.COMMA));

                consume(TokenType.RIGHT_PAREN, "Expected ')' after column definitions");
            }

            return new CreateCommand(tableToken.getValue(), columnNames);
        } else {
            throw new RuntimeException("Expected 'DATABASE' or 'TABLE' after 'CREATE'");
        }
    }

    private DropCommand parseDropCommand() {
        boolean isDatabase = false;
        if (matchKeyword("DATABASE")) {
            isDatabase = true;
        } else if (matchKeyword("TABLE")) {
            isDatabase = false;
        } else {
            throw new RuntimeException("Expected 'DATABASE' or 'TABLE' after 'DROP'");
        }

        Token nameToken = consume(TokenType.IDENTIFIER, "Expected name");
        return new DropCommand(nameToken.getValue(), isDatabase);
    }

    private AlterCommand parseAlterCommand() {
        consumeKeyword("TABLE", "Expected 'TABLE' after 'ALTER'");
        Token tableToken = consume(TokenType.IDENTIFIER, "Expected table name");

        boolean isAdd = false;
        if (matchKeyword("ADD")) {
            isAdd = true;
        } else if (matchKeyword("DROP")) {
            isAdd = false;
        } else {
            throw new RuntimeException("Expected 'ADD' or 'DROP' after table name");
        }

        Token columnToken = consume(TokenType.IDENTIFIER, "Expected column name");
        return new AlterCommand(tableToken.getValue(), columnToken.getValue(), isAdd);
    }

    private InsertCommand parseInsertCommand() {
        consumeKeyword("INTO", "Expected 'INTO' after 'INSERT'");
        Token tableToken = consume(TokenType.IDENTIFIER, "Expected table name");

        consumeKeyword("VALUES", "Expected 'VALUES' after table name");
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'VALUES'");

        List<String> values = new ArrayList<>();
        do {
            if (match(TokenType.STRING_LITERAL)) {
                values.add("'" + tokens.get(position - 1).getValue() + "'");
            } else if (match(TokenType.NUMBER)) {
                values.add(tokens.get(position - 1).getValue());
            } else if (matchKeyword("TRUE") || matchKeyword("FALSE")) {
                values.add(tokens.get(position - 1).getValue());
            } else if (matchKeyword("NULL")) {
                values.add("NULL");
            } else {
                throw new RuntimeException("Expected value");
            }
        } while (match(TokenType.COMMA));

        consume(TokenType.RIGHT_PAREN, "Expected ')' after values");
        return new InsertCommand(tableToken.getValue(), values);
    }

    private SelectCommand parseSelectCommand() {
        // Parse the attribute list
        List<String> attributes = new ArrayList<>();
        if (match(TokenType.STAR)) {
            attributes.add("*");
        } else {
            do {
                Token attributeToken = consume(TokenType.IDENTIFIER, "Expected attribute name");
                attributes.add(attributeToken.getValue());
            } while (match(TokenType.COMMA));
        }

        // Parse the FROM clause
        consumeKeyword("FROM", "Expected 'FROM' after attribute list");
        Token tableToken = consume(TokenType.IDENTIFIER, "Expected table name");

        // Parse the WHERE clause if present
        Condition condition = null;
        if (matchKeyword("WHERE")) {
            condition = parseCondition();
        }

        return new SelectCommand(tableToken.getValue(), attributes, condition);
    }

    private UpdateCommand parseUpdateCommand() {
        Token tableToken = consume(TokenType.IDENTIFIER, "Expected table name");

        consumeKeyword("SET", "Expected 'SET' after table name");

        // Parse name-value pairs
        Map<String, String> assignments = new HashMap<>();
        do {
            Token attributeToken = consume(TokenType.IDENTIFIER, "Expected attribute name");
            consume(TokenType.OPERATOR, "Expected '=' after attribute name");

            String value;
            if (match(TokenType.STRING_LITERAL)) {
                value = "'" + tokens.get(position - 1).getValue() + "'";
            } else if (match(TokenType.NUMBER)) {
                value = tokens.get(position - 1).getValue();
            } else if (matchKeyword("TRUE") || matchKeyword("FALSE")) {
                value = tokens.get(position - 1).getValue();
            } else if (matchKeyword("NULL")) {
                value = "NULL";
            } else {
                throw new RuntimeException("Expected value");
            }

            assignments.put(attributeToken.getValue(), value);
        } while (match(TokenType.COMMA));

        // Parse the WHERE clause
        consumeKeyword("WHERE", "Expected 'WHERE' after assignments");
        Condition condition = parseCondition();

        return new UpdateCommand(tableToken.getValue(), assignments, condition);
    }

    private DeleteCommand parseDeleteCommand() {
        consumeKeyword("FROM", "Expected 'FROM' after 'DELETE'");
        Token tableToken = consume(TokenType.IDENTIFIER, "Expected table name");

        // Parse the WHERE clause
        consumeKeyword("WHERE", "Expected 'WHERE' after table name");
        Condition condition = parseCondition();

        return new DeleteCommand(tableToken.getValue(), condition);
    }

    private JoinCommand parseJoinCommand() {
        Token table1Token = consume(TokenType.IDENTIFIER, "Expected first table name");

        consumeKeyword("AND", "Expected 'AND' after first table name");
        Token table2Token = consume(TokenType.IDENTIFIER, "Expected second table name");

        consumeKeyword("ON", "Expected 'ON' after second table name");
        Token attribute1Token = consume(TokenType.IDENTIFIER, "Expected first attribute name");

        consumeKeyword("AND", "Expected 'AND' after first attribute name");
        Token attribute2Token = consume(TokenType.IDENTIFIER, "Expected second attribute name");

        return new JoinCommand(table1Token.getValue(), table2Token.getValue(),
                attribute1Token.getValue(), attribute2Token.getValue());
    }

    private Condition parseCondition() {
        // Handle parenthesized conditions
        if (match(TokenType.LEFT_PAREN)) {
            Condition condition = parseCondition();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after condition");

            // Check if there's a boolean operator after this condition
            if (matchKeyword("AND")) {
                Condition rightCondition = parseCondition();
                return new AndCondition(condition, rightCondition);
            } else if (matchKeyword("OR")) {
                Condition rightCondition = parseCondition();
                return new OrCondition(condition, rightCondition);
            }

            return condition;
        }

        // Simple comparison condition
        Token attributeToken = consume(TokenType.IDENTIFIER, "Expected attribute name");
        String operator = consume(TokenType.OPERATOR, "Expected operator").getValue();

        String value;
        if (match(TokenType.STRING_LITERAL)) {
            value = "'" + tokens.get(position - 1).getValue() + "'";
        } else if (match(TokenType.NUMBER)) {
            value = tokens.get(position - 1).getValue();
        } else if (matchKeyword("TRUE") || matchKeyword("FALSE")) {
            value = tokens.get(position - 1).getValue();
        } else if (matchKeyword("NULL")) {
            value = "NULL";
        } else {
            throw new RuntimeException("Expected value");
        }

        // Create appropriate condition based on operator
        switch (operator) {
            case "==":
                return new EqualsCondition(attributeToken.getValue(), value);
            case ">":
                return new GreaterThanCondition(attributeToken.getValue(), value);
            case "<":
                return new LessThanCondition(attributeToken.getValue(), value);
            case ">=":
                return new GreaterThanOrEqualsCondition(attributeToken.getValue(), value);
            case "<=":
                return new LessThanOrEqualsCondition(attributeToken.getValue(), value);
            case "!=":
                return new NotEqualsCondition(attributeToken.getValue(), value);
            case "LIKE":
                return new LikeCondition(attributeToken.getValue(), value);
            default:
                throw new RuntimeException("Unsupported operator: " + operator);
        }
    }
}