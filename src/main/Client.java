package main;
import java.net.Socket;

/**
 * Contains the methods to start and close the client.
 */
public class Client {
    // Public methods
        /**
         * Starts the client and connects to the server.
         *
         * @param args The command line arguments (unused).
         */
        public static void main(String[] args) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                closeSocket();
                IO.closeIO();
                OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.SANE);
                Utils.clearConsole();
            }));

            IO.ioTaskWithErrorHandling(() -> {
                serverSocket = new Socket(SERVER_ADDRESS, PORT);
                String clientAddress = serverSocket.getLocalAddress().getHostAddress();
                System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + PORT);
                System.out.println("Client running...");
                IO.writeBufferedString("<" + clientAddress + "> <hello>;", serverSocket);
                while (!serverSocket.isClosed()) {
                    String input = IO.readLine();
                    IO.writeBufferedString(input, serverSocket);
                    if (input.equalsIgnoreCase("exit")) {
                        IO.writeBufferedString("<" + clientAddress + "> <bye>;", serverSocket);
                        closeSocket();
                        System.exit(0);
                    }
                }
            }, "Error connecting to server.");
        }

    // Helper methods
        /**
         * Class level variables
         */
        private static final String SERVER_ADDRESS = "127.0.0.1";
        private static final int PORT = 8080;
        private static Socket serverSocket;

        /**
         * Closes the client socket.
         */
        private static void closeSocket() {
            IO.ioTaskWithErrorHandling(() -> {
                serverSocket.close();
            }, "Error closing client socket.");
        }
}