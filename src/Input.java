import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

public class Input {
    // Public methods
        // Buffered input reader enum
        public static enum BufferedInputReader {
            SYSTEM_IN,
            FILE_READER
        }

        // Read a line with a scanner
        public static String readLine() {
            try {
                return getScanner().nextLine();
            } catch (Exception e) {
                Utils.clearConsole();
                System.out.println("Error reading input.");
                System.out.println("Exception: " + e);
                return null;
            }
        }

        // Read the integer value of a character with a buffered reader
        public static int readBufferedInt() {
            try {
                return getReader(BufferedInputReader.SYSTEM_IN, null).read();
            } catch (Exception e) {
                Utils.clearConsole();
                System.out.println("Error reading character.");
                System.out.println("Exception: " + e);
                return -1;
            }
        }

        // Read a line with a buffered reader
        public static String readBufferedString(BufferedInputReader readerType, File file) {
            try {
                return getReader(readerType, file).readLine();
            } catch (Exception e) {
                Utils.clearConsole();
                System.out.println("Error reading buffered input.");
                System.out.println("Exception: " + e);
                return null;
            }
        }

        // Close all input streams
        public static void closeInput() {
            closeScanner();
            closeReader();
            closeFileReader();
            closeInputReader();
        }

    // Helper methods
        // Class level variables
        private static Scanner scanner;
        private static BufferedReader reader;
        private static FileReader fileReader;
        private static InputStreamReader inputReader;
        private static BufferedInputReader currReaderType;

        // Return a singleton scanner
        private static Scanner getScanner() {
            if (scanner == null) {
                scanner = new Scanner(System.in);
            }
            return scanner;
        }

        // Return a singleton file reader
        private static FileReader getFileReader(File file) {
            try {
                fileReader = new FileReader(file);
            } catch (Exception e) {
                Utils.clearConsole();
                System.out.println("Error reading file.");
                System.out.println("Exception: " + e);
                System.exit(1);
            }
            return fileReader;
        }

        // Return a singleton input stream reader
        private static InputStreamReader getInputReader() {
            if (inputReader == null) {
                inputReader = new InputStreamReader(System.in);
            }
            return inputReader;
        }

        // Return a singleton buffered reader
        private static BufferedReader getReader(BufferedInputReader readerType, File file) {
            if (reader == null || currReaderType != readerType) {
                currReaderType = readerType;
                switch (readerType) {
                    case SYSTEM_IN:
                        reader = new BufferedReader(getInputReader());
                        break;
                    case FILE_READER:
                        reader = new BufferedReader(getFileReader(file));
                        break;
                    default:
                    Utils.clearConsole();
                        System.out.println("Invalid reader type.");
                        System.exit(1);
                }
            }
            return reader;
        }

        // Close the scanner
        private static void closeScanner() {
            if (scanner != null) {
                scanner.close();
                scanner = null;
            }
        }

        // Close the buffered reader
        private static void closeReader() {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (Exception e) {
                    Utils.clearConsole();
                    System.out.println("Error closing reader.");
                    System.out.println("Exception: " + e);
                    System.exit(1);
                }
            }
        }

        // Close the file reader
        private static void closeFileReader() {
            if (fileReader != null) {
                try {
                    fileReader.close();
                    fileReader = null;
                } catch (Exception e) {
                    Utils.clearConsole();
                    System.out.println("Error closing file reader.");
                    System.out.println("Exception: " + e);
                    System.exit(1);
                }
            }
        }

        // Close the input stream reader
        private static void closeInputReader() {
            if (inputReader != null) {
                try {
                    inputReader.close();
                    inputReader = null;
                } catch (Exception e) {
                    Utils.clearConsole();
                    System.out.println("Error closing input reader.");
                    System.out.println("Exception: " + e);
                    System.exit(1);
                }
            }
        }
}