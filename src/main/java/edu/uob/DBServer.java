// DBServer.java
package edu.uob;

import edu.uob.commands.Command;
import edu.uob.models.QueryResult;
import edu.uob.parser.SQLParser;
import edu.uob.storage.DBManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public class DBServer {
    private static final char END_OF_TRANSMISSION = 4;
    private DBManager dbManager;

    public static void main(String[] args) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    public DBServer() {
        // Initialize the DBManager with the correct storage folder path
        String workingDirectory = Paths.get("").toAbsolutePath().toString();
        String databaseDirectory = workingDirectory + "/databases";
        dbManager = new DBManager(databaseDirectory);
    }

    // This method handles client connections and processes SQL commands
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);

            // Handle incoming client connections
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

                    System.out.println("Client connected");
                    String line;
                    StringBuilder commandBuilder = new StringBuilder();

                    // Read client command
                    while ((line = in.readLine()) != null) {
                        commandBuilder.append(line);
                    }

                    String command = commandBuilder.toString();
                    System.out.println("Received command: " + command);

                    // Process command and send response
                    String response = handleCommand(command);
                    out.write(response);
                    out.write(END_OF_TRANSMISSION);
                    out.flush();

                    System.out.println("Response sent, closing connection");
                }
            }
        }
    }

    // This method handles incoming SQL commands
    public String handleCommand(String command) {
        try {
            // Parse the command
            SQLParser parser = new SQLParser(command);
            Command sqlCommand = parser.parse();

            // Execute the command
            QueryResult result = sqlCommand.execute(dbManager);

            // Return success response
            return "[OK]\n" + result.toString();
        } catch (Exception e) {
            // Return error response
            return "[ERROR] " + e.getMessage();
        }
    }
}