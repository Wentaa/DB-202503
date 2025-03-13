// TokenType.java
package edu.uob.parser;

public enum TokenType {
    KEYWORD,       // SQL keywords like SELECT, FROM, WHERE
    IDENTIFIER,    // Table names, column names
    STRING_LITERAL,// String values in quotes
    NUMBER,        // Integer or floating point numbers
    BOOLEAN,       // TRUE or FALSE
    OPERATOR,      // Comparison operators like ==, !=, >,
    COMMA,         // ,
    SEMICOLON,     // ;
    LEFT_PAREN,    // (
    RIGHT_PAREN,   // )
    STAR,          // *
    EOF            // End of input
}