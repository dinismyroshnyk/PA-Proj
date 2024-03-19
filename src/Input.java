import java.util.Scanner;

public class Input {
    private static Scanner scanner;

    private static Scanner getScanner() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }

    public static String readLine() {
        try {
            return getScanner().nextLine();
        } catch (Exception e) {
            System.out.println("Error reading input.");
            System.out.println("Exception: " + e);
            return null;
        }
    }

    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
            scanner = null;
        }
    }
}
