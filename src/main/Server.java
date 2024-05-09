package main;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Contains the methods to start and close the server.
 * Also contains the ClientHandler helper class to handle client connections.
 */
public class Server {
    // Public methods
        /**
         * Starts the server on the specified port.
         *
         * @param args The command line arguments (unused).
         */
        public static void main(String[] args) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                closeServer();
                IO.closeIO();
                Utils.clearConsole();
            }));

            new Thread(() -> {
                while (true) {
                    String input = IO.readLine();
                    if (input.equalsIgnoreCase("exit")) {
                        closeServer();
                        System.exit(0);
                    }
                }
            }).start();

            IO.ioTaskWithErrorHandling(() -> {
                int port = getPort();
                System.out.println("Port set to " + port);
                serverSocket = new ServerSocket(port);
                System.out.println("Server started on port " + port);
                while (!serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(new ClientHandler(clientSocket)).start();
                    } catch (SocketException e) {
                        System.out.println("Server closed.");
                    }
                }
            }, "Error starting server.");
        }

    // Helper methods
        /**
         * Class level variables
         */
        private static ServerSocket serverSocket;

        /**
         * Asks the server administrator to enter the server port.
         *
         * @return The server port.
         */
        private static int getPort() {
            int port;
            while (true) {
                System.out.print("Enter the exposed server port: ");
                String input = IO.readLine().trim();
                port = Utils.tryParseInt(input);
                if (port < 1 || port > 65535) {
                    System.out.println("Invalid port.");
                } else {
                    break;
                }
            }
            return port;
        }

        /**
         * Closes the server socket.
         */
        private static void closeServer() {
            IO.ioTaskWithErrorHandling(() -> {
                serverSocket.close();
            }, "Error closing server socket.");
        }

        /**
         * Contains the methods to handle client connections.
         */
        private static class ClientHandler implements Runnable {
            // Public methods
                /**
                 * Constructor for the ClientHandler class.
                 *
                 * @param socket The client socket to handle.
                 */
                public ClientHandler(Socket socket) {
                    this.clientSocket = socket;
                }

                /**
                 * Handles the client connection.
                 */
                @Override
                public void run() {
                    try {
                        String clientAddress = clientSocket.getInetAddress().getHostAddress();
                        System.out.println("New client connected: " + clientAddress);
                        validateConnection(clientAddress, clientSocket);
                        while (!clientSocket.isClosed()) {
                            String input = IO.readBufferedString(IO.BufferedInputReader.SOCKET, null, clientSocket);
                            if (input.equalsIgnoreCase("exit")) {
                                clientSocket.close();
                            } else {
                                System.out.println(input);
                            }
                        }
                        System.out.println("<" + clientAddress + ">" + " <bye>;");
                    } catch (Exception e) {
                        Utils.clearConsole();
                        System.out.println("Error handling client: " + e.getMessage());
                    } finally {
                        IO.ioTaskWithErrorHandling(() -> {
                            clientSocket.close();
                        }, "Error closing client socket.");
                    }
                }

            // Helper methods
                /**
                 * Class level variables
                 */
                private final Socket clientSocket;

                /**
                 * Validates the client connection.
                 *
                 * @param clientAddress The client address.
                 * @param clientSocket The client socket.
                 */
                private void validateConnection(String clientAddress, Socket clientSocket) {
                    String receivedMsg = IO.readBufferedString(IO.BufferedInputReader.SOCKET, null, clientSocket);
                    if (receivedMsg.equals("<" + clientAddress + "> <hello>;")) {
                        IO.writeBufferedString("<server> <ack>;", clientSocket);
                        System.out.println("<" + clientAddress + "> " + receivedMsg);
                    } else {
                        System.out.println("Client did not send the correct initial message.");
                    }
                }
        }
}