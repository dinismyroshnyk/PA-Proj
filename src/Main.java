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
            System.out.println("Menu:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("\nOption: ");
            String option = Input.readLine();
            switch (option) {
                case "1":
                    break;
                case "2":
                    break;
                case "0":
                    clearConsole();
                    showExecutionTime(startTime);
                    running = false;
                    break;
                default:
                    clearConsole();
                    System.out.println("Invalid option. Please try again.");
                    pressEnterKey();
                    break;
            }
        }
    }

    private static void showExecutionTime(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("EEEE; yyyy-MM-dd HH:mm:ss");
        long executionTimeMillis = Duration.between(startTime, endTime).toMillis();
        Duration duration = Duration.ofMillis(executionTimeMillis);
        String formattedExecutionTime = String.format("%d milliseconds (%d seconds; %d minutes; %d hours)", executionTimeMillis, duration.toSecondsPart(), duration.toMinutesPart(), duration.toHours());
        System.out.println("[Execution time]");
        System.out.println();
        System.out.println("Process start: " + startTime.format(date));
        System.out.println("Process end: " + endTime.format(date));
        System.out.println("Total time: " + formattedExecutionTime);
        System.out.println();
    }

    // Clear the console
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Press any key to continue
    public static void pressEnterKey() {
        System.out.print("Press Enter to continue...");
        Input.readLine();
    }

    // Get current date
    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
}