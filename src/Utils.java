import java.io.File;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;

public class Utils {
    // Public methods
        // Clear the console
        public static void clearConsole() {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }

        // Press Enter key to continue
        public static void pressEnterKey() {
            System.out.print("Press Enter to continue...");
            boolean pressed = false;
            while (!pressed) {
                switch (Input.readBufferedInt()) {
                    case 10: case 13:
                        pressed = true;
                        break;
                    default:
                        // Do nothing
                        break;
                }
            }
        }

        // Get current date
        public static Date getCurrentDate() {
            return new Date(System.currentTimeMillis());
        }

        // Create a pair of key-value
        public static <K, V> Map.Entry<K, V> pair(K key, V value) {
            return new AbstractMap.SimpleEntry<>(key, value);
        }

        // Application start time
        public static LocalDateTime getStartTime() {
            if (startTime == null) {
                startTime = LocalDateTime.now();
            }
            return startTime;
        }

        // Read the credentials from the file
        public static String[] readParamsFromFile(File file, String[] params) {
            for (int i = 0; i < params.length; i++) {
                params[i] = Input.readBufferedString(Input.BufferedInputReader.FILE_READER, file);
                if (i == 4) {
                    params[i] = Security.encryptDecryptString(params[i], Security.EncryptionParam.DECRYPT);
                }
            }
            return params;
        }

    // Helper methods
        // Class level variables
        private static LocalDateTime startTime;
}