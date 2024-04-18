import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        // Prepare and connect to the database
        Database.setUpDatabase();
        // Application and SQL handling
        Database.databaseTaskWithErrorHandling(() -> {
            mainLoop();
        }, true);
        // Close the scanner and exit the application
        Input.closeScanner();
        System.exit(0);
    }

    // Main loop of the application
    private static void mainLoop() {
        boolean running = true;
        LocalDateTime startTime = LocalDateTime.now();
        while (running) {
            clearConsole();
            OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), "raw");
            String title = "Menu";
            String[] menuItems = {
                "1. Login",
                "2. Register",
                "0. Exit"
            };
            OS.drawMenuBox(title, menuItems);
            OS.insertBoxItems(title, menuItems);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                char c = (char)reader.read();
                switch (c) {
                    case '1':
                        clearConsole();
                        System.out.println("Login");
                        pressEnterKey();
                        break;
                    case '2':
                        clearConsole();
                        System.out.println("Register");
                        pressEnterKey();
                        break;
                    case '0':
                        clearConsole();
                        showExecutionTime(startTime);
                        OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), "sane");
                        running = false;
                        break;
                    default:
                        clearConsole();
                        System.out.println("Invalid option. Please try again.");
                        pressEnterKey();
                        break;
                }
            } catch (IOException e) {
                System.out.println("Error reading input.");
                System.out.println("Exception: " + e);
            }
        }
    }

    private static void showExecutionTime(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("EEEE; yyyy-MM-dd HH:mm:ss");
        long executionTimeMillis = Duration.between(startTime, endTime).toMillis();
        Duration duration = Duration.ofMillis(executionTimeMillis);
        String formattedExecutionTime = String.format("%d milliseconds (%d seconds; %d minutes; %d hours)", executionTimeMillis, duration.toSecondsPart(), duration.toMinutesPart(), duration.toHours());
        String title = "Execution Time";
        String[] menuItems = {
            "Process start: " + startTime.format(date),
            "Process end: " + endTime.format(date),
            "Total time: " + formattedExecutionTime
        };
        OS.drawMenuBox(title, menuItems);
        OS.insertBoxItems(title, menuItems);
    }

    // Clear the console
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Press E nter key to continue
    public static void pressEnterKey() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Press Enter to continue...");
        try {
            boolean pressed = false;
            while (!pressed) {
                switch ((int)reader.read()) {
                    case 10: case 13:
                        pressed = true;
                        break;
                    default:
                        // Do nothing
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading input.");
            System.out.println("Exception: " + e);
        }
    }

    // Get current date
    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
}