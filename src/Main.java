import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        // Add a shutdown hook to close the input stream and restore the console mode
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.SANE);
            Input.closeInput();
        }));
        // Prepare and connect to the database
        Database.setUpDatabase();
        // Application and SQL handling
        Database.databaseTaskWithErrorHandling(() -> {
            mainLoop();
        }, true);
        // Exit the application
        System.exit(0);
    }

    // Main loop of the application
    private static void mainLoop() {
        boolean running = true;
        LocalDateTime startTime = LocalDateTime.now();
        String title = "Menu";
        String[] menuItems = {
            "1. Login",
            "2. Register",
            "3. DB params",
            "0. Exit"
        };
        OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.RAW);
        while (running) {
            Utils.clearConsole();
            OS.runTaskInSaneMode(() -> {
                Output.drawBox(title, menuItems);
            });
            switch (Input.readBufferedInt()) {
                case 49:
                    Utils.clearConsole();
                    OS.runTaskInSaneMode(() -> {
                        System.out.println("Login");
                    });
                    Utils.pressEnterKey();
                    break;
                case 50:
                    Utils.clearConsole();
                    OS.runTaskInSaneMode(() -> {
                        System.out.println("Register");
                    });
                    Utils.pressEnterKey();
                    break;
                case 51:
                    Utils.clearConsole();
                    OS.runTaskInSaneMode(() -> {
                        System.out.println("DB params");
                    });
                    Utils.pressEnterKey();
                    break;
                case 48:
                    Utils.clearConsole();
                    OS.runTaskInSaneMode(() -> {
                        showExecutionTime(startTime);
                    });
                    running = false;
                    break;
                default:
                    Utils.clearConsole();
                    OS.runTaskInSaneMode(() -> {
                        System.out.println("Invalid option. Please try again.");
                    });
                    Utils.pressEnterKey();
                    break;
            }
        }
    }

    // Show the execution time
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
        Output.drawBox(title, menuItems);
    }
}