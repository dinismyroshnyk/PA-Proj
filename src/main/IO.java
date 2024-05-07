package main;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Contains thread-safe methods for using input/output streams and handling IO exceptions.
 */
public class IO {
    // Public methods
        /**
         * Enumeration for buffered input reader types.
         */
        public static enum BufferedInputReader {
            SYSTEM_IN,
            FILE_READER,
            SOCKET
        }

        /**
         * IO task interface
         */
        public interface IOTask {
            void run() throws IOException, InterruptedException;
        }

        /**
         * Runs an IO task with general error handling.
         *
         * @param task The IO task to run.
         * @param error The error message to display.
         */
        public static void ioTaskWithErrorHandling(IOTask task, String error) {
            try {
                task.run();
            } catch (IOException e) {
                Utils.clearConsole();
                System.out.println(error);
                System.out.println("Exception: " + e);
                System.exit(1);
            } catch (InterruptedException e) {
                Utils.clearConsole();
                System.out.println("Thread interrupted.");
                System.out.println("Exception: " + e);
                Thread.currentThread().interrupt();
                System.exit(1);
            }
        }

        /**
         * Reads a line of input from the user using a scanner object.
         *
         * @return The user's input as a string or null if an exception is caught.
         */
        public static String readLine() {
            try {
                return getScanner().nextLine();
            } catch (Exception e) {
                Utils.clearConsole();
                System.out.println("Error reading line.");
                System.out.println("Exception: " + e);
                return null;
            }
        }

        /**
         * Reads an integer from the user using a buffered reader object.
         *
         * @return The user's input as an integer or -1 if an exception is caught.
         */
        public static int readBufferedInt() {
            try {
                return getReader(BufferedInputReader.SYSTEM_IN, null, null).read();
            } catch (Exception e) {
                Utils.clearConsole();
                System.out.println("Error reading character.");
                System.out.println("Exception: " + e);
                return -1;
            }
        }

        /**
         * Reads a string (user or file) using a buffered reader object.
         *
         * @param readerType The type of buffered reader to use (SYSTEM_IN or FILE_READER).
         * @param file The file to read from (null if reading from user input).
         * @return The string read from the file or null if an exception is caught.
         */
        public static String readBufferedString(BufferedInputReader readerType, File file, Socket socket) {
            try {
                return getReader(readerType, file, socket).readLine();
            } catch (Exception e) {
                Utils.clearConsole();
                System.out.println("Error reading buffered input.");
                System.out.println("Exception: " + e);
                return null;
            }
        }

        /**
         * Writes a string to the socket's output stream using a buffered writer object.
         *
         * @param output The string to write.
         * @param socket The socket to write to.
         */
        public static void writeBufferedString(String output, Socket socket) {
            ioTaskWithErrorHandling(() -> {
                getBufferedWriter(socket).write(output);
                getBufferedWriter(socket).newLine();
                getBufferedWriter(socket).flush();
            }, "Error writing buffered output.");
        }

        /**
         * Closes all the open IO streams.
         */
        public static void closeIO() {
            closeObject(CloseableObject.SCANNER);
            closeObject(CloseableObject.BUFFERED_SYSTEM_IN_READER);
            closeObject(CloseableObject.BUFFERED_FILE_READER);
            closeObject(CloseableObject.BUFFERED_SOCKET_READER);
            closeObject(CloseableObject.FILE_READER);
            closeObject(CloseableObject.INPUT_READER);
            closeObject(CloseableObject.BUFFERED_WRITER);
            closeObject(CloseableObject.OUTPUT_WRITER);
        }

    // Helper methods
        /**
         * Class level variables.
         */
        private static ThreadLocal<Scanner> scanner = new ThreadLocal<Scanner>();
        private static ThreadLocal<BufferedReader> bufferedSystemInReader = new ThreadLocal<BufferedReader>();
        private static ThreadLocal<BufferedReader> bufferedFileReader = new ThreadLocal<BufferedReader>();
        private static ThreadLocal<BufferedReader> bufferedSocketReader = new ThreadLocal<BufferedReader>();
        private static ThreadLocal<FileReader> fileReader = new ThreadLocal<FileReader>();
        private static ThreadLocal<InputStreamReader> inputReader = new ThreadLocal<InputStreamReader>();
        private static ThreadLocal<BufferedWriter> bufferedWriter = new ThreadLocal<BufferedWriter>();
        private static ThreadLocal<OutputStreamWriter> outputStreamWriter = new ThreadLocal<OutputStreamWriter>();

        /**
         * Enumeration for available objects to be closed.
         */
        private static enum CloseableObject {
            SCANNER,
            BUFFERED_SYSTEM_IN_READER,
            BUFFERED_FILE_READER,
            BUFFERED_SOCKET_READER,
            FILE_READER,
            INPUT_READER,
            BUFFERED_WRITER,
            OUTPUT_WRITER
        }

