package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class DBClient {
    private static final char END_OF_TRANSMISSION = 4;

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        // Connect to the server
        Socket socket = new Socket("localhost", 8888);
        System.out.println("Connected to database server");

        // Set up I/O
        BufferedReader socketReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        BufferedWriter socketWriter = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));

        // Keep accepting commands until user exits or we're interrupted
        while (!Thread.interrupted()) {
            try {
                processNextCommand(input, socketReader, socketWriter);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                // Try to continue anyway
            }
        }
    }

    private static void processNextCommand(BufferedReader inputReader,
                                           BufferedReader serverResponseReader,
                                           BufferedWriter serverWriter) throws IOException {
        // Prompt user
        System.out.print("SQL:> ");

        // Get command from user
        String command = inputReader.readLine();

        // Skip empty commands
        if (command == null || command.trim().isEmpty()) {
            return;
        }

        // Exit command
        if (command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit")) {
            System.out.println("Goodbye!");
            System.exit(0);
            return;
        }

        // Send to server
        serverWriter.write(command + "\n");
        serverWriter.flush();

        // Read and display response from server
        String responseLine;
        while ((responseLine = serverResponseReader.readLine()) != null) {
            // Stop at end-of-transmission marker
            if (responseLine.contains("" + END_OF_TRANSMISSION)) {
                break;
            }
            System.out.println(responseLine);
        }
    }
}