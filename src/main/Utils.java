package main;
import java.io.File;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;

/**
 * Contains utility methods used throughout the program.
 */
public class Utils {
    // Public methods
        /**
         * Clears the console using the appropriate command for the operating system.
         */
        public static void clearConsole() {
            String os = System.getProperty("os.name").toLowerCase();
            String error = "Error clearing console.";
            IO.ioTaskWithErrorHandling(()-> {
                if (os.contains("win")) {
                    getProcessBuilder().command("cmd", "/c", "cls").inheritIO().start().waitFor();
                } else {
                    String[] cmd = {"/bin/sh", "-c", "clear"};
                    Runtime.getRuntime().exec(cmd).waitFor();
                }
            }, error);
        }

        /**
         * Asks the user to press Enter before continuing.
         */
        public static void pressEnterKey() {
            System.out.print("Press Enter to continue...");
            boolean pressed = false;
            while (!pressed) {
                switch (IO.readBufferedInt()) {
                    case 10: case 13:
                        pressed = true;
                        break;
                    default:
                        // Do nothing
                        break;
                }
            }
        }

        /**
         * Gets the current date based on the system time.
         *
         * @return The current date.
         */
        public static Date getCurrentDate() {
            return new Date(System.currentTimeMillis());
        }

        /**
         * Creates a key-value pair.
         *
         * @param key The key.
         * @param value The value.
         * @return The key-value pair.
         */
        public static <K, V> Map.Entry<K, V> pair(K key, V value) {
            return new AbstractMap.SimpleEntry<>(key, value);
        }

        /**
         * Gets the start time of the program.
         *
         * @return The start time.
         */
        public static LocalDateTime getStartTime() {
            if (startTime == null) {
                startTime = LocalDateTime.now();
            }
            return startTime;
        }

        /**
         * Reads parameters from a file.
         *
         * @param file The file to read from.
         * @param params The array of strings to store the read parameters.
         * @return The parameters as an array of strings.
         */
        public static String[] readParamsFromFile(File file, String[] params) {
            for (int i = 0; i < params.length; i++) {
                params[i] = IO.readBufferedString(IO.BufferedInputReader.FILE_READER, file, null);
                if (i == 4) {
                    params[i] = Security.encryptDecryptString(params[i], Security.EncryptionParam.DECRYPT);
                }
            }
            return params;
        }

        /**
         * Tries to parse an integer from a string.
         *
         * @param input The string to parse.
         * @return The integer if successful, or -1 if not.
         */
        public static int tryParseInt(String input) {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                return -1;
            }
        }

    // Helper methods
        /**
         * Class level variables
         */
        private static LocalDateTime startTime;
        private static ProcessBuilder processBuilder;

        /**
         * Gets a singleton instance of the process builder to execute commands on a Windows machine.
         *
         * @return The process builder.
         */
        public static ProcessBuilder getProcessBuilder() {
            if (processBuilder == null) {
                processBuilder = new ProcessBuilder();
            }
            return processBuilder;
        }
}