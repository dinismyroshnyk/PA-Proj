package main;
import java.net.ServerSocket;
import java.net.Socket;

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
                serverSocket = new ServerSocket(PORT);
                System.out.println("Server started on port " + PORT);
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket)).start();
                }
            }, "Error starting server.");
        }

    // Helper methods
        /**
         * Class level variables
         */
        private static final int PORT = 8080;
        private static ServerSocket serverSocket;

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
                        // Espera a mensagem de hello do cliente
                        String input = IO.readBufferedString(IO.BufferedInputReader.SOCKET, null, clientSocket);
                        if (input.equals("<hello>;")) {
                            // Envia a resposta de confirmação para o cliente
                            IO.writeBufferedString("<login> <ack>;", clientSocket);
                            System.out.println("<" + clientAddress + "> " + input);
                        } else {
                            System.out.println("Client did not send the correct initial message.");
                        }
                        while (!clientSocket.isClosed()) {
                            input = IO.readBufferedString(IO.BufferedInputReader.SOCKET, null, clientSocket);
                            System.out.println(clientAddress + ": " + input);
                            if (input.equalsIgnoreCase("exit")) {
                                clientSocket.close();
                            }
                        }
                        System.out.println("Client disconnected: " + clientAddress);
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
        }
}