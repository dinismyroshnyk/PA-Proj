import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) {
        // Add a shutdown hook to close the input stream and restore the console mode
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.SANE);
            Input.closeInput();
            Utils.clearConsole();
        }));
        // Start the timer
        Utils.getStartTime();
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
        Map<String, Consumer<Integer>> actions = new HashMap<>();
        actions.put("Login", id -> loginAction());
        actions.put("Register", id -> registerAction());
        actions.put("DB params", id -> dbParamsAction());
        actions.put("Exit", id -> exitAction());
        Utils.clearConsole();
        drawMenu(title, menuItems, selectedId);
        while (true) {
            int input = checkArrowKeys();
            switch (input) {
                case 106:
                    Utils.clearConsole();
                    selectedId[0] = (selectedId[0] + 1) % (maxId + 1);
                    drawMenu(title, menuItems, selectedId);
                    break;
                case 107:
                    Utils.clearConsole();
                    selectedId[0] = (selectedId[0] - 1 + (maxId + 1)) % (maxId + 1);
                    drawMenu(title, menuItems, selectedId);
                    break;
                case 99:
                    Utils.clearConsole();
                    Output.drawControls();
                    Utils.pressEnterKey();
                    Utils.clearConsole();
                    drawMenu(title, menuItems, selectedId);
                    break;
                case 10: case 13:
                    Utils.clearConsole();
                    actions.get(menuItems[selectedId[0]]).accept(selectedId[0]);
                    Utils.pressEnterKey();
                    if (menuItems[selectedId[0]].equals("Exit")) {
                        return;
                    } else {
                        Utils.clearConsole();
                        drawMenu(title, menuItems, selectedId);
                    }
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

    // Check the arrow keys
    private static int checkArrowKeys() {
        int input = Input.readBufferedInt();
        if (input == 27) {
            input = Input.readBufferedInt();
            if (input == 91) {
                input = Input.readBufferedInt();
                switch (input) {
                    case 65:
                        input = 107;
                        break;
                    case 66:
                        input = 106;
                        break;
                    default:
                        // Do nothing
                        break;
                }
            }
        }
        return input;
    }

    // Draw the menu
    private static void drawMenu(String title, String[] menuItems, int[] selectedId) {
        OS.runTaskInSaneMode(() -> {
            Output.drawBox(title, menuItems, selectedId);
            System.out.println("Controls: c");
        });
    }

    // Login action
    private static void loginAction() {
        OS.runTaskInSaneMode(() -> {
            System.out.println("Login");
        });
    }

    // Register action
    private static void registerAction() {
        OS.runTaskInSaneMode(() -> {
            System.out.println("Register");
        });
    }

    // DB params action
    private static void dbParamsAction() {
        OS.runTaskInSaneMode(() -> {
            System.out.println("DB params");
        });
    }

    // Exit action
    private static void exitAction() {
        OS.runTaskInSaneMode(() -> {
            showExecutionTime(Utils.getStartTime());
        });
    }
}