        /**
         * Map of closeable objects and their respective error messages.
         */
        private static Map<CloseableObject, String> errorMsg = Map.of(
            CloseableObject.SCANNER, "Error closing scanner.",
            CloseableObject.BUFFERED_SYSTEM_IN_READER, "Error closing buffered reader for system input.",
            CloseableObject.BUFFERED_FILE_READER, "Error closing buffered reader for file input.",
            CloseableObject.BUFFERED_SOCKET_READER, "Error closing buffered reader for socket input.",
            CloseableObject.FILE_READER, "Error closing file reader.",
            CloseableObject.INPUT_READER, "Error closing input stream reader.",
            CloseableObject.BUFFERED_WRITER, "Error closing buffered writer.",
            CloseableObject.OUTPUT_WRITER, "Error closing output stream writer."
        );

        /**
         * Hash map of closeable objects enumeration and their respective objects.
         */
        private static Map<CloseableObject, Closeable> closeableObjects = new HashMap<CloseableObject, Closeable>();

        /**
         * Creates a per-thread "singleton" scanner object if it doesn't already exist.
         *
         * @return The scanner object.
         */
        private static Scanner getScanner() {
            if (scanner.get() == null) {
                scanner.set(new Scanner(System.in));
                closeableObjects.put(CloseableObject.SCANNER, scanner.get());
            }
            return scanner.get();
        }

        /**
         * Creates a per-thread "singleton" file reader object if it doesn't already exist.
         *
         * @param file The file to read from.
         * @return The file reader object.
         */
        private static FileReader getFileReader(File file) {
            closeObject(CloseableObject.FILE_READER);
            try {
                fileReader.set(new FileReader(file));
                closeableObjects.put(CloseableObject.FILE_READER, fileReader.get());
            } catch (Exception e) {
                Utils.clearConsole();
                System.out.println("Error creating file reader.");
                System.out.println("Exception: " + e);
                System.exit(1);
            }
            return fileReader.get();
        }

        /**
         * Creates a singleton input stream reader object.
         *
         * @return The input stream reader object.
         */
        private static InputStreamReader getInputReader() {
            if (inputReader.get() == null) {
                inputReader.set(new InputStreamReader(System.in));
                closeableObjects.put(CloseableObject.INPUT_READER, inputReader.get());
            }
            return inputReader.get();
        }

        /**
         * Creates a per-thread "singleton" buffered reader object if it doesn't already exist.
         *
         * @param readerType The type of buffered reader to use (SYSTEM_IN or FILE_READER).
         * @param file The file to read from (null if reading from user input).
         * @return The buffered reader object.
         */
        private static BufferedReader getReader(BufferedInputReader readerType, File file, Socket socket) {
            switch (readerType) {
                case SYSTEM_IN:
                    if (bufferedSystemInReader.get() == null) {
                        bufferedSystemInReader.set(new BufferedReader(getInputReader()));
                        closeableObjects.put(CloseableObject.BUFFERED_SYSTEM_IN_READER, bufferedSystemInReader.get());
                    }
                    return bufferedSystemInReader.get();
                case FILE_READER:
                    if (bufferedFileReader.get() == null) {
                        bufferedFileReader.set(new BufferedReader(getFileReader(file)));
                        closeableObjects.put(CloseableObject.BUFFERED_FILE_READER, bufferedFileReader.get());
                    }
                    return bufferedFileReader.get();
                case SOCKET:
                    ioTaskWithErrorHandling(() -> {
                        if (bufferedSocketReader.get() == null) {
                            bufferedSocketReader.set(new BufferedReader(new InputStreamReader(socket.getInputStream())));
                            closeableObjects.put(CloseableObject.BUFFERED_SOCKET_READER, bufferedSocketReader.get());
                        }
                    }, "Error creating buffered reader for socket input.");
                    return bufferedSocketReader.get();
                default:
                    Utils.clearConsole();
                    System.out.println("Invalid reader type.");
                    System.exit(1);
                    return null;
            }
        }

        /**
         * Creates a per-thread "singleton" buffered writer object if it doesn't already exist.
         *
         * @param socket The socket to pass to the output stream writer.
         */
        private static BufferedWriter getBufferedWriter(Socket socket) {
            if (bufferedWriter.get() == null) {
                bufferedWriter.set(new BufferedWriter(getOutputStreamWriter(socket)));
                closeableObjects.put(CloseableObject.BUFFERED_WRITER, bufferedWriter.get());
            }
            return bufferedWriter.get();
        }

        /**
         * Creates a per-thread "singleton" output stream writer object if it doesn't already exist.
         *
         * @param socket The socket to write to.
         */
        private static OutputStreamWriter getOutputStreamWriter(Socket socket) {
            ioTaskWithErrorHandling(() -> {
                if (outputStreamWriter.get() == null) {
                    outputStreamWriter.set(new OutputStreamWriter(socket.getOutputStream()));
                    closeableObjects.put(CloseableObject.OUTPUT_WRITER, outputStreamWriter.get());
                }
            }, "Error creating output stream writer.");
            return outputStreamWriter.get();
        }

        /**
         * Closes the specified object if it exists.
         *
         * @param object The object to be closed.
         */
        private static void closeObject(CloseableObject object) {
            ioTaskWithErrorHandling(() -> {
                Closeable obj = closeableObjects.get(object);
                if (obj != null) {
                    obj.close();
                    closeableObjects.remove(object);
                }
            }, errorMsg.get(object));
        }
}