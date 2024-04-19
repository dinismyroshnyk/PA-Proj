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
            "Login",
            "Register",
            "DB params",
            "Exit"
        };
        final int[] selectedId = {0};
        int maxId = menuItems.length - 1;
        OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.RAW);
        OS.runTaskInSaneMode(() -> {
            Output.drawBox(title, menuItems, selectedId);
        });
        while (running) {
            int input = Input.readBufferedInt();
            if (input == 27) {
                input = Input.readBufferedInt();
                if (Input.readBufferedInt() == 91) {
                    input = Input.readBufferedInt();
                    if (input == 65) {
                        input = 106;
                    } else if (input == 66) {
                        input = 107;
                    }
                }
            }
            switch (input) {
                case 106:
                    Utils.clearConsole();
                    if (selectedId[0] < maxId) {
                        selectedId[0] += 1;
                    } else {
                        selectedId[0] = 0;
                    }
                    OS.runTaskInSaneMode(() -> {
                        Output.drawBox(title, menuItems, selectedId);
                    });
                    break;
                case 107:
                    Utils.clearConsole();
                    if (selectedId[0] > 0) {
                        selectedId[0] -= 1;
                    } else {
                        selectedId[0] = maxId;
                    }
                    OS.runTaskInSaneMode(() -> {
                        Output.drawBox(title, menuItems, selectedId);
                    });
                    break;
                case 10: case 13:
                    Utils.clearConsole();
                    switch (selectedId[0]) {
                        case 0:
                            OS.runTaskInSaneMode(() -> {
                                System.out.println("Login");
                            });
                            Utils.pressEnterKey();
                            break;
                        case 1:
                            OS.runTaskInSaneMode(() -> {
                                System.out.println("Register");
                            });
                            Utils.pressEnterKey();
                            break;
                        case 2:
                            OS.runTaskInSaneMode(() -> {
                                System.out.println("DB params");
                            });
                            Utils.pressEnterKey();
                            break;
                        case 3:
                            Utils.clearConsole();
                            OS.runTaskInSaneMode(() -> {
                                showExecutionTime(startTime);
                            });
                            running = false;
                            return;
                        default:
                            // Do nothing
                            break;
                    }
                    Utils.clearConsole();
                    OS.runTaskInSaneMode(() -> {
                        Output.drawBox(title, menuItems, selectedId);
                    });
                    break;
                default:
                    // Do nothing
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
        int[] outOfBoundsId = {-1};
        Output.drawBox(title, menuItems, outOfBoundsId);
    }
}